package com.novv.dzdesk.res.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VModel implements Serializable {

    private static final long serialVersionUID = -7525049963738822292L;
    @SerializedName("img")
    public String img;
    @SerializedName("zip")
    public String zip;
    @SerializedName("need_vip")
    public boolean needVip;
    @SerializedName("price")
    public int price;
    @SerializedName("free")
    public boolean free;
    @SerializedName("free_time")
    public String freeTime;
    @SerializedName("_id")
    public String id;
    @SerializedName("desc")
    public String desc;

    @SerializedName("hot")
    public String hot;
}
