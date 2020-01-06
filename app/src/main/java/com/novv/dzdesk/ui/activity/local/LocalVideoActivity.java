package com.novv.dzdesk.ui.activity.local;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import com.adesk.tool.plugin.PluginManager;
import com.ark.baseui.XAppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.novv.dzdesk.Const;
import com.novv.dzdesk.R;
import com.novv.dzdesk.http.DefaultDisposableObserver;
import com.novv.dzdesk.http.DefaultScheduler;
import com.novv.dzdesk.http.ServerApi;
import com.novv.dzdesk.live.LwPrefUtil;
import com.novv.dzdesk.live.LwVideoLiveWallpaper;
import com.novv.dzdesk.live.PrefUtil;
import com.novv.dzdesk.live.VideoUploadUtil;
import com.novv.dzdesk.res.model.BaseResult;
import com.novv.dzdesk.res.model.CategoryBean;
import com.novv.dzdesk.res.model.ResourceBean;
import com.novv.dzdesk.ui.dialog.LoginDialog;
import com.novv.dzdesk.ui.dialog.SpinnerDialog;
import com.novv.dzdesk.util.*;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 本地视频预览，支持设置和上传 Created by lingyfh on 2017/6/7.
 */
public class LocalVideoActivity extends XAppCompatActivity implements
        View.OnClickListener, SpinnerDialog.CallBack, UmConst {

    public static final String FILE_NAME_CATEGORY_CACHE = "cache_cagetory";
    private static final String tag = LocalDetailActivity.class.getSimpleName();
    private static final String KEY_RES_LOCAL_PATH = "key_res_local_path";
    private static final String KEY_RES_LOCAL_UPLOAD = "key_res_local_upload";
    private String localVideoPath;
    private VideoView mVideoView;
    private View mVoiceView;
    private boolean isLwOn = false;
    private boolean isUpload;
    private MediaPlayer mPlayer;
    private View mBackView;
    private Button mSetView;
    private boolean conn;
    private PluginManager pluginManager;
    private ArrayList<CategoryBean> mItems = new ArrayList<>();

    public static void launch(Context context, String localVideoPath, boolean isUpload) {
        if (TextUtils.isEmpty(localVideoPath) || !new File(localVideoPath).exists()) {
            ToastUtil.showToast(context,
                    context.getResources().getString(R.string.video_file_not_exist));
            return;
        }
        Intent intent = new Intent(context, LocalVideoActivity.class);
        intent.putExtra(KEY_RES_LOCAL_PATH, localVideoPath);
        intent.putExtra(KEY_RES_LOCAL_UPLOAD, isUpload);
        context.startActivity(intent);
    }

    private void processExtraData() {
        Intent intent = getIntent();
        localVideoPath = intent.getStringExtra(KEY_RES_LOCAL_PATH);
        isUpload = intent.getBooleanExtra(KEY_RES_LOCAL_UPLOAD, false);
        initData();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_local_video;
    }

    @Nullable
    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void setUpView(@NonNull View rootView) {
        super.setUpView(rootView);
        mVideoView = findViewById(R.id.videoview);
        mVoiceView = findViewById(R.id.voice_imagev);
        mBackView = findViewById(R.id.back_imgv);
        mSetView = findViewById(R.id.set_wp_btn);
    }

    @Override
    public void initData(@Nullable Bundle bundle) {
        processExtraData();
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
        UmUtil.anaGeneralOp(this, OP_LOCAL_VIDEO_SHOW);
        isLwOn = LwPrefUtil.isLwpVoiceOpened(this);
        if (TextUtils.isEmpty(localVideoPath)) {
            ToastUtil.showToast(this, R.string.op_open_file_failed);
            finish();
            return;
        }
        mVoiceView.setSelected(LwPrefUtil.getPreviewVoice(this));
        mBackView.setOnClickListener(this);
        mSetView.setOnClickListener(this);
        mVoiceView.setOnClickListener(this);
        mVoiceView.setVisibility(isUpload ? View.GONE : View.VISIBLE);
        mSetView.setText(isUpload ? getResources().getString(R.string.upload)
                : getResources().getString(R.string.set_live_wp));
        initVideoView();
        getCategory();
    }

    private void getCategory() {
        mItems = new ArrayList<>();
        String str = PrefUtil.getString(this, FILE_NAME_CATEGORY_CACHE, "");
        if (TextUtils.isEmpty(str)) {
            ServerApi.getCategory()
                    .compose(
                            DefaultScheduler.<BaseResult<List<CategoryBean>>>getDefaultTransformer())
                    .subscribe(new DefaultDisposableObserver<BaseResult<List<CategoryBean>>>() {
                        @Override
                        public void onFailure(Throwable throwable) {

                        }

                        @Override
                        public void onSuccess(BaseResult<List<CategoryBean>> result) {
                            List<CategoryBean> mList = result.getRes();
                            if (mList != null && mList.size() > 0) {
                                mItems.clear();
                                mItems.addAll(mList);
                                Gson gson = new Gson();
                                String str = gson.toJson(mList);
                                PrefUtil.putString(LocalVideoActivity.this,
                                        FILE_NAME_CATEGORY_CACHE, str);
                            }
                        }
                    });
        } else {
            List<CategoryBean> mList = new Gson()
                    .fromJson(str, new TypeToken<List<CategoryBean>>() {
                    }.getType());
            if (mList != null) {
                mItems.clear();
                mItems.addAll(mList);
            }
        }
    }

    private void initData() {
        if (TextUtils.isEmpty(localVideoPath)) {
            UmUtil.anaGeneralOp(this, OP_LOCAL_VIDEO_SHOW_OTHER);
            try {
                initFromOther();
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.showToast(this, R.string.op_open_file_failed);
                finish();
            } catch (Error e) {
                ToastUtil.showToast(this, R.string.op_open_file_failed);
                e.printStackTrace();
                finish();
            }
        } else {
            UmUtil.anaGeneralOp(this, OP_LOCAL_VIDEO_SHOW_OURSELF);
        }
    }

    private void showUploadDialog() {
        if (TextUtils.isEmpty(HeaderSpf.getSessionId())) {
            LoginDialog.launch(this, "登录后才可以上传哦~");
            return;
        }
        if (mItems == null || mItems.size() == 0) {
            ToastUtil.showToast(this, "暂未获取到分类列表，请稍后尝试！");
        } else {
            ArrayList<String> idList = new ArrayList<>();
            ArrayList<String> nameList = new ArrayList<>();
            idList.add("-1");
            nameList.add("请选择");
            for (CategoryBean categoryBean : mItems) {
                idList.add(categoryBean.getId());
                nameList.add(categoryBean.getName());
            }
            SpinnerDialog fragment = SpinnerDialog.launch(this, idList, nameList);
            fragment.setCallBack(this);
        }
    }

    private void initFromOther() {
        if (getIntent() == null) {
            ToastUtil.showToast(this, R.string.op_failed);
            finish();
            return;
        }

        String action = getIntent().getAction();
        String scheme = getIntent().getScheme();
        Uri uri = getIntent().getData();
        String type = getIntent().getType();

        if (uri == null) {
            ToastUtil.showToast(this, R.string.op_failed);
            finish();
            return;
        }
        String path;
        if ("file".equalsIgnoreCase(scheme)) {
            path = uri.getPath();
            parseFilePath(path);
        } else if ("content".equalsIgnoreCase(scheme)) {
            path = FileUtil.getPath(this, uri);
            parseFilePath(path);
        } else {
            ToastUtil.showToast(this, R.string.op_not_support);
            finish();
            return;
        }
        LogUtil.i(tag, "action = " + action + " scheme = " + scheme + " uri = "
                + uri + " type = " + type + " path = " + path);
    }

    private void parseFilePath(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            ToastUtil.showToast(this, R.string.op_failed);
            finish();
            return;
        }

        if (new File(filePath).exists()) {
            localVideoPath = filePath;
        } else {
            ToastUtil.showToast(this, R.string.op_not_file);
            finish();
        }
    }

    private void initVideoView() {
        mVideoView.setVideoPath(localVideoPath);
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
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mVideoView.start();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_imgv:
                finish();
                break;
            case R.id.voice_imagev:
                mVoiceView.setSelected(!mVoiceView.isSelected());
                LwPrefUtil.setPreviewVoice(v.getContext(), mVoiceView.isSelected());
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
                if (isUpload) {
                    showUploadDialog();
                } else {
                    UmUtil.anaGeneralOp(this, OP_LOCAL_VIDEO_SET);
                    setLw();
                }
                break;
        }
    }

    private void setLw() {
        if (!DeviceUtil.isSDCardMounted()) {
            ToastUtil.showToast(this, R.string.op_sdcard_no_mount);
            return;
        }

        File mp4File = new File(localVideoPath);
        if (!mp4File.exists()) {
            ToastUtil.showToast(this, R.string.op_failed);
            return;
        }

        String mp4FilePath = localVideoPath;
        LogUtil.i(tag, "mp4 file path = " + mp4FilePath);
        PrefUtil.putString(this, Const.PARAMS.LiveMp4Key, mp4FilePath);
        //TODO AndroidQ 权限适配  暂时直接设置为壁纸
        LwVideoLiveWallpaper.setToWallPaper(context);
//        pluginManager.checkPluginVersion(5, new PluginManager.OnCheckListener() {
//            @Override
//            public void onShouldUpdate() {
//                new PluginDialog(LocalVideoActivity.this)
//                        .setTitle("检测到更新")
//                        .showOrigin(true).show();
//            }
//
//            @Override
//            public void onSuccess() {
//                String currentMp4Path = PrefUtil
//                        .getString(LocalVideoActivity.this, Const.PARAMS.LiveMp4Key, "");
//                if (!conn) {
//                    pluginManager.startSetWallpaper(currentMp4Path);
//                } else {
//                    pluginManager.startPluginActivity(currentMp4Path);
//                }
//            }
//
//            @Override
//            public void onShouldInstall() {
//                new PluginDialog(LocalVideoActivity.this).showOrigin(true).show();
//            }
//        });
    }

    @Override
    public void onSureClick(String inputText, String spinnerId) {
        ResourceBean mItem = new ResourceBean();
        mItem.setLinkMp4(localVideoPath);
        mItem.setName(inputText);
        mItem.setCid(spinnerId);
        VideoUploadUtil.getInstance().uploadVideo(this, mItem, new VideoUploadUtil.UploadListener() {
            @Override
            public void uploadFinish(boolean success) {
                try {
                    CleanUtil.deleteDir(new File(Const.Dir.VIDEO_ROOT_PATH));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LocalVideoActivity.this.finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LwVideoLiveWallpaper.REQUEST_CODE_SET_WALLPAPER
                && resultCode == RESULT_OK) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Context context = LocalVideoActivity.this;
                    if (CtxUtil.isLwServiceRun(context)) {
                        CtxUtil.launchHome(context);
                    }
                }
            }, 500);
        }
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
        pluginManager.unBindPluginService();
    }
}
