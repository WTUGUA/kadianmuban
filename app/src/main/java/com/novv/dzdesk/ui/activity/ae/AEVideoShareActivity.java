package com.novv.dzdesk.ui.activity.ae;

import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adesk.tool.plugin.PluginManager;
import com.ark.adkit.basics.handler.Action;
import com.ark.adkit.basics.handler.Run;
import com.ark.adkit.basics.utils.LogUtils;
import com.ark.baseui.XAppCompatActivity;
import com.ark.dict.ConfigMapLoader;
import com.gyf.barlibrary.ImmersionBar;
import com.novv.dzdesk.Const;
import com.novv.dzdesk.R;
import com.novv.dzdesk.ffmpeg.EpDraw;
import com.novv.dzdesk.ffmpeg.EpEditor;
import com.novv.dzdesk.ffmpeg.EpVideo;
import com.novv.dzdesk.live.LwVideoLiveWallpaper;
import com.novv.dzdesk.live.PrefUtil;
import com.novv.dzdesk.res.user.LoginContext;
import com.novv.dzdesk.ui.dialog.ProcessDialog;
import com.novv.dzdesk.ui.dialog.VideoShareDialog;
import com.novv.dzdesk.ui.dialog.haopingDialog;
import com.novv.dzdesk.ui.view.player.ScaleType;
import com.novv.dzdesk.ui.view.player.TextureVideoView;
import com.novv.dzdesk.util.DiyEvent;
import com.novv.dzdesk.util.FileUtil;
import com.novv.dzdesk.util.JoeVideo;
import com.novv.dzdesk.util.ToastUtil;
import com.novv.dzdesk.util.UmUtil;
import com.qiniu.pili.droid.shortvideo.PLMediaFile;
import com.qiniu.pili.droid.shortvideo.PLVideoFrame;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.microshow.rxffmpeg.RxFFmpegInvoke;
import jp.wasabeef.blurry.Blurry;

import static com.novv.dzdesk.ui.activity.ae.onClickButtonListener.BUTTON_TYPE_A;

public class AEVideoShareActivity extends XAppCompatActivity implements View.OnClickListener {

    private static final int TYPE_DOWNLOAD = 0;
    private static final int TYPE_SHARE = 1;
    private ImageView mCover;
    private ImageView mBgCover;
    private TextView btnSave;
    private ImageView mChangeView;
    private ProgressBar mLoadingView;
    private TextureVideoView mVideoView;
    private String mp4Path, mp4Id;
    private boolean conn = false;
    private PluginManager pluginManager;
    private ProcessDialog progressDialog;
    private File mShareFile;
    private Date date;
    private boolean checkSucces;
    private  ImageView setting;



    //获取好评弹窗开关
    public static int getenable() {
        String rate_button_enable = ConfigMapLoader.getInstance().getResponseMap().get("rate_button_enable");

        if (rate_button_enable == null) {
            return 0;
        }
        return Integer.parseInt(rate_button_enable);
    }

    //获取弹窗最大次数
    public static int getmaxshow() {
        String rate_max_show = ConfigMapLoader.getInstance().getResponseMap().get("rate_max_show");

        if (rate_max_show == null) {
            return 5;
        }
        return Integer.parseInt(rate_max_show);
    }

    //获取弹窗间隔
    public static int getsplitcount() {
        String rate_split_count = ConfigMapLoader.getInstance().getResponseMap().get("rate_split_count");

        if (rate_split_count == null) {
            return 4;
        }
        return Integer.parseInt(rate_split_count);
    }

    //共享参数
    //创建共享数据存入次数
    //弹窗次数
    public int getSharedPreference(String key) {
        SharedPreferences sp = getSharedPreferences("showtime", Context.MODE_PRIVATE);
        int str = sp.getInt(key, 0);
        return str;
    }

    public void setSharedPreference(String key, int values) {
        SharedPreferences sp = getSharedPreferences("showtime", Context.MODE_PRIVATE);
        SharedPreferences.Editor et = sp.edit();
        et.putInt(key, values);
        et.commit();
    }

