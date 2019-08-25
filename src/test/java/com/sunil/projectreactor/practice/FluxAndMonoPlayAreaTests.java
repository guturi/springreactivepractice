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

//        Flux<Flux<String>> just = Flux.just(stringFlux);
//
//        Flux<String> stringFlux1 = just.flatMap(i -> {
//            return i
//        });

//        Flux<String> map = stringFlux.map(i -> {
//            return i;
//        });
//
//        stringFlux.flatMap(i -> {
//
//            return Flux.just(i);
//        });
//                .

    }
}
