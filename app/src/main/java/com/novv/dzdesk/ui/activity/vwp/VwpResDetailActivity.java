package com.novv.dzdesk.ui.activity.vwp;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.adesk.tool.plugin.PAppUtils;
import com.adesk.tool.plugin.PluginManager;
import com.ark.adkit.basics.data.ADMetaData;
import com.ark.adkit.basics.handler.Run;
import com.ark.adkit.basics.models.OnNativeListener;
import com.ark.adkit.basics.utils.LogUtils;
import com.ark.adkit.polymers.polymer.ADTool;
import com.ark.adkit.polymers.ydt.utils.ScreenUtils;
import com.ark.baseui.XAppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.danikula.videocache.HttpProxyCacheServer;
import com.gyf.barlibrary.ImmersionBar;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.novv.dzdesk.Const;
import com.novv.dzdesk.R;
import com.novv.dzdesk.VVApplication;
import com.novv.dzdesk.http.BaseObserver;
import com.novv.dzdesk.http.DefaultScheduler;
import com.novv.dzdesk.http.ServerApi;
import com.novv.dzdesk.http.SimpleObserver;
import com.novv.dzdesk.live.LwPrefUtil;
import com.novv.dzdesk.live.LwVideoLiveWallpaper;
import com.novv.dzdesk.live.PrefUtil;
import com.novv.dzdesk.res.dto.CommentModelListDTO;
import com.novv.dzdesk.res.model.BaseResult;
import com.novv.dzdesk.res.model.CommentList;
import com.novv.dzdesk.res.model.CommentModel;
import com.novv.dzdesk.res.model.ResourceBean;
import com.novv.dzdesk.res.model.UserModel;
import com.novv.dzdesk.res.user.LoginContext;
import com.novv.dzdesk.service.AppReceiver;
import com.novv.dzdesk.ui.HomeActivity;
import com.novv.dzdesk.ui.activity.SplashActivity;
import com.novv.dzdesk.ui.activity.user.UserDetailActivity;
import com.novv.dzdesk.ui.dialog.CommentFragment;
import com.novv.dzdesk.ui.dialog.LoginDialog;
import com.novv.dzdesk.ui.dialog.SetInfoDialog;
import com.novv.dzdesk.ui.fragment.ShareFragment;
import com.novv.dzdesk.ui.view.CustomerVideoView;
import com.novv.dzdesk.util.AcceptUserProtocol;
import com.novv.dzdesk.util.AnaUtil;
import com.novv.dzdesk.util.CtxUtil;
import com.novv.dzdesk.util.DeviceUtil;
import com.novv.dzdesk.util.LogUtil;
import com.novv.dzdesk.util.PermissionsUtils;
import com.novv.dzdesk.util.RateTLUtils;
import com.novv.dzdesk.util.ToastUtil;
import com.novv.dzdesk.util.UmConst;
import com.novv.dzdesk.util.UmUtil;
import com.novv.dzdesk.util.VideoTimeUtils;
import com.umeng.analytics.MobclickAgent;
import com.xslczx.widget.ShapeImageView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import jp.wasabeef.blurry.Blurry;

