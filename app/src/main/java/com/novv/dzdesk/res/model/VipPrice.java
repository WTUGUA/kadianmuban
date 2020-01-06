package com.novv.dzdesk.res.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VipPrice implements Serializable {

    @SerializedName("app_id")
    private String appId;
    private String type;
    private double price;
    @SerializedName("origin_price")
    private double originPrice;
    private int days;
    private String desc;
    private String id;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getOriginPrice() {
        return originPrice;
    }

    public void setOriginPrice(double originPrice) {
        this.originPrice = originPrice;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
