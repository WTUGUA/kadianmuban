package com.novv.dzdesk.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.adesk.tool.plugin.PluginManager;
import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.ark.adkit.basics.configs.Strategy;
import com.ark.adkit.polymers.polymer.ADTool;
import com.ark.baseui.XAppCompatActivity;
import com.gyf.barlibrary.ImmersionBar;
import com.novv.dzdesk.Const;
import com.novv.dzdesk.R;
import com.novv.dzdesk.live.LwPrefUtil;
import com.novv.dzdesk.live.LwVideoLiveWallpaper;
import com.novv.dzdesk.res.model.RxAsyncTask;
import com.novv.dzdesk.res.user.LoginContext;
import com.novv.dzdesk.service.AppReceiver;
import com.novv.dzdesk.ui.activity.user.UserInfoEditActivity;
import com.novv.dzdesk.util.AcceptUserProtocol;
import com.novv.dzdesk.util.CleanUtil;
import com.novv.dzdesk.util.CtxUtil;
import com.novv.dzdesk.util.HeaderSpf;
import com.novv.dzdesk.util.LogUtil;
import com.novv.dzdesk.util.PathUtils;
import com.novv.dzdesk.util.PermissionsUtils;
import com.novv.dzdesk.util.ToastUtil;
import com.novv.dzdesk.util.UmUtil;
import com.umeng.analytics.MobclickAgent;
import java.io.File;

public class SettingsActivity extends XAppCompatActivity implements View.OnClickListener {

  private static final String tag = SettingsActivity.class.getSimpleName();
  private int clickCount = 0;
  private TextView mVolumeTv;
  private SeekBar mSeekBar;
  private boolean needUpdateVoice = false;
  private PluginManager pluginManager;
  private AppReceiver appReceiver;

  public static void launch(Context context) {
    Intent intent = new Intent(context, SettingsActivity.class);
    context.startActivity(intent);
  }

  @Override
  public int getLayoutId() {
    return R.layout.activity_set;
  }

  @Nullable
  @Override
  public Object newP() {
    return null;
  }

  @Override
  public void setUpView(@NonNull View rootView) {
    super.setUpView(rootView);
    mVolumeTv = findViewById(R.id.video_volume_count_tv);
    mSeekBar = findViewById(R.id.video_volume_seekbar);

    View mCacheSetV = findViewById(R.id.set_cache);
    mCacheSetV.setOnClickListener(this);

    View mSetFailHelpV = findViewById(R.id.set_fail);
    mSetFailHelpV.setOnClickListener(this);

    View mFeedbackSetV = findViewById(R.id.set_feedback);
    mFeedbackSetV.setOnClickListener(this);

    View mJoinQQGroupV = findViewById(R.id.set_cooperation);
    mJoinQQGroupV.setOnClickListener(this);

    View mAboutSetV = findViewById(R.id.set_about);
    mAboutSetV.setOnClickListener(this);

    View mInfoSetV = findViewById(R.id.set_user_info);
    mInfoSetV.setOnClickListener(this);

    View mUserProtocol = findViewById(R.id.set_user_protocol);
    mUserProtocol.setOnClickListener(this);

    View mLogoutSetV = findViewById(R.id.set_logout);
    mLogoutSetV.setOnClickListener(this);

//    View mSetPlugin = findViewById(R.id.set_plugin);
//    mSetPlugin.setOnClickListener(this);
//
//    View mListPlugin = findViewById(R.id.list_plugin);
//    mListPlugin.setOnClickListener(this);

    View mBackView = findViewById(R.id.btn_back);
    mBackView.setOnClickListener(this);

    boolean isLogin = LoginContext.getInstance().isLogin();
    mInfoSetV.setVisibility(isLogin ? View.VISIBLE : View.GONE);
    mLogoutSetV.setVisibility(isLogin ? View.VISIBLE : View.GONE);
  }

