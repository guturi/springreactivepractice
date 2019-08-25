package com.sunil.projectreactor.practice.router;

import com.sunil.projectreactor.practice.constants.ItemConstants;
import com.sunil.projectreactor.practice.handler.ItemsHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ItemsRouter {

//    @Autowired
//    ItemsHandler itemsHandler;

    @Bean
    public RouterFunction<ServerResponse> itemsRout(ItemsHandler itemsHandler){

//        return RouterFunctions.route(GET(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1).and(accept(MediaType.APPLICATION_JSON)),
//                new HandlerFunction<ServerResponse>() {
//                    @Override
//                    public Mono<ServerResponse> handle(ServerRequest serverRequest) {
//                        return itemsHandler.getAllItems(serverRequest);
//                    }
//                });

                return RouterFunctions.route(GET(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1).and(accept(MediaType.APPLICATION_JSON)), itemsHandler::getAllItems)
                        .andRoute(GET(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1+"/{id}").and(accept(MediaType.APPLICATION_JSON)), itemsHandler::getOneItem)
                        .andRoute(POST(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1).and(accept(MediaType.APPLICATION_JSON)), itemsHandler::createItem)
                        .andRoute(DELETE(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1+"/{id}").and(accept(MediaType.APPLICATION_JSON)), itemsHandler::deleteItem);

    }
}
