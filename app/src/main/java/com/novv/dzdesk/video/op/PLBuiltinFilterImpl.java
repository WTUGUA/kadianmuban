package com.novv.dzdesk.video.op;

import com.qiniu.pili.droid.shortvideo.PLBuiltinFilter;

public class PLBuiltinFilterImpl {
    private String mName;
    private String mAssetFilePath;
    private final PLBuiltinFilter plBuiltinFilter;
    private boolean isVip;

    public PLBuiltinFilterImpl(PLBuiltinFilter plBuiltinFilter) {
        this.plBuiltinFilter = plBuiltinFilter;
        this.mName = plBuiltinFilter.getName();
        this.mAssetFilePath = plBuiltinFilter.getAssetFilePath();
    }

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }

    public PLBuiltinFilter getPlBuiltinFilter() {
        return plBuiltinFilter;
    }

    public String getName() {
        return this.mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getAssetFilePath() {
        return this.mAssetFilePath;
    }

    public void setAssetFilePath(String assetFilePath) {
        this.mAssetFilePath = assetFilePath;
    }
}