    //弹窗间隔
    public int getShowCount(String key) {
        SharedPreferences sp = getSharedPreferences("showcount", Context.MODE_PRIVATE);
        int str = sp.getInt(key, 0);
        return str;
    }

    public void setShowCount(String key, int values) {
        SharedPreferences sp = getSharedPreferences("showcount", Context.MODE_PRIVATE);
        SharedPreferences.Editor et = sp.edit();
        et.putInt(key, values);
        et.commit();
    }

    //是否评论
    public int getPingLun(String key) {
        SharedPreferences sp = getSharedPreferences("pinglun", Context.MODE_PRIVATE);
        int str = sp.getInt(key, 0);
        return str;
    }

    public void setPingLun(String key, int values) {
        SharedPreferences sp = getSharedPreferences("pinglun", Context.MODE_PRIVATE);
        SharedPreferences.Editor et = sp.edit();
        et.putInt(key, values);
        et.commit();
    }

    public void setTime(String key, String values) {
        SharedPreferences sp = getSharedPreferences("Time", Context.MODE_PRIVATE);
        SharedPreferences.Editor et = sp.edit();
        et.putString(key, values);
        et.commit();
    }

    public String getTime(String key) {
        SharedPreferences sp = getSharedPreferences("Time", Context.MODE_PRIVATE);
        String str = sp.getString(key, "null");
        return str;
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_aevideo_share;
    }

    @Nullable
    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void setUpView(@NonNull View rootView) {
        super.setUpView(rootView);
        View mBackView = rootView.findViewById(R.id.back_view);
        mCover = rootView.findViewById(R.id.ic_cover);
        mBgCover = rootView.findViewById(R.id.bg_cover);
        TextView btnShare = rootView.findViewById(R.id.btn_share);
        btnSave = rootView.findViewById(R.id.btn_save);
        TextView btnSet = rootView.findViewById(R.id.btn_set_wp);
        mChangeView = rootView.findViewById(R.id.change_button);
        mLoadingView = rootView.findViewById(R.id.loading);
        mVideoView = rootView.findViewById(R.id.vv_videoView);
        mBackView.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnSet.setOnClickListener(this);
        mChangeView.setOnClickListener(this);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

        ImmersionBar.with(this)
                .titleBar(findViewById(R.id.ll_top), false)
                .transparentStatusBar()
                .init();
        mp4Path = getIntent().getStringExtra("mp4Path");
        mp4Id = getIntent().getStringExtra("mp4Id");
        mShareFile = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
                new File(mp4Path).getName());
        showCover();
        initPlayer();
        boolean isFromMake = getIntent().getBooleanExtra("isFromMake", false);
        if (isFromMake) {
            btnSave.setVisibility(View.GONE);
        } else {
            btnSave.setText("保存本地");
        }
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
        //加入好评弹窗
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initAd();
        int enable = getenable();
        int maxshow = getmaxshow();
        int splitcount = getsplitcount();
        int pinglun = getPingLun("pinglun");
        int showtime = getSharedPreference("showtime");
        int showcount = getShowCount("showcount");
        showcount += 1;
        setShowCount("showcount", showcount);
        if (enable == 1) {
            if (pinglun == 0) {
                if (showtime < maxshow) {
                    if (showcount % splitcount == 0 || showcount == 1) {
                        MobclickAgent.onEvent(AEVideoShareActivity.this,UmCount.RateShow);
                        // Toast.makeText(this, "弹出弹窗", Toast.LENGTH_SHORT);
                        showDialog();
                        showtime += 1;
                        setSharedPreference("showtime", showtime);
                    }
                }
            }
        }
        setting=findViewById(R.id.btn_settings);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AEVideoShareActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }



    public void showDialog() {
        //实例化自定义对话框
        final haopingDialog mdialog = new haopingDialog(this);
        //对话框中退出按钮事件
        mdialog.setOnExitListener(new onClickButtonListener(BUTTON_TYPE_A) {
            @Override
            public void onClick(View v) {
                //如果对话框处于显示状态
                if (mdialog.isShowing()) {
                    //关闭对话框
                    try {
                        MobclickAgent.onEvent(AEVideoShareActivity.this,UmCount.RateAsure);
                        //离开页面时间
                        date = new Date(System.currentTimeMillis());
                        //记录离开页面时间
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String time = simpleDateFormat.format(date);
                        setTime("Time", time);
                        checkSucces=true;
                        Uri uri = Uri.parse("market://details?id=" + getPackageName());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivityForResult(intent, 1);
                    } catch (Exception e) {
                        Toast.makeText(AEVideoShareActivity.this, "您的手机没有安装应用市场", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    //   setPingLun("pinglun",1);
                    mdialog.dismiss();
                }
            }
        });
        //对话框中取消按钮事件
        mdialog.setOnCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mdialog != null && mdialog.isShowing()) {
                    mdialog.dismiss();
                }
            }
        });
        mdialog.show();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
