package com.groupGreen.ProvenanceCollector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import com.groupGreen.ProvenanceCollector.MetricsClient;

@SpringBootApplication
public class ProvenanceCollectorApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(ProvenanceCollectorApplication.class, args);
		MetricsClient metricsClient = context.getBean(MetricsClient.class);
		metricsClient.fetchMetrics();
	}

}
