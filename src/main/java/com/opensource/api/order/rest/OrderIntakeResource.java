package com.opensource.api.order.rest;

import com.opensource.api.order.model.OrderRequest;
import com.opensource.api.order.service.api.DispatchOrderRequestService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("order")
public class OrderIntakeResource {

    @Inject
    private DispatchOrderRequestService orderRequestService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response takeOrder(OrderRequest orderRequest)
    {
        System.out.println(orderRequest.getName() + "," + orderRequest.getCount());
        orderRequestService.sendOrderForProcessing(orderRequest);
        return  Response.status(Response.Status.ACCEPTED).build();
    }
}


/*
 * Copyright 2016 Capital One Financial Corporation All Rights Reserved.
 * 
 * This software contains valuable trade secrets and proprietary information of
 * Capital One and is protected by law. It may not be copied or distributed in
 * any form or medium, disclosed to third parties, reverse engineered or used in
 * any manner without prior written authorization from Capital One.
 */
