package com.groupGreen.ProvenanceCollector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import main.java.com.groupGreen.ProvenanceCollector.MetricsClient;

@SpringBootApplication
public class ProvenanceCollector {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(ProvenanceCollector.class, args);
		MetricsClient metricsClient = context.getBean(MetricsClient.class);
		metricsClient.fetchMetrics();
	}

}
