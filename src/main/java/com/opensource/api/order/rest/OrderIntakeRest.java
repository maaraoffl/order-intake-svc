package com.opensource.api.order.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensource.api.order.model.OrderRequest;
import com.opensource.api.order.service.api.DispatchOrderRequestService;
import com.opensource.api.order.service.api.OrderResponseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.inject.Inject;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by amg871 on 5/21/17.
 */
@RestController
@RequestMapping("maximus")
public class OrderIntakeRest {

    @Inject
    private DispatchOrderRequestService orderRequestService;

    @Inject
    private OrderResponseService orderResponseService;

    public static ConcurrentHashMap<String, DeferredResult<ResponseEntity<?>>> requestResponseMap = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger(OrderIntakeRest.class);

    @RequestMapping(value = "order", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public DeferredResult<ResponseEntity<?>> processIntakeOrder(@RequestBody OrderRequest orderRequest)
    {
        try {


//            Runnable task = () -> orderRequestService.sendOrderForProcessing(orderRequest);
//            Executor executor = Executors.newSingleThreadExecutor();
//            executor.execute(task);

            DeferredResult<ResponseEntity<?>> result = new DeferredResult<>(5000L);

            String id = UUID.randomUUID().toString();
            orderRequest.setId(id);

            logger.info("Request is " + new ObjectMapper().writeValueAsString(orderRequest));

//            CompletableFuture<Void> completableFuture2 = CompletableFuture.runAsync(() -> {
//                orderResponseService.recieveOrderResponseFromTopic();
//            }).thenRunAsync(() -> logger.info("Thread at callback 2: " + Thread.currentThread().getName()));


            logger.info("Assigned request Id is {}", id);


            logger.info("Thread at main: " + Thread.currentThread().getName());
            CompletableFuture<Void> completableFuture1 = CompletableFuture.runAsync(() -> {
                logger.info("Thread at runnable 1: " + Thread.currentThread().getName());
                orderRequestService.sendOrderForProcessing(orderRequest);
                requestResponseMap.put(id, result);
            });
//            .thenRunAsync(() -> handleResponse(result));

//            result.setResult(ResponseEntity.accepted().build())

//            CompletableFuture<Void> completableFuture2 = CompletableFuture.runAsync(() -> {
//                logger.info("Thread at runnable 2: " + Thread.currentThread().getName());
//                orderRequestService.sendOrderForProcessing(orderRequest);
//            }).thenRunAsync(() -> logger.info("Thread at callback 2: " + Thread.currentThread().getName()));

//            CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(completableFuture1, completableFuture2);
//            combinedFuture.get();

            /*
             *  Synchronous call to fulfill request
             */
//            orderRequestService.sendOrderForProcessing(orderRequest);
//            return Response.status(Response.Status.ACCEPTED).build();
            return result;
        }
        catch(Exception e){
            logger.error(e.getMessage());
        }
        return null;
    }
}
