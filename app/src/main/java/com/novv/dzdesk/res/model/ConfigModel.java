package com.novv.dzdesk.res.model;

/**
 * 在线广告配置类 Created by lijianglong on 2017/8/16.
 */

public class ConfigModel<T> {

    private T params;
    private String name;

    public T getParams() {
        return params;
    }

    public void setParams(T params) {
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
