package com.prometheus.component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prometheus.acpect.MyTimed;
import com.prometheus.model.GaugeInfo;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;

@RestController
@Component
public class JobMetrics implements MeterBinder,SchedulingConfigurer{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	//更新监控数据间隔
    @Value("${Scheduled.metrices_interval}")
    private  String CRON;
	public Counter job1Counter;

    public Counter job2Counter;
    //保存所需采集的监控项名称
    public static List<String> collectList = new ArrayList<String>();
    //保存所有gauge监控项数据
    public static Map<String, Double> gaugeMap = new HashMap<>();;

    private List<GaugeInfo> gauges;
    
    public static MeterRegistry meterRegistry;

    @Override
    public void bindTo(MeterRegistry meterRegistry) {
    	logger.info("=============================运行JobMetrics bindTo============================");
    	this.meterRegistry = meterRegistry;
    }
 
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		// TODO Auto-generated method stub
		taskRegistrar.addTriggerTask(new Runnable() {
		      @Override
		      public void run() {
		    	  getMetrices();
		      }
		    }, new Trigger() {
				@Override
				public Date nextExecutionTime(TriggerContext triggerContext) {

			        return  new CronTrigger(CRON).nextExecutionTime(triggerContext);
				}
		    	
		    });
	}
    
    public void setCron(String cron) {
    	logger.info("当前cron="+this.CRON+"->将被改变为："+cron);
        this.CRON = cron;
    }
    
//	@Scheduled(fixedRate = 3000)
    @MyTimed(
            value = "getMetrices"
    )
    @RequestMapping("/test")
	public void getMetrices(){	
		for(String collect_name:collectList) {
			double double_value = -1;
			String value = Shmgr.data_shmgr.get(collect_name);
			if(value!=null) {
				try {
					double_value = Double.valueOf(value);
				} catch (Exception e) {}				
			}
			gaugeMap.put(collect_name,double_value);
		}
	}
	
    
    //注册监控项
    public void addGauges(String collect_name) {	
    	if(gaugeMap.get(collect_name)==null) {
    	     gaugeMap.put(collect_name, Double.valueOf(-1));
    	   	 Gauge.builder(collect_name, gaugeMap, x -> x.get(collect_name)).register(meterRegistry);
    	   	logger.info("=============================添加gauge成功============================");
    	}
    }
    
	public void stopMetrices(String collect_name) {
		gaugeMap.put(collect_name,Double.valueOf(-1));
	}
     
    public List<GaugeInfo> getGauges() {
		return gauges;
	}
	public void setGauges(List<GaugeInfo> gauges) {
		this.gauges = gauges;
	}


}
