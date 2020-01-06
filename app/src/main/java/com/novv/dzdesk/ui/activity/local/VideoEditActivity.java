package com.novv.dzdesk.ui.activity.local;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.ark.baseui.XAppCompatActivity;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.novv.dzdesk.Const;
import com.novv.dzdesk.R;
import com.novv.dzdesk.http.DefaultDisposableObserver;
import com.novv.dzdesk.http.DefaultScheduler;
import com.novv.dzdesk.http.ServerApi;
import com.novv.dzdesk.live.PrefUtil;
import com.novv.dzdesk.res.model.BaseResult;
import com.novv.dzdesk.res.model.CategoryBean;
import com.novv.dzdesk.res.model.RxAsyncTask;
import com.novv.dzdesk.ui.view.CutView;
import com.novv.dzdesk.ui.view.TwoSideSeekBar;
import com.novv.dzdesk.util.DeviceUtil;
import com.novv.dzdesk.util.FileUtil;
import com.novv.dzdesk.util.JoeVideo;
import com.novv.dzdesk.util.LogUtil;
import com.novv.dzdesk.util.ToastUtil;
import com.novv.dzdesk.util.VideoNotifyUtils;
import com.novv.dzdesk.util.VideoTimeUtils;
import com.novv.dzdesk.util.VideoTypeUtils;
import com.qiniu.pili.droid.shortvideo.PLMediaFile;
import com.qiniu.pili.droid.shortvideo.PLVideoFrame;
import com.umeng.analytics.MobclickAgent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 视频编辑页面 Created by lijianglong on 2017/8/7.
 */
@SuppressLint("DefaultLocale")
public class VideoEditActivity extends XAppCompatActivity {

  private static final String VIDEO_PATH = "video_path";
  private static final String IS_UPLOAD = "is_upload";
  private static final String tag = VideoEditActivity.class.getSimpleName();

  private float mCurrentX;
  private float mCurrentY;
  private String videoDuration = "10000";
  private String videoPath;
  private String videoName;
  private int videoHeight = 720;
  private int videoWidth = 1280;
  private float rotate = 0f;
  private int screenWidth;

  private TextView tvLeft;
  private TextView tvCenter;
  private TextView tvRight;
  private TextView tvCrop;
  private LinearLayout llTime;
  private CutView cv_video;
  private RelativeLayout rlVideo;
  private VideoView vv_play;
  private RecyclerView recyclerView;
  private TwoSideSeekBar seekBar;

  private boolean isUpload;
  private boolean isCrop;