public class VwpResDetailActivity extends XAppCompatActivity implements
    CompoundButton.OnCheckedChangeListener, UmConst, View.OnClickListener,
    CommentFragment.CallBack {

  public static final String Detail_res_key = "Detail_res_key";
  private static final String url = "http://service.videowp.adesk.com/v2/temp?url=";
  private static final String Detail_res_key_IS_FIRST_OPEN = "Detail_res_key_is_first_open";
  private static final String Detail_res_key_IS_Favor = "Detail_res_key_IS_Favor";
  private static final String Detail_res_key_IS_Disable_Res = "Detail_res_key_IS_Disable_Res";
  private static final String tag = VwpResDetailActivity.class.getSimpleName();
  private ImmersionBar immersionBar;
  private ResourceBean mItem;
  private CustomerVideoView videoPlayer;
  private ImageView bgImgV;
  private ShapeImageView userAvatar;
  private View mFavoriteView;
  private TextView mFavoriteTv;
  private UserModel mUserInfo;
  private View mCommentView;
  private TextView mCommentTv;
  private CheckBox mVoiceView;
  private Button mSetBtn;
  private boolean isFromFavor;
  private TextView mTitleView;
  private View mBackView;
  private View mShareView;
  private View llRight;
  private View ivFavorite;
  private File mHDVideoFile;
  private boolean isHDVideoDownloading = false;
  private TextView tvVideoInfo;
  private ProgressBar mProgressBar;
  private ImageView pauseButton;
  private boolean isFavor;
  private ProgressBar loadingBar;
  private boolean isPrepared;
  private MediaPlayer mediaPlayer;
  private boolean isShowRateDialog;
  private boolean isPauseForRate;
  private ViewGroup adContainer;
  private PluginManager pluginManager;
  private boolean conn = false;
  private boolean isDisableRes;
  private boolean isWebLaunch;
  private AppReceiver appReceiver;
  private FileDownloadListener downloadHDListener = new FileDownloadListener() {
    @Override
    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
      mProgressBar.setProgress(0);
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
      if (isFinishing()) {
        return;
      }
      LogUtil.i(tag, "progress  = " + soFarBytes + "/" + totalBytes);
      String downloading = getString(R.string.set_live_wp_downloading);
      int progress = (int) ((float) soFarBytes / (float) totalBytes * 100);
      mSetBtn.setText(String.format(downloading + "%d%% ", progress));
      mProgressBar.setProgress(progress);
    }

    @Override
    protected void completed(BaseDownloadTask task) {
      if (isFinishing()) {
        return;
      }
      mProgressBar.setProgress(100);
      uploadDownloadRes();
      LogUtil.i(tag, "completed");
      isHDVideoDownloading = false;
      final File tempFile = mItem.getMp4TempFile();
      if (tempFile == null || !tempFile.exists()) {
        mSetBtn.setText(R.string.set_live_wp_download);
        return;
      }
      if (tempFile.exists()) {
        //noinspection ResultOfMethodCallIgnored
        tempFile.renameTo(mHDVideoFile);
      }
      mSetBtn.setText(R.string.set_live_wp);
      if (mHDVideoFile.exists()) {
        playLocalFile(mHDVideoFile);
        ResourceBean saveBean = mItem;
        if (!saveBean.getLinkMp4().contains("temp")) {
          saveBean.setCoverURL(url + saveBean.getCoverURL());
          saveBean.setPreviewMp4(url + saveBean.getPreviewMp4());
          saveBean.setLinkMp4(url + saveBean.getLinkMp4());
        }
      }
      showToast(R.string.donwloaded_finished);
      if (!CtxUtil.isLwServiceRun(VwpResDetailActivity.this)) {
        onClick(mSetBtn);
      }
    }

    @Override
    protected void paused(BaseDownloadTask baseDownloadTask, int i, int i1) {
      LogUtil.i(tag, "paused");
    }

    @Override
    protected void error(BaseDownloadTask task, Throwable e) {
      if (e != null) {
        e.printStackTrace();
      }
      isHDVideoDownloading = false;
    }

    @Override
    protected void warn(BaseDownloadTask task) {
      LogUtil.i(tag, "warn");
    }
  };
  private View mViewVipTag;
  private View mViewDiy;

  public static void launch(Context context, ResourceBean bean) {
    Intent intent = new Intent(context, VwpResDetailActivity.class);
    intent.putExtra(Detail_res_key, bean);
    context.startActivity(intent);
  }

  public static void launch(Context context, ResourceBean bean, boolean isFavor,
      boolean isDisableRes) {
    Intent intent = new Intent(context, VwpResDetailActivity.class);
    intent.putExtra(Detail_res_key, bean);
    intent.putExtra(Detail_res_key_IS_Favor, isFavor);
    intent.putExtra(Detail_res_key_IS_Disable_Res, isDisableRes);
    context.startActivity(intent);
  }

  @Override
  public int getLayoutId() {
    return R.layout.detail_activity;
  }

  @Nullable
  @Override
  public Object newP() {
    return null;
  }

  @Override
  public void setUpView(@NonNull View rootView) {
    super.setUpView(rootView);
    adContainer = findViewById(R.id.video_ad_container);
    bgImgV = findViewById(R.id.background_imgv);
    mSetBtn = findViewById(R.id.set_wp_btn);
    mTitleView = findViewById(R.id.title_tv);
    mBackView = findViewById(R.id.back_imgv);
    mFavoriteView = findViewById(R.id.rl_favorite);
    mFavoriteTv = findViewById(R.id.favorite_tv);

    mCommentView = findViewById(R.id.rl_comment);
    mCommentTv = findViewById(R.id.comment_tv);
    mViewDiy = findViewById(R.id.btn_diy);
    mViewVipTag = findViewById(R.id.iv_u_vip);
    mShareView = findViewById(R.id.share_imgv);
    mVoiceView = findViewById(R.id.voice_imagev);
    tvVideoInfo = findViewById(R.id.tv_video_info);
    videoPlayer = findViewById(R.id.videoPlayer);
    mProgressBar = findViewById(R.id.down_progress);
    pauseButton = findViewById(R.id.change_button);
    loadingBar = findViewById(R.id.loading);
    userAvatar = findViewById(R.id.iv_avatar);
    llRight = findViewById(R.id.ll_right);
    ivFavorite = findViewById(R.id.favorite_iv);
    mViewDiy.setOnClickListener(this);
  }

  @Override
  public void initData(@Nullable Bundle savedInstanceState) {
    pluginManager = new PluginManager(this);
    pluginManager.bindPluginService(new PluginManager.OnBindCallBack() {
      @Override
      public void onServiceConnected(ComponentName componentName, Messenger messenger) {
        conn = true;
      }

      @Override
      public void onServiceDisconnected(ComponentName componentName) {
        conn = false;
      }
    });
    immersionBar = ImmersionBar.with(this)
        .titleBar(findViewById(R.id.detail_top_rl), false).transparentStatusBar();

    immersionBar.init();
    mItem = (ResourceBean) getIntent().getSerializableExtra(Detail_res_key);
    isFromFavor = getIntent().getBooleanExtra(Detail_res_key_IS_Favor, false);
    isDisableRes = getIntent().getBooleanExtra(Detail_res_key_IS_Disable_Res, false);
    isWebLaunch = getIntent().getBooleanExtra("isWebLaunch", false);
    if (mItem == null) {
      showToast(R.string.op_failed);
      finish();
      return;
    }
    mHDVideoFile = mItem.getMp4File();
    initData();
    initEvent();
    Run.getUiHandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        getCommentList();
        loadADRetry();
      }
    }, 1000);
    if (RateTLUtils.showRateDialog(this)) {
      isShowRateDialog = true;
    }
    userAvatar.setBorderColor(
        mItem.isVipUser() ? Color.parseColor("#E3BB73") : Color.parseColor("#FFFFFF"));
    mViewVipTag.setVisibility(mItem.isVipUser() ? View.VISIBLE : View.GONE);
  }

  private void showToast(int res) {
    getVDelegate().toastShort(getResources().getString(res));
  }

  private void showToast(String res) {
    getVDelegate().toastShort(res);
  }

  private void uploadDownloadRes() {
    if (!LoginContext.getInstance().isLogin()) {
      return;
    }
    List<String> ids = new ArrayList<>();
    ids.add(mItem.get_id());
    ServerApi.upDownloads(ids)
        .compose(DefaultScheduler.<BaseResult<String>>getDefaultTransformer())
        .subscribe(new BaseObserver<String>() {
          @Override
          public void onSuccess(@NonNull String s) {
            Log.e("logger", "上传下载记录" + s);
          }

          @Override
          public void onFailure(int code, @NonNull String message) {

          }
        });
  }

  private void loadADRetry() {
    if (LoginContext.getInstance().isVip()) {
      return;
    }
    ADTool.getADTool().getManager()
        .getNativeWrapper()
        .loadBannerView(VwpResDetailActivity.this, adContainer,
            new OnNativeListener<ADMetaData>() {
              @Override
              public void onSuccess(@NonNull ADMetaData adMetaData) {

              }

              @Override
              public void onFailure() {
                retryLoadAD();
              }
            });
  }

  private void retryLoadAD() {
    Run.getUiHandler()
        .postDelayed(new Runnable() {
          @Override
          public void run() {
            ADTool.getADTool().getManager()
                .getNativeWrapper()
                .loadBannerView(VwpResDetailActivity.this, adContainer);
          }
        }, 500);
  }

  private void initData() {
    regAppReceiver();
    pauseButton.setVisibility(View.VISIBLE);
    loadingBar.setVisibility(View.GONE);
    String duration = "00:00";
    try {
      duration = VideoTimeUtils
          .convertSecondsToTimeSimple(
              Long.parseLong(String.valueOf(mItem.getDuration())));
    } catch (Exception e) {
      e.printStackTrace();
    }
    tvVideoInfo.setText(String
        .format(getResources().getString(R.string.detail_video_info), duration,
            (int) mItem.getSize()));
    String imageurl = mItem.getCoverURL();
    LogUtil.i(tag, "imageurl = " + imageurl);
    Glide.with(this).asBitmap().load(imageurl).into(new SimpleTarget<Bitmap>() {
      @Override
      public void onResourceReady(@NonNull Bitmap bitmap,
          @Nullable Transition<? super Bitmap> transition) {
        Blurry.with(VwpResDetailActivity.this)
            .sampling(2)
            .animate(500)
            .from(bitmap)
            .into(bgImgV);
      }

      @Override
      public void onLoadFailed(@Nullable Drawable errorDrawable) {
        super.onLoadFailed(errorDrawable);
        bgImgV.setVisibility(View.GONE);
      }
    });
    Glide.with(this).load(mItem.getUserAvatar())
        .apply(new RequestOptions().placeholder(R.drawable.user_avatar_default))
        .into(userAvatar);
    boolean isLwOn = LwPrefUtil.isLwpVoiceOpened(this);
    if (PrefUtil.getBoolean(this, Detail_res_key_IS_FIRST_OPEN, true)) {
      showToast(R.string.detail_play_preview_notice);
    }
    PrefUtil.putBoolean(this, Detail_res_key_IS_FIRST_OPEN, false);
    mSetBtn.setText(
        mHDVideoFile.exists() ? R.string.set_live_wp : R.string.set_live_wp_download);
    //隐藏分享收藏评论
    llRight.setVisibility(isDisableRes ? View.GONE : View.VISIBLE);
    updateFavor();
    mTitleView.setText(mItem.getName());
    mTitleView.setSelected(true);
    if (mHDVideoFile.exists()) {
      mProgressBar.setProgress(100);
    } else {
      mProgressBar.setProgress(0);
    }
    mVoiceView.setChecked(LwPrefUtil.getPreviewVoice(this));
    videoPlayer.post(new Runnable() {
      @Override public void run() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(metric);
        int widthPixels = metric.widthPixels; // 宽度（PX）
        int heightPixels = metric.heightPixels; // 高度（PX）

        LogUtils.i(
            "changeVideoSize:"
                + ScreenUtils.getScreenWidth(
                context)
                + "/"
                + ScreenUtils.getScreenHeight(context)
                + ","
                + widthPixels
                + "/"
                + heightPixels);
        videoPlayer.setMeasure(widthPixels, heightPixels);
        videoPlayer.requestLayout();
      }
    });
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

  private void playLocalFile(File file) {
    if (videoPlayer == null) {
      return;
    }
    loadingBar.setVisibility(View.GONE);
    isPrepared = false;
    videoPlayer.setVideoPath(file.getAbsolutePath());
    videoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
      @Override
      public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
          @Override
          public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){
              videoPlayer.setBackgroundColor(Color.TRANSPARENT);
              bgImgV.setVisibility(View.GONE);
              loadingBar.setVisibility(View.GONE);
              pauseButton.setVisibility(View.GONE);
            }
            return true;
          }
        });
        isPrepared = true;
        mediaPlayer.setVideoScalingMode(
            MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        mediaPlayer.setLooping(true);
        VwpResDetailActivity.this.mediaPlayer = mediaPlayer;
        setVolume(LwPrefUtil.getPreviewVoice(VwpResDetailActivity.this) ? 1.0f : 0f);
        videoPlayer.start();
      }
    });
  }

  private void initEvent() {
    AnaUtil.anaClick(this, mItem);
    UmUtil.anaGeneralEventOp(this, EVENT_OP_LIVE_ONLINE_DETAIL_SHOW);
    mSetBtn.setOnClickListener(this);
    mBackView.setOnClickListener(this);
    mFavoriteView.setOnClickListener(this);
    mCommentView.setOnClickListener(this);
    mShareView.setOnClickListener(this);
    mVoiceView.setOnCheckedChangeListener(this);
    pauseButton.setOnClickListener(this);
    videoPlayer.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          if (videoPlayer.isPlaying()) {
            stopPlay();
          } else {
            startPlay();
          }
          return true;
        }
        return false;
      }
    });
    userAvatar.setOnClickListener(this);
  }

  private void startPlay() {
    pauseButton.setVisibility(View.GONE);
    if (isPrepared) {
      videoPlayer.start();
      loadingBar.setVisibility(View.GONE);
    } else if (mHDVideoFile.exists()) {
      playLocalFile(mHDVideoFile);
    } else {
      playOnNet();
    }
  }

  private HttpProxyCacheServer getProxy() {
    return VVApplication.getProxy(getApplicationContext());
  }

  private void playOnNet() {
    loadingBar.setVisibility(View.VISIBLE);
    isPrepared = false;
    HttpProxyCacheServer proxy = getProxy();
    String previewURl = mItem.getPreviewMp4();
    if (!previewURl.contains("temp")) {
      previewURl = url + previewURl;
    }
    LogUtil.i(tag, "previewURl = " + previewURl);
    String proxyUrl = proxy.getProxyUrl(previewURl);
    videoPlayer.setVideoPath(proxyUrl);
    videoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
      @Override
      public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
          @Override
          public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){
              videoPlayer.setBackgroundColor(Color.TRANSPARENT);
              bgImgV.setVisibility(View.GONE);
              loadingBar.setVisibility(View.GONE);
              pauseButton.setVisibility(View.GONE);
            }
            return true;
          }
        });
        mediaPlayer.setVideoScalingMode(
            MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        mediaPlayer.setLooping(true);
        VwpResDetailActivity.this.mediaPlayer = mediaPlayer;
        isPrepared = true;
        setVolume(LwPrefUtil.getPreviewVoice(VwpResDetailActivity.this) ? 1.0f : 0f);
        videoPlayer.start();
      }
    });
  }

  private void stopPlay() {
    pauseButton.setVisibility(View.VISIBLE);
    loadingBar.setVisibility(View.GONE);
    videoPlayer.pause();
  }

  private void setVolume(float volume) {
    if (mediaPlayer != null) {
      try {
        mediaPlayer.setVolume(volume, volume);
      } catch (IllegalStateException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void onClick(final View v) {

    if (v.getId() == R.id.share_imgv || v.getId() == R.id.set_wp_btn) {
      if (AcceptUserProtocol.needShowUserProtocol(v.getContext())) {
        AcceptUserProtocol.showUserProtocol(v.getContext(),
            new AcceptUserProtocol.OperationListener() {
              @Override
              public void accept() {
                onClick(v);
                LogUtil.i(tag, "accept");
              }

              @Override
              public void reject() {
                LogUtil.i(tag, "reject");
              }
            });
        return;
      }
    }

    switch (v.getId()) {
      case R.id.rl_favorite:
        toFavor();
        break;
      case R.id.rl_comment:
        CommentFragment commentFragment = CommentFragment
            .launch(this, mItem.get_id(), mUserInfo);
        commentFragment.setCallBack(this);
        break;
      case R.id.share_imgv:
        AnaUtil.anaShare(this, mItem);
        ShareFragment.launch(this, mItem);
        break;
      case R.id.set_wp_btn:
        String key = "set_wp_show_share_times";
        int setClickTimes = PrefUtil.getInt(this, key, 0);
        PrefUtil.putInt(this, key, setClickTimes + 1);
        if (setClickTimes != 0 && setClickTimes == getShowShareTimes()) {
          ShareFragment.launch(this, mItem);
          return;
        }
        setLw();
        break;
      case R.id.back_imgv:
        onBackPressed();
        break;
      case R.id.change_button:
        startPlay();
        break;
      case R.id.iv_avatar:
        if (TextUtils.isEmpty(mItem.getUserId()) || TextUtils
            .equals(mItem.getUserId(), "None")) {
          ToastUtil.showToast(this, "用户不存在");
          return;
        }
        System.err.println("userId---->" + mItem.getUserId());
        UserModel userModel = new UserModel();
        userModel.setNickname(mItem.getUserNickname());
        userModel.setImg(mItem.getUserAvatar());
        userModel.setUserId(mItem.getUserId());
        UserDetailActivity.start(this, userModel);
        break;
      case R.id.btn_diy:
        UmUtil.anaDIYPageView(context, "详情页");
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra("tab", "diy");
        startActivity(intent);
        finish();
        break;
      default:
        break;
    }
  }

  public int getShowShareTimes() {
    String times = PrefUtil.getString(this, Const.OnlineKey.ONLINE_PARAMS, "");
    if (TextUtils.isEmpty(times)) {
      times = "10000";
    }
    int showShareTimes = 100000;
    if (TextUtils.isDigitsOnly(times)) {
      showShareTimes = Integer.parseInt(times);
    }
    if (showShareTimes <= 0) {
      showShareTimes = 0;
    }
    return showShareTimes;
  }

  private void setLw() {
    if (!DeviceUtil.isSDCardMounted()) {
      showToast(R.string.op_sdcard_no_mount);
      return;
    }
    if (!mHDVideoFile.exists()) {
      mSetBtn.setText(R.string.set_live_wp_downloading);
      downloadHDVideo();
      UmUtil.anaOp(this, UmConst.UM_PREVIEW_ACTION_DOWNLOAD);
      UmUtil.anaGeneralEventOp(this, EVENT_OP_SET_DOWNLOAD);
      return;
    }
    UmUtil.anaGeneralEventOp(this, EVENT_OP_SET_LIVE);
    String mHDVideoFilePath = mHDVideoFile.getAbsolutePath();
    PrefUtil.putString(this, Const.PARAMS.LiveMp4Key, mHDVideoFilePath);
    AnaUtil.anaSet(this, mItem);


    //TODO AndroidQ 权限适配  暂时直接设置为壁纸
    LwVideoLiveWallpaper.setToWallPaper(context);


//    pluginManager.checkPluginVersion(5, new PluginManager.OnCheckListener() {
//      @Override
//      public void onShouldUpdate() {
//        if (isFinishing()) {
//          return;
//        }
//        new PluginDialog(VwpResDetailActivity.this)
//            .setTitle("检测到更新")
//            .showOrigin(true).show();
//      }
//
//      @Override
//      public void onSuccess() {
//        String currentMp4Path = PrefUtil
//            .getString(VwpResDetailActivity.this, Const.PARAMS.LiveMp4Key, "");
//        if (!conn) {
//          pluginManager.startSetWallpaper(currentMp4Path);
//        } else {
//          pluginManager.startPluginActivity(currentMp4Path);
//        }
//      }
//
//      @Override
//      public void onShouldInstall() {
//        if (isFinishing()) {
//          return;
//        }
//        new PluginDialog(VwpResDetailActivity.this).showOrigin(true).show();
//      }
//    });
  }

  private void downloadHDVideo() {
    if (isHDVideoDownloading) {
      showToast("下载中");
      return;
    }
    isHDVideoDownloading = true;
    AnaUtil.anaDownload(this, mItem);
    PermissionsUtils.checkStorage(context, new PermissionsUtils.OnPermissionBack() {
      @Override public void onResult(boolean success) {
        if (success) {
          String hdVideoUrl = mItem.getLinkMp4();
          if (TextUtils.isEmpty(hdVideoUrl)) {
            ToastUtil.showGeneralToast(context, "视频地址错误,无法下载");
            return;
          }
          if (!hdVideoUrl.contains("temp")) {
            hdVideoUrl = url + hdVideoUrl;
          }
          LogUtil.i(tag, "hdVideoUrl = " + hdVideoUrl);
          File tempHDFile = mItem.getMp4TempFile();
          FileDownloader.getImpl()
              .create(hdVideoUrl)
              .setPath(tempHDFile.getAbsolutePath())
              .setListener(downloadHDListener)
              .start();
        }
      }
    });
  }

  private void exitConfirm() {
    SweetAlertDialog dialog = new SweetAlertDialog(this,
        SweetAlertDialog.NORMAL_TYPE)
        .setTitleText(getResources().getString(R.string.dialog_title))
        .setContentText(
            getResources().getString(
                R.string.dialog_download_content))
        .setConfirmText(
            getResources().getString(R.string.dialog_cancel))
        .setConfirmClickListener(
            new SweetAlertDialog.OnSweetClickListener() {
              @Override
              public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();
              }
            })
        .setCancelText(getResources().getString(R.string.dialog_sure))
        .setCancelClickListener(
            new SweetAlertDialog.OnSweetClickListener() {
              @Override
              public void onClick(SweetAlertDialog sDialog) {
                FileDownloader.getImpl().pause(downloadHDListener);
                sDialog.dismissWithAnimation();
                finish();
              }
            });
    dialog.show();
  }

  @Override
  public void toSendComment(String content) {
    if (mUserInfo == null) {
      LoginDialog.launch(this, "登录后才可以评论哦~");
      return;
    }
    if (!mUserInfo.isActive()) {
      showToast("升级到新版本才可以评论哦～");
      return;
    }
    if (TextUtils.isEmpty(mUserInfo.getNickname())) {
      SetInfoDialog.launch(this, mUserInfo);
      return;
    }
    ServerApi.comment(mItem.get_id(), content)
        .compose(DefaultScheduler.<BaseResult>getDefaultTransformer())
        .subscribe(new SimpleObserver() {
          @Override
          public void onSuccess() {
            showToast("评论发送成功");
          }

          @Override
          public void onFailure(int code, String message) {
            LogUtil.e(tag, "评论失败" + ":" + message);
          }

          @Override
          public void onLogout() {
            super.onLogout();
            LoginDialog.launch(VwpResDetailActivity.this, "登录后才可以评论哦~");
          }
        });
  }

  @Override
  public void toPraiseComment(final CommentModel commentModel) {
    if (mUserInfo == null) {
      LoginDialog.launch(this, "登录后才可以点赞哦~");
      return;
    }
    if (!commentModel.isPraise(this)) {
      ServerApi.praise(commentModel.getCommentId())
          .compose(DefaultScheduler.<BaseResult>getDefaultTransformer())
          .subscribe(new SimpleObserver() {
            @Override
            public void onSuccess() {
              commentModel.setPraise(VwpResDetailActivity.this, true);
              showToast("点赞成功！");
            }

            @Override
            public void onFailure(int code, String message) {
              showToast("点赞失败，" + message);
            }

            @Override
            public void onLogout() {
              super.onLogout();
              LoginDialog.launch(VwpResDetailActivity.this, "登录后才可以点赞哦~");
            }
          });
    } else {
      showToast("已经点过赞了！");
    }
  }

  private void toFavor() {
    if (mUserInfo == null) {
      LoginDialog.launch(this, "登录后才可以收藏哦~");
      return;
    }
    boolean isSelect = ivFavorite.isSelected();
    final boolean favor = !isSelect;
    ServerApi.favor(mItem.get_id(), favor)
        .compose(DefaultScheduler.<BaseResult>getDefaultTransformer())
        .subscribe(new SimpleObserver() {
          @Override
          public void onSuccess() {
            showToast(favor ? "收藏成功" : "已取消收藏");
            isFavor = favor;
            int num = mItem.getFavnum();
            num = favor ? num + 1 : num - 1;
            mItem.setFavnum(num < 0 ? 0 : num);
            ivFavorite.setSelected(isFavor);
            mFavoriteTv.setText(String.valueOf(mItem.getFavnum()) + "");
          }

          @Override
          public void onFailure(int code, String message) {
            showToast("收藏失败，" + message);
          }

          @Override
          public void onLogout() {
            super.onLogout();
            LoginDialog.launch(VwpResDetailActivity.this, "登录后才可以收藏哦~");
          }
        });
  }

  private void getCommentList() {
    ServerApi.getCommentList(0, 10, mItem.get_id())
        .compose(
            DefaultScheduler.<BaseResult<CommentList<List<CommentModelListDTO.CommentDTO>>>>
                getDefaultTransformer())
        .subscribe(new BaseObserver<CommentList<List<CommentModelListDTO.CommentDTO>>>() {
          @Override
          public void onSuccess(
              @NonNull CommentList<List<CommentModelListDTO.CommentDTO>> listCommentList) {
            if (listCommentList != null) {
              mCommentTv.setText(String.valueOf(listCommentList.getCount()) + "");
              isFavor = listCommentList.isfavor();
              updateFavor();
            }
          }

          @Override
          public void onFailure(int code, @NonNull String message) {
            LogUtil.e(tag, code + ":" + message);
          }
        });
  }

  private void updateFavor() {
    ivFavorite.setSelected(isFromFavor || isFavor);
    mFavoriteTv.setText(String.valueOf(mItem.getFavnum()) + "");
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      if (isHDVideoDownloading) {
        exitConfirm();
        return true;
      }
    }
    return super.onKeyDown(keyCode, event);
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    setVolume(isChecked ? 1f : 0f);
    LwPrefUtil.setPreviewVoice(this, isChecked);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == LwVideoLiveWallpaper.REQUEST_CODE_SET_WALLPAPER
        && resultCode == RESULT_OK) {
      new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
        @Override
        public void run() {
          Context context = VwpResDetailActivity.this;
          if (CtxUtil.isLwServiceRun(context)) {
            CtxUtil.launchHome(context);
          }
        }
      }, 500);
    }
    if (requestCode == 666) {
      PAppUtils.launchHome(this);
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    stopPlay();
  }

  @Override
  protected void onResume() {
    MobclickAgent.onResume(this);
    super.onResume();
    if (isPauseForRate) {
      RateTLUtils.checkSuccess(this);
    }
    isPauseForRate = false;
    mUserInfo = LoginContext.getInstance().getUser();
  }

  @Override
  protected void onPause() {
    MobclickAgent.onPause(this);
    super.onPause();
    if (isShowRateDialog) {
      isPauseForRate = true;
    }
    isShowRateDialog = false;
  }

  @Override protected void onRestart() {
    super.onRestart();
    if (pluginManager != null && !conn) {
      pluginManager.bindPluginService(new PluginManager.OnBindCallBack() {
        @Override
        public void onServiceConnected(ComponentName componentName, Messenger messenger) {
          conn = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
          conn = false;
        }
      });
    }
  }

  @Override
  protected void onDestroy() {
    if (isWebLaunch) {
      startActivity(new Intent(context, SplashActivity.class));
    }
    super.onDestroy();
    if (immersionBar != null) {
      immersionBar.destroy();
    }
    if (videoPlayer != null) {
      videoPlayer.stopPlayback();
      videoPlayer = null;
    }
    pluginManager.unBindPluginService();
    FileDownloader.getImpl().pause(downloadHDListener);
    try {
      unregisterReceiver(appReceiver);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