  @Override
  public void initData(@Nullable Bundle savedInstanceState) {
    ImmersionBar.with(this)
        .titleBar(findViewById(R.id.fl_top), false)
        .transparentStatusBar()
        .init();
    regAppReceiver();
    mSeekBar.setProgress(LwPrefUtil.getVideoVolume(this));
    mVolumeTv.setText("(" + mSeekBar.getProgress() + "%)");
    mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        mVolumeTv.setText("(" + i + "%)");
        needUpdateVoice = true;
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });
    pluginManager = new PluginManager(this);
    pluginManager.bindPluginService(null);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_back:
        finish();
        break;
      case R.id.set_cache:
        clearCache();
        break;
      case R.id.set_fail:
        LwVideoLiveWallpaper.setToWallPaper(this);
        break;
      case R.id.set_feedback:
        feedback();
        break;
      case R.id.set_cooperation:
        about("官方QQ群：561633383");
        break;
      case R.id.set_about:
        String content = getResources()
            .getString(R.string.about_prefix)
            + CtxUtil.getVersionName(this)
            + ","
            + getResources().getString(R.string.about_suffix);
        about(content);
        showInfoToast();
        break;
      case R.id.set_user_info:
        if (!LoginContext.getInstance().isLogin()) {
          ToastUtil.showToast(this, "你还未登录呢～");
          return;
        }
        UserInfoEditActivity.launch(SettingsActivity.this);
        break;
      case R.id.set_logout:
        HeaderSpf.clear();
        LogUtil.e(tag, "--------------->" + HeaderSpf.getSessionId());
        LoginContext.getInstance().logout();
        finish();
        break;
      case R.id.set_user_protocol:
        try {
          Intent intent = new Intent(Intent.ACTION_VIEW,
              Uri.parse(AcceptUserProtocol.UserProtocolURL));
          startActivity(intent);
        } catch (Exception e) {
          ToastUtil.showGeneralToast(this, "打开网页失败");
        }
        break;
