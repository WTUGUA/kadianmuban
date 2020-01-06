package com.novv.dzdesk.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lingyfh on 2018/4/18.
 */
public class AdeskOnlineConfigUtil {

    private static final String sRequestURL = "http://service.kv.dandanjiang.tv/online/params";
    private static final String tag = AdeskOnlineConfigUtil.class.getSimpleName();

    private static final String ONLINE_PARAMS = "online_params_pre";

    private SharedPreferences mPres;
    private JSONObject res;

    private AdeskOnlineConfigUtil() {

    }

    public static String getConfigParams(Context context, String key) {
        return getInstance().getParams(context, key);
    }

    public static AdeskOnlineConfigUtil getInstance() {
        return AdeskOnlineConfigUtilHolder.sInstance;
    }

    private String reCreateURL(String url, HashMap<String, String> params) {
        if (TextUtils.isEmpty(url)) {
            url = "";
        }
        if (params == null) {
            return url;
        }
        String newURL = url;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (newURL.contains("?")) {
                newURL += "&" + entry.getKey() + "="
                        + URLEncoder.encode(entry.getValue());
            } else {
                newURL += "?" + entry.getKey() + "="
                        + URLEncoder.encode(entry.getValue());
            }
        }
        LogUtil.i(tag, "newURL = " + newURL);
        return newURL;
    }

    public void init(final Context context) {
        mPres = context.getApplicationContext().getSharedPreferences(
                ONLINE_PARAMS, Context.MODE_PRIVATE);

        if (mPres == null) {
            return;
        }

        String resStr = mPres.getString("res", "");
        if (!TextUtils.isEmpty(resStr)) {
            try {
                res = new JSONObject(resStr);
                res = res.getJSONObject("params");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        final HashMap<String, String> requestParams = new HashMap<>();
        requestParams.put("package_name", context.getPackageName());

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                return NetUtil.requestData(context,
                        reCreateURL(sRequestURL, requestParams));
            }

            @Override
            protected void onPostExecute(String content) {
                super.onPostExecute(content);
                LogUtil.i(tag, "content = " + content);
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    JSONObject jsonRes = jsonObject.optJSONObject("res");
                    mPres.edit().putString("res", jsonRes.toString()).commit();
                    res = new JSONObject(jsonRes.toString());
                    res = jsonRes.getJSONObject("params");
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (Error e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private String getParams(Context context, String key) {
        if (res == null) {
            init(context);
        }
        if (res == null) {
            return "";
        }
        return res.optString(key);
    }

    private static class AdeskOnlineConfigUtilHolder {

        public static final AdeskOnlineConfigUtil sInstance = new AdeskOnlineConfigUtil();
    }

}
