package com.prometheus.utils;

import java.time.Duration;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.lang.Nullable;

public class SampleConfig {
    public static MeterRegistry myMonitoringSystem() {
        // Pick a monitoring system here to use in your samples.
        return Metrics.globalRegistry;
    }
}
