package com.novv.dzdesk.ui.activity.vwp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;
import com.ark.baseui.XAppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.danikula.videocache.HttpProxyCacheServer;
import com.gyf.barlibrary.ImmersionBar;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.novv.dzdesk.Const;
import com.novv.dzdesk.R;
import com.novv.dzdesk.VVApplication;
import com.novv.dzdesk.res.model.ResourceBean;
import com.novv.dzdesk.util.LogUtil;
import com.novv.dzdesk.util.PermissionsUtils;
import com.novv.dzdesk.util.PlatformUtil;
import com.novv.dzdesk.util.ToastUtil;
import com.novv.dzdesk.util.VideoNotifyUtils;
import com.umeng.analytics.MobclickAgent;
import java.io.File;
import jp.wasabeef.blurry.Blurry;

public class VwpShareActivity extends XAppCompatActivity implements
    View.OnClickListener {

  private static final String tag = VwpShareActivity.class.getSimpleName();
  private static final String SHARE_RES_KEY = "Detail_res_key";
  private File mMp4File;
  private VideoView videoPlayer;
  private ImmersionBar immersionBar;
  private ResourceBean mItem;
  private boolean isDownload;
  private ImageView bgImgV;
  private int progress;

  public static void launch(Context context, ResourceBean bean) {
    Intent intent = new Intent(context, VwpShareActivity.class);
    intent.putExtra(SHARE_RES_KEY, bean);
    context.startActivity(intent);
  }

  @Override
  public int getLayoutId() {
    return R.layout.activity_sharevideo;
  }

  @Override
  public void setUpView(@NonNull View rootView) {
    super.setUpView(rootView);
    View backView = findViewById(R.id.detail_top_rl);
    immersionBar = ImmersionBar.with(this)
        .titleBar(backView, false)
        .transparentBar();
    immersionBar.init();

    videoPlayer = findViewById(R.id.videoPlayer);
    bgImgV = findViewById(R.id.background_imgv);
    View tvShareQQ = findViewById(R.id.tv_shareQQ);
    View tvShareWX = findViewById(R.id.tv_shareWX);

    tvShareQQ.setOnClickListener(this);
    tvShareWX.setOnClickListener(this);
    backView.setOnClickListener(this);
  }

  @Nullable
  @Override
  public Object newP() {
    return null;
  }

  @Override
  public void initData(@Nullable Bundle bundle) {
    mItem = (ResourceBean) getIntent().getSerializableExtra(SHARE_RES_KEY);
    Glide.with(this).asBitmap().load(mItem.getCoverURL()).into(new SimpleTarget<Bitmap>() {
      @Override
      public void onResourceReady(@NonNull Bitmap bitmap,
          @Nullable Transition<? super Bitmap> transition) {
        Blurry.with(VwpShareActivity.this)
            .sampling(2)
            .animate(500)
            .from(bitmap)
            .into(bgImgV);
      }

      @Override
      public void onLoadFailed(@Nullable Drawable errorDrawable) {
        super.onLoadFailed(errorDrawable);
        bgImgV.setVisibility(View.GONE);
      }
    });
    final String fileDir = Const.Dir.SHARE_PATH;
    final String downloadFileName = mItem.get_id() + ".mp4";
    mMp4File = new File(fileDir, downloadFileName);

    resetVideoPath();
    if (mMp4File.exists() && mMp4File.length() != 0) {
      LogUtil.i(tag, "已下载");
      isDownload = true;
    } else {
      isDownload = false;
      LogUtil.i(tag, "开始下载");
      PermissionsUtils.checkStorage(this, new PermissionsUtils.OnPermissionBack() {
        @Override public void onResult(boolean success) {
          if (success) {
            FileDownloader.getImpl().create(mItem.getShare_video())
                .setPath(fileDir + "/" + downloadFileName, false)
                .setListener(new FileDownloadLargeFileListener() {
                  @Override
                  protected void pending(BaseDownloadTask task, long soFarBytes,
                      long totalBytes) {

                  }

                  @Override
                  protected void progress(BaseDownloadTask task, long soFarBytes,
                      long totalBytes) {
                    float p = soFarBytes / totalBytes;
                    VwpShareActivity.this.progress = (int) p * 100;
                  }

                  @Override
                  protected void paused(BaseDownloadTask task, long soFarBytes,
                      long totalBytes) {

                  }

                  @Override
                  protected void completed(BaseDownloadTask task) {
                    isDownload = true;
                    VideoNotifyUtils.notify(VwpShareActivity.this, mMp4File);
                  }

                  @Override
                  protected void error(BaseDownloadTask task, Throwable e) {
                    ToastUtil.showToast(VwpShareActivity.this, "下载失败" + e.getMessage());
                  }

                  @Override
                  protected void warn(BaseDownloadTask task) {

                  }
                })
                .start();
          }
        }
      });
    }
  }

  private void resetVideoPath() {
    if (mMp4File.exists() && mMp4File.length() != 0) {
      videoPlayer.setVideoPath(mMp4File.getAbsolutePath());
      videoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
          mediaPlayer.setLooping(true);
          videoPlayer.start();
          bgImgV.setVisibility(View.GONE);
        }
      });
    } else {
      HttpProxyCacheServer proxy = getProxy();
      String proxyUrl = proxy.getProxyUrl(mItem.getShare_video());
      videoPlayer.setVideoPath(proxyUrl);
      videoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
          mediaPlayer.setLooping(true);
          videoPlayer.start();
          bgImgV.setVisibility(View.GONE);
        }
      });
    }
  }

  private HttpProxyCacheServer getProxy() {
    return VVApplication.getProxy(getApplicationContext());
  }

  @SuppressLint("DefaultLocale")
  @Override
  public void onClick(View v) {
    if (!isDownload) {
      getVDelegate().toastShort(String.format("视频已下载%d%%，请稍后", progress));
      return;
    }
    switch (v.getId()) {
      case R.id.tv_shareQQ:
        PlatformUtil.shareToQQ(this, mMp4File.getAbsolutePath(), "video/*");
        break;
      case R.id.tv_shareWX:
        PlatformUtil.shareWxSession(this, mMp4File.getAbsolutePath(), "video/*");
        break;
      case R.id.detail_top_rl:
        finish();
        break;
      default:
        break;
    }
  }

  @Override
  protected void onResume() {
    MobclickAgent.onResume(this);

    super.onResume();
    if (videoPlayer != null && !videoPlayer.isPlaying()) {
      videoPlayer.start();
    }
  }

  @Override
  protected void onPause() {
    MobclickAgent.onPause(this);
    super.onPause();
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (videoPlayer != null) {
      videoPlayer.pause();
    }
  }

  @Override
  protected void onDestroy() {
    if (immersionBar != null) {
      immersionBar.destroy();
    }
    if (videoPlayer != null) {
      videoPlayer.stopPlayback();
    }
    super.onDestroy();
  }
}
