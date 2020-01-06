package com.novv.dzdesk.res.model;

import com.google.gson.annotations.SerializedName;

public class OrderInfo {
    /**
     * _id : 5c4c5f448e175348f1420cd6
     * app_id : 5c4ae7d78e175348f1420ccc
     * open_id : 4982EEEFDE8B0EB05117705D298E0DE4
     * pay_way : wechat
     * total_fee : 45
     * price_id : 5c4ae8948e175348f1420ccd
     * user_ip : 58.19.1.113
     * status : 0
     * atime : 1.548508996E9
     * pay_time : 1.548508996E9
     * trade_no :
     */

    @SerializedName("_id")
    public String id;
    @SerializedName("app_id")
    public String appId;
    @SerializedName("open_id")
    public String openId;
    @SerializedName("pay_way")
    public String payWay;
    @SerializedName("total_fee")
    public String totalFee;
    @SerializedName("price_id")
    public String priceId;
    @SerializedName("user_ip")
    public String userIp;
    @SerializedName("status")
    public int status;      // 支付状态,-1支付失败,0未支付,1已付款,(new;2019.5.13 add)2:订单已支付待校验
    @SerializedName("atime")
    public double atime;
    @SerializedName("pay_time")
    public double payTime;
    @SerializedName("trade_no")
    public String tradeNo;
}
