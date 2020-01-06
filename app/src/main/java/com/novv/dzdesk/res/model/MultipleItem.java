package com.novv.dzdesk.res.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * 多类型的item，用于recyclerview adapter Created by lijianglong on 2017/8/21.
 */

public class MultipleItem implements MultiItemEntity {

    public static final int TYPE_NORMAL = 1002;//正常数据
    public static final int TYPE_AD = 1003;//广告类型
    public static final int TYPE_PASS = 1004;//已通过
    public static final int TYPE_REFUSED = 1005;//不通过
    public static final int TYPE_REVIEW = 1006;//审核中


    public static final int TYPE_DIY = 1007;

    private int itemType;
    private int spanSize;
    private String content;

    public MultipleItem(int itemType, int spanSize, String content) {
        this.itemType = itemType;
        this.spanSize = spanSize;
        this.content = content;
    }

    public MultipleItem(int itemType, int spanSize) {
        this.itemType = itemType;
        this.spanSize = spanSize;
    }

    public int getSpanSize() {
        return spanSize;
    }

    public void setSpanSize(int spanSize) {
        this.spanSize = spanSize;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