//        if(requestCode==1) {
//            Toast.makeText(AEVideoShareActivity.this, "评论完成", Toast.LENGTH_SHORT).show();
//            String time = getTime("Time");
//            if (time.equals("null") == false) {
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                /* 当前系统时间*/
//                Date date = new Date(System.currentTimeMillis());
//                String time1 = simpleDateFormat.format(date);
//
//                /*计算时间差*/
//                Date begin = null;
//                Date end = null;
//                try {
//                    begin = simpleDateFormat.parse(time);
//                    end = simpleDateFormat.parse(time1);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                long diff = end.getTime() - begin.getTime();
//                /*计算秒*/
//                long seconds = (diff % (1000 * 60)) / 1000;
//                System.out.println("日期时间差==" + seconds);
//                if (seconds > 5) {
//                    setPingLun("pinglun", 1);
//                }
//            }
//        }
    }

    /**
     * 预览图及背景图
     */
    private void showCover() {
        PLMediaFile plMediaFile = new PLMediaFile(mp4Path);
        int count = plMediaFile.getVideoFrameCount(true);
        if (count >= 0) {
            PLVideoFrame frame = plMediaFile.getVideoFrameByIndex(0, true);
            Bitmap bitmap;
            if (frame != null && (bitmap = frame.toBitmap()) != null) {
                mCover.setImageBitmap(bitmap);
                Blurry.with(context)
                        .sampling(2)
                        .animate(500)
                        .from(bitmap)
                        .into(mBgCover);
            }
        }
    }

    /**
     * 初始化播放器
     */
    private void initPlayer() {
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
                } else {
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
        if (!TextUtils.isEmpty(mp4Path) || new File(mp4Path)
                .exists()) {
            mVideoView.stop();
            mVideoView.setScaleType(ScaleType.FIT_CENTER);
            mVideoView.setVideoPath(mp4Path);
            if (!mVideoView.isPlaying()) {
                mVideoView.start();
            } else {
                mVideoView.resume();
            }
        } else {
            getVDelegate().toastShort("无法播放");
            finish();
        }
    }

    //判断点击事件
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back_view) {
            checkSucces=false;
            finish();
        } else if (id == R.id.btn_share) {
            checkSucces=false;
            //DiyEvent.onEvent(mp4Id, DiyEvent.TYPE.share);
            MobclickAgent.onEvent(AEVideoShareActivity.this,UmCount.ShareClick);
            pausePlay();
            if (mShareFile != null && mShareFile.exists()) {
                new VideoShareDialog(context).showShare(mShareFile.getAbsolutePath());
            } else {
                saveToLocal(TYPE_SHARE);
            }
        } else if (id == R.id.change_button) {
            mVideoView.resume();
            mChangeView.setVisibility(View.GONE);
            mLoadingView.setVisibility(View.GONE);
        } else if (id == R.id.btn_set_wp) {
            pausePlay();
            setWp(mp4Path);
        } else if (id == R.id.btn_save) {
            pausePlay();
            saveToLocal(TYPE_DOWNLOAD);
        }
    }

    private void pausePlay() {
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
            mChangeView.setVisibility(View.VISIBLE);
            mLoadingView.setVisibility(View.GONE);
        }
    }

    private void setWp(String path) {
        MobclickAgent.onEvent(AEVideoShareActivity.this,UmCount.WallpaperClick);
        UmUtil.anaDIYClickSet(context, mp4Id);
        DiyEvent.onEvent(mp4Id, DiyEvent.TYPE.set);
        PrefUtil.putString(context, Const.PARAMS.LiveMp4Key, path);

        //TODO AndroidQ 权限适配  暂时直接设置为壁纸
        LwVideoLiveWallpaper.setToWallPaper(context);
//    pluginManager.checkPluginVersion(5, new PluginManager.OnCheckListener() {
//      @Override
//      public void onShouldUpdate() {
//        if (isFinishing()) {
//          return;
//        }
//        new PluginDialog(context)
//            .setTitle("检测到更新")
//            .showOrigin(true).show();
//      }
//
//      @Override
//      public void onSuccess() {
//        String currentMp4Path = PrefUtil
//            .getString(context, Const.PARAMS.LiveMp4Key, "");
//        if (!conn) {
//          Class<?> clazz = pluginManager.getClass();
//          //获得私有的成员属性
//          Field[] fields = clazz.getDeclaredFields();
//          if(fields.length > 0){
//            for(Field f : fields){
//              f.setAccessible(true);
//              //反射获取Messenger是否存在
//              if (f.getType().getName().equals(android.os.Messenger.class.getName())){
//                try {
//                  if (f.get(pluginManager) ==null){
//                    pluginManager.startPluginActivity(currentMp4Path);
//                  }else {
//                    pluginManager.startSetWallpaper(currentMp4Path);
//                  }
//                  break;
//                } catch (IllegalAccessException e) {
//                  e.printStackTrace();
//                }
//              }
//            }
//          }
//        } else {
//          pluginManager.startPluginActivity(currentMp4Path);
//        }
//
//      }
//
//      @Override
//      public void onShouldInstall() {
//        if (isFinishing()) {
//          return;
//        }
//        new PluginDialog(context).showOrigin(true).show();
//      }
//    });
    }

    //保存到本地
    private void saveToLocal(final int type) {
        checkSucces=false;
        MobclickAgent.onEvent(AEVideoShareActivity.this,UmCount.SaveClick);
        progressDialog = new ProcessDialog();
        progressDialog.setMsg("正在保存...").show(context);
        UmUtil.anaDIYClickSave(context, mp4Id);
        DiyEvent.onEvent(mp4Id, DiyEvent.TYPE.save);
        PLMediaFile plMediaFile = new PLMediaFile(mp4Path);
        int videoWidth = plMediaFile.getVideoWidth();
        int videoHeight = plMediaFile.getVideoHeight();
        EpVideo epVideo = new EpVideo(mp4Path);
        EpEditor.OutputOption outputOption = new EpEditor.OutputOption(
                mShareFile.getAbsolutePath());
        outputOption.frameRate = plMediaFile.getVideoFrameRate();
        boolean addWatermark = !LoginContext.getInstance().isVip();
//        if (addWatermark) {
//            File watermark = getWatermark();
//            Utils.copyFileFromAssets(context, "watermark.png", watermark.getAbsolutePath());
//            int owidth = 165;
//            int oheight = 35;
//            int width = videoWidth / 3;
//            int height = width * oheight / owidth;
//            EpDraw epDraw = new EpDraw(watermark.getAbsolutePath(), videoWidth - width - 10,
//                    videoHeight - height - 10,
//                    width,
//                    height, false);
//            epVideo.addDraw(epDraw);
//        }
//        if (addWatermark) {
//            try {
//                EpEditor.exec(epVideo, outputOption, new EditImpl(type));
//            } catch (Exception e) {
//                Toast.makeText(getApplicationContext(), "保存失败", Toast.LENGTH_SHORT).show();
//            } catch (Error error) {
//                Toast.makeText(getApplicationContext(), "保存失败", Toast.LENGTH_SHORT).show();
//            }
//        } else {
            copyVideo(type);
//        }
    }

    private void copyVideo(final int type) {
        Run.onBackground(new Action() {
            @Override
            public void call() {
                boolean success = FileUtil.copyFile(new File(mp4Path), mShareFile);
                if (!success) {
                    ToastUtil.showToast(context, "保存失败");
                    return;
                }
                Run.onUiSync(new Action() {
                    @Override
                    public void call() {
                        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
                        animator.setDuration(1500);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int animatedValue = (int) animation.getAnimatedValue();
                                showProgress(animatedValue);
                                if (animatedValue == 100) {
                                    onSaveSuccess(type);
                                }
                            }
                        });
                        animator.start();
                    }
                });
            }
        });
    }

    private void onSaveSuccess(int type) {
        hideProgressDialog();
        MediaScannerConnection
                .scanFile(context, new String[]{mShareFile.getAbsolutePath()},
                        new String[]{"video/*"},
                        new MediaScannerConnection.OnScanCompletedListener() {

                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                                LogUtils.i("刷新成功:path->" + path + ",uri->" + uri
                                        .toString());
                            }
                        });
        if (type == TYPE_SHARE) {
            new VideoShareDialog(context).showShare(mShareFile.getAbsolutePath());
        } else if (type == TYPE_DOWNLOAD) {
            String msg = "视频已经保存到本地";
            if (LoginContext.getInstance().isVip()) {
                msg = "视频已经保存到本地";
            }
            getVDelegate().toastLong(msg);
        }
    }

    private File getWatermark() {
        File dir = new File(Environment.getExternalStorageDirectory(), "avideoshare/ae/.watermark");
        dir.mkdirs();
        return new File(dir, "watermark.png");
    }

    private void showProgress(int p) {
        if (progressDialog != null) {
            progressDialog.setProgress(p);
        }
    }

    private void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.release();
            progressDialog = null;
        }
    }

    @Override
    protected void onResume() {
        MobclickAgent.onResume(this);
        super.onResume();
        if(checkSucces==true) {
            checkSucces=false;
            String time = getTime("Time");
            if (time.equals("null") == false) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                /* 当前系统时间*/
                Date date = new Date(System.currentTimeMillis());
                String time1 = simpleDateFormat.format(date);

                /*计算时间差*/
                Date begin = null;
                Date end = null;
                try {
                    begin = simpleDateFormat.parse(time);
                    end = simpleDateFormat.parse(time1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long diff = end.getTime() - begin.getTime();
                /*计算秒*/
                long seconds = (diff % (1000 * 60)) / 1000;
//                System.out.println("日期时间差==" + seconds);
                if (seconds > 5) {
                    setPingLun("pinglun", 1);
                }
            }
        }
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
    protected void onStop() {
        super.onStop();
        RxFFmpegInvoke.getInstance().exit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
        ImmersionBar.with(this).destroy();
        if (mVideoView != null) {
            mVideoView.stop();
        }
        pluginManager.unBindPluginService();
    }

    private class EditImpl implements JoeVideo.OnEditorListener {
        int type;

        public EditImpl(int type) {
            this.type = type;
        }

        @Override
        public void onSuccess() {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    onSaveSuccess(type);
                }
            });
        }

        @Override
        public void onFailure() {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    hideProgressDialog();
                    getVDelegate().toastShort("保存失败");
                }
            });
        }

        @Override
        public void onProgress(final int v) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    showProgress(v);
                }
            });
        }
    }
}
