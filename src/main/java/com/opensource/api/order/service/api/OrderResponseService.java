package com.opensource.api.order.service.api;

import com.opensource.api.order.model.Order;

/**
 * Created by amg871 on 6/3/17.
 */
public interface OrderResponseService {
    Order recieveOrderResponseFromTopic();
}

