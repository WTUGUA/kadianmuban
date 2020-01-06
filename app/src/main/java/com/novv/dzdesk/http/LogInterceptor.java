package com.novv.dzdesk.http;

import android.support.annotation.NonNull;
import com.novv.dzdesk.BuildConfig;
import com.novv.dzdesk.util.LogUtil;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * 日志拦截
 */
public class LogInterceptor implements Interceptor {

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        long t1 = System.nanoTime();
        Response response = chain.proceed(request);
        long t2 = System.nanoTime();
        if (BuildConfig.DEBUG) {
            LogUtil.i("logger", String.format("接收回应 url: %s 耗时 %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));
        }
        return response;
    }
}
