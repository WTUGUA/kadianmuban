package com.novv.dzdesk.res.ae;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.ark.adkit.basics.utils.LogUtils;
import com.lansosdk.LanSongAe.LSOAeDrawable;
import com.lansosdk.LanSongAe.LSOTextDelegate;
import com.lansosdk.videoeditor.DrawPadAEExecute;
import com.lansosdk.videoeditor.DrawPadAEPreview;
import com.novv.dzdesk.ui.activity.ae.Utils;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AELayer extends Layer {

  private static final long serialVersionUID = -4732982249889344483L;
  private List<LSOAeTextImpl> mLSOAeTexts;
  private TreeMap<String, LSOAeImageImpl> mAeImageMap;
  private Map<String, WeakReference<Bitmap>> bitmapMaps = new HashMap<>();
  private AEConfig mAEConfig;

  public AELayer(AEConfig aeConfig, List<LSOAeTextImpl> lsoAeTexts,
      TreeMap<String, LSOAeImageImpl> lsoAeImageMap) {
    this.mAEConfig = aeConfig;
    this.mLSOAeTexts = lsoAeTexts;
    this.mAeImageMap = lsoAeImageMap;
  }

  @Override
  public void preview(@NonNull LSOAeDrawable drawable, @NonNull DrawPadAEPreview padAEPreview) {
    if (mLSOAeTexts != null) {
      LSOTextDelegate textDelegate = new LSOTextDelegate(drawable);
      for (LSOAeTextImpl text : mLSOAeTexts) {
        drawable.setFontFilePath(mAEConfig.fontDir + text.fontName);
        textDelegate.setText(text.getLSOAeText().text, text.text);
        LogUtils.i(
            "LSOAeText-->text:" + text.getLSOAeText().text + ",newText:" + text.text
                + ",font:" + text.fontName);
      }
      drawable.setTextDelegate(textDelegate);
    }
    if (mAeImageMap != null) {
      for (String key : mAeImageMap.keySet()) {
        LSOAeImageImpl asset = mAeImageMap.get(key);
        if (asset == null) {
          return;
        }
        String path = asset.getAbsFilePath();
        LogUtils.i("LSOAeImage-->id=" + asset.getId() + ",path=" + path);
        if (!TextUtils.isEmpty(path) && new File(path).exists()) {
          Bitmap bitmap = Utils.decodeSampledBitmapFromFile(path, asset.getWidth(),
              asset.getHeight());
          bitmapMaps.put(asset.getId(), new WeakReference<>(bitmap));
          if (bitmap != null && !bitmap.isRecycled()) {
            LogUtils.i("updateBitmap--->" + asset.getId());
            drawable.updateBitmap(asset.getId(), bitmap);
          } else {
            LogUtils.i("updateBitmap--->null");
          }
        } else {
          LogUtils.e(key + ",图片文件不存在:" + path);
        }
      }
    } else {
      LogUtils.e("mAeImageMap = null");
    }
    padAEPreview.addAeLayer(drawable);
  }

  @Override
  public void execute(@NonNull LSOAeDrawable drawable, @NonNull DrawPadAEExecute padAEExecute) {
    if (mLSOAeTexts != null) {
      LSOTextDelegate textDelegate = new LSOTextDelegate(drawable);
      for (LSOAeTextImpl text : mLSOAeTexts) {
        drawable.setFontFilePath(mAEConfig.fontDir + text.fontName);
        textDelegate.setText(text.getLSOAeText().text, text.text);
        LogUtils.i(
            "LSOAeText-->text:" + text.getLSOAeText().text + ",newText:" + text.text
                + ",font:" + text.fontName);
      }
      drawable.setTextDelegate(textDelegate);
    }
    if (mAeImageMap != null) {
      for (String key : mAeImageMap.keySet()) {
        LSOAeImageImpl asset = mAeImageMap.get(key);
        if (asset == null) {
          return;
        }
        String path = asset.getAbsFilePath();
        LogUtils.i("LSOAeImage-->id=" + asset.getId() + ",path=" + path);
        if (!TextUtils.isEmpty(path) && new File(path).exists()) {
          Bitmap bitmap = Utils.decodeSampledBitmapFromFile(path, asset.getWidth(),
              asset.getHeight());
          bitmapMaps.put(asset.getId(), new WeakReference<>(bitmap));
          if (bitmap != null && !bitmap.isRecycled()) {
            drawable.updateBitmap(asset.getId(), bitmap);
            LogUtils.i("updateBitmap--->" + asset.getId());
          } else {
            LogUtils.i("updateBitmap--->null");
          }
        } else {
          LogUtils.e(key + ",图片文件不存在:" + path);
        }
      }
    } else {
      LogUtils.e("mAeImageMap = null");
    }
    padAEExecute.addAeLayer(drawable);
  }

  @Override
  public void release() {
    for (String key : bitmapMaps.keySet()) {
      WeakReference<Bitmap> reference = bitmapMaps.get(key);
      Bitmap bitmap;
      if (reference != null && (bitmap = reference.get()) != null) {
        if (!bitmap.isRecycled()) {
          bitmap.recycle();
        }
      }
    }
    bitmapMaps.clear();
  }
}
