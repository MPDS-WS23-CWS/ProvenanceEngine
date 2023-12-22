package com.groupGreen.ProvenanceCollector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ProvenanceCollectorApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ProvenanceCollectorApplication.class, args);
        MetricsClient metricsClient = context.getBean(MetricsClient.class);

        metricsClient.fetchInstantMetrics();
        metricsClient.fetchRangeMetrics();
    }
}

