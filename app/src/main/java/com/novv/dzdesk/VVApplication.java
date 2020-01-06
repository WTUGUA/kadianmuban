package com.novv.dzdesk;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ark.adkit.basics.configs.Strategy;
import com.ark.adkit.basics.utils.AppUtils;
import com.ark.adkit.basics.utils.LogUtils;
import com.ark.adkit.polymers.polymer.ADTool;
import com.ark.auth.Auth;
import com.ark.auth.alipay.AuthBuildForZFB;
import com.ark.auth.qq.AuthBuildForQQ;
import com.ark.auth.weixin.AuthBuildForWX;
import com.crashlytics.android.Crashlytics;
import com.danikula.videocache.HttpProxyCacheServer;
import com.danikula.videocache.file.FileNameGenerator;
import com.leon.channel.helper.ChannelReaderUtil;
import com.liulishuo.filedownloader.FileDownloader;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.DBCookieStore;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.model.HttpHeaders;
import com.novv.dzdesk.http.AddCookiesInterceptor;
import com.novv.dzdesk.http.ReceivedCookiesInterceptor;
import com.novv.dzdesk.http.SafeHostnameVerifier;
import com.novv.dzdesk.http.SafeTrustManager;
import com.novv.dzdesk.res.model.SwitchBackgroundCallbacks;
import com.novv.dzdesk.ui.activity.SplashActivity;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.interfaces.BetaPatchListener;
import com.tencent.mmkv.MMKV;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import io.fabric.sdk.android.Fabric;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import okhttp3.OkHttpClient;

public class VVApplication extends Application {

  private HttpProxyCacheServer proxy;
  public static String rootPath;
  private static Context mContext;

