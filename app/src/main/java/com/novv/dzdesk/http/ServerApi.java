/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.novv.dzdesk.http;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import com.ark.dict.Utils;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.model.HttpMethod;
import com.lzy.okgo.model.HttpParams;
import com.novv.dzdesk.res.dto.CommentModelListDTO.CommentDTO;
import com.novv.dzdesk.res.model.*;
import com.novv.dzdesk.util.CtxUtil;
import com.novv.dzdesk.util.DeviceUtil;
import io.reactivex.Observable;

import java.lang.reflect.Type;
import java.util.List;

import static com.novv.dzdesk.http.utils.Urls.*;

public class ServerApi {

    public static final int LIMIT = 30;

    /**
     * 免费使用模板次数增加
     * @return Observable
     * */
    public static Observable<BaseResult> setTrialAdd() {
        Type type = new TypeToken<BaseResult>() {
        }.getType();
        return RxUtils.request(HttpMethod.POST, defaultUrl + "v2/user/trial/add", type);
    }


    /**
     * 免费使用模板次数减少
     * @return Observable
     * */
    public static Observable<BaseResult> setTrialUse() {
        Type type = new TypeToken<BaseResult>() {
        }.getType();
        return RxUtils.request(HttpMethod.POST, defaultUrl + "v2/user/trial/use", type);
    }


    /**
     * 获取分类列表
     *
     * @return Observable
     */
    public static Observable<BaseResult<List<CategoryBean>>> getCategory() {
        Type type = new TypeToken<BaseResult<List<CategoryBean>>>() {
        }.getType();
        HttpParams params = createGeneralMap(Utils.getContext());
        return RxUtils.request(HttpMethod.GET, defaultUrl + "v1/category", type, params);
    }

    public static Observable<BaseResult<UploadMsg>> getUploadMsg(String names, String cids,
                                                                 String privacy, int coverTime) {
        HttpParams params = ServerApi.createGeneralMap(Utils.getContext());
        params.put("deviceid", DeviceUtil.getUniqueID(Utils.getContext()));
        params.put("name", names);
        params.put("cids", cids);
        params.put("privacy", privacy);
        if (coverTime >= 0) {
            params.put("cover_time", coverTime + "");
        }
        Type type = new TypeToken<BaseResult<UploadMsg>>() {
        }.getType();
        return RxUtils.request(HttpMethod.POST, defaultUrl + "v2/user/upload", type, params);
    }

    /**
     * 获取关键词搜索列表
     *
     * @return Observable
     */
    public static Observable<BaseResult<List<ResourceBean>>> getSearch(String key, int skip) {
        HttpParams params = createGeneralMap(Utils.getContext());
        params.put("key", key);
        params.put("version", "2");
        params.put("skip", String.valueOf(skip));
        params.put("limit", String.valueOf(LIMIT));
        Type type = new TypeToken<BaseResult<List<ResourceBean>>>() {
        }.getType();
        return RxUtils.request(HttpMethod.GET, defaultUrl + "v1/search", type, params);
    }

    /**
     * 获取推荐资源
     *
     * @param skip 跳过
     * @return Observable
     */
    public static Observable<BaseResult<List<ResourceBean>>> getRecommend(int skip, int limit) {
        HttpParams params = createGeneralMap(Utils.getContext());
        params.put("skip", String.valueOf(skip));
        params.put("limit", String.valueOf(limit));
        Type type = new TypeToken<BaseResult<List<ResourceBean>>>() {
        }.getType();
        return RxUtils.request(HttpMethod.GET, defaultUrl + "v1/featured", type, params);
    }

