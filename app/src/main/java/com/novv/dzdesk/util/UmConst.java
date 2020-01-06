package com.novv.dzdesk.util;

/**
 * 友盟事件常量 Created by lijianglong on 2017/9/12.
 */

public interface UmConst {

    String UM_DIY_CLICK = "DIY_videoclick";
    String UM_DIY_CLICK_PREVIEW = "DIY_preview";
    String UM_DIY_PAGE_VIEW = "DIY_pageview";
    String UM_DIY_DETAIL_VIEW = "DIY_detailview";
    String UM_DIY_CLICK_MAKE = "DIY_click_make";
    String UM_DIY_CLICK_GENERATE = "DIY_click_generate";
    String UM_DIY_CLICK_GENERATE_SUCCESS = "UM_DIY_CLICK_GENERATE";
    String UM_DIY_CLICK_SHARE = "DIY_click_share";
    String UM_DIY_CLICK_SET = "DIY_click_set";
    String UM_DIY_CLICK_SAVE = "DIY_click_download";
    String UM_VIP_PAGE="VIP_pageview";
    String UM_VIP_CLICK_BUY="VIP_click_buy";
    String UM_VIP_CLICK_BUY_SUCCESS="VIP_success_buy";
    String UM_LOGIN_PREVIEW="login_pageview";



    String UM_DIY_AD_DIALOG = "ad_detail_dialog";
    String UM_DIY_AD_NEW = "ad_detail_new";
    String UM_DIY_AD_RING = "ad_detail_ring";
    String UM_DIY_AD_FAIL = "ad_detail_fail";
    String UM_DIY_AD_SUCCESS= "ad_detail_success";


    //-------------自定义事件op_general start--------------//
    String UM_GENERAL = "op_general";
    String OP_TAB_HOME = "nav-home";//底部首页
    String OP_TAB_CATEGORY = "nav-category";//底部分类
    String OP_TAB_UPLOAD = "nav-upload";//底部上传
    String OP_TAB_LOCAL = "nav-local";//底部本地
    String OP_TAB_PERSON = "nav-person";//底部我的
    String OP_LOCAL_VIDEO_SHOW = "local_video_show";
    String OP_LOCAL_VIDEO_SHOW_OTHER = "local_video_show_other";
    String OP_LOCAL_VIDEO_SHOW_OURSELF = "local_video_show_ourself";
    String OP_LOCAL_VIDEO_SET = "local_video_set";
    String EVENT_OP_LIVE_ONLINE_DETAIL_SHOW = "live_online_detail_show";
    String EVENT_OP_SET_DOWNLOAD = "set_download";
    String EVENT_OP_SET_LIVE = "set_live";
    String EVENT_OP_SET_LIVE_EXISTS = "set_live_exists";
    String EVENT_OP_SET_LIVE_NOTEXISTS = "set_live_notexists";
    //-------------自定义事件op_general end--------------//


    String UM_CATEGORY = "op_category";//分类的点击
    String UM_JOINQQ = "join_qq_group";// 点击加入QQ群的次数
    String UM_PREVIEW_ACTION_PLAY = "preview_action_play";// 预览页面点击播放
    String UM_PREVIEW_ACTION_DOWNLOAD = "preview_action_download";// 预览页面点击下载
    String UM_DESK_VOICE_OPEN = "voice_desk_open";// 桌面开启动态壁纸声音
    String UM_DESK_VOICE_CLOSE = "voice_desk_close";// 桌面关闭动态壁纸声音
    String UM_VOICE_SET = "voice_set";// 设置页面点击声音操作次数
    String UM_VOICE_SET_OPEN = "voice_set_open";// 设置页面点击声音开启
    String UM_VOICE_SET_CLOSE = "voice_set_close";// 设置页面点击声音关闭


    //-------------自定义事件op_tab_bottom start--------------//
    String UM_TAB_BOTTOM = "op_tab_bottom";//分类的点击
    String MAIN_TAB_HOME = "tab_home";//底部首页
    String MAIN_TAB_CATEGORY = "tab_category";//底部分类
    String MAIN_TAB_UPLOAD = "tab_upload";//底部上传
    String MAIN_TAB_LOCAL = "tab_local";//底部本地
    String MAIN_TAB_PERSON = "tab_person";//底部我的
    //-------------自定义事件op_tab_bottom end--------------//


    //-------------自定义事件op_title_top start--------------//
    String UM_TITLE_TOP = "op_title_top";
    String HOME_TITLE_NEW = "title_new";//顶部最新
    String HOME_TITLE_AMUSEMENT = "title_amusement";//顶部娱乐
    String HOME_TITLE_RECOMMEND = "title_recommend";//顶部推荐
    String HOME_TITLE_HOT = "title_hot";//顶部热门
    String HOME_TITLE_CAT = "title_cat";//顶部热门
    String HOME_TITLE_DIY = "title_diy";//顶部热门
    //-------------自定义事件op_title_top end--------------//


    //-------------自定义事件op_login start--------------//
    String UM_LOGIN_OP = "op_login";
    String LOGIN_QQ_SUCCESS = "login_qq_success";
    String LOGIN_QQ_FAIL = "login_qq_fail";
    String LOGIN_WX_SUCCESS = "login_wx_success";
    String LOGIN_WX_FAIL = "login_wx_fail";
    String LOGIN_TEL_SUCCESS = "login_tel_success";
    String LOGIN_TEL_FAIL = "login_tel_fail";

    String QQ_GET_USER_INFO_ERROR = "qq_get_user_info_error";
    String QQ_NOT_INSTALL_ERROR = "qq_not_install_error";
    String QQ_GET_ACCESS_TOKEN_ERROR = "qq_get_access_token_error";
    String QQ_USER_CANCEL_ERROR = "qq_user_cancel_error";

    String WX_GET_USER_INFO_ERROR = "wx_get_user_info_error";
    String WX_NOT_INSTALL_ERROR = "wx_not_install_error";
    String WX_GET_ACCESS_TOKEN_ERROR = "wx_get_access_token_error";
    String WX_USER_CANCEL_ERROR = "wx_user_cancel_error";

    //-------------自定义事件op_login end--------------//

    String UM_LOGIN_USER_ACTIVE = "login_user_active";//登录用户活跃状态


    String MAIN_TAB_AVATAR = "tab_avatar";//头像栏目
    String MAIN_TAB_RINGS = "tab_rings";//铃声栏目

    String VIEW_AVATAR_DETAIL = "view_avatar_detail";//头像详情查看
    String CLICK_RINGS_PLAY = "click_rings_play";// 铃声点击播放
    String CLICK_DOWNLOAD_AVATAR = "click_download_avatar";//下载头像
    String CLICK_DOWNLOAD_RINGS = "click_download_rings";//下载 铃声

    String CLICK_VIDEO_SELECT = "click_video_select";//选择视频
    String CLICK_VIDEO_SELECT_NEXT = "click_video_select_next";//选择视频下一步
    String CLICK_VIDEO_EDIT_NEXT = "click_video_edit_next";// 编辑页面下一步
    String CLICK_VIDEO_COVER = "click_video_cover";//点击封面
    String CLICK_VIDEO_FILTER = "click_video_filter";//点击滤镜
    String CLICK_VIDEO_MUSIC = "click_video_music";//点击音乐
    String CLICK_VIDEO_POST = "click_video_post";//发布

    String GOTO_STORE_AVATAR = "avatar_goto_store";//头像跳转市场
    String GOTO_STORE_RINGS = "rings_goto_store";// 铃声跳转市场
}
