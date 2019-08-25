package com.sunil.projectreactor.practice.repository;

import com.sunil.projectreactor.practice.document.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@RunWith(SpringRunner.class)
@DirtiesContext
public class ItemReactiveRepositoryTest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    List<Item> itemList = Arrays.asList(new Item(null, "samsung TV", 400.0),
            new Item(null, "Beats headphones", 149.9),
            new Item(null, "LG TV", 420.0),
            new Item(null, "Apple Watch", 299.9),
            new Item("ABCD", "Bose headphones ", 499.9));

//    Arrays.asList(new Item(null, "samsung TV", 400.0),new Item(null, "samsung TV", 400.0),new Item(null, "samsung TV", 400.0),new Item(null, "samsung TV", 400.0))

    @Before
    public void setup(){
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> {
                    System.out.println("inserted item is " + item);
                }).blockLast();
    }

    @Test
    public void getAllItems(){
        Flux<Item> itemFlux = itemReactiveRepository.findAll();

        StepVerifier.create(itemFlux)
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    public void getItemByID(){
        Mono<Item> itemACBD = itemReactiveRepository.findById("ABCD");
        StepVerifier.create(itemACBD)
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals("Bose headphones "));
    }

    @Test
    public void findByItemByDescription(){
        StepVerifier.create(itemReactiveRepository.findByDescription("Bose headphones "))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveItem(){
        Item item = new Item(null, "Google home mini", 120.0);

        Mono<Item> savedItem = itemReactiveRepository.save(item);

        StepVerifier.create(savedItem.log())
                .expectSubscription()
                .expectNextMatches((item1) -> {
                        return item1.getId()!=null && item.getDescription().equals("Google home mini");
                }).verifyComplete();
    }

    @Test
    public void flatAndMap(){
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C"));

        stringFlux.map( e -> e.concat("hello"));
    }

    @Test
    public void updateItem(){

        Mono<Item> newItem = itemReactiveRepository.findByDescription("LG TV")
                                     .map(item -> {
                                         item.setPrice(500.0);
                                         return item;
                                     }).flatMap(itemReactiveRepository::save);
        StepVerifier.create(newItem)
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice() == 500.0);
    }

    @Test
    public void deleteItem(){
        Mono<Void> deletedItem = itemReactiveRepository.findById("ABCD")
                .map(item -> item.getId())
                .flatMap(itemReactiveRepository::deleteById);

        StepVerifier.create(deletedItem.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll())
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void deleteItemAnotherApproach(){
        Mono<Void> deleteHeadPhones = itemReactiveRepository.findByDescription("Bose headphones ")
                .flatMap(itemReactiveRepository::delete);

        StepVerifier.create(deleteHeadPhones.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll())
                .expectNextCount(4)
                .verifyComplete();
    }
}