    /**
     * 获取热门资源
     *
     * @param hot  是否精选
     * @param skip 跳过
     * @return Observable
     */
    public static Observable<BaseResult<List<ResourceBean>>> getHot(boolean hot, int skip,
                                                                    int limit) {
        HttpParams params = createGeneralMap(Utils.getContext());
        params.put("hot", String.valueOf(hot));
        params.put("skip", String.valueOf(skip));
        params.put("limit", String.valueOf(limit));
        Type type = new TypeToken<BaseResult<List<ResourceBean>>>() {
        }.getType();
        return RxUtils.request(HttpMethod.GET, defaultUrl + "v1/list", type, params);
    }

    /**
     * 获取最新资源
     *
     * @param skip 跳过
     * @return Observable
     */
    public static Observable<BaseResult<List<ResourceBean>>> getNew(int skip, int limit) {
        HttpParams params = createGeneralMap(Utils.getContext());
        params.put("skip", String.valueOf(skip));
        params.put("limit", String.valueOf(limit));
        Type type = new TypeToken<BaseResult<List<ResourceBean>>>() {
        }.getType();
        return RxUtils.request(HttpMethod.GET, defaultUrl + "v1/list", type, params);
    }

    /**
     * 获取分类资源
     *
     * @param category 分类
     * @param skip     跳过
     * @return Observable
     */
    public static Observable<BaseResult<List<ResourceBean>>> getCategoryList(String category,
                                                                             int skip, int limit) {
        HttpParams params = createGeneralMap(Utils.getContext());
        params.put("category", category);
        params.put("skip", String.valueOf(skip));
        params.put("limit", String.valueOf(limit));
        Type type = new TypeToken<BaseResult<List<ResourceBean>>>() {
        }.getType();
        return RxUtils.request(HttpMethod.GET, defaultUrl + "v1/list", type, params);
    }

    /**
     * 检查更新
     *
     * @param umengChannel 友盟渠道
     * @return Observable
     */
    public static Observable<BaseResult<UpdateBean>> checkUpdate(String umengChannel) {
        HttpParams params = createGeneralMap(Utils.getContext());
        if (TextUtils.isEmpty(umengChannel)) {
            umengChannel = "default";
        }
        params.put("channel", umengChannel);
        Type type = new TypeToken<BaseResult<UpdateBean>>() {
        }.getType();
        return RxUtils.request(HttpMethod.GET, defaultUrl + "v1/update", type, params);
    }

    /**
     * 获取登陆验证码
     *
     * @param telNum 手机号
     * @return Observable
     */
    public static Observable<BaseResult> getLoginSms(String telNum) {
        HttpParams params = createGeneralMap(Utils.getContext());
        params.put("type", "login");
        params.put("tel", telNum);
        Type type = new TypeToken<BaseResult>() {
        }.getType();
        return RxUtils.request(HttpMethod.POST, defaultUrl + "v2/sms", type, params);
    }

    /**
     * 获取评论列表
     *
     * @param skip  页
     * @param limit 行
     * @param id    资源id
     * @return Observable
     */
    public static Observable<BaseResult<CommentList<List<CommentDTO>>>> getCommentList(
            int skip, int limit, String id) {
        HttpParams params = createGeneralMap(Utils.getContext());
        params.put("skip", String.valueOf(skip));
        params.put("id", id);
        params.put("limit", String.valueOf(limit));
        Type type = new TypeToken<BaseResult<CommentList<List<CommentDTO>>>>() {
        }.getType();
        return RxUtils.request(HttpMethod.GET, defaultUrl + "v2/user/comment/list", type, params);
    }

    /**
     * 登陆注册
     *
     * @param userName 用户昵称，手机号
     * @param code     验证码
     * @return Observable
     */
    public static Observable<BaseResult<UserModel>> login(String userName, String code) {
        HttpParams params = createGeneralMap(Utils.getContext());
        params.put("username", userName);
        params.put("code", code);
        Type type = new TypeToken<BaseResult<UserModel>>() {
        }.getType();
        return RxUtils.request(HttpMethod.POST, defaultUrl + "v2/user/login", type, params);
    }

