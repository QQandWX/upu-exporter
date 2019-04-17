package com.prometheus.zookeeper;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.prometheus.component.JobMetrics;
import com.prometheus.utils.SpringUtil;


public class ZkClient {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private CuratorFramework client;
    private String zookeeperServer;
    private int sessionTimeoutMs;
    private int connectionTimeoutMs;
    private int baseSleepTimeMs;
    private int maxRetries;
    public TreeCache cache;
    private String listenerPath;
    
    private JobMetrics jobMetrics = SpringUtil.getBean(JobMetrics.class);

    
    public void init() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries);
        client = CuratorFrameworkFactory.builder().connectString(zookeeperServer).retryPolicy(retryPolicy)
                .sessionTimeoutMs(sessionTimeoutMs).connectionTimeoutMs(connectionTimeoutMs).build();
        client.start();
        logger.error("init成功===================================================");
        initLocalCache("/upu");
    }

    
    public void stop() {
        client.close();
    }
    
    public void register() {
        try {
            String rootPath = "/" + "services";
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            String serviceInstance = "prometheus" + "-" +  hostAddress + "-";
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(rootPath + "/" + serviceInstance);
            logger.error("注册成功===================================================");           
        } catch (Exception e) {
            logger.error("注册出错", e);
        }
    }
    
    public List<String> getChildren(String path) {
        List<String> childrenList = new ArrayList<>();
        try {
            childrenList = client.getChildren().forPath(path);
        } catch (Exception e) {
            logger.error("获取子节点出错", e);
        }
        return childrenList;
    }
    
    public int getChildrenCount(String path) {
        return getChildren(path).size();
    }

    public List<String> getInstances() {
        return getChildren("/services");
    }

    public int getInstancesCount() {
        return getInstances().size();
    }
    
    
    private void initLocalCache(String watchRootPath) throws Exception {
        cache = new TreeCache(client, watchRootPath);
        TreeCacheListener listener = (client1, event) ->{
        	logger.info("================================监控zookeeper==========================================");
        	logger.info("event:" + event.getType() + " |path:" + (null != event.getData() ? event.getData().getPath() : null));
            if(event.getData()!=null && event.getData().getData()!=null){
            	String node_path = event.getData().getPath();
            	logger.info("node_path:"+node_path);
            	if(node_path==null) {
            		logger.info("node_path为空");
            	}
            	String node_type = event.getType().toString();
            	String node_name = node_path.substring(node_path.lastIndexOf("/")+1);
            	
            	String node_data = new String(event.getData().getData());
            	logger.info("发生变化的节点内容为：" + node_data);
            	if(node_type.equals("NODE_REMOVED")&&node_data.equals("0")) {       
            		jobMetrics.collectList.remove(node_name);
            		jobMetrics.stopMetrices(node_name);
            		logger.info("删除节点(删除监控)："+event.getData().getPath()+"-"+node_data); 
            	}else if(node_type.equals("NODE_ADDED")&&node_data.equals("0")){
            		 jobMetrics.addGauges(node_name);//注册监控项
            		 jobMetrics.collectList.add(node_name);//把监控项名称添加进列表，表示定时器需定时采集此监控项数据    			
                	 logger.info("新增节点："+event.getData().getPath()+"-"+node_data);
            	}else if(node_type.equals("NODE_UPDATED")) {
            		if(node_data.equals("0")) {
            			jobMetrics.collectList.add(node_name);
                		logger.info("更新节点(添加监控)："+event.getData().getPath()+"-"+node_data);
            		}else {  			
            			jobMetrics.collectList.remove(node_name);
            			jobMetrics.stopMetrices(node_name);
            			logger.info("更新节点(删除监控)："+event.getData().getPath()+"-"+node_data);
            		}
            	}
            }

           // client1.getData().
        };
        cache.getListenable().addListener(listener);
        cache.start();
    }
 
    /**
     * 在注册监听器的时候，如果传入此参数，当事件触发时，逻辑由线程池处理
     */
    ExecutorService pool = Executors.newFixedThreadPool(2);
    public void watchPath(String watchPath,TreeCacheListener listener){
        //   NodeCache nodeCache = new NodeCache(client, watchPath, false);
           TreeCache cache = new TreeCache(client, watchPath);
           cache.getListenable().addListener(listener,pool);
           try {
               cache.start();
           } catch (Exception e) {
               e.printStackTrace();
           }
       }

	public CuratorFramework getClient() {
		return client;
	}
	public void setClient(CuratorFramework client) {
		this.client = client;
	}
	public String getZookeeperServer() {
		return zookeeperServer;
	}
	public void setZookeeperServer(String zookeeperServer) {
		this.zookeeperServer = zookeeperServer;
	}
	public int getSessionTimeoutMs() {
		return sessionTimeoutMs;
	}
	public void setSessionTimeoutMs(int sessionTimeoutMs) {
		this.sessionTimeoutMs = sessionTimeoutMs;
	}
	public int getConnectionTimeoutMs() {
		return connectionTimeoutMs;
	}
	public void setConnectionTimeoutMs(int connectionTimeoutMs) {
		this.connectionTimeoutMs = connectionTimeoutMs;
	}
	public int getBaseSleepTimeMs() {
		return baseSleepTimeMs;
	}
	public void setBaseSleepTimeMs(int baseSleepTimeMs) {
		this.baseSleepTimeMs = baseSleepTimeMs;
	}
	public int getMaxRetries() {
		return maxRetries;
	}
	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}  
}
