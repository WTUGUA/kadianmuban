package com.novv.dzdesk.res.model;

public class DataResult<T> {

    private String msg;
    private T data;
    private int code;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getRes() {
        return data;
    }

    public void setRes(T res) {
        this.data = res;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
