package com.groupGreen.ProvenanceCollector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;

@SpringBootApplication
@EnableScheduling
public class ProvenanceCollectorApplication {

    // public static void main(String[] args) {
    //     ConfigurableApplicationContext context = SpringApplication.run(ProvenanceCollectorApplication.class, args);
    //     MetricsClient metricsClient = context.getBean(MetricsClient.class)

        // List<WorkflowTask> newData= metricsClient.fetchNewData();
        // for(WorkflowTask t : newData) {
        //     System.out.println(t);
        // }
    // }

    private static final Logger logger = LoggerFactory.getLogger(ProvenanceCollectorApplication.class);

    @Autowired
    private ConfigurableApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(ProvenanceCollectorApplication.class, args);
    }

    @Scheduled(fixedRate = 10000)
    public void fetchMetricsRegularly() {
        MetricsClient metricsClient = context.getBean(MetricsClient.class);
        List<WorkflowTask> newData = metricsClient.fetchNewData();
//        for (WorkflowTask task : newData) {
//            System.out.println(task);
//        }
    }

    @Bean
    // avoid DataBufferLimitException
    // https://stackoverflow.com/questions/59735951/databufferlimitexception-exceeded-limit-on-max-bytes-to-buffer-webflux-error
    public WebClient webClient() {
        final int size = 256 * 1024 * 1024;
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();
        return WebClient.builder()
                .exchangeStrategies(strategies)
                .build();
    }

}

