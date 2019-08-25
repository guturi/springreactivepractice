package com.sunil.projectreactor.practice;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoPlayAreaTests {

    @Test
    public void fluxExample(){
        Flux<String> stringFlux = Flux.just("A", "B")
                .log();
//        stringFlux.subscribe(System.out::println);

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("A", "B")
                .verifyComplete();

    }
}
