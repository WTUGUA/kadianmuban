package com.novv.dzdesk.res.ae;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.ark.adkit.basics.utils.LogUtils;
import com.lansosdk.LanSongAe.LSOAeDrawable;
import com.lansosdk.videoeditor.DrawPadAEExecute;
import com.lansosdk.videoeditor.DrawPadAEPreview;

import java.io.File;
import java.io.IOException;

public class VideoLayer extends Layer {

    private static final long serialVersionUID = 7355522504985174205L;
    private String videoAbsPath;

    public VideoLayer(AEConfig aeConfig) {
        this.videoAbsPath = aeConfig.bgVideoPath;
    }

    @Override
    public void preview(@NonNull LSOAeDrawable drawable, @NonNull DrawPadAEPreview padAEPreview) {
        if (!TextUtils.isEmpty(videoAbsPath) && new File(videoAbsPath).exists()) {
            try {
                LogUtils.i("添加背景视频--->" + videoAbsPath);
                padAEPreview.addVideoLayer(videoAbsPath);
            } catch (IOException e)  {
                e.printStackTrace();
            }
        } else {
            LogUtils.e("背景视频不存在");
        }
    }

    @Override
    public void execute(@NonNull LSOAeDrawable drawable, @NonNull DrawPadAEExecute padAEExecute) {

    }

    @Override
    public void release() {

    }
}