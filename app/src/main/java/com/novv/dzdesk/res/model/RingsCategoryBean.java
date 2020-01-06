package com.novv.dzdesk.res.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RingsCategoryBean implements Serializable {

    private static final long serialVersionUID = 2592276114236793435L;
    @SerializedName("ename")
    private String ename;
    @SerializedName("name")
    private String name;
    @SerializedName("coverid")
    private String coverid;
    @SerializedName("cateurl")
    private String cateurl;
    @SerializedName("type")
    private String type;
    @SerializedName("order")
    private int order;

    public static RingsCategoryBean objectFromData(String str) {

        return new Gson().fromJson(str, RingsCategoryBean.class);
    }

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverid() {
        return coverid;
    }

    public void setCoverid(String coverid) {
        this.coverid = coverid;
    }

    public String getCateurl() {
        return cateurl;
    }

    public void setCateurl(String cateurl) {
        this.cateurl = cateurl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
