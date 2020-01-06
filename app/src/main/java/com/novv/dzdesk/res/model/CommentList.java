package com.novv.dzdesk.res.model;

/**
 * Created by lijianglong on 2017/9/5.
 */

public class CommentList<T> {

    private boolean isfavor;
    private T list;
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isfavor() {
        return isfavor;
    }

    public void setIsfavor(boolean isfavor) {
        this.isfavor = isfavor;
    }

    public T getList() {
        return list;
    }

    public void setList(T list) {
        this.list = list;
    }
}
