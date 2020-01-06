package com.novv.dzdesk.http.model;

import java.io.Serializable;

public class LzyResponse<T> implements Serializable {

    private static final long serialVersionUID = 5213230387175987834L;

    public int code;
    public String msg;
    public T res;

    @Override
    public String toString() {
        return "LzyResponse{\n" +//
                "\tcode=" + code + "\n" +//
                "\tmsg='" + msg + "\'\n" +//
                "\tres=" + res + "\n" +//
                '}';
    }
}
