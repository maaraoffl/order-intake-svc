package com.opensource.api.order.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensource.api.order.model.OrderRequest;
import com.opensource.api.order.service.api.DispatchOrderRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by amg871 on 5/21/17.
 */
@RestController
@RequestMapping("maximus")
public class OrderIntakeRest {

    @Inject
    private DispatchOrderRequestService orderRequestService;

    private final Logger logger = LoggerFactory.getLogger(OrderIntakeRest.class);

    @RequestMapping(value = "order", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public Response processIntakeOrder(@RequestBody OrderRequest orderRequest)
    {
        try {
            logger.info("Request is " + new ObjectMapper().writeValueAsString(orderRequest));
            orderRequestService.sendOrderForProcessing(orderRequest);
            return Response.status(Response.Status.ACCEPTED).build();
        }
        catch(Exception e){
            logger.error(e.getMessage());
        }
        return null;
    }
}
