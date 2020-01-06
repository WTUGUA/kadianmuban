package com.novv.dzdesk.res.model;

import com.google.gson.annotations.SerializedName;

/**
 * Retrofit响应体基类 Created by adesk on 2017/7/20.
 */

public class BaseResult<T> extends ItemBean {

    private String msg;
    @SerializedName(value = "res", alternate = {"data"})
    private T res;
    private int code;
    private String oid;


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getRes() {
        return res;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public void setRes(T res) {
        this.res = res;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
