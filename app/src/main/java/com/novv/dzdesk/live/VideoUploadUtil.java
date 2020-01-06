package com.novv.dzdesk.live;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.ark.adkit.basics.utils.LogUtils;
import com.novv.dzdesk.Const;
import com.novv.dzdesk.R;
import com.novv.dzdesk.http.BaseObserver;
import com.novv.dzdesk.http.DefaultScheduler;
import com.novv.dzdesk.http.ServerApi;
import com.novv.dzdesk.res.model.BaseResult;
import com.novv.dzdesk.res.model.ResourceBean;
import com.novv.dzdesk.res.model.UploadMsg;
import com.novv.dzdesk.ui.activity.ae.Utils;
import com.novv.dzdesk.ui.activity.vwp.VwpUploadActivity;
import com.novv.dzdesk.util.ToastUtil;
import com.qiniu.pili.droid.shortvideo.PLShortVideoUploader;
import com.qiniu.pili.droid.shortvideo.PLUploadProgressListener;
import com.qiniu.pili.droid.shortvideo.PLUploadResultListener;
import com.qiniu.pili.droid.shortvideo.PLUploadSetting;
import java.io.File;
import org.json.JSONObject;

public class VideoUploadUtil {

  private PLShortVideoUploader uploader;
  private SweetAlertDialog dialog;

  public static VideoUploadUtil getInstance() {
    return Holder.instance;
  }

  private void cancelUpload() {
    if (uploader != null) {
      uploader.cancelUpload();
    }
  }

  public void uploadVideo(final Context context, ResourceBean mItem,
      final UploadListener uploadListener) {
    uploadVideo(context, mItem, -1, VwpUploadActivity.PRIVACY_PUB, uploadListener);
  }

  public void uploadVideo(final Context context, ResourceBean mItem, int coverTime,
      String privacy, final UploadListener uploadListener) {
    dialog = new SweetAlertDialog(context,
        SweetAlertDialog.PROGRESS_TYPE);
    dialog.setTitleText("视频上传中……");
    dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
      @Override
      public void onClick(SweetAlertDialog sweetAlertDialog) {
        sweetAlertDialog.dismiss();
        cancelUpload();
        ToastUtil.showToast(context, "取消上传");
      }
    });
    dialog.setCancelable(false);
    dialog.setCanceledOnTouchOutside(false);
    dialog.show();
    dialog.setConfirmText("取消");

    ToastUtil.showToast(context, R.string.upload_ing);
    final String path = mItem.getLinkMp4();
    ServerApi.getUploadMsg(mItem.getName(), mItem.get_id(), privacy, coverTime)
        .compose(DefaultScheduler.<BaseResult<UploadMsg>>getDefaultTransformer())
        .subscribe(new BaseObserver<UploadMsg>(context,true) {
          @Override
          public void onSuccess(@NonNull UploadMsg uploadMsg) {
            String token = null;
            String fileKey = null;
            try {
              token = uploadMsg.getToken();
              fileKey = uploadMsg.getFileKeys().get(0);
            } catch (Exception e) {
              //
            }
            LogUtils.i("upload msg success--->" + fileKey);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(fileKey)) {
              startUpload(context, path, fileKey, token, uploadListener);
            } else {
              ToastUtil.showGeneralToast(context, "请求失败，无法上传");
              if (dialog != null && dialog.isShowing()) {
                dialog.dismissWithAnimation();
              }
            }
          }

          @Override
          public void onFailure(int code, @NonNull String message) {
            ToastUtil.showGeneralToast(context, "请求失败，无法上传");
            if (dialog != null && dialog.isShowing()) {
              dialog.dismissWithAnimation();
            }
          }
        });
  }

  private void startUpload(final Context context, final String path, final String fileKey,
      final String token,
      final UploadListener uploadListener) {
    final File dir = new File(Const.Dir.EDIT_VIDEO_PATH, ".upload");
    dir.mkdirs();
    final String newPath = new File(dir, "upload.mp4").getAbsolutePath();
    PLUploadSetting uploadSetting = new PLUploadSetting();
    uploadSetting.setHttpsEnabled(true);
    uploader = new PLShortVideoUploader(context, uploadSetting);
    uploader.setUploadProgressListener(new PLUploadProgressListener() {
      @Override
      public void onUploadProgress(String s, double v) {
        LogUtils.i("upload progress--->" + v + "/" + s);
        if (dialog == null || !dialog.isShowing()) {
          return;
        }
        if (v > 0.05) {
          dialog.getProgressHelper().setInstantProgress(
              (float) v);
        }
        dialog.getProgressHelper().getProgressWheel()
            .setText(((int) (v * 100)) + "%");
      }
    });
    uploader.setUploadResultListener(new PLUploadResultListener() {
      @Override
      public void onUploadVideoSuccess(JSONObject jsonObject) {
        if (dialog == null || !dialog.isShowing()) {
          return;
        }
        dialog.getProgressHelper().setInstantProgress(1);
        dialog.setTitleText("上传成功！等待审核");
        dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
        dialog.setConfirmText("确定");
        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
          @Override
          public void onClick(SweetAlertDialog sweetAlertDialog) {
            sweetAlertDialog.dismiss();
            if (uploadListener != null) {
              uploadListener.uploadFinish(true);
            }
          }
        });
      }

      @Override
      public void onUploadVideoFailed(int i, String s) {
        if (dialog == null || !dialog.isShowing()) {
          return;
        }
        dialog.setTitleText("上传失败");
        dialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
        dialog.setConfirmText("确定");
        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
          @Override
          public void onClick(SweetAlertDialog sweetAlertDialog) {
            sweetAlertDialog.dismiss();
            if (uploadListener != null) {
              uploadListener.uploadFinish(false);
            }
          }
        });
      }
    });
    Utils.copyFileToPath(context, path, newPath);
    uploader.startUpload(newPath, fileKey, token);
  }

  public interface UploadListener {

    void uploadFinish(boolean success);
  }

  private static class Holder {
    private static VideoUploadUtil instance = new VideoUploadUtil();
  }
}
