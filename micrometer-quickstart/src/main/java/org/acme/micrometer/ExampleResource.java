package org.acme.micrometer;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;

import java.util.LinkedList;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/example")
@Produces("text/plain")
public class ExampleResource {

    private final MeterRegistry registry;

    private final LinkedList<Long> list = new LinkedList<>();

    ExampleResource(MeterRegistry registry) {
        this.registry = registry;
        registry.gaugeCollectionSize("example.list.size", Tags.empty(), list);
    }

    @GET
    @Path("prime/{number}")
    public String checkIfPrime(@PathParam("number") long number) {
        if (number < 1) {
            registry.counter("example.prime.number", "type", "not-natural")
                    .increment();
            return "Only natural numbers can be prime numbers.";
        }
        if (number == 1) {
            registry.counter("example.prime.number", "type", "one")
                    .increment();
            return number + " is not prime.";
        }
        if (number == 2 || number % 2 == 0) {
            registry.counter("example.prime.number", "type", "even");
            return number + " is not prime.";
        }

        if (testPrimeNumber(number)) {
            registry.counter("example.prime.number", "type", "prime")
                    .increment();
            return number + " is prime.";
        } else {
            registry.counter("example.prime.number", "type", "not-prime")
                    .increment();
            return number + " is not prime.";
        }
    }

    protected boolean testPrimeNumber(long number) {
        Timer timer = registry.timer("example.prime.number.test");
        return timer.record(() -> {
            for (int i = 3; i < Math.floor(Math.sqrt(number)) + 1; i = i + 2) {
                if (number % i == 0) {
                    return false;
                }
            }
            return true;
        });
    }
}