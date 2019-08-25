package com.sunil.projectreactor.practice.datainitialize;

import com.sunil.projectreactor.practice.document.Item;
import com.sunil.projectreactor.practice.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;


@Component
@Profile("!test")
public class ItemInitializer implements CommandLineRunner {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;
    @Override
    public void run(String... args) throws Exception {

        initialDataSetup();
    }

    private void initialDataSetup() {

        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(Arrays.asList(new Item(null, "samsung TV", 400.0),
                        new Item(null, "Beats headphones", 149.9),
                        new Item(null, "LG TV", 420.0),
                        new Item(null, "Apple Watch", 299.9),
                        new Item("ABCD", "Bose headphones ", 499.9))))
                .flatMap(itemReactiveRepository::save)
                .thenMany(itemReactiveRepository.findAll())
                .subscribe(item -> {
                    System.out.println("Item inserted from commandline interface" + item);
                });
    }
}
