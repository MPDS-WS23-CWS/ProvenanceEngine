package com.groupGreen.ProvenanceCollector;

import com.groupGreen.Database.DatabaseClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

@SpringBootApplication
public class ProvenanceCollectorApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ProvenanceCollectorApplication.class, args);
        //MetricsClient metricsClient = context.getBean(MetricsClient.class);

        //metricsClient.fetchInstantMetrics();
        //metricsClient.fetchRangeMetrics();


        try {
            DatabaseClient databaseClient=new DatabaseClient();
            databaseClient.init();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}

