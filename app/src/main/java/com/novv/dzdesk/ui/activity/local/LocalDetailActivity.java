package com.novv.dzdesk.ui.activity.local;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.VideoView;
import com.adesk.tool.plugin.PluginManager;
import com.ark.baseui.XAppCompatActivity;
import com.gyf.barlibrary.ImmersionBar;
import com.novv.dzdesk.Const;
import com.novv.dzdesk.R;
import com.novv.dzdesk.live.LwPrefUtil;
import com.novv.dzdesk.live.LwVideoLiveWallpaper;
import com.novv.dzdesk.live.PrefUtil;
import com.novv.dzdesk.res.model.ResourceBean;
import com.novv.dzdesk.util.*;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

public class LocalDetailActivity extends XAppCompatActivity implements
        View.OnClickListener {

    private static final String tag = LocalDetailActivity.class.getSimpleName();
    private static final String KEY_RES_LOCAL = "key_res_local";
    private ResourceBean mItem;
    private VideoView mVideoView;
    private View mVoiceView;
    private boolean isLwOn = false;
    private MediaPlayer mPlayer;
    private ImmersionBar immersionBar;
    private PluginManager pluginManager;
    private boolean conn;

    public static void launch(Context context, ResourceBean bean) {
        if (bean == null) {
            ToastUtil.showToast(context, R.string.op_failed);
            return;
        }
        Intent intent = new Intent(context, LocalDetailActivity.class);
        intent.putExtra(KEY_RES_LOCAL, bean);
        context.startActivity(intent);
    }

    private void processExtraData() {
        Intent intent = getIntent();
        mItem = (ResourceBean) intent.getSerializableExtra(KEY_RES_LOCAL);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_local_detail;
    }

    @Override
    public void setUpView(@NonNull View rootView) {
        super.setUpView(rootView);
        mVideoView = findViewById(R.id.videoview);
        View mBackView = findViewById(R.id.back_imgv);
        View mShareView = findViewById(R.id.share_imgv);
        mVoiceView = findViewById(R.id.voice_imagev);
        View mSetView = findViewById(R.id.set_wp_btn);
        View mDeleteView = findViewById(R.id.delete_imgv);
        mBackView.setOnClickListener(this);
        mSetView.setOnClickListener(this);
        mShareView.setOnClickListener(this);
        mVoiceView.setOnClickListener(this);
        mDeleteView.setOnClickListener(this);
    }

    @Nullable
    @Override
    public Object newP() {
        return null;
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
                .titleBar(findViewById(R.id.detail_top_rl), false)
                .transparentBar();
        immersionBar.init();
        processExtraData();
        mVoiceView.setSelected(LwPrefUtil.getPreviewVoice(this));
        isLwOn = LwPrefUtil.isLwpVoiceOpened(this);
        initVideoView();
    }

    private void initVideoView() {
        if (mItem == null) {
            ToastUtil.showToast(this, getResources().getString(R.string.op_failed));
            finish();
            return;
        }
        mVideoView.setVideoPath(mItem.getMp4File().getAbsolutePath());
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mPlayer = mp;
                if (mVoiceView.isSelected()) {
                    mp.setVolume(1, 1);
                } else {
                    mp.setVolume(0, 0);
                }
                mVideoView.requestFocus();
                mVideoView.start();
            }
        });
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                LogUtil.e(this, "MyOnErrorListener", "what=" + what + ", extra="
                        + extra);
                ToastUtil.showToast(LocalDetailActivity.this, R.string.op_failed);
                return true;
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mVideoView.start();
            }
        });
        mVideoView.setBackgroundResource(android.R.color.transparent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_imgv:
                finish();
                break;
            case R.id.share_imgv:
                ShareUtils.shareOriginal(LocalDetailActivity.this, null, mItem);
                break;
            case R.id.voice_imagev:
                mVoiceView.setSelected(!mVoiceView.isSelected());
                LwPrefUtil.setPreviewVoice(v.getContext(), true);
                if (mVoiceView.isSelected()) {
                    if (mPlayer != null) {
                        mPlayer.setVolume(1, 1);
                    }
                } else {
                    if (mPlayer != null) {
                        mPlayer.setVolume(0, 0);
                    }
                }
                break;
            case R.id.set_wp_btn:
                setLw();
                break;
            case R.id.delete_imgv:
                deleteItem();
                break;
        }
    }

    private void deleteItem() {
        String currentSetPath = PrefUtil.getString(this, Const.PARAMS.LiveMp4Key, "");
        if (mItem.getMp4File().getAbsolutePath().equalsIgnoreCase(currentSetPath)) {
            if (CtxUtil.isLwServiceRun(this)) {
                ToastUtil.showToast(this, R.string.op_delete_cannot_currentlw);
                return;
            } else {
                PrefUtil.putString(this, Const.PARAMS.LiveMp4Key, LwVideoLiveWallpaper.defaultMp4);
            }
        }
        FileUtil.deleteFile(mItem.getMp4File().getAbsolutePath());
        finish();
    }

    private void setLw() {
        if (!DeviceUtil.isSDCardMounted()) {
            ToastUtil.showToast(this, R.string.op_sdcard_no_mount);
            return;
        }

        File mp4File = mItem.getMp4File();
        if (!mp4File.exists()) {
            ToastUtil.showToast(this, R.string.op_failed);
            return;
        }

        String mp4FilePath = mItem.getMp4File().getAbsolutePath();
        LogUtil.i(tag, "mp4 file path = " + mp4FilePath);
        PrefUtil.putString(this, Const.PARAMS.LiveMp4Key, mp4FilePath);

        //TODO AndroidQ 权限适配  暂时直接设置为壁纸
        LwVideoLiveWallpaper.setToWallPaper(context);


//        pluginManager.checkPluginVersion(5, new PluginManager.OnCheckListener() {
//            @Override
//            public void onShouldUpdate() {
//                new PluginDialog(LocalDetailActivity.this)
//                        .setTitle("检测到更新")
//                        .showOrigin(true).show();
//            }
//
//            @Override
//            public void onSuccess() {
//                String currentMp4Path = PrefUtil
//                        .getString(LocalDetailActivity.this, Const.PARAMS.LiveMp4Key, "");
//                if (!conn) {
//                    pluginManager.startSetWallpaper(currentMp4Path);
//                } else {
//                    pluginManager.startPluginActivity(currentMp4Path);
//                }
//            }
//
//            @Override
//            public void onShouldInstall() {
//                new PluginDialog(LocalDetailActivity.this).showOrigin(true).show();
//            }
//        });
    }

    @Override
    protected void onResume() {
        MobclickAgent.onResume(this);
        super.onResume();
        if (mVideoView != null) {
            mVideoView.start();
        }
    }

    @Override
    protected void onPause() {
        MobclickAgent.onPause(this);
        super.onPause();
        if (mVideoView != null) {
            mVideoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (immersionBar != null) {
            immersionBar.destroy();
        }
        pluginManager.unBindPluginService();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LwVideoLiveWallpaper.REQUEST_CODE_SET_WALLPAPER
                && resultCode == RESULT_OK) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Context context = LocalDetailActivity.this;
                    if (CtxUtil.isLwServiceRun(context)) {
                        CtxUtil.launchHome(context);
                    }
                }
            }, 500);
        }
    }
}
