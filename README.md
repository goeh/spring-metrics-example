# Example - Add metrics to Spring application

This project is a minimal example on how to add application metrics to a Spring Application (not Spring Boot)
using [Micrometer](https://micrometer.io) and [Prometheus](https://prometheus.io).

Step 1: Add Micrometer+Prometheus dependency.

```xml

<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

Step 2: Create a `PrometheusMeterRegistry` bean.

```java
@Bean
public PrometheusMeterRegistry prometheusMeterRegistry() {
    final PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    new JvmMemoryMetrics().bindTo(registry);
    return registry;
}
```

Step 3: Inject bean into target class and add custom metrics.

```java
Counter.builder("http.requests.total")
    .description("Http Request Total")
    .tags("method","GET","handler","/hello","status","200")
    .register(registry).increment();
```

Step 4: Configure **Prometheus** to scrape the `/metrics` endpoint.

```yaml
scrape_configs:
  - job_name: myapp
    static_configs:
      - targets: ['localhost:8080']
    metrics_path: /metrics
```

Done!

You should now be able to view your application's metrics in Prometheus.
