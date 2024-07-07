package com.commerce.queue_for_reserve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient
@EnableScheduling
@SpringBootApplication
public class QueueForReserveApplication {

	public static void main(String[] args) {
		SpringApplication.run(QueueForReserveApplication.class, args);
	}

}
