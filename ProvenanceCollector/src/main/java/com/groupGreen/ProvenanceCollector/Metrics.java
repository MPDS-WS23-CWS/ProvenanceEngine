package com.groupGreen.ProvenanceCollector;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "metrics")
@Setter
@Getter
public class Metrics {
    private Map<String, String> resources;
    private Map<String, String> resourcesTimeSeries;
}
