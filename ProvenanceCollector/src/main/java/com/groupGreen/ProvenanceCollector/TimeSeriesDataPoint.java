package com.groupGreen.ProvenanceCollector;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TimeSeriesDataPoint {

    private double timestamp;

    private double value;

    public TimeSeriesDataPoint(double timestamp, double value) {
        this.timestamp = timestamp;
        this.value = value;
    }
}
