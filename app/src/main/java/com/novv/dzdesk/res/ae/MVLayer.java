package com.novv.dzdesk.res.ae;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.lansosdk.LanSongAe.LSOAeDrawable;
import com.lansosdk.videoeditor.DrawPadAEExecute;
import com.lansosdk.videoeditor.DrawPadAEPreview;

import java.io.File;

public class MVLayer extends Layer {

    private static final long serialVersionUID = 7035148747734543580L;
    private String colorAbsPath;
    private String maskAbsPath;

    public MVLayer(AEConfig aeConfig) {
        this.colorAbsPath = aeConfig.colorPath;
        this.maskAbsPath = aeConfig.maskPath;
    }

    @Override
    public void preview(@NonNull LSOAeDrawable drawable, @NonNull DrawPadAEPreview padAEPreview) {
        if (!TextUtils.isEmpty(colorAbsPath) && new File(colorAbsPath).exists()
                && !TextUtils.isEmpty(maskAbsPath) && new File(maskAbsPath).exists()) {
            padAEPreview.addMVLayer(colorAbsPath, maskAbsPath);
        }
    }

    @Override
    public void execute(@NonNull LSOAeDrawable drawable, @NonNull DrawPadAEExecute padAEExecute) {
        if (!TextUtils.isEmpty(colorAbsPath) && new File(colorAbsPath).exists()
                && !TextUtils.isEmpty(maskAbsPath) && new File(maskAbsPath).exists()) {
            padAEExecute.addMVLayer(colorAbsPath, maskAbsPath);
        }
    }

    @Override
    public void release() {

    }
}