    /**
     * 验证手机号
     *
     * @param tel  用手机号
     * @param code 验证码
     * @return Observable
     */
    public static Observable<BaseResult<UserModel>> telVerify(String tel, String code) {
        HttpParams params = createGeneralMap(Utils.getContext());
        params.put("tel", tel);
        params.put("code", code);
        Type type = new TypeToken<BaseResult<UserModel>>() {
        }.getType();
        return RxUtils.request(HttpMethod.POST, defaultUrl + "v2/user/vertel", type, params);
    }

    /**
     * 三方平台登陆
     *
     * @param userName 昵称
     * @param auth     类型
     * @param openid   id
     * @return Observable
     */
    public static Observable<BaseResult<UserModel>> login(String userName, String auth,
                                                          String openid,
                                                          String img, String token) {
        HttpParams params = createGeneralMap(Utils.getContext());
        params.put("nickname", userName);
        params.put("auth", auth);
        params.put("openid", openid);
        params.put("img", img);
        params.put("token", token);
        Type type = new TypeToken<BaseResult<UserModel>>() {
        }.getType();
        return RxUtils.request(HttpMethod.POST, defaultUrl + "v2/user/login", type, params);
    }

    public static Observable<BaseResult<Object>> account(String types, String uuid) {
        HttpParams params = createGeneralMap(Utils.getContext());
        params.put("type", types);
        params.put("uuid", uuid);
        Type type = new TypeToken<BaseResult<Object>>() {
        }.getType();
        return RxUtils
                .request(HttpMethod.POST, defaultUrl + "v2/user/select_main_account", type, params);
    }

    /**
     * 对某一资源的评论
     *
     * @param resourceId 资源id
     * @param content    评论内容
     * @return Observable
     */
    public static Observable<BaseResult> comment(String resourceId, String content) {
        HttpParams params = createGeneralMap(Utils.getContext());
        params.put("id", resourceId);
        params.put("comment", content);
        Type type = new TypeToken<BaseResult>() {
        }.getType();
        return RxUtils.request(HttpMethod.POST, defaultUrl + "v2/user/comment", type, params);
    }

    /**
     * 对某一条评论的回复
     *
     * @param resourceId 资源id
     * @param commentId  评论id
     * @param content    回复内容
     * @return Observable
     */
    @SuppressWarnings("unused")
    public static Observable<BaseResult> reply(String resourceId, String commentId,
                                               String content) {
        HttpParams params = createGeneralMap(Utils.getContext());
        params.put("id", resourceId);
        params.put("replyid", commentId);
        params.put("comment", content);
        Type type = new TypeToken<BaseResult>() {
        }.getType();
        return RxUtils.request(HttpMethod.POST, defaultUrl + "v2/user/comment", type, params);
    }

    /**
     * 获取个人信息
     *
     * @return Observable
     */
    public static Observable<BaseResult<UserModel>> getUserInfo() {
        HttpParams params = createGeneralMap(Utils.getContext());
        Type type = new TypeToken<BaseResult<UserModel>>() {
        }.getType();
        return RxUtils.request(HttpMethod.GET, defaultUrl + "v2/user/info", type, params);
    }

    /**
     * 点赞
     *
     * @param commentId 评论id
     * @return Observable
     */
    public static Observable<BaseResult> praise(String commentId) {
        HttpParams params = createGeneralMap(Utils.getContext());
        params.put("id", commentId);
        Type type = new TypeToken<BaseResult>() {
        }.getType();
        return RxUtils.request(HttpMethod.POST, defaultUrl + "v2/user/comment/up", type, params);
    }

    /**
     * 收藏
     *
     * @param resId   资源id
     * @param isFavor 是否收藏
     * @return Observable
     */
    public static Observable<BaseResult> favor(String resId, boolean isFavor) {
        HttpParams params = createGeneralMap(Utils.getContext());
        params.put("id", resId);
        params.put("type", isFavor ? "add" : "del");
        Type type = new TypeToken<BaseResult>() {
        }.getType();
        return RxUtils.request(HttpMethod.POST, defaultUrl + "v2/user/favor", type, params);
    }

