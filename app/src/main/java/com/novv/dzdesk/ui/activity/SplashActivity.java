package com.novv.dzdesk.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ark.adkit.polymers.ttad.config.TTAdManagerHolder;
import com.ark.baseui.XAppCompatActivity;
import com.ark.dict.ConfigMapLoader;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.novv.dzdesk.R;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends XAppCompatActivity {

  private RelativeLayout defaultImg;
  private FrameLayout mAdContainer;
  private TextView adPlatform;
  private ViewGroup mRootView;
    private long exitTime = 0;
    private final int mRequestCode = 100;//权限请求码
    String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //2、创建一个mPermissionList，逐个判断哪些权限未授予，未授予的权限存储到mPerrrmissionList中
    List<String> mPermissionList = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPermission();
        int splash=getspenable();
//        //去除标题栏
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        //去除状态栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(splash==1){
            TTAdManager ttAdManager = TTAdManagerHolder.getInstance(this, "5037968");

            ttAdManager.requestPermissionIfNecessary(this);
            TTAdNative ttAdNative = ttAdManager.createAdNative(getApplicationContext());
            AdSlot adSlot = new AdSlot.Builder()
                    .setCodeId("837968978")
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(1080, 1920)
                    .build();
            ttAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
                @Override
                public void onError(int i, String s) {
                }

                @Override
                public void onTimeout() {
                }

                @Override
                public void onSplashAdLoad(final TTSplashAd ttSplashAd) {
                    View splashAdView = ttSplashAd.getSplashView();
                    mAdContainer.addView(splashAdView);
                    ttSplashAd.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                        @Override
                        public void onAdClicked(View view, int i) {
                        }

                        @Override
                        public void onAdShow(View view, int i) {

                        }

                        @Override
                        public void onAdSkip() {
                            launchMainActivity();
                        }

                        @Override
                        public void onAdTimeOver() {
                            launchMainActivity();
                        }
                    });
                }
            }, 5000);
        }else{
            defaultImg.setVisibility(View.GONE);
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            };
            Timer timer = new Timer();
            timer.schedule(task, 1500);//3秒后执行TimeTask的run方法
        }
    }

    @Override
  public void initData(@Nullable Bundle savedInstanceState) {
    defaultImg = findViewById(R.id.default_img);
    mAdContainer = findViewById(R.id.splash_fr);
    mRootView = findViewById(R.id.splash_rl);
    adPlatform = findViewById(R.id.ad_platform);




//    PermissionsUtils.checkSplash(this, new PermissionsUtils.OnPermissionBack() {
//      @Override public void onResult(boolean success) {
//        if (!isTaskRoot()) {
//          Intent i = getIntent();
//          String action = i.getAction();
//          if (i.hasCategory(Intent.CATEGORY_APP_CALENDAR)
//              && TextUtils.equals(action, Intent.ACTION_MAIN)) {
//            finish();
//            return;
//          }
//        }
//        if (success) {
//          RateTLUtils.init(context);
//          loadSplash();
//        } else {
//          finish();
//        }
//      }
//    });
  }

  @Override
  public int getLayoutId() {
    return R.layout.activity_splash;
  }

  @Nullable @Override public Object newP() {
    return null;
  }

//  private void loadSplash() {
//    ADTool.getADTool().getManager()
//        .getSplashWrapper()
//        .setMills(3000, 5000, 500)
//       // .isVipSkip(LoginContext.getInstance().isVip())
//        //.needPermissions(false)
//        .loadSplash(SplashActivity.this, mAdContainer, mRootView, new OnSplashImpl() {
//
//          @Override
//          public void onAdDisplay(@NonNull String platform) {
//            defaultImg.setVisibility(View.VISIBLE);
//            adPlatform.setText(platform);
//            if (ADTool.getADTool().isDebugMode()) {
//              adPlatform.setVisibility(View.VISIBLE);
//            }
//          }
//
//          @Override
//          public void onAdTimeTick(long tick) {
//
//          }
//
//          @Override
//          public void onAdShouldLaunch() {
//            launchMainActivity();
//          }
//        });
//  }

  @Override
  protected void onRestart() {
    super.onRestart();
    launchMainActivity();
  }

  private void launchMainActivity() {
    if (!isFinishing()) {
      Intent intent = new Intent(this,MainActivity.class);
      startActivity(intent);
      getWindow().setFlags(
          WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
          WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
      finish();
    }
  }

  /**
   * 屏蔽返回键
   */
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    return keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME || super
        .onKeyDown(keyCode, event);
  }

  @Override
  protected void onResume() {
    MobclickAgent.onResume(this);
    super.onResume();
  }

  @Override
  protected void onPause() {
    MobclickAgent.onPause(this);
    super.onPause();
  }
    //权限判断和申请
    private void initPermission() {

        mPermissionList.clear();//清空没有通过的权限

        //逐个判断你要的权限是否已经通过
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);//添加还未授予的权限
            }
        }

        //申请权限
        if (mPermissionList.size() > 0) {//有权限没有通过，需要申请
            ActivityCompat.requestPermissions(this, permissions, mRequestCode);
        } else {
            //说明权限都已经通过，可以做你想做的事情去
        }
    }
    //
    //获取开屏广告开关
    public static int getspenable() {
        String rate_button_enable = ConfigMapLoader.getInstance().getResponseMap().get("ad_launch_enable");

        if (rate_button_enable == null) {
            return 0;
        }
        return Integer.parseInt(rate_button_enable);
    }

}
