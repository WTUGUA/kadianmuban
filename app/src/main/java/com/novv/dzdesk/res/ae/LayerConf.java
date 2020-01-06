package com.novv.dzdesk.res.ae;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.ark.adkit.basics.utils.LogUtils;
import com.lansosdk.LanSongAe.LSOAeDrawable;
import com.lansosdk.videoeditor.DrawPadAEExecute;
import com.lansosdk.videoeditor.DrawPadAEPreview;
import com.novv.dzdesk.ui.activity.ae.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LayerConf implements Serializable {

    private static final long serialVersionUID = 4151199080981968244L;
    private List<Layer> layers = new ArrayList<>();
    private boolean showWatermark;
    private String watermark;
    private boolean watermarkLeftSide;

    public void setWatermarkLeftSide(boolean watermarkLeftSide) {
        this.watermarkLeftSide = watermarkLeftSide;
    }

    public void setLayers(List<Layer> layers) {
        this.layers = layers;
    }

    public void setShowWatermark(boolean showWatermark) {
        this.showWatermark = showWatermark;
    }

    public void setWatermark(String watermark) {
        this.watermark = watermark;
    }

    public void addLayer(Layer layer) {
        LogUtils.e("addLayer--->" + layer.getClass());
        layers.add(layer);
    }

    public void updatePreview(@NonNull LSOAeDrawable drawable,
            @NonNull DrawPadAEPreview padAEPreview) {
        int width = drawable.getJsonWidth();
        int height = drawable.getJsonHeight();
        for (Layer layer : layers) {
            layer.preview(drawable, padAEPreview);
        }
        if (showWatermark && !TextUtils.isEmpty(watermark) && new File(watermark).exists()) {
            padAEPreview.addBitmapLayer(getWaterMark(watermark, width, height));
        }
    }

    public void updateExecute(@NonNull LSOAeDrawable drawable,
            @NonNull DrawPadAEExecute padAEExecute) {
        int width = drawable.getJsonWidth();
        int height = drawable.getJsonHeight();
        for (Layer layer : layers) {
            layer.execute(drawable, padAEExecute);
        }
        if (showWatermark && !TextUtils.isEmpty(watermark) && new File(watermark).exists()
                && width != 0 && height != 0) {
            padAEExecute.addBitmapLayer(getWaterMark(watermark, width, height));
        }
    }

    public void release() {
        for (Layer layer : layers) {
            layer.release();
        }
    }

    public Bitmap getWaterMark(String watermark, int rectWidth, int rectHeight) {
        int owidth = 165;
        int oheight = 35;
        int width = rectWidth / 3;
        int height = width * oheight / owidth;
        Bitmap watermarkBitmap = Utils.decodeSampledBitmapFromFile(watermark, width, height);
        Bitmap bitmap = Bitmap.createBitmap(rectWidth, rectHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        if (watermarkLeftSide) {
            canvas.drawBitmap(watermarkBitmap, 9, 9, null);
        } else {
            canvas.drawBitmap(watermarkBitmap, rectWidth - width - 9, rectHeight - height - 9,
                    null);
        }
        canvas.save();
        canvas.restore();
        watermarkBitmap.recycle();
        return bitmap;
    }
}
