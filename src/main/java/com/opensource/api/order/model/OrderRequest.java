package com.opensource.api.order.model;

/**
 * Created by amg871 on 5/6/17.
 */
public class OrderRequest {
    private String name;
    private String count;
    private String id;

    public String getId() {
        return id;
    }
    public void setId(String id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}

