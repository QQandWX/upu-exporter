package com.example.actuator;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.test.context.junit4.SpringRunner;


public class ActuatorApplicationTests {

	public static void main(String[] args) {
		
		String cron  = "'0/2 * * * * ?'";

		CronTrigger ct =  new CronTrigger(cron);
		System.out.println("222222");
	}

}
