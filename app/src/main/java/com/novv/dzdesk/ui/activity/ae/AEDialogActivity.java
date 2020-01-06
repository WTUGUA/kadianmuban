package com.novv.dzdesk.ui.activity.ae;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import com.ark.adkit.basics.handler.Run;
import com.ark.adkit.basics.utils.LogUtils;
import com.ark.baseui.XAppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.lansosdk.LanSongAe.LSOAeDrawable;
import com.lansosdk.LanSongAe.LSOAeImage;
import com.lansosdk.LanSongAe.LSOAeText;
import com.lansosdk.LanSongAe.LSOLoadAeJsons;
import com.lansosdk.LanSongAe.OnLSOAeJsonLoadedListener;
import com.lansosdk.box.DrawPad;
import com.lansosdk.box.onDrawPadCompletedListener;
import com.lansosdk.box.onDrawPadErrorListener;
import com.lansosdk.box.onDrawPadProgressListener;
import com.lansosdk.videoeditor.DrawPadAEExecute;
import com.lansosdk.videoeditor.MediaInfo;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.ae.AEConfig;
import com.novv.dzdesk.res.ae.AELayer;
import com.novv.dzdesk.res.ae.LSOAeImageImpl;
import com.novv.dzdesk.res.ae.LSOAeTextImpl;
import com.novv.dzdesk.res.ae.LayerConf;
import com.novv.dzdesk.res.ae.MVLayer;
import com.novv.dzdesk.res.ae.MapKeyComparator;
import com.novv.dzdesk.res.ae.VideoLayer;
import com.novv.dzdesk.ui.dialog.ProcessDialog;
import com.novv.dzdesk.ui.presenter.PresentAeEdit;
import com.novv.dzdesk.ui.view.BottomFinishLayout;
import com.novv.dzdesk.ui.view.SmartScrollView;
import com.novv.dzdesk.util.DeviceUtil;
import com.novv.dzdesk.util.PermissionsUtils;
import com.novv.dzdesk.util.SelectorFactory;
import com.novv.dzdesk.util.ToastUtil;
import com.novv.dzdesk.util.UmUtil;
import com.novv.dzdesk.util.UriUtils;
import com.umeng.analytics.MobclickAgent;
import com.yalantis.ucrop.UCrop;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AEDialogActivity extends XAppCompatActivity<PresentAeEdit> implements
    View.OnClickListener, OnLSOAeJsonLoadedListener {

  private static final int CROP_CODE = 333;
  private static final int START_CODE = 100;
  private LinearLayout mContainerImg, mContainerText;
  private AEConfig aeConfig;
  private ProcessDialog progressDialog;
  private DrawPadAEExecute execute;
  private SparseArray<WeakReference<View>> mImageRequestViews = new SparseArray<>();
  private SparseArray<WeakReference<View>> mTextRequestViews = new SparseArray<>();
  private SparseArray<String> idCodeArray = new SparseArray<>();
  private List<LSOAeTextImpl> mLSOAeTexts = new ArrayList<>();
  private TreeMap<String, LSOAeImageImpl> mLSOAeImageTreeMap = new TreeMap<>();
  private LSOAeDrawable drawable;
  private View mBtnAction;
  private View scrollViewImage;
  private SmartScrollView scrollViewText;
  private BottomFinishLayout mLayout;

  @Override
  public int getLayoutId() {
    return R.layout.dialog_ae_video_edit;
  }

  @Nullable
  @Override
  public PresentAeEdit newP() {
    return new PresentAeEdit();
  }

  @Override
  public void setUpView(@NonNull View rootView) {
    super.setUpView(rootView);
    mLayout = findViewById(R.id.root_view);
    mContainerImg = findViewById(R.id.ll_container_img);
    mContainerText = findViewById(R.id.ll_container_text);
    mBtnAction = findViewById(R.id.btn_action);
    scrollViewImage = findViewById(R.id.scrollView_image);
    scrollViewText = findViewById(R.id.scrollView_text);
    mBtnAction.setOnClickListener(this);
    mBtnAction.setEnabled(false);
    mBtnAction.setBackground(SelectorFactory.newShapeSelector()
        .setDefaultBgColor(Color.parseColor("#ff8319f9"))
        .setDisabledBgColor(Color.parseColor("#ffe8e8e8"))
        .setCornerRadius(DeviceUtil.dip2px(context, 20))
        .create());
    //检查文件名
    MediaInfo.checkFile(new File(Environment.getExternalStorageDirectory(), "avideoshare/color.mp4")
        .getAbsolutePath());
  }

  @Override
  public void initData(@Nullable Bundle savedInstanceState) {
    WindowManager m = getWindowManager();
    Display d = m.getDefaultDisplay();
    WindowManager.LayoutParams p = getWindow().getAttributes();
    p.height = DeviceUtil.dip2px(context, 330);
    p.width = d.getWidth();
    p.gravity = Gravity.BOTTOM;
    getWindow().setAttributes(p);
    mContainerText
        .setLayoutParams(new ScrollView.LayoutParams(d.getWidth(),
            DeviceUtil.dip2px(context, 200)));
    mContainerImg
        .setLayoutParams(new HorizontalScrollView.LayoutParams(d.getWidth(),
            DeviceUtil.dip2px(context, 122)));
    setFinishOnTouchOutside(true);
    aeConfig = (AEConfig) getIntent().getSerializableExtra(AEConfig.class.getSimpleName());
    if (aeConfig == null) {
      finish();
      return;
    }
    mLayout.setOnFinishListener(new BottomFinishLayout.OnFinishListener() {
      @Override
      public void onFinish() {
        finish();
      }
    });
    LSOLoadAeJsons.loadAsync(this, new String[] { aeConfig.jsonPath }, this);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void onCompositionsLoaded(LSOAeDrawable[] lsoAeDrawables) {
    int height = 35;
    int textHeight = 0;
    int imageHeight = 0;
    int code = START_CODE;
    if (aeConfig != null && lsoAeDrawables != null && lsoAeDrawables.length > 0) {
      drawable = lsoAeDrawables[0];
      if (drawable != null) {
        List<LSOAeText> texts = drawable.getJsonTexts();
        if (texts != null && !texts.isEmpty()) {
          for (int i = 0; i < texts.size(); i++) {
            textHeight = 200;
            scrollViewText.setDisallow(true);
            mLSOAeTexts.add(new LSOAeTextImpl(texts.get(i)));
            View textView = getAEText(i);
            LogUtils.i("add textview--->" + texts.get(i).text);
            mContainerText.addView(textView);
            mTextRequestViews.put(i, new WeakReference<>(textView));
          }
        } else {
          scrollViewText.setVisibility(View.GONE);
        }
        Map<String, LSOAeImage> maps = drawable.getJsonImages();
        TreeMap<String, LSOAeImage> treeMap = sortMapByKey(maps);
        if (treeMap != null && !treeMap.isEmpty()) {
          for (String key : treeMap.keySet()) {
            imageHeight = 122;
            LSOAeImage lsoAeImage = treeMap.get(key);
            if (lsoAeImage != null) {
              LSOAeImageImpl lsoAeImageImpl = new LSOAeImageImpl(lsoAeImage, aeConfig);
              mLSOAeImageTreeMap.put(lsoAeImageImpl.getId(), lsoAeImageImpl);
              idCodeArray.put(code, lsoAeImageImpl.getId());
              View image = getAEImage(code, lsoAeImageImpl);
              mImageRequestViews.put(code, new WeakReference<>(image));
              mContainerImg.addView(image);
              code++;
            }
          }
        } else {
          scrollViewImage.setVisibility(View.GONE);
        }
      }
    }
    height = height + 15 + imageHeight + textHeight + 80;
    WindowManager m = getWindowManager();
    Display d = m.getDefaultDisplay();
    android.view.WindowManager.LayoutParams p = getWindow().getAttributes();
    p.height = DeviceUtil.dip2px(context, height);
    p.width = d.getWidth();
    p.gravity = Gravity.BOTTOM;
    getWindow().setAttributes(p);
  }

  private TreeMap<String, LSOAeImage> sortMapByKey(Map<String, LSOAeImage> map) {
    if (map == null || map.isEmpty()) {
      return null;
    }
    TreeMap<String, LSOAeImage> sortMap = new TreeMap<>(
        new MapKeyComparator());
    sortMap.putAll(map);
    return sortMap;
  }

  private View getAEImage(final int requestCode, LSOAeImageImpl aeImage) {
    View view = LayoutInflater.from(this).inflate(R.layout.item_ae_img, mContainerImg, false);
    view.setLayoutParams(new LinearLayout.LayoutParams(DeviceUtil.dip2px(this, 96),
        DeviceUtil.dip2px(this, 122)));
    ImageView imageView = view.findViewById(R.id.imageView);
    imageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        PermissionsUtils.checkStorage(context, new PermissionsUtils.OnPermissionBack() {
          @Override public void onResult(boolean success) {
            try {
              Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                  .setType("image/*")
                  .addCategory(Intent.CATEGORY_OPENABLE);
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String[] mimeTypes = { "image/jpeg", "image/png" };
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
              }
              startActivityForResult(Intent.createChooser(intent, "选择图片"), requestCode);
            } catch (Exception e) {
              ToastUtil.showToast(context, "打开相册失败");
            }
          }
        });
      }
    });
    RoundedCorners roundedCorners = new RoundedCorners(20);
    RequestOptions options = RequestOptions.bitmapTransform(roundedCorners);
    options.transform(new CenterCrop(), new RoundedCorners(20));
    Glide.with(this).load(new File(aeImage.getAbsFilePath())).apply(options).into(imageView);
    return view;
  }

  /**
   * 已经选取了裁剪后的图片
   *
   * @param id lsoAeImageImpl id
   * @param requestCode requestCode
   * @param path 图片路径
   */
  public void onPhotoSelected(final String id, int requestCode, String path) {
    final LSOAeImageImpl lsoAeImageImpl = mLSOAeImageTreeMap.get(id);
    WeakReference<View> ref = mImageRequestViews.get(requestCode - CROP_CODE);
    View view;
    if (lsoAeImageImpl != null && ref != null && (view = ref.get()) != null) {
      final ImageView imageView = view.findViewById(R.id.imageView);
      RoundedCorners roundedCorners = new RoundedCorners(20);
      RequestOptions options = RequestOptions.bitmapTransform(roundedCorners);
      options.transform(new CenterCrop(), new RoundedCorners(20));
      Glide.with(this)
          .load(path)
          .apply(new RequestOptions()
              .skipMemoryCache(true)
              .centerCrop()
              .placeholder(R.drawable.placeholder_sq)
              .error(R.drawable.placeholder_sq)
              .diskCacheStrategy(DiskCacheStrategy.NONE))
              .apply(options)
          .into(imageView);
      lsoAeImageImpl.setNewPath(path);
      mLSOAeImageTreeMap.put(id, lsoAeImageImpl);
      changeBtn();
    }
  }

  private void changeBtn() {
    boolean isChanged = false;
    for (String id : mLSOAeImageTreeMap.keySet()) {
      LSOAeImageImpl lsoAeImage;
      if ((lsoAeImage = mLSOAeImageTreeMap.get(id)) != null && lsoAeImage.isChanged()) {
        isChanged = true;
        break;
      }
    }
    for (LSOAeTextImpl lsoAeText : mLSOAeTexts) {
      if (lsoAeText.isChanged()) {
        isChanged = true;
        break;
      }
    }
    mBtnAction.setEnabled(isChanged);
  }

  private View getAEText(final int index) {
    final LSOAeTextImpl lsoAeText = mLSOAeTexts.get(index);
    View view = LayoutInflater.from(this).inflate(R.layout.item_ae_text, mContainerText, false);
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.dip2px(this, 55));
    lp.leftMargin = DeviceUtil.dip2px(this, 10);
    lp.rightMargin = DeviceUtil.dip2px(this, 10);
    view.setLayoutParams(lp);
    EditText editText = view.findViewById(R.id.edit_input);
    String s = lsoAeText.getLSOAeText().text;
    editText.setText(s);
    editText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        lsoAeText.setNewText(s.toString());
        mLSOAeTexts.set(index, lsoAeText);
        changeBtn();
      }
    });
    GradientDrawable drawable = new GradientDrawable();
    drawable.setColor(Color.parseColor("#414141"));
    drawable.setCornerRadius(10);
    editText.setBackground(drawable);
    return view;
  }

  private void showProgress(long currentTimeUs) {
    if (progressDialog != null && execute != null) {
      float time = (float) currentTimeUs / (float) execute.getDuration();
      int b = Math.round(time * 100);
      if (b < 100) {
        LogUtils.i("progress--->" + b);
        progressDialog.setProgress(b);
      }
    }
  }

  private void hideProgressDialog() {
    if (progressDialog != null) {
      progressDialog.release();
      progressDialog = null;
    }
  }

  /**
   * 开始生成视频
   */
  private void startExecute() {
    if (aeConfig == null) {
      Toast.makeText(context, "模板信息获取失败，请退出重试", Toast.LENGTH_SHORT).show();
      return;
    }
    aeConfig.resetOutMp4();
    final LayerConf layerConf = new LayerConf();
    //添加视频背景
    layerConf.addLayer(new VideoLayer(aeConfig));
    layerConf.addLayer(new AELayer(aeConfig, mLSOAeTexts, mLSOAeImageTreeMap));
    //添加mv层
    layerConf.addLayer(new MVLayer(aeConfig));
    //设置水印
    layerConf.setShowWatermark(false);
    layerConf.setWatermark(aeConfig.watermark);
    if (!TextUtils.isEmpty(aeConfig.bgVideoPath) || new File(
        aeConfig.bgVideoPath).exists()) {
      LogUtils.i("execute-->带背景视频");
      execute = new DrawPadAEExecute(context, aeConfig.bgVideoPath,
          aeConfig.outMp4Path);
    } else {
      LogUtils.i("execute-->不带背景视频");
      execute = new DrawPadAEExecute(context, aeConfig.outMp4Path);
    }
    //初始化编辑
    if (drawable != null) {
      layerConf.updateExecute(drawable, execute);
    }
    execute.setDrawPadProgressListener(new onDrawPadProgressListener() {
      @Override
      public void onProgress(DrawPad drawPad, long currentTimeUs) {
        showProgress(currentTimeUs);
      }
    });
    execute.setDrawPadCompletedListener(
        new onDrawPadCompletedListener() {
          @Override
          public void onCompleted(DrawPad drawPad) {
            if (execute != null) {
              execute.release();
              execute = null;
            }
            layerConf.release();
            changeBtn();
            UmUtil.anaDIYClickGenerateSucess(context, aeConfig.mVmodel.id);
            hideProgressDialog();
            Intent intent = new Intent(context,
                AEVideoShareActivity.class);
            intent.putExtra("isFromMake", false);
            intent.putExtra("mp4Path", aeConfig.outMp4Path);
            intent.putExtra("mp4Id", aeConfig.mVmodel.id);
            startActivity(intent);
          }
        });
    execute.setDrawPadErrorListener(new onDrawPadErrorListener() {
      @Override
      public void onError(DrawPad drawPad, int i) {
        if (execute != null) {
          execute.cancel();
          execute = null;
        }
        layerConf.release();
        hideProgressDialog();
        changeBtn();
      }
    });
    execute.start();
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.btn_action) {
      MobclickAgent.onEvent(AEDialogActivity.this,UmCount.GenerateClick);
      mBtnAction.setEnabled(false);
      progressDialog = new ProcessDialog();
      progressDialog.setMsg("正在合成...").show(context);
      progressDialog.setOnCancelListener(new ProcessDialog.OnCancelListener() {
        @Override
        public void onCancel() {
          if (execute != null) {
            execute.cancel();
            execute = null;
            getVDelegate().toastShort("已取消");
            mBtnAction.setEnabled(true);
          }
        }
      });
      Run.getUiHandler().postDelayed(new Runnable() {
        @Override
        public void run() {
          if (aeConfig != null) {
            UmUtil.anaDIYClickGenerate(context, aeConfig.mVmodel.id);
            startExecute();
          }
        }
      }, 500);
    }
  }

  @Override
  protected void onResume() {
    MobclickAgent.onResume(this);
    super.onResume();
  }

  @Override
  protected void onPause() {
    MobclickAgent.onPause(this);
    super.onPause();
  }

  @Override
  protected void onStop() {
    super.onStop();
    hideProgressDialog();
    if (execute != null) {
      execute.cancel();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    LogUtils.i("onActivityResult resultCode:"
        + resultCode
        + ",requestCode"
        + requestCode
        + "data==null:"
        + (data == null));
    if (requestCode < START_CODE) {
      LogUtils.e("onActivityResult requestCode:" + requestCode);
    } else if (requestCode < CROP_CODE) {
      LogUtils.i("select result===>");
      if (data == null || data.getData() == null) {
        getVDelegate().toastShort("获取相册图片失败");
        return;
      }
      String id;
      LSOAeImageImpl image;
      if (idCodeArray != null && mLSOAeImageTreeMap != null && aeConfig != null) {
        id = idCodeArray.get(requestCode);
        if (!TextUtils.isEmpty(id) && (image = mLSOAeImageTreeMap.get(id)) != null) {
          LogUtils.e(
              "photo result id:" + image.getId() + ",requestCode:" + requestCode);
          int width = image.getWidth();
          int height = image.getHeight();

          String path = image.getId() + "_" + image.getWidth() + "x" + image.getHeight()
              + "."
              + Bitmap.CompressFormat.JPEG
              .toString();
          File dir = new File(aeConfig.unZipDir).getParentFile();
          if (!dir.exists()) {
            dir.mkdirs();
          }
          File file = new File(dir, path);
          try {
            file.createNewFile();
          } catch (IOException e) {
            e.printStackTrace();
          }
          UCrop.Options options = new UCrop.Options();
          options.setToolbarCancelDrawable(R.mipmap.ic_close);
          options.setHideBottomControls(true);
          options.setFreeStyleCropEnabled(false);
          options.setStatusBarColor(ContextCompat.getColor(this, R.color.color_primary_dark));
          UCrop.of(data.getData(), Uri.fromFile(file))
              .withAspectRatio(width, height)
              .withMaxResultSize(width, height)
              .withOptions(options)
              .start(this, requestCode + CROP_CODE);
        }
      }
    } else {
      Uri uri;
      if (data == null || (uri = UCrop.getOutput(data)) == null) {
        getVDelegate().toastShort("取消裁剪");
        return;
      }
      String imgPath = uri.toString();
      if (imgPath.contains("file://")) {
        imgPath = imgPath.replace("file://", "");
      } else if (imgPath.contains("content://")) {
        imgPath = UriUtils.getImagePathByUri(this, uri);
      }
      LogUtils.i("crop result," + uri.toString() + " ,convert to:" + imgPath);
      String id;
      LSOAeImageImpl image;
      if (idCodeArray != null && mLSOAeImageTreeMap != null) {
        id = idCodeArray.get(requestCode - CROP_CODE);
        if (id != null) {
          image = mLSOAeImageTreeMap.get(id);
          if (image != null) {
            LogUtils.e(
                "crop result id:" + image.getId() + ",requestCode:" + requestCode);
            onPhotoSelected(image.getId(), requestCode, imgPath);
          }
        }
      }
    }
  }

  @Override
  public void finish() {
    super.finish();
    overridePendingTransition(R.anim.bottom_sheet_slide_in, R.anim.bottom_sheet_slide_out);
  }
}
