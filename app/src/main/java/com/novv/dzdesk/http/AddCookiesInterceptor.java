package com.novv.dzdesk.http;

import com.novv.dzdesk.util.HeaderSpf;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * 为请求添加统一的header：Session-id Created by lijianglong on 2017/9/5.
 */

public class AddCookiesInterceptor implements Interceptor {

    //"videoWallpaper-3ce17af7999140aba76082d1cc08487f-59ae0c7fab3d131832bc2947"
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder request = chain.request().newBuilder();
        //为空则清除登录状态
        request.addHeader("Session-Id", HeaderSpf.getSessionId());
        return chain.proceed(request.build());
    }
}

