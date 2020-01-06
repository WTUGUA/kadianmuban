package com.novv.dzdesk.res.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BannerResult {

    @SerializedName("msg")
    private String msg;
    @SerializedName("count")
    private int count;
    @SerializedName("code")
    private int code;
    @SerializedName("data")
    private List<DataEntity> data;

    public static BannerResult objectFromData(String str) {

        return new Gson().fromJson(str, BannerResult.class);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<DataEntity> getData() {
        return data;
    }

    public void setData(List<DataEntity> data) {
        this.data = data;
    }
}