    /**
     * 获取收藏列表
     *
     * @param skip  页数
     * @param limit 条数
     * @return Observable
     */
    public static Observable<BaseResult<List<ResourceBean>>> getFavorList(int skip, int limit) {
        HttpParams params = createGeneralMap(Utils.getContext());
        params.put("skip", String.valueOf(skip));
        params.put("limit", String.valueOf(limit));
        Type type = new TypeToken<BaseResult<List<ResourceBean>>>() {
        }.getType();
        return RxUtils.request(HttpMethod.GET, defaultUrl + "v2/user/favor", type, params);
    }

    /**
     * 获取收藏列表
     *
     * @param skip  页数
     * @param limit 条数
     * @return Observable
     */
    public static Observable<BaseResult<List<ResourceBean>>> getFavorList(String uid, int skip,
                                                                          int limit) {
        HttpParams params = createGeneralMap(Utils.getContext());
        params.put("skip", String.valueOf(skip));
        params.put("limit", String.valueOf(limit));
        Type type = new TypeToken<BaseResult<List<ResourceBean>>>() {
        }.getType();
        return RxUtils.request(HttpMethod.GET, defaultUrl + "v2/user/favor/" + uid, type, params);
    }

    /**
     * 获取上传列表
     *
     * @param skip  页数
     * @param limit 条数
     * @return Observable
     */
    public static Observable<BaseResult<List<ResourceBean>>> getUploadList(int skip, int limit) {
        HttpParams params = createGeneralMap(Utils.getContext());
        params.put("skip", String.valueOf(skip));
        params.put("limit", String.valueOf(limit));
        Type type = new TypeToken<BaseResult<List<ResourceBean>>>() {
        }.getType();
        return RxUtils.request(HttpMethod.GET, defaultUrl + "v2/user/videolist", type, params);
    }

    /**
     * 获取上传列表
     *
     * @param skip  页数
     * @param limit 条数
     * @return Observable
     */
    public static Observable<BaseResult<List<ResourceBean>>> getUploadList(String uid, int skip,
                                                                           int limit) {
        HttpParams params = createGeneralMap(Utils.getContext());
        params.put("skip", String.valueOf(skip));
        params.put("limit", String.valueOf(limit));
        Type type = new TypeToken<BaseResult<List<ResourceBean>>>() {
        }.getType();
        return RxUtils
                .request(HttpMethod.GET, defaultUrl + "v2/user/videolist/" + uid, type, params);
    }

    /**
     * 关联本地视频
     *
     * @param deviceid 设备id
     * @return Observable
     */
    public static Observable<BaseResult> relationLocalVideo(String deviceid) {
        HttpParams params = createGeneralMap(Utils.getContext());
        params.put("deviceid", deviceid);
        Type type = new TypeToken<BaseResult>() {
        }.getType();
        return RxUtils.request(HttpMethod.POST, defaultUrl + "v2/user/videolist", type, params);
    }

    /**
     * 修改昵称
     *
     * @param nickname 昵称
     * @return Observable
     */
    public static Observable<BaseResult> modifyUserInfo(String nickname, String desc) {
        HttpParams params = createGeneralMap(Utils.getContext());
        params.put("nickname", nickname);
        params.put("desc", desc);
        Type type = new TypeToken<BaseResult>() {
        }.getType();
        return RxUtils.request(HttpMethod.POST, defaultUrl + "v2/user/info", type, params);
    }

    /**
     * 获取热门标签
     *
     * @return Observable
     */
    public static Observable<BaseResult<List<String>>> getHotTag() {
        HttpParams params = createGeneralMap(Utils.getContext());
        params.put("limit", "10");
        Type type = new TypeToken<BaseResult<List<String>>>() {
        }.getType();
        return RxUtils.request(HttpMethod.GET, defaultUrl + "v1/search_hotkey", type, params);
    }

