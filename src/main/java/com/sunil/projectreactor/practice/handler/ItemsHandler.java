package com.sunil.projectreactor.practice.handler;

import com.sunil.projectreactor.practice.document.Item;
import com.sunil.projectreactor.practice.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Component
public class ItemsHandler {

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest){

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.findAll(), Item.class);

    }

    public Mono<ServerResponse> getOneItem(ServerRequest serverRequest) {

        String id = serverRequest.pathVariable("id");

        Mono<Item> item = itemReactiveRepository.findById(id);

//        return ServerResponse.ok()
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(item, Item.class)
//                .switchIfEmpty(ServerResponse.notFound().build());

//        Mono<Mono<ServerResponse>> map = item.map(item1 -> ServerResponse.ok()
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(fromObject(item1)));
//
//        map.switchIfEmpty(ServerResponse.notFound().build());

//        return item.map(item1 -> {
//            return ServerResponse.ok()
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(fromObject(item1));
//        });

//        Mono<ServerResponse> serverResponseMono = item.flatMap(item1 -> {
//            return ServerResponse.ok()
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(fromObject(item1));
//        }).switchIfEmpty(ServerResponse.notFound().build());
//
        return item.flatMap(item1 -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromObject(item1)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> createItem(ServerRequest serverRequest){
        Mono<Item> itemMono = serverRequest.bodyToMono(Item.class);

        return itemMono.flatMap(item -> ServerResponse.ok()
                .body(itemReactiveRepository.save(item), Item.class));
    }
}
