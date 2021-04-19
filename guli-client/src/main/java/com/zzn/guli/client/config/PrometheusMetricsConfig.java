package com.zzn.guli.client.config;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Summary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PrometheusMetricsConfig {
    @Autowired
    private PrometheusMeterRegistry registry;

    @Bean("requestCounter")
    public Counter counter(){
        Counter counter = Counter.build("uriCount","uriCount-help").labelNames("uri", "method","code")
                .name("requestCounter").register(registry.getPrometheusRegistry());
        return counter;
    }

    @Bean
    public Summary requestLatency(){
        Summary summary = Summary.build("is_request_latency","monite request latency by service")
                .quantile(0.5, 0.05).quantile(0.9, 0.01)
                .labelNames("service", "method", "code")
                .register(registry.getPrometheusRegistry());
        return summary;
    }
}
