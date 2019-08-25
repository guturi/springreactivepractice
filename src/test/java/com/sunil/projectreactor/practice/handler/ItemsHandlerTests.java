package com.sunil.projectreactor.practice.handler;

import com.sunil.projectreactor.practice.constants.ItemConstants;
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
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.*;


@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext
public class ItemsHandlerTests {

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

//        webTestClient.get()
//                .uri(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1)
//                .accept(MediaType.APPLICATION_JSON_UTF8)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBodyList(Item.class)
//                .hasSize(5);
//
//        webTestClient.get()
//                .uri(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1)
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectBodyList(Item.class)
//                .doesNotContain(new Item("sdf","sdfasdf", 97.90))
//                .consumeWith(itemEntityExchangeResult -> {
//                    List<Item> responseBody = itemEntityExchangeResult.getResponseBody();
//                    assert responseBody != null;
//                    System.out.println("checking the item Id value ......");
//                    responseBody.forEach(item -> assertNotNull(item.getId()));
//                });

        Flux<Item> itemsFlux = webTestClient.get()
                .uri(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .returnResult(Item.class)
                .getResponseBody();

        itemsFlux.subscribe(item -> {
            System.out.println("Emitting item");
            assertNotNull(item.getId());
        });
    }

    @Test
    public void getOneItem(){

//        webTestClient.get()
//                .uri(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1.concat("/{id}"), "ABCD")
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectBody(Item.class)
//                .consumeWith(itemEntityExchangeResult -> {
//                    Item item1 = itemEntityExchangeResult.getResponseBody();
//                    assert item1 != null;
//                    assertEquals(item1.getPrice(), 499.9);
//                });
//
//        webTestClient.get()
//                .uri(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1.concat("/{id}"), "ABCD")
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectBody()
//                .jsonPath("$.price", 499.99);

        Flux<Item> itemFlux = webTestClient.get()
                .uri(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1.concat("/{id}"), "ABCD")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(itemFlux)
                .expectSubscription()
                .expectNext(new Item("ABCD", "Bose headphones ", 499.9))
                .verifyComplete();
    }

    @Test
    public void getOneItem_NotFound(){
        webTestClient.get()
                .uri(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1.concat("/{id}"), "ABCDEF")
                .exchange()
                .expectStatus().isNotFound();

    }

}
