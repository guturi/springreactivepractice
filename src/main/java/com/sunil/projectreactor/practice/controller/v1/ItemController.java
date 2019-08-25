package com.sunil.projectreactor.practice.controller.v1;


import com.sunil.projectreactor.practice.document.Item;
import com.sunil.projectreactor.practice.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.cdi.Eager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.sunil.projectreactor.practice.constants.ItemConstants.ITEM_ENDPOINT_V1;

@RestController
@Slf4j
public class ItemController {

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;


    @GetMapping(ITEM_ENDPOINT_V1)
    public Flux<Item> getAllItems(){

        return itemReactiveRepository.findAll();
    }

    @GetMapping(ITEM_ENDPOINT_V1+"/{id}")
    public Mono<ResponseEntity<Item>> getOneItem(@PathVariable String id ){

        return itemReactiveRepository.findById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(ITEM_ENDPOINT_V1)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createItem(@RequestBody Item item){

        return itemReactiveRepository.save(item);
    }

    @DeleteMapping(ITEM_ENDPOINT_V1+"/{id}")
    public Mono<Void> deleteItem(@PathVariable String id ){

        return itemReactiveRepository.deleteById(id);
    }
}