    /**
     * 获取 首页banner
     *
     * @return Observable
     */
    public static Observable<BannerResult> getBanner() {
        HttpParams params = createGeneralMap(Utils.getContext());
        params.put("current", "" + System.currentTimeMillis());
        Type type = new TypeToken<BannerResult>() {
        }.getType();
        return RxUtils.request(HttpMethod.GET, defaultUrl + "v2/banner", type, params);
    }

    /**
     * 根据 id获取壁纸信息
     *
     * @return Observable
     */
    public static Observable<BaseResult<ResourceBean>> getVideoWpDetails(String sourceId) {
        HttpParams params = createGeneralMap(Utils.getContext());
        Type type = new TypeToken<BaseResult<ResourceBean>>() {
        }.getType();
        return RxUtils.request(HttpMethod.GET, defaultUrl + "v1/videowp/" + sourceId, type, params);
    }

    public static Observable<BaseResult<AvatarResult>> getAvatarList(int request_type, String skip,
                                                                     String cid) {
        HttpParams params = createGeneralMap(Utils.getContext());
        if (request_type == 0) {
            params.put("order", "new");
        } else if (request_type == 1) {
            params.put("order", "hot");
        } else if (request_type == 2) {
            params.put("cid", cid);
        }
        params.put("skip", skip);
        params.put("limit", "18");
        Type type = new TypeToken<BaseResult<AvatarResult>>() {
        }.getType();
        return RxUtils.request(HttpMethod.GET, avatarUrl + "v1/avatar/avatar", type, params);
    }

    public static Observable<BaseResult<AvatarResult>> getAvatarRcmd(String skip) {
        HttpParams params = createGeneralMap(Utils.getContext());
        params.put("limit", "4");
        params.put("skip", skip);
        Type type = new TypeToken<BaseResult<AvatarResult>>() {
        }.getType();
        return RxUtils.request(HttpMethod.GET, avatarUrl + "v1/avatar/recommend", type, params);
    }

    public static Observable<BaseResult<AvatarResult>> getAvatarCategory() {
        HttpParams params = createGeneralMap(Utils.getContext());
        Type type = new TypeToken<BaseResult<AvatarResult>>() {
        }.getType();
        return RxUtils.request(HttpMethod.GET, avatarUrl + "v1/avatar/category", type, params);
    }

    public static Observable<RingsRespons> getRings(int request_type, String skip, String cid) {
        HttpParams params = createGeneralMap(Utils.getContext());
        params.put("limit", "15");
        params.put("skip", skip);
        Type type = new TypeToken<RingsRespons>() {
        }.getType();
        if (request_type == 2) {
            params.put("cid", cid);
            return RxUtils.request(HttpMethod.GET, ringsUrl + "category", type, params);
        }
        String oder = "hot";
        if (request_type == 1) {
            oder = "new";
        }
        return RxUtils.request(HttpMethod.GET, ringsUrl + oder, type, params);
    }

    public static Observable<RingsRespons> getRingsCategory() {
        Type type = new TypeToken<RingsRespons>() {
        }.getType();
        HttpParams params = createGeneralMap(Utils.getContext());
        return RxUtils.request(HttpMethod.GET, ringsUrl, type, params);
    }

    public static Observable<BaseResult<List<DownloadInfo>>> getDownload(int limit, int skip) {
        HttpParams params = createGeneralMap(Utils.getContext());
        params.put("skip", String.valueOf(skip));
        params.put("limit", String.valueOf(limit));
        Type type = new TypeToken<BaseResult<List<DownloadInfo>>>() {
        }.getType();
        return RxUtils
                .request(HttpMethod.GET, defaultUrl + "v2/user/vip/downloadlog", type, params);
    }

