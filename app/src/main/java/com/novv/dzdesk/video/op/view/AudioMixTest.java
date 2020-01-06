package com.novv.dzdesk.video.op.view;

import android.app.Activity;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.novv.dzdesk.R;
import com.novv.dzdesk.video.op.Config;
import com.novv.dzdesk.video.op.GetPathFromUri;
import com.qiniu.pili.droid.shortvideo.PLShortVideoEditor;
import com.qiniu.pili.droid.shortvideo.PLVideoEditSetting;
import com.qiniu.pili.droid.shortvideo.PLVideoSaveListener;

/**
 * Created by jemy on 2018/6/6.
 */

public class AudioMixTest extends Activity implements PLVideoSaveListener {

    private static final String MP4_PATH = "MP4_PATH";
    private static final int REQUEST_CODE_PICK_AUDIO_MIX_FILE = 0;
    private static final int REQUEST_CODE_DUB = 1;
    private String videopath;
    private String outputpath;
    private PLShortVideoEditor mShortVideoEditor;
    private Button mpath, audio;
    private String mSelectedFilter;
    private String mSelectedMV;
    private String mSelectedMask;
    private GLSurfaceView mPreviewView;

    public static void launch(Activity activity, String mp4Path) {
        Intent intent = new Intent(activity, AudioMixTest.class);
        intent.putExtra(MP4_PATH, mp4Path);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audiomix);
        videopath = getIntent().getStringExtra(MP4_PATH);

        outputpath = Config.EDITED_FILE_PATH_9;

        mpath = (Button) findViewById(R.id.saveaudio_button);
        audio = (Button) findViewById(R.id.addaudio_button);

        mPreviewView = (GLSurfaceView) findViewById(R.id.preview);

        //Log.e("zw",videopath);
        PLVideoEditSetting setting = new PLVideoEditSetting();
        // 视频源文件路径
        setting.setSourceFilepath(videopath);
        // 编辑后保存的目标文件路径
        setting.setDestFilepath(outputpath);
        // 编辑保存后，是否保留源文件
        setting.setKeepOriginFile(true);
        mShortVideoEditor = new PLShortVideoEditor(mPreviewView, setting);

        mpath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 监听保存状态和结果
                mShortVideoEditor.setVideoSaveListener(AudioMixTest.this);
                // 执行保存操作
                mShortVideoEditor.save();
            }
        });

        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //intent.setType("video/*");
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, "选择音频"), 0);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_PICK_AUDIO_MIX_FILE) {
            String selectedFilepath = GetPathFromUri.getPath(this, data.getData());
            Log.i("zw", "Select file: " + selectedFilepath);
            if (!TextUtils.isEmpty(selectedFilepath)) {
                mShortVideoEditor.setAudioMixFile(selectedFilepath);
                mShortVideoEditor.setAudioMixFileRange(0, mShortVideoEditor.getDurationMs());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mShortVideoEditor.startPlayback();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mShortVideoEditor.pausePlayback();
    }

    @Override
    public void onSaveVideoSuccess(String s) {
        Log.e("zw", "回调路径" + s);
        Toast.makeText(this, "保存成功", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSaveVideoFailed(int i) {
        Log.e("zw", "失败" + i);
        Toast.makeText(this, "保存失败", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSaveVideoCanceled() {

    }

    @Override
    public void onProgressUpdate(float v) {

    }
}
