package com.novv.dzdesk.res.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VipDesc implements Serializable {

    @SerializedName("id")
    public int resId;
    @SerializedName("title")
    public String title;
    @SerializedName("desc")
    public String desc;
}