  private List<String> mData = new ArrayList<>();
  private File outFile;
  private ThumbnailAdapter adapter;
  private SweetAlertDialog pDialog;
  private JoeVideo.OnEditorListener editListener = new JoeVideo.OnEditorListener() {

    @Override
    public void onSuccess() {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          tvCrop.setEnabled(true);
          pDialog.setTitleText(String.format("已完成 %d%% %n", 100));
          pDialog.dismissWithAnimation();
          VideoNotifyUtils.notify(VideoEditActivity.this, outFile);
          ToastUtil
              .showToast(VideoEditActivity.this,
                  getResources().getString(R.string.video_saved));
          LocalVideoActivity.launch(VideoEditActivity.this, outFile.getPath(), isUpload);
          finish();
        }
      });
    }

    @Override
    public void onFailure() {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          pDialog.setTitleText(getResources().getString(R.string.video_crop_fail))
              .setConfirmText(getResources().getString(R.string.video_crop_ensure))
              .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                  pDialog.dismissWithAnimation();
                }
              })
              .changeAlertType(SweetAlertDialog.ERROR_TYPE);
        }
      });
    }

    @Override
    public void onProgress(final int d) {
      LogUtil.e(tag, "--------" + d);
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          pDialog.setTitleText(String.format("已完成 %d%% %n", d));
          pDialog.getProgressHelper().setInstantProgress(d / 100f);
        }
      });
    }
  };
  private long cropTime;
  private Runnable runnable = new Runnable() {
    @Override
    public void run() {
      File file = new File(
          isUpload ? Const.Dir.VIDEO_CROP_PATH_UPLOAD : Const.Dir.VIDEO_CROP_PATH_LOCAL);
      if (!file.exists()) {
        //noinspection ResultOfMethodCallIgnored
        file.mkdirs();
      }
      long startTime = getCurrentTime(mCurrentX, mCurrentY) / 1000;
      String outFileName =
          VideoTimeUtils.getRandomFileName() + VideoTypeUtils.getVideoType(videoPath);
      outFile = new File(file, outFileName);
      try {
        //noinspection ResultOfMethodCallIgnored
        outFile.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
      if (isCrop) {
        //得到裁剪后的margin值
        float[] cutArr = cv_video.getCutArr();
        float left = cutArr[0];
        float top = cutArr[1];
        float right = cutArr[2];
        float bottom = cutArr[3];
        int cutWidth = cv_video.getRectWidth();
        int cutHeight = cv_video.getRectHeight();

        //计算宽高缩放比
        float leftPro = left / cutWidth;
        float topPro = top / cutHeight;
        float rightPro = right / cutWidth;
        float bottomPro = bottom / cutHeight;

        //得到裁剪位置
        int cropWidth = (int) (videoWidth * (rightPro - leftPro));
        int cropHeight = (int) (videoHeight * (bottomPro - topPro));
        int x = (int) (leftPro * videoWidth);
        int y = (int) (topPro * videoHeight);
        JoeVideo.cropVideo(videoPath
            , startTime
            , cropTime
            , cropWidth
            , cropHeight
            , x
            , y
            , outFile.getPath()
            , editListener);
      } else {
        JoeVideo.cropVideo(videoPath
            , startTime
            , cropTime
            , outFile.getAbsolutePath()
            , editListener);
      }
    }
  };
  private long maxTime;
  private TwoSideSeekBar.OnVideoStateChangeListener listener = new TwoSideSeekBar
      .OnVideoStateChangeListener() {
    @Override
    public void onStart(float x, float y) {
      mCurrentX = x;
      mCurrentY = y;
      updateTime();
      if (vv_play != null) {
        vv_play.seekTo(getCurrentTime(mCurrentX, mCurrentY));
      }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onEnd() {
      if (vv_play != null) {
        vv_play.seekTo(getCurrentTime(mCurrentX, mCurrentY));
      }
      updateTime();
    }
  };
  private PLMediaFile plMediaFile;

  public static void launch(Context context, String path, boolean isUpload) {
    if (TextUtils.isEmpty(path) || !new File(path).exists()) {
      ToastUtil.showToast(context,
          context.getResources().getString(R.string.video_file_not_exist));
      return;
    }
    Intent intent = new Intent(context, VideoEditActivity.class);
    intent.putExtra(VIDEO_PATH, path);
    intent.putExtra(IS_UPLOAD, isUpload);
    context.startActivity(intent);
  }

  @Override
  public int getLayoutId() {
    return R.layout.activity_video_edit;
  }

  @Override
  public void setUpView(@NonNull View rootView) {
    super.setUpView(rootView);
    recyclerView = findViewById(R.id.recycler);
    vv_play = findViewById(R.id.video_view);
    seekBar = findViewById(R.id.seekbar);
    tvCrop = findViewById(R.id.tv_crop);
    RelativeLayout rlClose = findViewById(R.id.rl_close);
    tvCenter = findViewById(R.id.tv_center);
    tvLeft = findViewById(R.id.tv_left);
    tvRight = findViewById(R.id.tv_right);
    llTime = findViewById(R.id.ll_time);
    cv_video = findViewById(R.id.cut_video);
    rlVideo = findViewById(R.id.rl_video);
    tvCrop.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        cropVideo();
      }
    });
    rlClose.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
    seekBar.setOnVideoStateChangeListener(listener);
  }

  @Nullable
  @Override
  public Object newP() {
    return null;
  }

  @Override
  public void initData(@Nullable Bundle bundle) {
    videoPath = getIntent().getStringExtra(VIDEO_PATH);
    isUpload = getIntent().getBooleanExtra(IS_UPLOAD, false);
    if (TextUtils.isEmpty(videoPath)) {
      ToastUtil.showToast(this, getResources().getString(R.string.op_failed));
      finish();
      return;
    }
    videoName = FileUtil.getFileNameWithoutSuffix(videoPath);
    mCurrentX = DeviceUtil.dip2px(this, 30);
    screenWidth = DeviceUtil.getRealDisplayW(this);
    //判断是否支持裁剪
    if (VideoTypeUtils.checkVideoType(videoPath) == 1) {
      ToastUtil.showLongTimeToast(this,
          getResources().getString(R.string.video_type_not_support));
    }
    try {
      initData(); //初始化视频文件播放，设置视频长宽时长等
    } catch (Exception e) {
      e.printStackTrace();
      ToastUtil.showToast(this, R.string.op_failed);
    } catch (Error e) {
      e.printStackTrace();
      ToastUtil.showToast(this, R.string.op_failed);
    }
  }

  private void updateTime() {
    llTime.setVisibility(View.VISIBLE);
    long leftTime = getCurrentTime(mCurrentX, mCurrentY) / 1000L;
    long rightTime = getCurrentTime(mCurrentX, mCurrentY) + seekBar.getCropTime() / 1000L;
    rightTime = Math.min(rightTime, maxTime);
    cropTime = rightTime - leftTime;
    tvCenter.setText(
        String.format(getResources().getString(R.string.video_time_format),
            (int) cropTime));
    tvLeft.setText(VideoTimeUtils.convertSecondsToTime(leftTime));
    tvRight.setText(VideoTimeUtils.convertSecondsToTime(rightTime));
  }

  private void initData() {
    vv_play.setVideoPath(videoPath);//设置视频路径
    plMediaFile = new PLMediaFile(videoPath);
    videoWidth = plMediaFile.getVideoWidth();
    videoHeight = plMediaFile.getVideoHeight();
    rotate = plMediaFile.getVideoRotation();
    videoDuration = String.valueOf(plMediaFile.getDurationMs());
    if (isRotate(rotate)) {
      int i = videoWidth;
      videoWidth = videoHeight;
      videoHeight = i;
    }
    LogUtil.e(tag, "视频时长：" + videoDuration);
    LogUtil.e(tag, "视频宽度：" + videoWidth);
    LogUtil.e(tag, "视频高度：" + videoHeight);
    if (TextUtils.equals(videoDuration, "0") || videoHeight == 0 || videoWidth == 0) {
      ToastUtil.showLongTimeToast(this, R.string.video_file_not_support);
      finish();
      return;
    }
    maxTime = Long.parseLong(videoDuration) / 1000 > 60 ? 60
        : Long.parseLong(videoDuration) / 1000;
    int duration = Integer.parseInt(videoDuration) / 1000;
    seekBar.setMaxDuration(duration > 60 ? 60 : duration);
    //横屏的视频需要裁剪
    if (videoHeight < videoWidth) {
      ViewGroup.LayoutParams layoutParams = rlVideo.getLayoutParams();
      layoutParams.width = screenWidth;
      layoutParams.height = screenWidth * 9 / 16;
      rlVideo.setLayoutParams(layoutParams);

      ViewGroup.LayoutParams layoutParams1 = vv_play.getLayoutParams();
      layoutParams1.width = screenWidth;
      layoutParams1.height = screenWidth * 9 / 16;
      vv_play.setLayoutParams(layoutParams1);

      ViewGroup.LayoutParams layoutParams2 = cv_video.getLayoutParams();
      layoutParams2.width = screenWidth;
      layoutParams2.height = screenWidth * 9 / 16;
      cv_video.setLayoutParams(layoutParams2);
      isCrop = true;
    } else {
      isCrop = false;
      cv_video.setVisibility(View.GONE);
    }
    vv_play.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
      @Override
      public void onPrepared(MediaPlayer mp) {
        vv_play.start();
        seekBar.resetIndicatorAnimator();
        updateTime();
      }
    });
    vv_play.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
      @Override
      public void onCompletion(MediaPlayer mp) {
        vv_play.start();
        seekBar.resetIndicatorAnimator();
        updateTime();
      }
    });
    vv_play.setOnErrorListener(new MediaPlayer.OnErrorListener() {
      @Override
      public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        LogUtil.e(tag, "MyOnErrorListener", "what=" + what + ", extra="
            + extra);
        ToastUtil.showToast(VideoEditActivity.this, R.string.op_failed);
        return true;
      }
    });
    initRecyclerView();//初始化下方视频截图
  }

  private boolean isRotate(float rotate) {
    float abs = Math.abs(rotate);
    return abs == 90.0F || abs == 270.0F;
  }

  private void initRecyclerView() {
    File file = new File(Const.Dir.THUMB_NAIL_PATH);
    if (!file.exists()) {
      //noinspection ResultOfMethodCallIgnored
      file.mkdirs();
    }
    for (int i = 1; i <= Integer.parseInt(videoDuration) / (1000 * N()) + 2; i++) {
      mData.add(Const.Dir.THUMB_NAIL_PATH + File.separator + videoName + "_" + i + ".jpg");
    }
    adapter = new ThumbnailAdapter(R.layout.item_thumbnail, mData);
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
          vv_play.seekTo(getCurrentTime(mCurrentX, mCurrentY));
          updateTime();
          seekBar.resetIndicatorAnimator();
        }
      }
    });
    getCategory();
  }

  private int getCurrentTime(float x, float y) {
    int position = recyclerView.getChildAdapterPosition(recyclerView.findChildViewUnder(x, y));
    return position == 0 ? 0 : (position - 1) * 1000 * N();
  }

  private int N() {
    int d = Integer.parseInt(videoDuration) / 1000;
    if (d <= 30) {
      return 1;//每1秒截一张图
    } else if (d <= 60) {
      return 2;//每2秒截一张图
    } else {
      return 3;//每3秒截一张图
    }
  }

  private void cropVideo() {
    vv_play.pause();
    try {
      if (pDialog == null) {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            .setTitleText("请稍后...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
      }
      if (!pDialog.isShowing()) {
        pDialog.show();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    tvCrop.setEnabled(false);
    new Thread(runnable).start();
  }

  /**
   * 只有上传需要这个参数，把更新分类请求放到裁剪处
   */
  private void getCategory() {
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

              Gson gson = new Gson();
              String str = gson.toJson(mList);
              PrefUtil.putString(VideoEditActivity.this,
                  LocalVideoActivity.FILE_NAME_CATEGORY_CACHE, str);
            }
          }
        });
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
    if (vv_play != null) {
      vv_play.pause();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (seekBar != null) {
      seekBar.release();
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (vv_play != null) {
      vv_play.pause();
    }
    if (pDialog != null) {
      pDialog.dismiss();
    }
  }

  private class ThumbnailAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    ThumbnailAdapter(@LayoutRes int layoutResId, @Nullable List<String> data) {
      super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String imagePath) {
      ImageView thumbnail = helper.getView(R.id.imv_crop);
      thumbnail.setLayoutParams(
          new FrameLayout.LayoutParams(seekBar.getSingleWidth(),
              seekBar.getSingleHeight()));
      int position = helper.getLayoutPosition();
      if (position == 0 || position == getItemCount() - 1) {
        thumbnail.setBackgroundColor(Color.BLACK);
        thumbnail.setImageBitmap(null);
      } else {
        thumbnail.setImageBitmap(null);
        File imageFile = new File(imagePath);
        if (!TextUtils.isEmpty(imagePath) && imageFile.exists()) {//如果图片路径不为空或者路径图片存在
          Glide.with(mContext).load(imageFile).into(thumbnail);
        } else {
          File file = new File(
              Const.Dir.THUMB_NAIL_PATH + File.separator + videoName + "_"
                  + N() * position
                  + ".jpg");
          if (file.exists()) {
            thumbnail.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
          } else {
            new GetVideoFrameTask(position).execute(position);
          }
        }
      }
    }
  }

  /**
   * 获取某一个位置的图片并存储到本地
   */
  private class GetVideoFrameTask extends RxAsyncTask<Integer, Integer, String> {

    private int position;

    GetVideoFrameTask(int position) {
      this.position = position;
    }

    @Override
    protected void onResult(String path) {
      super.onResult(path);
      if (!TextUtils.isEmpty(path)) {
        mData.set(position, path);
        adapter.notifyItemChanged(position);
      }
    }

    @Override
    protected String call(Integer... integers) {
      FileOutputStream fos = null;
      Bitmap bitmap = null;
      try {
        PLVideoFrame videoFrame =
            plMediaFile.getVideoFrameByTime(integers[0] * 1000 * 1000 * N(), true);
        bitmap = videoFrame.toBitmap();
        if (bitmap != null) {
          File f = new File(
              Const.Dir.THUMB_NAIL_PATH + File.separator + videoName + "_"
                  + N() * position
                  + ".jpg");
          //noinspection ResultOfMethodCallIgnored
          f.createNewFile();
          fos = new FileOutputStream(f);
          bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
          return f.getPath();
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        try {
          if (fos != null) {
            fos.flush();
            fos.close();
          }
          if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            //noinspection UnusedAssignment
            bitmap = null;
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      return null;
    }
  }
}
