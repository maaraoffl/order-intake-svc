package com.opensource.api.order.model;

import java.io.Serializable;

/**
 * Created by amg871 on 5/20/17.
 */
public class Order extends OrderRequest implements Serializable{
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
