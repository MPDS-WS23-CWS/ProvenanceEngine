package com.groupGreen.ProvenanceCollector;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.util.ClientBuilder;

import java.beans.BeanProperty;

import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class K8sClientConfig {

        @Bean
        public ApiClient apiClient() throws Exception {
                ApiClient client = ClientBuilder.standard().build();
                Configuration.setDefaultApiClient(client);
                return client;
        }
    
}
