package com.novv.dzdesk.util;

import android.content.Context;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * 友盟统计 Created by lingyfh on 2017/6/6.
 */
public class UmUtil implements UmConst {

    public static void anaOp(Context context, String eventId) {
        MobclickAgent.onEvent(context, eventId);
    }

    public static void anaGeneralOp(Context context, String action) {
        Map<String, String> keys = new HashMap<>();
        keys.put("action", action);
        MobclickAgent.onEvent(context, UM_GENERAL, keys);
    }

    public static void anaGeneralEventOp(Context context, String event) {
        Map<String, String> keys = new HashMap<>();
        keys.put("event", event);
        MobclickAgent.onEvent(context, UM_GENERAL, keys);
    }


    /**
     * 分类点击事件
     *
     * @param context 上下文
     * @param action  分类名
     */
    public static void anaCategoryOp(Context context, String action) {
        Map<String, String> keys = new HashMap<>();
        keys.put("name", action);
        MobclickAgent.onEvent(context, UM_CATEGORY, keys);
    }


    /**
     * 底部tab点击事件，
     *
     * @param context 上下文
     * @param action  五个tab类型
     */
    public static void anaTabBottomOp(Context context, String action) {
        Map<String, String> keys = new HashMap<>();
        keys.put("action", action);
        MobclickAgent.onEvent(context, UM_TAB_BOTTOM, keys);
    }

    /**
     * 顶部title点击事件，
     *
     * @param context 上下文
     * @param action  顶部类型
     */
    public static void anaTitleTopOp(Context context, String action) {
        Map<String, String> keys = new HashMap<>();
        keys.put("action", action);
        MobclickAgent.onEvent(context, UM_TITLE_TOP, keys);
    }

    public static void anaDIYClick(Context context, String id) {
        Map<String, String> keys = new HashMap<>();
        keys.put("action", id);
        MobclickAgent.onEvent(context, UM_DIY_CLICK, keys);
    }

    public static void anaDIYClickPreview(Context context, String id) {
        Map<String, String> keys = new HashMap<>();
        keys.put("action", id);
        MobclickAgent.onEvent(context, UM_DIY_CLICK_PREVIEW, keys);
    }

    public static void anaDIYDetail(Context context, String id, String entrance) {
        Map<String, String> keys = new HashMap<>();
        keys.put("action", id);
        keys.put("entrance", entrance);
        MobclickAgent.onEvent(context, UM_DIY_DETAIL_VIEW, keys);
    }

    public static void anaDIYPageView(Context context, String entrance) {
        Map<String, String> keys = new HashMap<>();
        keys.put("entrance", entrance);
        MobclickAgent.onEvent(context, UM_DIY_PAGE_VIEW, keys);
    }

    public static void anaDIYClickMake(Context context, String id) {
        Map<String, String> keys = new HashMap<>();
        keys.put("action", id);
        MobclickAgent.onEvent(context, UM_DIY_CLICK_MAKE, keys);
    }


    /***
     * diy广告打点
     * */
    public static void anaDIYDIALOG(Context context) {
        MobclickAgent.onEvent(context, UM_DIY_AD_DIALOG);
    }


    public static void anaDIYNEW(Context context) {
        MobclickAgent.onEvent(context, UM_DIY_AD_NEW);
    }


    public static void anaDIYRING(Context context) {
        MobclickAgent.onEvent(context, UM_DIY_AD_RING);
    }

    public static void anaDIYSUCCESS(Context context) {
        MobclickAgent.onEvent(context, UM_DIY_AD_SUCCESS);
    }


    public static void anaDIYFAIL(Context context) {
        MobclickAgent.onEvent(context, UM_DIY_AD_FAIL);
    }


    public static void anaDIYClickGenerate(Context context, String id) {
        Map<String, String> keys = new HashMap<>();
        keys.put("action", id);
        MobclickAgent.onEvent(context, UM_DIY_CLICK_GENERATE, keys);
    }

    public static void anaDIYClickGenerateSucess(Context context, String id) {
        Map<String, String> keys = new HashMap<>();
        keys.put("action", id);
        MobclickAgent.onEvent(context, UM_DIY_CLICK_GENERATE_SUCCESS, keys);
    }

    public static void anaDIYClickShare(Context context, String type) {
        Map<String, String> keys = new HashMap<>();
        keys.put("type", type);
        MobclickAgent.onEvent(context, UM_DIY_CLICK_SHARE, keys);
    }

    public static void anaDIYClickSet(Context context, String id) {
        Map<String, String> keys = new HashMap<>();
        keys.put("action", id);
        MobclickAgent.onEvent(context, UM_DIY_CLICK_SET, keys);
    }

    public static void anaDIYClickSave(Context context, String id) {
        Map<String, String> keys = new HashMap<>();
        keys.put("action", id);
        MobclickAgent.onEvent(context, UM_DIY_CLICK_SAVE, keys);
    }

    /**
     * make,mine、music、filter、download
     *
     * @param context
     * @param page
     */
    public static void anaVIPPage(Context context, String page) {
        Map<String, String> keys = new HashMap<>();
        keys.put("page", page);
        MobclickAgent.onEvent(context, UM_VIP_PAGE, keys);
    }

    public static void anaVIPClickBuy(Context context, String kind) {
        Map<String, String> keys = new HashMap<>();
        keys.put("kind", kind);
        MobclickAgent.onEvent(context, UM_VIP_CLICK_BUY, keys);
    }

    public static void anaVIPClickBuySuccess(Context context, String kind) {
        Map<String, String> keys = new HashMap<>();
        keys.put("kind", kind);
        MobclickAgent.onEvent(context, UM_VIP_CLICK_BUY_SUCCESS, keys);
    }

    public static void anaLoginFrom(Context context, String entrance) {
        Map<String, String> keys = new HashMap<>();
        keys.put("entrance", entrance);
        MobclickAgent.onEvent(context, UM_LOGIN_PREVIEW, keys);
    }

    /**
     * 登录事件
     *
     * @param context 上下文
     * @param action  登录事件类型
     */
    public static void anaLoginOp(Context context, String action) {
        Map<String, String> keys = new HashMap<>();
        keys.put("action", action);
        MobclickAgent.onEvent(context, UM_LOGIN_OP, keys);
    }

    public static void anaEvent(Context context, String event) {
        MobclickAgent.onEvent(context, event);
    }

}
