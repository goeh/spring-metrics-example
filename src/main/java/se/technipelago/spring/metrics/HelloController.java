package se.technipelago.spring.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Example controller that collect request metrics.
 */
@RestController
public class HelloController {

    private final PrometheusMeterRegistry registry;

    public HelloController(PrometheusMeterRegistry registry) {
        this.registry = registry;
    }

    @GetMapping("/")
    public Map<String, Object> index() {

        Counter.builder("http.requests.total").description("Http Request Total").tags("method", "GET", "handler",
                "/", "status", "200").register(registry).increment();

        return Map.of("message", "Hello");
    }

    @GetMapping("/hello")
    public Map<String, Object> hello() {
        long startTime = System.nanoTime();

        Counter.builder("http.requests.total")
                .description("Http Request Total")
                .tags("method", "GET", "handler", "/hello", "status", "200")
                .register(registry).increment();

        Gauge.builder("queue.length", () -> new Random().nextInt(10) + 100)
                .register(registry);

        Timer.builder("hello.latency")
                .description("Time it takes to process the hello function")
                .tags("env", "demo")
                .register(registry)
                .record(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);

        DistributionSummary.builder("hello.summary")
                .register(registry)
                .record(new Random().nextInt(10) * 10 + 30);

        return Map.of("message", "World");
    }

}
