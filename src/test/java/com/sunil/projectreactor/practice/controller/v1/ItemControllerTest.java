package com.sunil.projectreactor.practice.controller.v1;

import com.sunil.projectreactor.practice.document.Item;
import com.sunil.projectreactor.practice.repository.ItemReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static com.sunil.projectreactor.practice.constants.ItemConstants.ITEM_ENDPOINT_V1;
import static junit.framework.TestCase.*;


@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    @Before
    public void setUp(){

        List<Item> items = Arrays.asList(new Item(null, "samsung TV", 400.0),
                new Item(null, "Beats headphones", 149.9),
                new Item(null, "LG TV", 420.0),
                new Item(null, "Apple Watch", 299.9),
                new Item("ABCD", "Bose headphones ", 499.9));

        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(items))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> System.out.println("inserted Item is " + item))
                .blockLast();

    }

    @Test
    public void getAllItems(){

        webTestClient.get()
                .uri(ITEM_ENDPOINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBodyList(Item.class)
                .hasSize(5);
    }

    @Test
    public void getAllItemsApproach2(){

        webTestClient.get()
                .uri(ITEM_ENDPOINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBodyList(Item.class)
                .consumeWith(listEntityExchangeResult -> {
                    List<Item> responseBody = listEntityExchangeResult.getResponseBody();
                    assert responseBody != null;
                    responseBody.forEach(item -> assertNotNull(item.getId()));
                })
                .hasSize(5);
    }

    @Test
    public void getAllItemsApproach3(){

        Flux<Item> itemsFlux = webTestClient.get()
                .uri(ITEM_ENDPOINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(itemsFlux.log("value from network : "))
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    public void getItem(){

        webTestClient.get()
                .uri(ITEM_ENDPOINT_V1.concat("/{id}"), "ABCD")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", 499.9);

//        webTestClient.get()
//                .uri(ItemConstants.ITEM_ENDPOINT_V1.concat("/{id}"), "ABCD")
//                .exchange()
//                .expectBody(Item.class)
//                .consumeWith(itemEntityExchangeResult -> {
//                    Item responseBody = itemEntityExchangeResult.getResponseBody();
//                    assertEquals(responseBody.getPrice(), 499.9);
//                });

//        Flux<Item> itemFlux = webTestClient.get()
//                .uri(ItemConstants.ITEM_ENDPOINT_V1.concat("/{id}"), "ABCD")
//                .exchange()
//                .returnResult(Item.class)
//                .getResponseBody();
//
//        StepVerifier.create(itemFlux)
//                .expectSubscription()
//                .expectNextCount(1l)
//                .verifyComplete();
    }

    @Test
    public void getItem_NotFound(){

        webTestClient.get()
                .uri(ITEM_ENDPOINT_V1.concat("/{id}"), "ABCDE")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void createItem(){

        Item iphoneXItem = new Item(null, "iphone X", 999.99);

        webTestClient.post()
                .uri(ITEM_ENDPOINT_V1)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(iphoneXItem), Item.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("iphone X")
                .jsonPath("$.price").isEqualTo(999.99);
    }

    @Test
    public void deleteItem(){
        webTestClient.delete()
                .uri(ITEM_ENDPOINT_V1.concat("/{id}"), "ABCD")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    @Test
    public void updateItem(){
        Double newPrice = 599.99;
        Item item = new Item("ABCD", "Bose headphones ", newPrice);
        webTestClient.put()
                .uri(ITEM_ENDPOINT_V1.concat("/{id}"), "ABCD")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Item.class)
                .consumeWith(updatedItem -> assertEquals(599.99, updatedItem.getResponseBody().getPrice()));

//        webTestClient.put()
//                .uri(ItemConstants.ITEM_ENDPOINT_V1.concat("/{id}"), "ABCD")
//                .contentType(MediaType.APPLICATION_JSON_UTF8)
//                .accept(MediaType.APPLICATION_JSON_UTF8)
//                .body(Mono.just(item), Item.class)
//                .exchange()
//                .expectBody()
//                .jsonPath("$.price").isEqualTo(599.99);
//
//        Flux<Item> itemFlux = webTestClient.put()
//                .uri(ItemConstants.ITEM_ENDPOINT_V1.concat("/{id}"), "ABCD")
//                .contentType(MediaType.APPLICATION_JSON_UTF8)
//                .accept(MediaType.APPLICATION_JSON_UTF8)
//                .body(Mono.just(item), Item.class)
//                .exchange()
//                .returnResult(Item.class)
//                .getResponseBody();
//
//        StepVerifier.create(itemFlux)
//                .expectSubscription()
//                .expectNextMatches(item1 -> item.getPrice().equals(599.99))
//                .verifyComplete();
    }

    @Test
    public void updateItem_NotFound(){

        Double newPrice = 599.99;
        Item item = new Item("ABCD", "Bose headphones ", newPrice);

        webTestClient.put()
                .uri(ITEM_ENDPOINT_V1.concat("/{id}"), "ABCDE")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isNotFound();

    }
}
