package com.novv.dzdesk.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import com.ark.baseui.XAppCompatActivity;
import com.novv.dzdesk.R;
import com.novv.dzdesk.http.DefaultDisposableObserver;
import com.novv.dzdesk.http.DefaultScheduler;
import com.novv.dzdesk.http.ServerApi;
import com.novv.dzdesk.res.model.BaseResult;
import com.novv.dzdesk.res.model.ResourceBean;
import com.novv.dzdesk.ui.activity.user.UserVipActivity;
import com.novv.dzdesk.ui.activity.vwp.VwpResDetailActivity;
import com.novv.dzdesk.ui.dialog.ProcessDialog;
import com.novv.dzdesk.util.PermissionsUtils;
import com.umeng.analytics.MobclickAgent;

public class SchemeActivity extends XAppCompatActivity {
  private static final String TAG = SchemeActivity.class.getSimpleName();

  private ProcessDialog progressDialog;

  @Override
  public int getLayoutId() {
    return R.layout.activity_scheme;
  }

  @Nullable
  @Override
  public Object newP() {
    return null;
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
    processData();
  }

  @Override
  public void initData(@Nullable Bundle savedInstanceState) {
    PermissionsUtils.checkSplash(this, new PermissionsUtils.OnPermissionBack() {
      @Override public void onResult(boolean success) {
        if (!success) {
          finish();
        } else {
          processData();
        }
      }
    });
  }

  private void processData() {
    Uri uri = getIntent().getData();
    if (uri != null) {
      String scheme = uri.getScheme();
      String host = uri.getHost();
      String path = uri.getPath();
      if (TextUtils.equals("adesk", scheme)) {
        if (TextUtils.equals("videowp", host)) {
          if (TextUtils.equals("/main", path)) {
            startActivity(new Intent(context, SplashActivity.class));
            finish();
            return;
          } else if (TextUtils.equals("/detail", path)) {
            String id = uri.getQueryParameter("id");
            Log.i(TAG, "id:" + id);
            if (!TextUtils.isEmpty(id)) {
              getSourceInfo(id);
              return;
            }
          } else if (TextUtils.equals(path, "/vip")) {
            MobclickAgent.onEvent(context, "text_web_app");
            Intent intent = new Intent(context, UserVipActivity.class);
            intent.putExtra("isWebLaunch", true);
            startActivity(intent);
            finish();
            return;
          }
        }
      }
    }
    startActivity(new Intent(context, SplashActivity.class));
    finish();
  }

  public void showProgress(int current) {
    if (progressDialog != null) {
      progressDialog.setProgress(current);
    }
  }

  public void hideProgressDialog() {
    if (progressDialog != null) {
      progressDialog.release();
      progressDialog = null;
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    hideProgressDialog();
  }

  /**
   * 根据资源 ID 获取资源详细信息
   *
   * @param sourceId 资源 ID
   */
  private void getSourceInfo(final String sourceId) {
    progressDialog = new ProcessDialog();
    if (!progressDialog.isShowing()) {
      progressDialog.show(context);
    }
    progressDialog.setMsg("正在加载...");
    ServerApi.getVideoWpDetails(sourceId)
        .compose(DefaultScheduler.<BaseResult<ResourceBean>>getDefaultTransformer())
        .subscribe(new DefaultDisposableObserver<BaseResult<ResourceBean>>() {
          @Override
          public void onFailure(Throwable throwable) {
            finish();
          }

          @Override
          public void onSuccess(BaseResult<ResourceBean> result) {
            if (result.getCode() == 0) {
              ResourceBean resourceBean = result.getRes();
              hideProgressDialog();
              Intent intent = new Intent(context, VwpResDetailActivity.class);
              intent.putExtra("isWebLaunch", true);
              intent.putExtra(VwpResDetailActivity.Detail_res_key, resourceBean);
              context.startActivity(intent);
            }
            finish();
          }
        });
  }
}
