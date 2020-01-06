package com.novv.dzdesk.ui.activity.ae;

import android.arch.lifecycle.Lifecycle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ark.adkit.basics.handler.Run;
import com.ark.adkit.polymers.ttad.config.TTAdManagerHolder;
import com.ark.baseui.XAppCompatActivity;
import com.ark.dict.ConfigMapLoader;
import com.ark.uikit.webview.WebActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.gyf.barlibrary.ImmersionBar;
import com.novv.dzdesk.R;
import com.novv.dzdesk.http.BaseObserver;
import com.novv.dzdesk.http.DefaultScheduler;
import com.novv.dzdesk.http.ServerApi;
import com.novv.dzdesk.res.ae.AEConfig;
import com.novv.dzdesk.res.model.BaseResult;
import com.novv.dzdesk.res.model.UserModel;
import com.novv.dzdesk.res.model.VModel;
import com.novv.dzdesk.res.user.LoginContext;
import com.novv.dzdesk.ui.dialog.ProcessDialog;
import com.novv.dzdesk.ui.fragment.ae.ShowAddressLoadingFragment;
import com.novv.dzdesk.ui.presenter.PresentAePreview;
import com.novv.dzdesk.ui.view.player.ScaleType;
import com.novv.dzdesk.ui.view.player.TextureVideoView;
import com.novv.dzdesk.util.DiyEvent;
import com.novv.dzdesk.util.PermissionsUtils;
import com.novv.dzdesk.util.UmUtil;
import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.umeng.analytics.MobclickAgent;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.core.log.DeviceLog;

import java.io.File;
import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.blurry.Blurry;

