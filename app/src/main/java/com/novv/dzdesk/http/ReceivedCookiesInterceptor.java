package com.novv.dzdesk.http;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.novv.dzdesk.util.HeaderSpf;
import com.novv.dzdesk.util.LogUtil;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * 保存登陆的时候服务端返回在header中的Session-id Created by lijianglong on 2017/9/5.
 */

public class ReceivedCookiesInterceptor implements Interceptor {

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        Headers responseHeaders = response.headers();
        int responseHeadersLength = responseHeaders.size();
        for (int i = 0; i < responseHeadersLength; i++) {
            String headerName = responseHeaders.name(i);
            String headerValue = responseHeaders.get(headerName);
            if (TextUtils.equals("Session-Id", headerName)) {
                if (request.url().toString().contains("user/login")) {
                    HeaderSpf.saveSessionId(headerValue);
                    LogUtil.e("retrofit", "ReceivedCookiesInterceptor---->" + headerValue);
                }
            }
        }
        return response;
    }
}
