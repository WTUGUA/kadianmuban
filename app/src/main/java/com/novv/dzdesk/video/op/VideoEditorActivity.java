package com.novv.dzdesk.video.op;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.ark.adkit.basics.handler.Action;
import com.ark.adkit.basics.handler.Run;
import com.ark.dict.ConfigMapLoader;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.model.UserModel;
import com.novv.dzdesk.res.user.LoginContext;
import com.novv.dzdesk.ui.activity.local.AudioSelectActivity;
import com.novv.dzdesk.ui.activity.user.UserLoginActivity;
import com.novv.dzdesk.ui.activity.user.UserVipActivity;
import com.novv.dzdesk.ui.activity.vwp.VwpUploadActivity;
import com.novv.dzdesk.ui.adapter.edit.AdapterVideoFilter;
import com.novv.dzdesk.util.*;
import com.novv.dzdesk.video.op.model.BaseAdapter;
import com.novv.dzdesk.video.op.model.CoverAdapter;
import com.novv.dzdesk.video.op.model.MusicAdapter;
import com.novv.dzdesk.video.op.model.MusicItem;
import com.novv.dzdesk.video.op.task.MusicFetchTask;
import com.qiniu.pili.droid.shortvideo.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoEditorActivity extends Activity implements PLVideoSaveListener {

    private static final String TAG = "VideoEditorActivity";
    private static final String MP4_PATH = "MP4_PATH";

    private static final int REQUEST_CODE_PICK_AUDIO_MIX_FILE = 0;
    private SweetAlertDialog dialog;
    private PLShortVideoEditorStatus mShortVideoEditorStatus = PLShortVideoEditorStatus.Idle;
    private boolean mIsEffectShow = false;
    private View mPreviewOpV;
    private FullGLSurfaceLayout mPreviewView;
    private RecyclerView mRecyclerView;
    private CustomProgressDialog mProcessingDialog;
    private PLShortVideoEditor mShortVideoEditor;
    private PLVideoPlayerListener mVideoPlayerListener = new PLVideoPlayerListener() {
        @Override
        public void onCompletion() {
            if (mIsEffectShow) {
                mShortVideoEditor.pausePlayback();
            }
        }
    };
    private PLTextView mCurTextView;
    private PLImageView mCurImageView;
    private String mMp4path;
    private int coverTime;
    private View mMusicChoseV;
    private View mChoseLocalMusic;
    private ImageView mChoseLocalMusicTag;
    private View mChoseOPView;
    private View mChoseRlView;
    private TextView mChoseRlTitleTv;
    private CoverAdapter mCoverAdapter;
    private AdapterVideoFilter mFilterAdapter;
    private MusicAdapter mMusicAdapter;
    private volatile boolean mCancelSave;
    /**
     * 预览时为视频添加场景特效
     */
    private PLVideoFilterListener mVideoPlayFilterListener = new PLVideoFilterListener() {
        @Override
        public void onSurfaceCreated() {

        }

        @Override
        public void onSurfaceChanged(int i, int i1) {

        }

        @Override
        public void onSurfaceDestroy() {

        }

        @Override
        public int onDrawFrame(int texId, int texWidth, int texHeight, long timestampNs,
                               float[] transformMatrix) {

            if (mCancelSave) {
                mCancelSave = false;
                pausePlayback();
            }

            return texId;
        }
    };
    private boolean isUsingVipMusic, isUsingVipFilter;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.video_cover_chose:
                    choseType(ChoseType.VIDEO_COVER);
                    UmUtil.anaEvent(VideoEditorActivity.this, UmConst.CLICK_VIDEO_COVER);
                    break;
                case R.id.video_filter:
                    choseType(ChoseType.VIDEO_FILTER);
                    UmUtil.anaEvent(VideoEditorActivity.this, UmConst.CLICK_VIDEO_FILTER);
                    break;
                case R.id.video_music:
                    choseType(ChoseType.VIDEO_MUSIC);
                    UmUtil.anaEvent(VideoEditorActivity.this, UmConst.CLICK_VIDEO_MUSIC);
                    break;
            }
        }
    };

    public static void launch(Activity activity, String mp4Path) {
        Intent intent = new Intent(activity, VideoEditorActivity.class);
        intent.putExtra(MP4_PATH, mp4Path);
        activity.startActivity(intent);
        CoverAdapter.deleteCover();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_again);
        View mVideoCoverChoseV = findViewById(R.id.video_cover_chose);
        View mFilterChoseV = findViewById(R.id.video_filter);
        mMusicChoseV = findViewById(R.id.video_music);
        mChoseLocalMusic = findViewById(R.id.chose_music_local_tv);
        mChoseLocalMusicTag = findViewById(R.id.chose_music_local_iv);
        mChoseOPView = findViewById(R.id.video_op_rl);
        mChoseRlView = findViewById(R.id.chose_rl);
        mChoseRlTitleTv = findViewById(R.id.chose_rl_title_tv);
        mVideoCoverChoseV.setOnClickListener(onClickListener);
        mFilterChoseV.setOnClickListener(onClickListener);
        mMusicChoseV.setOnClickListener(onClickListener);
        mChoseLocalMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserModel userModel = LoginContext.getInstance().getUser();
                if (userModel == null) {
                    UserLoginActivity.launch(VideoEditorActivity.this);
                } else {
                    onClickMix(v);
                }
            }
        });

        findViewById(R.id.chose_rl_done_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChoseRlView.setVisibility(View.GONE);
                mChoseOPView.setVisibility(View.VISIBLE);
            }
        });

        initPreviewView();
        initProcessingDialog();
        initShortVideoEditor();
        initFiltersList();
    }

    protected void choseType(ChoseType type) {
        if (type == ChoseType.VIDEO_COVER) {
            mChoseRlTitleTv.setText("选择封面");
            mChoseLocalMusicTag.setVisibility(View.GONE);
            mChoseLocalMusic.setVisibility(View.GONE);
            loadVideoCover(this);
            return;
        } else if (type == ChoseType.VIDEO_FILTER) {
            mChoseRlTitleTv.setText("选择滤镜");
            mChoseLocalMusicTag.setVisibility(View.GONE);
            mChoseLocalMusic.setVisibility(View.GONE);
            loadVideoFilter();
        } else if (type == ChoseType.VIDEO_MUSIC) {
            if (!loadMusic()) {
                return;
            }
            mChoseLocalMusicTag.setVisibility(View.VISIBLE);
            mChoseLocalMusic.setVisibility(View.VISIBLE);
            mChoseRlTitleTv.setText("选择音乐");
        }

        findViewById(R.id.video_op_rl).setVisibility(View.INVISIBLE);
        findViewById(R.id.chose_rl).setVisibility(View.VISIBLE);
    }

    /**
     * 启动预览
     */
    private void startPlayback() {
        if (mShortVideoEditorStatus == PLShortVideoEditorStatus.Idle) {
            mShortVideoEditor.startPlayback(mVideoPlayFilterListener);
            mShortVideoEditorStatus = PLShortVideoEditorStatus.Playing;
        } else if (mShortVideoEditorStatus == PLShortVideoEditorStatus.Paused) {
            mShortVideoEditor.resumePlayback();
            mShortVideoEditorStatus = PLShortVideoEditorStatus.Playing;
        }
    }

    /**
     * 停止预览
     */
    private void stopPlayback() {
        mShortVideoEditor.stopPlayback();
        mShortVideoEditorStatus = PLShortVideoEditorStatus.Idle;
    }

    /**
     * 暂停预览
     */
    private void pausePlayback() {
        mShortVideoEditor.pausePlayback();
        mShortVideoEditorStatus = PLShortVideoEditorStatus.Paused;
    }

    private void initFiltersList() {
        mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        findViewById(R.id.chose_rl).setVisibility(View.GONE);
    }

    @SuppressLint("StaticFieldLeak")
    private boolean loadMusic() {

        if (mMusicAdapter != null) {
            mRecyclerView.setAdapter(mMusicAdapter);
            return true;
        }

        if (MusicFetchTask.sMusicItems == null || MusicFetchTask.sMusicItems.isEmpty()) {
            dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            // dialog.setContentText();
            dialog.setTitleText(getResources().getText(R.string.loading).toString());
            dialog.setCancelText(getResources().getText(R.string.dialog_cancel).toString());
            dialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismiss();
                }
            });
            if (isFinishing()) {
                return false;
            }
            dialog.show();
            new MusicFetchTask(this) {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(ArrayList arrayList) {
                    super.onPostExecute(arrayList);
                    if (dialog == null || !dialog.isShowing()) {
                        return;
                    }
                    dialog.dismiss();
                    onClickListener.onClick(mMusicChoseV);
                }

            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            return false;
        }

        final ArrayList<MusicItem> musicItems = new ArrayList<>();
        musicItems.addAll(MusicFetchTask.sMusicItems);

        mMusicAdapter = new MusicAdapter(this, musicItems);
        mMusicAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onClick(final int position) {
                isUsingVipMusic = false;
                if (position == 0) {
                    mShortVideoEditor.setAudioMixFile("");
                    mShortVideoEditor.setAudioMixVolume(1, 0);
                    return;
                }
                LogUtil.i(TAG, "onClick position = " + position);
                final MusicItem item = musicItems.get(position - 1);

                if (!FileUtil.isFileExists(item.getFilePath())) {
                    ToastUtil.showToast(VideoEditorActivity.this, "音乐不存在，请选择其他的音乐");
                    return;
                }
                LogUtil.i(TAG, "audio mix file = " + item.getFilePath());
                mShortVideoEditor.setAudioMixFile("");
                mShortVideoEditor.setAudioMixLooping(true);
                mShortVideoEditor.setAudioMixFile(item.getFilePath());
                mShortVideoEditor.setAudioMixVolume(0, 1);
                scrollMusic(position);
            }
        });
        mRecyclerView.setAdapter(mMusicAdapter);
        return true;
    }

    private void loadVideoFilter() {
        if (mFilterAdapter == null) {
            PLBuiltinFilter[] plBuiltinFilters = mShortVideoEditor.getBuiltinFilterList();
            String freeLimit = ConfigMapLoader.getInstance().getResponseMap()
                    .get("filters_free_limit");
            int limit = 0;
            try {
                limit = Integer.parseInt(freeLimit);
            } catch (Exception e) {
                //
            }

            List<PLBuiltinFilterImpl> newList = new ArrayList<>();
            PLBuiltinFilter noneFilter = new PLBuiltinFilter();
            noneFilter.setName("none_无");
            noneFilter.setAssetFilePath("filters_default/none.png");
            PLBuiltinFilterImpl noneFilterImpl = new PLBuiltinFilterImpl(noneFilter);
            noneFilterImpl.setVip(false);
            newList.add(noneFilterImpl);
            for (int i = 0; i < plBuiltinFilters.length; i++) {
                PLBuiltinFilter plBuiltinFilter = plBuiltinFilters[i];
                PLBuiltinFilterImpl impl = new PLBuiltinFilterImpl(plBuiltinFilter);
                if (limit == 0) {
                    impl.setVip(false);
                } else if (limit == -1) {
                    impl.setVip(true);
                } else if (i >= limit) {
                    impl.setVip(true);
                } else {
                    impl.setVip(false);
                }
                newList.add(impl);
            }
            mFilterAdapter = new AdapterVideoFilter(newList);
            mFilterAdapter.setOnItemSelectListener(new AdapterVideoFilter.OnItemSelectListener() {
                @Override
                public void onItemSelected(int position) {
                    isUsingVipFilter = mFilterAdapter.getData().get(position).isVip();
                    String mSelectedFilter;
                    if (position == 0) {
                        mSelectedFilter = null;
                    } else {
                        mSelectedFilter = mFilterAdapter.getData().get(position).getName();
                    }
                    mShortVideoEditor.setBuiltinFilter(mSelectedFilter);
                    if (mPreviewView.isSelected()) {
                        startPlayback();
                        new Handler(getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                pausePlayback();
                            }
                        }, 100);
                    }
                    scrollFilter();
                }
            });
            mFilterAdapter.setSelectItem(0);
        }
        mRecyclerView.setAdapter(mFilterAdapter);
        scrollFilter();
    }

    private void scrollTo(int position) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        if (layoutManager != null && position > 0) {
            int firstPosition = layoutManager.findFirstVisibleItemPosition();
            int lastPosition = layoutManager.findLastVisibleItemPosition();
            try {
                int left = mRecyclerView.getChildAt(position - firstPosition).getLeft();
                int right = mRecyclerView.getChildAt(lastPosition - position).getLeft();
                mRecyclerView.smoothScrollBy((left - right) / 2, 0);
            } catch (Exception e) {
                //
            }
        }
    }

    private void scrollFilter() {
        if (mRecyclerView == null || mFilterAdapter == null) {
            return;
        }
        int position = mFilterAdapter.getLastSelect();
        scrollTo(position);
    }

    private void scrollMusic(int position) {
        if (mRecyclerView == null || mMusicAdapter == null) {
            return;
        }
        scrollTo(position);
    }

    @SuppressLint("StaticFieldLeak")
    private void loadVideoCover(final Context context) {

        if (mCoverAdapter != null) {
            mRecyclerView.setAdapter(mCoverAdapter);
            findViewById(R.id.video_op_rl).setVisibility(View.INVISIBLE);
            findViewById(R.id.chose_rl).setVisibility(View.VISIBLE);
            return;
        }

        new AsyncTask<Void, Float, ArrayList<Bitmap>>() {

            private CustomProgressDialog dialog;

            private int SLICE_COUNT;
            private long durationMs;
            private ArrayList<Long> coverTimes = new ArrayList<>();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new CustomProgressDialog(context);
                dialog.setMax(100);
                dialog.show();
            }

            @Override
            protected ArrayList<Bitmap> doInBackground(Void... voids) {
                ArrayList<Bitmap> rs = new ArrayList<>();

                PLMediaFile mediaFile = new PLMediaFile(mMp4path);
                int width = mediaFile.getVideoWidth();
                int height = mediaFile.getVideoHeight();
                LogUtil.i(TAG, "video width = " + width + " height = " + height);
                int maxWidth = DeviceUtil.dip2px(context, 70);
                if (width >= maxWidth) {
                    height = (int) ((float) maxWidth / ((float) width / (float) height));
                    width = maxWidth;
                }
                LogUtil.i(TAG, "end video width = " + width + " height = " + height);
                durationMs = mediaFile.getDurationMs();
                SLICE_COUNT = (int) (durationMs / 1000);
                for (int i = 0; i < SLICE_COUNT; ++i) {
                    final int index = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onProgressUpdate((float) index / (float) SLICE_COUNT);
                        }
                    });

                    long frameTime = (long) ((1.0f * i / SLICE_COUNT) * durationMs);
                    coverTimes.add(frameTime);
                    PLVideoFrame frame = mediaFile
                            .getVideoFrameByTime(frameTime, true, width, height);
                    if (frame != null) {
                        rs.add(frame.toBitmap());
                    }
                }
                return rs;
            }

            @Override
            protected void onProgressUpdate(Float... values) {
                super.onProgressUpdate(values);
                dialog.setProgress((int) (values[0] * 100));
            }

            @Override
            protected void onPostExecute(ArrayList<Bitmap> bitmaps) {
                super.onPostExecute(bitmaps);
                LogUtil.i(TAG,
                        "onPostExecute bitmaps = " + bitmaps + " dialog is showing = " + dialog
                                .isShowing());
                mCoverAdapter = new CoverAdapter(context, bitmaps);
                mCoverAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(int position) {
                        coverTime = coverTimes.get(position).intValue();
                    }
                });
                if (!dialog.isShowing()) {
                    return;
                }
                dialog.dismiss();
                mRecyclerView.setAdapter(mCoverAdapter);
                findViewById(R.id.video_op_rl).setVisibility(View.INVISIBLE);
                findViewById(R.id.chose_rl).setVisibility(View.VISIBLE);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void initShortVideoEditor() {
        mMp4path = getIntent().getStringExtra(MP4_PATH);
        Log.i(TAG, "editing file: " + mMp4path);

        PLVideoEditSetting setting = new PLVideoEditSetting();
        setting.setSourceFilepath(mMp4path);
        setting.setDestFilepath(Config.EDITED_FILE_PATH);

        mShortVideoEditor = new PLShortVideoEditor(mPreviewView, setting);
        mShortVideoEditor.setVideoSaveListener(this);
        mShortVideoEditor.setVideoPlayerListener(mVideoPlayerListener);
        PLMediaFile plMediaFile = new PLMediaFile(mMp4path);
        mPreviewView.reLayout(plMediaFile.getVideoWidth(), plMediaFile.getVideoHeight());
    }

    private void initPreviewView() {
        mPreviewView = findViewById(R.id.preview);
        mPreviewView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideViewBorder();
            }
        });
        mPreviewOpV = findViewById(R.id.video_op_iv);
        mPreviewView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setSelected(!view.isSelected());
                if (view.isSelected()) {
                    pausePlayback();
                    mPreviewOpV.setVisibility(View.VISIBLE);
                } else {
                    startPlayback();
                    mPreviewOpV.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initProcessingDialog() {
        mProcessingDialog = new CustomProgressDialog(this);
        mProcessingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mShortVideoEditor.cancelSave();
            }
        });
    }

    public void onClickMix(View v) {
        Intent intent = new Intent(this, AudioSelectActivity.class);
        startActivityForResult(intent, REQUEST_CODE_PICK_AUDIO_MIX_FILE);
    }

    public void onClickBack(View v) {
        finish();
    }

    private void hideViewBorder() {
        if (mCurTextView != null) {
            mCurTextView.setBackgroundResource(0);
            mCurTextView = null;
        }

        if (mCurImageView != null) {
            mCurImageView.setBackgroundResource(0);
            mCurImageView = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_PICK_AUDIO_MIX_FILE) {
            String selectedFilepath = data.getStringExtra("data");
            Log.i(TAG, "Select file: " + selectedFilepath);
            if (!TextUtils.isEmpty(selectedFilepath) && new File(selectedFilepath).exists()) {
                mShortVideoEditor.setAudioMixFile("");
                mShortVideoEditor.setAudioMixLooping(true);
                mShortVideoEditor.setAudioMixFile(selectedFilepath);
                mShortVideoEditor.setAudioMixVolume(0, 1);
                isUsingVipMusic = true;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPlayback();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String mSelectedFilter = null;
        if (mFilterAdapter != null && mFilterAdapter.getLastSelect() > 0) {
            mSelectedFilter = mFilterAdapter.getData().get(mFilterAdapter.getLastSelect()).getName();
        }
        mShortVideoEditor.setBuiltinFilter(mSelectedFilter);
        mShortVideoEditor.setMVEffect(null, null);
        startPlayback();
    }

    public void onSaveEdit(View v) {
        if (isUsingVipMusic || isUsingVipFilter) {
            if (!LoginContext.getInstance().isVip()) {
                Toast.makeText(this, "您正在使用vip功能", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, UserVipActivity.class));
                return;
            }
        }
        if (!CoverAdapter.hasCover()) {
            Run.onBackground(new Action() {
                @Override
                public void call() {
                    PLMediaFile plMediaFile = new PLMediaFile(mMp4path);
                    try {
                        PLVideoFrame plVideoFrame = plMediaFile.getVideoFrameByIndex(0, true);
                        if (plVideoFrame != null) {
                            CoverAdapter.saveCover(plVideoFrame.toBitmap());
                        }
                    } catch (Exception e) {
                        ToastUtil.showGeneralToast(VideoEditorActivity.this, "当前视频无法获取封面");
                    }
                    if (!CoverAdapter.hasCover()) {
                        ToastUtil.showToast(VideoEditorActivity.this, "请选择一个封面");
                    } else {
                        Run.onUiAsync(new Action() {
                            @Override
                            public void call() {
                                if (isFinishing()){
                                    return;
                                }
                                UmUtil.anaEvent(VideoEditorActivity.this,
                                        UmConst.CLICK_VIDEO_EDIT_NEXT);
                                mShortVideoEditor.setPlaybackLoop(false);
                                mProcessingDialog.show();
                                mShortVideoEditor.save();
                                hideViewBorder();
                            }
                        });
                    }
                }
            });
        } else {
            UmUtil.anaEvent(this, UmConst.CLICK_VIDEO_EDIT_NEXT);
            mShortVideoEditor.setPlaybackLoop(false);
            mProcessingDialog.show();
            mShortVideoEditor.save();
            hideViewBorder();
        }
    }

    @Override
    public void onSaveVideoSuccess(String filePath) {
        Log.i(TAG, "save edit success filePath: " + filePath);
        mProcessingDialog.dismiss();
        VwpUploadActivity.launch(this, filePath, coverTime);
    }

    @Override
    public void onSaveVideoFailed(final int errorCode) {
        Log.e(TAG, "save edit failed errorCode:" + errorCode);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProcessingDialog.dismiss();
                ToastUtils.toastErrorCode(VideoEditorActivity.this, errorCode);
            }
        });
    }

    @Override
    public void onSaveVideoCanceled() {
        mProcessingDialog.dismiss();
        mCancelSave = true;
        if (mIsEffectShow) {
            onResume();
        }
    }

    @Override
    public void onProgressUpdate(final float percentage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProcessingDialog.setProgress((int) (100 * percentage));
            }
        });
    }

    private boolean toggleBack() {
        if (mChoseRlView.getVisibility() == View.VISIBLE) {
            mChoseRlView.setVisibility(View.INVISIBLE);
            mChoseOPView.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mChoseRlView.getVisibility() == View.VISIBLE) {
            return toggleBack();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (mProcessingDialog != null && mProcessingDialog.isShowing()) {
            mProcessingDialog.dismiss();
        }
    }

    // 视频编辑器预览状态
    private enum PLShortVideoEditorStatus {
        Idle,
        Playing,
        Paused,
    }

    private enum ChoseType {
        VIDEO_COVER, VIDEO_FILTER, VIDEO_MUSIC
    }
}