    public static Observable<BaseResult<String>> upDownloads(List<String> ids) {
        HttpParams params = createGeneralMap(Utils.getContext());
        params.put("ids", listToString(ids, ','));
        params.put("type", "add");
        Type type = new TypeToken<BaseResult<String>>() {
        }.getType();
        return RxUtils
                .request(HttpMethod.POST, defaultUrl + "v2/user/vip/downloadlog", type, params);
    }

    public static Observable<BaseResult<String>> delDownloads(List<String> ids) {
        HttpParams params = createGeneralMap(Utils.getContext());
        params.put("ids", listToString(ids, ','));
        params.put("type", "del");
        Type type = new TypeToken<BaseResult<String>>() {
        }.getType();
        return RxUtils
                .request(HttpMethod.POST, defaultUrl + "v2/user/vip/downloadlog", type, params);
    }

    /**
     * 获取模板资源
     *
     * @param skip 跳过
     * @return Observable
     */
    public static Observable<BaseResult<List<VModel>>> getDiyRes(int skip, int limit) {
        HttpParams params = createGeneralMap(Utils.getContext());
        params.put("skip", String.valueOf(skip));
        params.put("limit", String.valueOf(limit));
        Type type = new TypeToken<BaseResult<List<VModel>>>() {
        }.getType();
        return RxUtils.request(HttpMethod.GET, defaultUrl + "v2/custom/list", type, params);
    }

    public static Observable<BaseResult<List<VModel>>> getHomeRcmdDiys() {
        HttpParams params = createGeneralMap(Utils.getContext());
        Type type = new TypeToken<BaseResult<List<VModel>>>() {
        }.getType();
        return RxUtils
                .request(HttpMethod.GET, defaultUrl + "v2/custom/rcmd_list", type, params);
    }

    public static Observable<BaseResult<List<VModel>>> getVipRcmdDiys() {
        HttpParams params = createGeneralMap(Utils.getContext());
        Type type = new TypeToken<BaseResult<List<VModel>>>() {
        }.getType();
        return RxUtils
                .request(HttpMethod.GET, defaultUrl + "v2/user/vip/rcmd_list", type, params);
    }

    public static String listToString(List list, char separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i == list.size() - 1) {
                sb.append(list.get(i));
            } else {
                sb.append(list.get(i));
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    /**
     * 基础参数
     *
     * @param context 上下文
     * @return HttpParams
     */
    public static HttpParams createGeneralMap(Context context) {
        HttpParams params = new HttpParams();
        String pkg = "";
        String appver = "";
        String appvercode = "";
        String sysname = "";
        String sysver = "";
        String sysmodel = "";
        String lan = "zh-CN";
        String channel = "default";
        if (context != null) {
            pkg = CtxUtil.getPackageName(context);
            appver = CtxUtil.getVersionName(context);
            appvercode = CtxUtil.getVersionCode(context) + "";
            sysname = Build.VERSION.RELEASE;
            sysver = Build.VERSION.SDK_INT + "";
            sysmodel = Build.MODEL.toLowerCase();
            lan = "zh-CN";
            channel = CtxUtil.getUmengChannel(context);
        }

        params.put("os", "android");
        params.put("appid", pkg);
        params.put("appver", appver);
        params.put("appvercode", appvercode);
        params.put("sys_name", sysname);
        params.put("sys_ver", sysver);
        params.put("sys_model", sysmodel);
        params.put("lan", lan);
        params.put("channel", channel);
        params.put("adult", "false");
        return params;
    }

    /**
     * 订单状态
     *
     * @param oid 订单id
     */
    public static Observable<BaseResult<OrderInfo>> checkOrder(String oid) {
        HttpParams params = new HttpParams();
        params.put("oid", oid);
        Type type = new TypeToken<BaseResult<OrderInfo>>() {
        }.getType();
        return RxUtils.request(HttpMethod.GET, vipUrl + "v1/user/order/result", type, params);
    }
}