  public static Context getContext(){
    return  mContext;
  }
  public static HttpProxyCacheServer getProxy(Context context) {
    VVApplication app = (VVApplication) context.getApplicationContext();
    return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
  }

  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiDex.install(base);
    Beta.installTinker();
  }

  private HttpProxyCacheServer newProxy() {
    HttpProxyCacheServer.Builder builder = new HttpProxyCacheServer.Builder(this);
    builder.fileNameGenerator(new VideoFileNameGenerator());
    return builder.build();
  }

  @Override
  public void onCreate() {
    super.onCreate();
    mContext  = this;
    Thread.setDefaultUncaughtExceptionHandler(VVCrashHandler.getInstance(this));
    MMKV.initialize(this);
    //友盟初始化
    // 初始化SDK
    UMConfigure.init(this,"5dd742470cafb2605200081a", ChannelReaderUtil.getChannel(this), UMConfigure.DEVICE_TYPE_PHONE, null);
    // 选用LEGACY_AUTO页面采集模式
    MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.LEGACY_MANUAL);
    // 支持在子进程中统计自定义事件
    UMConfigure.setProcessEvent(true);
    // 打开统计SDK调试模式
    UMConfigure.setLogEnabled(true);



    initBugly(this);
    Fabric.with(this, new Crashlytics());
   // FeedbackAPI.init(this, Const.AliConstants.APP_KEY, Const.AliConstants.APP_SECRET);
    registerActivityLifecycleCallbacks(new SwitchBackgroundCallbacks());
    initPlatform();
    initFileDownloader();
    initOkGo();
    initADTool();

    String absolutePath = getFilesDir().getAbsolutePath();
    if (getExternalFilesDir(null)!=null){
      String externalFileDir = getExternalFilesDir(null).getAbsolutePath();
      if (externalFileDir!=null){
        rootPath =getExternalFilesDir(null).getAbsolutePath();
      }else {
        rootPath = absolutePath;
      }
    }else {
      rootPath = absolutePath;
    }
  }

  private void initBugly(final Context context) {
    String channel = ChannelReaderUtil.getChannel(context);
    if (TextUtils.isEmpty(channel)) {
      channel = "adesk";
    }
    channel = channel.replace("_", "");
    Beta.canNotShowUpgradeActs.add(SplashActivity.class);
    Beta.autoCheckUpgrade = true;
    Beta.enableHotfix = true;
    Beta.canAutoDownloadPatch = false;
    Beta.canNotifyUserRestart = true;
    Beta.canAutoPatch = true;
    Beta.betaPatchListener = new BetaPatchListener() {
      @Override
      public void onPatchReceived(String patchFileUrl) {
        LogUtils.e("onPatchReceived:" + patchFileUrl);
      }

      @Override
      public void onDownloadReceived(long savedLength, long totalLength) {
        LogUtils.e("onDownloadReceived:" + savedLength + "/" + totalLength);
        Toast.makeText(context, String.format(Locale.getDefault(), "%s %d%%",
            Beta.strNotificationDownloading,
            (int) (totalLength == 0 ? 0 : savedLength * 100 / totalLength)),
            Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onDownloadSuccess(final String patchFilePath) {
        LogUtils.e("onDownloadSuccess:" + patchFilePath);
      }

      @Override
      public void onDownloadFailure(String msg) {
        LogUtils.e("onDownloadFailure:" + msg);
      }

      @Override
      public void onApplySuccess(String msg) {
        LogUtils.e("onApplySuccess:" + msg);
        Toast.makeText(context, "补丁应用成功", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onApplyFailure(String msg) {
        LogUtils.e("onApplyFailure:" + msg);
      }

      @Override
      public void onPatchRollback() {
        LogUtils.e("onPatchRollback");
        Toast.makeText(context, "补丁回滚", Toast.LENGTH_SHORT).show();
      }
    };
    Bugly.setAppChannel(context, channel);
    Bugly.init(context, "1d3f447860", BuildConfig.DEBUG);
  }


  private void initADTool() {
    ADTool.initialize(new ADTool.Builder()
        .setStrategy(Strategy.order)
        .setDebugMode(BuildConfig.DEBUG)
        .setLoadOtherWhenVideoDisable(true)
        .build());
  }

  private void initOkGo() {
    HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
      @Override public boolean verify(String hostname, SSLSession session) {
        return !TextUtils.isEmpty(hostname);
      }
    });
    String channel = AppUtils.getChannel(this);
    int code = AppUtils.getVersionCode(this);
    HttpHeaders headers = new HttpHeaders();
    headers.put(HttpHeaders.HEAD_KEY_USER_AGENT, code + "," + channel);
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    builder.readTimeout(30, TimeUnit.SECONDS);
    builder.writeTimeout(30, TimeUnit.SECONDS);
    builder.connectTimeout(10, TimeUnit.SECONDS);
    builder.cookieJar(new CookieJarImpl(new DBCookieStore(this)));
    HttpsUtils.SSLParams sslParams1 = HttpsUtils.getSslSocketFactory(new SafeTrustManager());
    builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);
    builder.hostnameVerifier(new SafeHostnameVerifier());
    builder.addInterceptor(new AddCookiesInterceptor());
    builder.addInterceptor(new ReceivedCookiesInterceptor());
    OkGo.getInstance().init(this)
        .addCommonHeaders(headers)
        .setOkHttpClient(builder.build())
        .setRetryCount(3);
    RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
      @Override
      public void accept(Throwable throwable) throws Exception {
        Log.e("logger", throwable.toString());
      }
    });
  }

  private void initFileDownloader() {
    FileDownloader.setupOnApplicationOnCreate(this);
  }

  private void initPlatform() {
    Auth.init().setQQAppID(Const.QQConstants.APP_ID)
        .setWXAppID(Const.WXConstants.APP_ID)
        .setWXSecret(Const.WXConstants.APP_SECRET)
        .addFactoryForQQ(AuthBuildForQQ.getFactory())
        .addFactoryForWX(AuthBuildForWX.getFactory())
        .addFactoryForZFB(AuthBuildForZFB.getFactory())
        .build();
  }

  public class VideoFileNameGenerator implements FileNameGenerator {

    @Override
    public String generate(String url) {
      if (TextUtils.isEmpty(url)) {
        return System.currentTimeMillis() + "";
      }
      try {
        Uri uri = Uri.parse(url);
        String paramURL = uri.getQueryParameter("url");
        if (!TextUtils.isEmpty(paramURL)) {
          uri = Uri.parse(paramURL);
        }
        if (uri == null || TextUtils.isEmpty(uri.getLastPathSegment())) {
          return url.hashCode() + "";
        }
        return uri.getLastPathSegment() + "";
      } catch (Exception e) {
        e.printStackTrace();
      } catch (Error e) {
        e.printStackTrace();
      }
      return url.hashCode() + "";
    }
  }
}
