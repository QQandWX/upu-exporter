package com.prometheus.controller;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prometheus.acpect.MyTimed;
import com.prometheus.component.JobMetrics;
import com.prometheus.model.GaugeInfo;
import com.prometheus.utils.ShareMemory;
import com.prometheus.zookeeper.ZkClient;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.search.Search;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

@RestController
public class UPUController {
	

    @Autowired
    private JobMetrics jobMetrics;

    private ShareMemory shareMemory;
/*    @Timed(
            value = "wjf.index",
            extraTags = {"version", "1.0"}
    )*/
    @MyTimed(
           value = "techprimers.hello.request"
    )
	@RequestMapping("/")
	public String index() {			
    	System.out.println("=============/index访问调用=======================================");
	    return "index";
	   
	 }
	@RequestMapping("/updateMetricesScheduled")
	public String updateMetricesScheduled(@RequestParam("cron") String cron) {
		System.out.println("=============/updateMetricesScheduled访问调用=======================================");
		try {
			CronTrigger ct =  new CronTrigger(cron);
		} catch (Exception e) {
			return "fail:cron wrong!";
		}
		jobMetrics.setCron(cron);
		return "success";
	}
	
	@RequestMapping("/get_field")
	public String get_field() throws UnsupportedEncodingException {
		byte[] b = new byte[20];  
		shareMemory.read(40, 20, b);  
        System.out.println(new String(b,"UTF-8"));  
		return "get_field";
	}	

	
	public void print() {
		Search.in(Metrics.globalRegistry).meters().forEach(each -> {
			StringBuilder builder = new StringBuilder();
			builder.append("name:")
					.append(each.getId().getName())
					.append(",tags:")
					.append(each.getId().getTags())
					.append(",type:").append(each.getId().getType())
					.append(",value:").append(each.measure());
			System.out.println(builder.toString());
		});

	}
	
	
}
