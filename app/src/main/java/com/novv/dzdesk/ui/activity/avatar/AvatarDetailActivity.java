package com.novv.dzdesk.ui.activity.avatar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import com.ark.adkit.basics.data.ADMetaData;
import com.ark.adkit.polymers.polymer.ADTool;
import com.ark.baseui.XAppCompatActivity;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.novv.dzdesk.Const;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.model.AvatarBean;
import com.novv.dzdesk.res.user.LoginContext;
import com.novv.dzdesk.ui.adapter.avatar.AdapterAvatarRes;
import com.novv.dzdesk.util.APPAlertUtils;
import com.novv.dzdesk.util.ToastUtil;
import com.novv.dzdesk.util.UmConst;
import com.novv.dzdesk.util.UmUtil;
import com.umeng.analytics.MobclickAgent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AvatarDetailActivity extends XAppCompatActivity {

  private static final String KEY_AVATAR_LIST = "key_avatar_list";
  private static final String KEY_AVATAR_LIST_POS = "key_avatar_list_pos";
  private AdapterAvatarRes mAdapter;
  private RecyclerView recyclerView;
  private int cPos, position;
  private LinearLayoutManager mLayoutManager;
  private View btnBack;
  private Button flDownload;
  private boolean reDownload;
  private FrameLayout flContainer;
  private ProgressDialog mProgressDialog;
  private int lastPos = -1;

  public static void launch(Context context, ArrayList<AvatarBean> avatarBeans, int pos) {
    Intent intent = new Intent(context, AvatarDetailActivity.class);
    intent.putExtra(KEY_AVATAR_LIST, avatarBeans);
    intent.putExtra(KEY_AVATAR_LIST_POS, pos);
    context.startActivity(intent);
  }

  @Nullable
  @Override
  public Object newP() {
    return null;
  }

  @Override
  public int getLayoutId() {
    return R.layout.activity_avatar_details;
  }

  @Override
  public void setUpView(@NonNull View rootView) {
    super.setUpView(rootView);
    flDownload = findViewById(R.id.flDownload);
    btnBack = findViewById(R.id.btnBack);
    flContainer = findViewById(R.id.fl_container);
    recyclerView = findViewById(R.id.rvDetails);
  }

  @Override
  public void initData(@Nullable Bundle savedInstanceState) {
    List<AvatarBean> list = null;
    try {
      //noinspection unchecked
      list = (List<AvatarBean>) getIntent().getSerializableExtra(KEY_AVATAR_LIST);
    } catch (Exception e) {
      e.printStackTrace();
    }
    List<AvatarBean> mList = new ArrayList<>();
    cPos = getIntent().getIntExtra(KEY_AVATAR_LIST_POS, 0);
    if (list != null && !list.isEmpty()) {
      mList.addAll(list);
    }

    mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    recyclerView.setLayoutManager(mLayoutManager);
    mAdapter = new AdapterAvatarRes(mList, true);
    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
          position = mLayoutManager.findFirstVisibleItemPosition();
          List<AvatarBean> avatarBeans = mAdapter.getData();
          if (position < avatarBeans.size()) {
            if (lastPos == position) {
              return;
            }
            lastPos = position;
            AvatarBean avatarBean = avatarBeans.get(position);
            if (!avatarBean.getFlagIsAd()) {
              String dir = Const.Dir.AVATAR_DIR;
              String name = "thumb_" + avatarBean.getIdStr() + ".jpg";
              File file = new File(dir, name);
              setDownloadButton(file.exists());
            }
            reloadAd(false);
          }
        }
      }

      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
      }
    });
    recyclerView.scrollToPosition(cPos);
    recyclerView.setAdapter(mAdapter);
    new PagerSnapHelper().attachToRecyclerView(recyclerView);
    mProgressDialog = new ProgressDialog(this);
    mProgressDialog.setMessage("下载中...");
    mProgressDialog.setIndeterminate(true);
    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    mProgressDialog.setCancelable(true);
    mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
      @Override
      public void onCancel(DialogInterface dialog) {
        FileDownloader.getImpl().clearAllTaskData();
      }
    });
    flDownload.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        List<AvatarBean> avatarBeans = mAdapter.getData();
        if (position < avatarBeans.size()) {
          AvatarBean avatarBean = avatarBeans.get(position);
          if (!avatarBean.getFlagIsAd()) {
            download(avatarBean);
          }
        }
        UmUtil.anaEvent(AvatarDetailActivity.this, UmConst.CLICK_DOWNLOAD_AVATAR);
      }
    });
    btnBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
    reloadAd(true);
  }

  private void reloadAd(boolean reload) {
    if (LoginContext.getInstance().isVip()) {
      return;
    }
    if (!reload) {
      ADTool.getADTool().getManager()
          .getNativeWrapper()
          .loadBannerView(AvatarDetailActivity.this, flContainer);
      return;
    }
    ADMetaData adMetaData = ADTool.getADTool().getManager()
        .getNativeWrapper()
        .getDetailNative(this);
    if (adMetaData == null) {
      new Handler(getMainLooper()).postDelayed(new Runnable() {
        @Override
        public void run() {
          ADTool.getADTool().getManager()
              .getNativeWrapper()
              .loadBannerView(AvatarDetailActivity.this, flContainer);
        }
      }, 1000);
    } else {
      ADTool.getADTool().getManager()
          .getNativeWrapper()
          .loadBannerView(AvatarDetailActivity.this, flContainer, adMetaData, null);
    }
  }

  private void download(AvatarBean avatarBean) {
    download(avatarBean, false);
  }

  private void download(final AvatarBean avatarBean, final boolean set) {
    new APPAlertUtils()
        .handleAvatar(AvatarDetailActivity.this, new APPAlertUtils.OnAlertListener() {
          @Override
          public void onAlertNext() {
            String thumb = avatarBean.getThumb();
            String dir = Const.Dir.AVATAR_DIR;
            String name = "thumb_" + avatarBean.getIdStr() + ".jpg";
            File file = new File(dir, name);
            FileDownloader.getImpl().create(thumb)
                .setPath(file.getAbsolutePath())
                .setForceReDownload(reDownload)
                .setListener(new FileDownloadSampleListener() {

                  @Override
                  protected void pending(BaseDownloadTask task, int soFarBytes,
                      int totalBytes) {
                    super.pending(task, soFarBytes, totalBytes);
                    if (!isFinishing() && mProgressDialog != null && !mProgressDialog.isShowing()) {
                      mProgressDialog.show();
                    }
                  }

                  @Override
                  protected void progress(BaseDownloadTask task, int soFarBytes,
                      int totalBytes) {
                    super.progress(task, soFarBytes, totalBytes);
                    if (mProgressDialog != null) {
                      mProgressDialog.setIndeterminate(false);
                      mProgressDialog.setMax(100);
                      mProgressDialog.setProgress(soFarBytes / totalBytes * 100);
                    }
                  }

                  @Override
                  protected void completed(final BaseDownloadTask task) {
                    super.completed(task);
                    reDownload = false;
                    if (mProgressDialog != null) {
                      mProgressDialog.dismiss();
                    }
                    onDownloadSuccess(task.getPath(), set);
                  }

                  @Override
                  protected void error(BaseDownloadTask task, Throwable e) {
                    super.error(task, e);
                    if (mProgressDialog != null) {
                      mProgressDialog.dismiss();
                    }
                    reDownload = true;
                    ToastUtil.showGeneralToast(AvatarDetailActivity.this,
                        "图片下载失败,请重试");
                  }
                }).start();
          }
        });
    UmUtil.anaEvent(this, UmConst.VIEW_AVATAR_DETAIL);
  }

  private void onDownloadSuccess(final String path, boolean set) {
    setDownloadButton(true);
    ToastUtil.showGeneralToast(AvatarDetailActivity.this, "已下载," + path);
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (mProgressDialog != null && mProgressDialog.isShowing()) {
      mProgressDialog.dismiss();
    }
  }

  private void setDownloadButton(boolean download) {
    if (flDownload != null) {
      if (download) {
        flDownload.setText("已下载");
        flDownload.setAlpha(0.5f);
        flDownload.setBackground(
            ContextCompat.getDrawable(AvatarDetailActivity.this,
                R.drawable.bg_btn_download_s));
      } else {
        flDownload.setText("下载");
        flDownload.setAlpha(1f);
        flDownload.setBackground(
            ContextCompat.getDrawable(AvatarDetailActivity.this,
                R.drawable.bg_btn_download_n));
      }
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
}