//      case R.id.set_plugin:
//        pluginManager.checkPluginVersion(5, new OnCheckListener() {
//          @Override
//          public void onShouldUpdate() {
//            new PluginDialog(SettingsActivity.this)
//                .setTitle("检测到更新").show();
//          }
//
//          @Override
//          public void onSuccess() {
//            ToastUtil.showGeneralToast(SettingsActivity.this, "组件已经是最新");
//          }
//
//          @Override
//          public void onShouldInstall() {
//            new PluginDialog(SettingsActivity.this).show();
//          }
//        });
//        break;
//      case R.id.list_plugin:
//        pluginManager.checkPluginVersion(5, new OnCheckListener() {
//          @Override
//          public void onShouldUpdate() {
//            new PluginDialog(SettingsActivity.this)
//                .setTitle("检测到更新").show();
//          }
//
//          @Override
//          public void onSuccess() {
//            ArrayList<String> list = PVideosUtils.getData(SettingsActivity.this, null);
//            Intent intent1 = new Intent(SettingsActivity.this,
//                VideoPlayListActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putStringArrayList("data", list);
//            intent1.putExtras(bundle);
//            startActivity(intent1);
//          }
//
//          @Override
//          public void onShouldInstall() {
//            new PluginDialog(SettingsActivity.this).show();
//          }
//        });
//        break;
      default:
        break;
    }
  }

  private void showInfoToast() {
    clickCount = clickCount + 1;
    if (clickCount > 5) {
      ToastUtil
          .showToast(
              this,
              "channel = " + CtxUtil.getUmengChannel(this)
                  + " version name = "
                  + CtxUtil.getVersionName(this)
                  + " version code = "
                  + CtxUtil.getVersionCode(this));
      ADTool.initialize(new ADTool.Builder()
          .setStrategy(Strategy.order)
          .setDebugMode(true)
          .setLoadOtherWhenVideoDisable(true)
          .build());
      clickCount = 0;
    }
  }

  private void clearCache() {
    LogUtil.i(tag, "clearCache");
    final SweetAlertDialog pDialog = new SweetAlertDialog(SettingsActivity.this,
        SweetAlertDialog.PROGRESS_TYPE);
    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
    pDialog.setTitleText(getResources().getString(R.string.operating));
    pDialog.setCancelable(false);
    pDialog.setCanceledOnTouchOutside(false);
    pDialog.show();
    new RxAsyncTask<Void, Void, Boolean>() {

      @Override
      protected Boolean call(Void... voids) {
        try {
          CleanUtil.clearAllCache(SettingsActivity.this);
          CleanUtil.deleteDir(new File(Const.Dir.VIDEO_ROOT_PATH));
          CleanUtil.deleteDir(new File(
              Const.Dir.VIDEO_CROP_PATH_UPLOAD));
          CleanUtil.deleteDir(new File(Const.Dir.SHARE_PATH));
          CleanUtil.deleteDir(new File(Const.Dir.APK_DOWNLOAD));
          CleanUtil.deleteDir(new File(Const.Dir.IMG_CROP_PATH));
          CleanUtil.deleteDir(new File(Const.Dir.VIDEO_TEMP_PATH));
          CleanUtil.deleteDir(new File(PathUtils.getDiyResRootDir()));
        } catch (Exception e) {
          return false;
        }
        return true;
      }

      @Override
      protected void onResult(Boolean aBoolean) {
        super.onResult(aBoolean);
        ToastUtil.showToast(SettingsActivity.this, R.string.op_success);
      }

      @Override
      protected void onCompleted() {
        super.onCompleted();
        new Handler(getMainLooper()).postDelayed(new Runnable() {
          @Override
          public void run() {
            if (pDialog.isShowing()) {
              pDialog.dismissWithAnimation();
            }
          }
        }, 500);
      }
    }.execute();
  }

  private void feedback() {
    PermissionsUtils.checkCamera(this, new PermissionsUtils.OnPermissionBack() {
      @Override public void onResult(boolean success) {
        new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
            try {
              FeedbackAPI.openFeedbackActivity();
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        }, 500);
      }
    });
  }

  private void about(String msg) {
    SweetAlertDialog dialog = new SweetAlertDialog(this,
        SweetAlertDialog.NORMAL_TYPE)
        .setTitleText(getResources().getString(R.string.dialog_title))
        .setConfirmText(
            getResources().getString(R.string.dialog_cancel))
        .setConfirmClickListener(
            new SweetAlertDialog.OnSweetClickListener() {
              @Override
              public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();
              }
            })
        .setContentText(msg)
        .setCancelText(getResources().getString(R.string.dialog_sure))
        .setCancelClickListener(
            new SweetAlertDialog.OnSweetClickListener() {
              @Override
              public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();
              }
            });
    dialog.show();
  }

  private void regAppReceiver() {
    IntentFilter filter = new IntentFilter();
    filter.addDataScheme("package");
    filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
    filter.addAction(Intent.ACTION_PACKAGE_ADDED);
    filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
    appReceiver = new AppReceiver();
    registerReceiver(appReceiver, filter);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
      if (requestCode == LwVideoLiveWallpaper.REQUEST_CODE_SET_WALLPAPER) {
        new Handler(Looper.getMainLooper()).postDelayed(
            new Runnable() {
              @Override
              public void run() {
                Context context = SettingsActivity.this;
                if (CtxUtil.isLwServiceRun(context)) {
                  CtxUtil.launchHome(context);
                }
              }
            }, 500);
      }
    }
  }

  @Override
  protected void onResume() {
    MobclickAgent.onResume(this);
    super.onResume();
    needUpdateVoice = false;
  }

  @Override
  protected void onPause() {
    MobclickAgent.onPause(this);
    super.onPause();
    if (needUpdateVoice) {
      LwPrefUtil.setVideoVolume(this, mSeekBar.getProgress());
      LwPrefUtil.setLwpVoiceOpened(this, true);
      pluginManager.setVolume(true);
      LwVideoLiveWallpaper.voiceNormal(this);
      UmUtil.anaEvent(this, "set_desktop_volume");
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    ImmersionBar.with(this).destroy();
    pluginManager.unBindPluginService();
    try {
      unregisterReceiver(appReceiver);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