public class AEPreviewActivity extends XAppCompatActivity<PresentAePreview> implements
        View.OnClickListener, IUnityAdsListener {

    private final LifecycleProvider<Lifecycle.Event> provider
            = AndroidLifecycle.createLifecycleProvider(this);


    private static final String TAG = "InitializeAdsScript";
    private static final String firstAdEnable = "ad_detail_dialog_enable";
    private static final String firstAdEnableimg = "ad_detail_dialog_img";
    private static final String firstAdEnablelinks = "ad_detail_dialog_links";

    private static final String secondAdEnable = "ad_detail_new_enable";
    private static final String secondAdEnableimg = "ad_detail_new_img";
    private static final String secondAdEnablelinks = "ad_detail_new_links";


    private static final String ringAdEnable = "ad_detail_new_enable";
    private static final String ringAdEnablelinks = "ad_detail_ring_links";


    private boolean hasAds = false;


    //Unity广告ID
    private String gameId = "3208397";
    private String placementId = "reward";


    private ImageView mCover;
    private ImageView mBgCover;
    private VModel mVModel;
    private TextView mBtn;
    private ImageView mChangeView;
    private ProgressBar mLoadingView;
    private View mVipDesc;
    private TextureVideoView mVideoView;
    private boolean isPrepared;
    private ProcessDialog progressDialog;
    private TextView tvDesc;
    private TextView try_free;
    private ShowAddressLoadingFragment fragment;
    private TTAdNative mTTAdNative;
    private TTRewardVideoAd mttRewardVideoAd;
    private TextView tv_remain;
    private RelativeLayout rl_try_free;

    @Override
    public int getLayoutId() {
        return R.layout.activity_ae_preview;
    }

    @Nullable
    @Override
    public PresentAePreview newP() {
        return new PresentAePreview();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // initAd();
        checkTryFreeSate();
    }

    private void initAd() {
//        UnityAds.initialize(AEPreviewActivity.this, gameId, this);
//        //step1:初始化sdk
        TTAdManager ttAdManager = TTAdManagerHolder.getInstance(this, "5007439");
        //step2:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        ttAdManager.requestPermissionIfNecessary(this);
//        //step3:创建TTAdNative对象,用于调用广告请求接口
        mTTAdNative = ttAdManager.createAdNative(getApplicationContext());
//
        loadAd("907439399", TTAdConstant.VERTICAL);
    }

    @Override
    public void setUpView(@NonNull View rootView) {
        super.setUpView(rootView);
        Log.d(TAG, "ready");
        View mBackView = findViewById(R.id.back_view);
        mCover = findViewById(R.id.ic_cover);
        mBgCover = findViewById(R.id.bg_cover);
        mBtn = findViewById(R.id.btn_action);
        mChangeView = findViewById(R.id.change_button);
        mLoadingView = findViewById(R.id.loading);
        //视频界面
        mVideoView = findViewById(R.id.vv_videoView);
        mVipDesc = findViewById(R.id.vip_desc);
        tvDesc = findViewById(R.id.tv_preview_desc);


        rl_try_free = (RelativeLayout) findViewById(R.id.rl_try_free);

        tv_remain = (TextView) findViewById(R.id.tv_remain);
        try_free = findViewById(R.id.btn_try_free);

        try_free.setOnClickListener(this);
        mBackView.setOnClickListener(this);
        mBtn.setOnClickListener(this);
        mChangeView.setOnClickListener(this);
    }

    private void checkTryFreeSate() {
        Disposable subscribe = Flowable.interval(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(provider.bindToLifecycle())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        if (UnityAds.isReady(placementId)) {
                            hasAds = true;
                        } else {
                            hasAds = false;
                        }
                    }
                });
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        ImmersionBar.with(this)
                .titleBar(findViewById(R.id.ll_top), false)
                .transparentStatusBar()
                .init();
        mVModel = null;
        try {
            mVModel = (VModel) getIntent().getSerializableExtra(VModel.class.getSimpleName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mVModel == null) {
            getVDelegate().toastShort("无效模板");
            finish();
            return;
        }
        DiyEvent.onEvent(mVModel.id, DiyEvent.TYPE.click);
        tvDesc.setText(mVModel.desc);

      //  initialAd();
        refreshBtn();
        Glide.with(this).asBitmap().load(mVModel.img)
                .apply(new RequestOptions().error(R.drawable.placeholder).skipMemoryCache(true))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap,
                                                @Nullable Transition<? super Bitmap> transition) {
                        mCover.setImageBitmap(bitmap);
                        Blurry.with(AEPreviewActivity.this)
                                .sampling(2)
                                .animate(500)
                                .from(bitmap)
                                .into(mBgCover);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        Glide.with(AEPreviewActivity.this)
                                .load(mVModel.img)
                                .apply(new RequestOptions().error(R.drawable.placeholder)
                                        .skipMemoryCache(true))
                                .into(mCover);
                    }
                });
        mCover.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                                mCover.getWidth(), mCover.getHeight());
                        lp.gravity = Gravity.CENTER;
                        mVideoView.setLayoutParams(lp);
                        mCover.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
        mVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    mChangeView.setVisibility(View.VISIBLE);
                } else if (isPrepared) {
                    mVideoView.resume();
                    mChangeView.setVisibility(View.GONE);
                }
            }
        });
        mVideoView.setLoop(false);
        mVideoView.setMediaPlayerCallback(new TextureVideoView.MediaPlayerCallback() {
            @Override
            public void onPrepared(MediaPlayer var1) {
                Run.getUiHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isPrepared = true;
                        mChangeView.setVisibility(View.GONE);
                        mCover.setVisibility(View.GONE);
                        mLoadingView.setVisibility(View.GONE);
                    }
                }, 300);
            }

            @Override
            public void onCompletion(MediaPlayer var1) {
                mCover.setVisibility(View.VISIBLE);
                mChangeView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onBufferingUpdate(MediaPlayer var1, int var2) {

            }

            @Override
            public void onVideoSizeChanged(MediaPlayer var1, int var2, int var3) {

            }

            @Override
            public boolean onInfo(MediaPlayer var1, int var2, int var3) {
                return false;
            }

            @Override
            public boolean onError(MediaPlayer var1, int var2, int var3) {
                mChangeView.setVisibility(View.VISIBLE);
                mCover.setVisibility(View.VISIBLE);
                mLoadingView.setVisibility(View.GONE);
                getVDelegate().toastShort("播放异常，请重试");
                return true;
            }
        });
    }


    //添加的三个广告
    private void initialAd() {
        ImageView iv_top_ad = findViewById(R.id.iv_top_ad);
        ImageView iv_center_ad = findViewById(R.id.iv_center_ad);
        Button bt_set_ring = findViewById(R.id.bt_set_ring);


        //获取KV字段 联合判断是否弹出隐私确认框
        final Map<String, String> dataMap = ConfigMapLoader.getInstance().getResponseMap();


        //1 判断开关 2加载图片  3 设置web跳转
        if (dataMap.containsKey(firstAdEnable)) {
            String firstEnable = dataMap.get(firstAdEnable);
            int i = Integer.parseInt(firstEnable);
            iv_top_ad.setVisibility(i == 0 ? View.GONE : View.VISIBLE);
            if (i != 0) {
                //加载图片
                Glide.with(this)
                        .load(dataMap.get(firstAdEnableimg))
                        .placeholder(R.drawable.ic_launcher_logo)
                        .into(iv_top_ad);
                iv_top_ad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = dataMap.get(firstAdEnablelinks);
                        if (url != null) {
                            UmUtil.anaDIYDIALOG(AEPreviewActivity.this);

                            WebActivity.launch(AEPreviewActivity.this, url);
                        }
                    }
                });
            }
        }

        if (dataMap.containsKey(secondAdEnable)) {
            String secondEnable = dataMap.get(secondAdEnable);
            int i = Integer.parseInt(secondEnable);
            iv_center_ad.setVisibility(i == 0 ? View.GONE : View.VISIBLE);
            if (i != 0) {
                //加载图片
                Glide.with(this)
                        .load(dataMap.get(secondAdEnableimg))
                        .placeholder(R.drawable.ic_launcher_logo)
                        .into(iv_center_ad);
                iv_center_ad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = dataMap.get(secondAdEnablelinks);
                        if (url != null) {
                            UmUtil.anaDIYNEW(AEPreviewActivity.this);
                            WebActivity.launch(AEPreviewActivity.this, url);
                        }
                    }
                });
            }
        }

        if (dataMap.containsKey(ringAdEnable)) {
            String ringEnable = dataMap.get(ringAdEnable);
            int i = Integer.parseInt(ringEnable);
            bt_set_ring.setVisibility(i == 0 ? View.GONE : View.VISIBLE);
            if (i != 0) {
                bt_set_ring.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = dataMap.get(ringAdEnablelinks);
                        if (url != null) {
                            UmUtil.anaDIYRING(AEPreviewActivity.this);
                            WebActivity.launch(AEPreviewActivity.this, url);
                        }
                    }
                });
            }
        }
    }

    private void refreshBtn() {
        mVipDesc.setVisibility(View.INVISIBLE);
        mBtn.setText("立即制作");
//        if (!LoginContext.getInstance().isLogin()) {
//            mVipDesc.setVisibility(View.VISIBLE);
//            mBtn.setText("登录后制作");
//            return;
//        }
//        LoginContext.getInstance().checkAEState(this, mVModel, new AEStateCallback() {
//            @Override
//            public void onFree() {
//                mBtn.setText("立即制作");
//            }
//
//            @Override
//            public void onLimitFree() {
//                mBtn.setText("立即制作");
//            }
//
//            @Override
//            public void onVipFree(boolean isVip, boolean isLogin, int count) {
//                if (!isVip) {
//                    mVipDesc.setVisibility(View.VISIBLE);
//                    rl_try_free.setVisibility(View.VISIBLE);
//                }
//                mBtn.setText(isVip ? "立即制作" : "开通VIP");
//            }
//
//            @Override
//            public void onPriceNeed() {
//                mBtn.setText("价格:" + ((float) mVModel.price / 100f) + "元");
//            }
//
//        });
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        refreshBtn();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back_view:
                finish();
                break;
            case R.id.btn_action:
                MobclickAgent.onEvent(AEPreviewActivity.this,UmCount.MakeClick);
                UmUtil.anaDIYClickMake(context, mVModel.id);
                checkEditState();
                break;
            case R.id.change_button:
                checkPreviewState();
                UmUtil.anaDIYClickPreview(context, mVModel.id);
                break;
        }
        UmUtil.anaDIYClick(context, mVModel.id);
    }

    /**
     * 检查预览状态
     */
    private void checkPreviewState() {
        if (isPrepared) {
            mVideoView.resume();
            mChangeView.setVisibility(View.GONE);
            mLoadingView.setVisibility(View.GONE);
        } else {
            mChangeView.setVisibility(View.GONE);
            mLoadingView.setVisibility(View.VISIBLE);
            progressDialog = new ProcessDialog();
            progressDialog.setMsg("资源准备中...").show(this);
            PermissionsUtils.checkStorage(this, new PermissionsUtils.OnPermissionBack() {
                @Override
                public void onResult(boolean success) {
                    if (success) {
                        getP().prepareRes(mVModel);
                    } else {
                        mChangeView.setVisibility(View.VISIBLE);
                        mLoadingView.setVisibility(View.GONE);
                        hideProgressDialog();
                    }
                }
            });
        }
    }

    /**
     * 检查编辑状态
     */
    private void checkEditState() {
        progressDialog = new ProcessDialog();
        progressDialog.setMsg("资源准备中...").show(this);
        mVideoView.stop();
        mChangeView.setVisibility(View.VISIBLE);
        DiyEvent.onEvent(mVModel.id, DiyEvent.TYPE.use);
        PermissionsUtils.checkStorage(this, new PermissionsUtils.OnPermissionBack() {
            @Override
            public void onResult(boolean success) {
                if (success) {
                    getP().prepareResToEdit(mVModel);
                } else {
                    mLoadingView.setVisibility(View.GONE);
                    hideProgressDialog();
                }
            }
        });
    }

    /**
     * 编辑配置准备就绪
     *
     * @param aeConfig AE配置
     */
    public void onEditConfigPrepared(final AEConfig aeConfig) {
        mLoadingView.setVisibility(View.GONE);
        //检查编辑目录
        if (!TextUtils.isEmpty(aeConfig.unZipDir) && new File(aeConfig.unZipDir).exists()) {
            Intent intent = new Intent(context, AEDialogActivity.class);
            intent.putExtra(AEConfig.class.getSimpleName(), aeConfig);
            startActivity(intent);
        } else {
            isPrepared = false;
            new File(aeConfig.zipFilePath).delete();
            getVDelegate().toastShort("资源不完整无法制作,请重试");
        }
    }

    /**
     * 预览配置准备就绪
     *
     * @param aeConfig AE配置
     */
    public void onResPrepared(final AEConfig aeConfig) {
        if (!TextUtils.isEmpty(aeConfig.previewPath) || new File(aeConfig.previewPath)
                .exists()) {
            mVideoView.stop();
            mVideoView.setScaleType(ScaleType.FIT_CENTER);
            mVideoView.setVideoPath(aeConfig.previewPath);
            if (!mVideoView.isPlaying()) {
                mVideoView.start();
            } else {
                mVideoView.resume();
            }
            DiyEvent.onEvent(mVModel.id, DiyEvent.TYPE.play);
        } else {
            isPrepared = false;
            new File(aeConfig.zipFilePath).delete();
            getVDelegate().toastShort("资源不完整无法预览,请重试");
        }
    }

    public void showProgress(int current) {
        if (progressDialog != null) {
            progressDialog.setProgress(current);
        }
    }

    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.release();
            progressDialog = null;
        }
    }

    @Override
    protected void onResume() {
        MobclickAgent.onResume(this);
        super.onResume();
        //refreshTryCount();

    }

    private void refreshTryCount() {
        //拉取个人信息
        ServerApi.getUserInfo()
                .compose(DefaultScheduler
                        .<BaseResult<UserModel>>getDefaultTransformer())
                .subscribe(new BaseObserver<UserModel>() {
                    @Override
                    public void onSuccess(@NonNull UserModel userModel) {
                        LoginContext.getInstance().login(userModel);

                        if (!userModel.isVip()) {
                            if (userModel.getTrialNum() == 0) {
                                //TODO
                                //修改免费次数
                                rl_try_free.setVisibility(View.GONE);
                            } else {
                                tv_remain.setText(MessageFormat.format("剩余{0}次", userModel.getTrialNum()));
                            }
                        } else {
                            rl_try_free.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(int code, @NonNull String message) {
                    }
                });
    }

    @Override
    protected void onPause() {
        MobclickAgent.onPause(this);
        super.onPause();
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UnityAds.removeListener(this);
        ImmersionBar.with(this).destroy();
        if (mVideoView != null) {
            mVideoView.stop();
        }
    }

    @Override
    public void onUnityAdsReady(String s) {
        Log.i(TAG, "==========onUnityAdsReady===" + s);
    }

    @Override
    public void onUnityAdsStart(String s) {
        DeviceLog.debug("onUnityAdsStart: " + s);
        Log.i(TAG, "==========onUnityAdsStart===" + s);
    }

    @Override
    public void onUnityAdsFinish(String zoneId, UnityAds.FinishState result) {
        DeviceLog.debug("onUnityAdsFinish: " + zoneId + " - " + result);
        Log.i(TAG, "==========onUnityAdsFinish===" + zoneId + "===result===" + result);

        ServerApi.setTrialUse()
                .compose(DefaultScheduler.<BaseResult>getDefaultTransformer())
                .subscribe(new DefaultObserver<BaseResult>() {
                    @Override
                    public void onNext(BaseResult baseResult) {
                        if (baseResult.getCode() == 0) {
                            refreshTryCount();
                            checkEditState();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Throwable e1 = e;
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public void onUnityAdsError(UnityAds.UnityAdsError error, String message) {
        DeviceLog.debug("onUnityAdsError: " + error + " - " + message);
    }

    private void loadAd(String codeId, int orientation) {
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .setRewardName("金币") //奖励的名称
                .setRewardAmount(3)  //奖励的数量
                .setUserID("1992")//用户id,必传参数
                .setMediaExtra("media_extra") //附加参数，可选
                .setOrientation(orientation) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .build();
        //step5:请求广告
        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {

            }

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            @Override
            public void onRewardVideoCached() {

            }

            //视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验。
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                mttRewardVideoAd = ad;
                mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                    }

                    @Override
                    public void onAdVideoBarClick() {
                    }

                    @Override
                    public void onAdClose() {
                        ServerApi.setTrialUse()
                                .compose(DefaultScheduler.<BaseResult>getDefaultTransformer())
                                .subscribe(new DefaultObserver<BaseResult>() {
                                    @Override
                                    public void onNext(BaseResult baseResult) {
                                        if (baseResult.getCode() == 0) {
                                            refreshTryCount();
                                            checkEditState();
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Throwable e1 = e;
                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
                    }

                    //视频播放完成回调
                    @Override
                    public void onVideoComplete() {

                    }

                    @Override
                    public void onVideoError() {
                    }

                    //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称
                    @Override
                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {
                    }

                });
            }
        });
    }
}
