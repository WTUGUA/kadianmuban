package com.novv.dzdesk.ui.fragment;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.ark.auth.Auth;
import com.ark.auth.AuthCallback;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.novv.dzdesk.Const;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.model.ResourceBean;
import com.novv.dzdesk.ui.activity.vwp.VwpShareActivity;
import com.novv.dzdesk.ui.dialog.BaseDialogFragment;
import com.novv.dzdesk.util.PlatformType;
import com.novv.dzdesk.util.ShareUtils;
import com.novv.dzdesk.util.ToastUtil;
import java.io.File;
import java.util.ArrayList;

/**
 * 分享对话框 Created by lingyfh on 2017/6/13.
 */
public class ShareFragment extends BaseDialogFragment implements
    View.OnClickListener {

  private static final String tag = ShareFragment.class.getSimpleName();
  private static final String KEY_ITEM = "key_itme";
  private ResourceBean mItem;
  private View shareQQ;
  private View shareQZone;
  private View shareWxSession;
  private View shareWxTimeLine;
  private View shareShortVideo;
  private View shareMore;
  private View closeView;
  private SweetAlertDialog mDialog;
  private File shareFile;
  private AuthCallback mAuthCallback = new AuthCallback() {

    @Override
    public void onSuccessForShare() {
      super.onSuccessForShare();
      Toast.makeText(mContext, "分享成功", Toast.LENGTH_SHORT).show();
      dismissAllowingStateLoss();
    }

    @Override
    public void onCancel() {
      super.onCancel();
      dismissAllowingStateLoss();
    }

    @Override
    public void onFailed(@NonNull String code, @NonNull String msg) {
      super.onFailed(code, msg);
      Log.e("logger", "code:" + code + ",msg:" + msg);
    }
  };

  public static ShareFragment launch(FragmentActivity activity, ResourceBean bean) {
    ShareFragment splash = new ShareFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(KEY_ITEM, bean);
    splash.setArguments(bundle);
    splash.show(activity, tag);
    return splash;
  }

  @Override
  public void setWindowSize(final Window window) {
    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    WindowManager.LayoutParams mWindowAttributes = window.getAttributes();
    mWindowAttributes.width = WindowManager.LayoutParams.MATCH_PARENT;
    mWindowAttributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
    mWindowAttributes.gravity = Gravity.BOTTOM;
    window.setAttributes(mWindowAttributes);
    window.getDecorView()
        .setOnSystemUiVisibilityChangeListener(
            new View.OnSystemUiVisibilityChangeListener() {
              @Override
              public void onSystemUiVisibilityChange(int visibility) {
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                if (Build.VERSION.SDK_INT >= 19) {
                  uiOptions |= 0x00001000;
                } else {
                  uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
                }
                window.getDecorView().setSystemUiVisibility(uiOptions);
              }
            });
  }

  @Override
  public int setFragmentStyle() {
    setCancelable(true);
    return R.style.AppDialogDark;
  }

  @Override
  protected int getLayoutId() {
    return R.layout.share_fragment;
  }

  @Override
  public void handleArgument(Bundle argument) {
    super.handleArgument(argument);
    mItem = (ResourceBean) argument.getSerializable(KEY_ITEM);
  }

  public void initData() {
    shareQQ.setOnClickListener(this);
    shareQZone.setOnClickListener(this);
    shareWxSession.setOnClickListener(this);
    shareWxTimeLine.setOnClickListener(this);
    shareShortVideo.setOnClickListener(this);
    shareMore.setOnClickListener(this);
    closeView.setOnClickListener(this);
  }

  @Override
  public void pullData() {

  }

  public void initView(View view) {
    shareQQ = view.findViewById(R.id.share_to_qq);
    shareQZone = view.findViewById(R.id.share_to_qzone);
    shareWxSession = view.findViewById(R.id.share_to_wxsession);
    shareWxTimeLine = view.findViewById(R.id.share_to_wxtimeline);
    shareShortVideo = view.findViewById(R.id.share_short_video);
    shareMore = view.findViewById(R.id.share_more);
    closeView = view.findViewById(R.id.share_cancel);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.share_to_qq:
        downloadAndShareImage(PlatformType.QQ);
        break;
      case R.id.share_to_qzone:
        downloadAndShareImage(PlatformType.QZONE);
        break;
      case R.id.share_to_wxsession:
        downloadAndShareImage(PlatformType.WEIXIN);
        break;
      case R.id.share_to_wxtimeline:
        downloadAndShareImage(PlatformType.WEIXIN_CIRCLE);
        break;
      case R.id.share_short_video:
        VwpShareActivity.launch(mContext, mItem);
        break;
      case R.id.share_more:
        downloadAndShareImage(PlatformType.OTHER);
        break;
      case R.id.share_cancel:
        dismissAllowingStateLoss();
        break;
    }
  }

  private void share(PlatformType type) {
    String title =
        TextUtils.isEmpty(mItem.getName()) ? getActivity().getString(R.string.app_name)
            : mItem.getName() + "-视频壁纸";
    String targetUrl = Const.SHARE.SharePageURL + "?id=" + mItem.get_id();
    String imgUrl = shareFile.getAbsolutePath();
    String summary = getActivity().getString(R.string.share_desc);
    String appName = getActivity().getString(R.string.app_name);
    ArrayList<String> imgUrls = new ArrayList<>();
    imgUrls.add(imgUrl);
    if (type == PlatformType.QQ) {
      Auth.withQQ(mContext)
          .setAction(Auth.ShareImage)
          .shareImageTitle(title)
          .shareImageUrl(imgUrl)
          .shareImageTargetUrl(targetUrl)
          .shareImageName(appName)
          .shareImageDescription(summary)
          .build(mAuthCallback);
    } else if (type == PlatformType.QZONE) {
      Auth.withQQ(mContext)
          .setAction(Auth.ShareImage)
          .shareToQzone(true)
          .shareImageTitle(title)
          .shareImageDescription(summary)
          .shareImageName(appName)
          .shareImageMultiImage(imgUrls)
          .shareImageTargetUrl(targetUrl)
          .shareImageUrl(targetUrl)
          .shareVideoUrl(targetUrl)
          .build(mAuthCallback);
    } else if (type == PlatformType.WEIXIN) {
      Auth.withWX(mContext)
          .setAction(Auth.ShareLink)
          .shareToSession()
          .shareLinkTitle(title)
          .shareLinkDescription(summary)
          .shareLinkImage(BitmapFactory.decodeFile(imgUrl))
          .shareLinkUrl(targetUrl)
          .build(mAuthCallback);
    } else if (type == PlatformType.WEIXIN_CIRCLE) {
      Auth.withWX(mContext)
          .setAction(Auth.ShareLink)
          .shareToTimeline()
          .shareLinkTitle(title)
          .shareLinkDescription(summary)
          .shareLinkImage(BitmapFactory.decodeFile(imgUrl))
          .shareLinkUrl(targetUrl)
          .build(mAuthCallback);
    } else if (type == PlatformType.OTHER) {
      ShareUtils.shareOriginal(getActivity(), imgUrl, mItem);
    }
  }

  private void downloadAndShareImage(final PlatformType type) {
    if (mItem == null) {
      return;
    }
    if (shareFile != null && shareFile.exists()) {
      if (getActivity() != null && !getActivity().isFinishing()) {
        share(type);
      }
      return;
    }
    mDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
    mDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
    mDialog.setTitleText(mContext.getResources().getString(
        R.string.operating));
    mDialog.setCancelable(true);
    mDialog.show();
    FileDownloader.getImpl().create(mItem.getCoverURL())
        .setPath(Const.Dir.SHARE_PATH + "/" + Const.SHARE.IMG, false)
        .setForceReDownload(true)
        .setListener(new FileDownloadLargeFileListener() {
          @Override
          protected void pending(BaseDownloadTask task, long soFarBytes,
              long totalBytes) {

          }

          @Override
          protected void progress(BaseDownloadTask task, long soFarBytes,
              long totalBytes) {
            float p = (float) soFarBytes / totalBytes;
            mDialog.getProgressHelper().setInstantProgress(Math.min(p, 1));
          }

          @Override
          protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {

          }

          @Override
          protected void completed(BaseDownloadTask task) {
            shareFile = new File(task.getPath());
            if (mDialog != null && mDialog.isShowing()) {
              mDialog.dismiss();
            }
            if (getActivity() == null || getActivity().isFinishing()) {
              return;
            }
            share(type);
          }

          @Override
          protected void error(BaseDownloadTask task, Throwable e) {
            if (mDialog != null && mDialog.isShowing()) {
              mDialog.dismiss();
            }
            ToastUtil.showToast(getActivity(), R.string.down_fail);
          }

          @Override
          protected void warn(BaseDownloadTask task) {

          }
        })
        .start();
  }
}
