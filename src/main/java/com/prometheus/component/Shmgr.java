package com.prometheus.component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
@Component
public class Shmgr {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public static Map<String,String> data_shmgr = new HashMap<String,String>();
	public  Gson gson = new Gson();
	

	@Scheduled(fixedRate = 5000)
	public  void getShareMemoryData() {
		String result = "";
        try {
        	File directory = new File("abc");
        	System.out.println(System.getProperty("user.dir"));
   
        	//InputStream io = Thread.currentThread().getContextClassLoader().getResourceAsStream("shm.py");
        	
        	//FileOutputStream out=new FileOutputStream(System.getProperty("user.dir")+"/shm.py");
        	System.out.println(Shmgr.class.getResource("/").getFile());
        	System.out.println("=======当前类的绝对路径==========");
        	String python_path=Shmgr.class.getResource("/").getFile()+"shm.py";
        	System.out.println(python_path);
            Process process = Runtime.getRuntime().exec("python "+python_path);  
            InputStreamReader ir = new InputStreamReader(process.getInputStream(),"utf-8");
            LineNumberReader input = new LineNumberReader(ir);
            result = input.readLine();
            input.close();
            ir.close();
//          process.waitFor();
        } catch (Exception e) {
            System.out.println("调用python脚本并读取结果时出错：" + e.getMessage());
        }
          
        if(result!=null) {
        	logger.info("==================从共享内存中取得数据============================");    
        	 data_shmgr = gson.fromJson(result, data_shmgr.getClass());
             System.out.println(data_shmgr.toString());
        }else {
        	logger.error("==================从共享内存中获取数据失败！============================");
        }
       
	}
}
