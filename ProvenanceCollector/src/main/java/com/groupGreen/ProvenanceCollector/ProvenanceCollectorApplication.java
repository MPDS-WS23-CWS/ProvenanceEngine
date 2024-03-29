package com.groupGreen.ProvenanceCollector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
@EnableScheduling
public class ProvenanceCollectorApplication {

    @Autowired
    private ConfigurableApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(ProvenanceCollectorApplication.class, args);
    }

    @Scheduled(fixedRate = 10000)
    public void fetchMetricsRegularly() {
        MetricsClient metricsClient = context.getBean(MetricsClient.class);
        metricsClient.fetchNewData();
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

