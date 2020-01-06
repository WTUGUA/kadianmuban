package com.novv.dzdesk.res.user;

public interface AEStateCallback {

    /**
     * 免费的
     */
    void onFree();

    /**
     * 限时免费的
     */
    void onLimitFree();

    /**
     * Vip免费的,包含非试用次数
     */
    void onVipFree(boolean isVip,boolean isLogin,int trialNum);

    /**
     * 需要付费
     */
    void onPriceNeed();

}
