package com.novv.dzdesk.ui.activity.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.ark.adkit.basics.utils.LogUtils;
import com.ark.baseui.XAppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.gyf.barlibrary.ImmersionBar;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.novv.dzdesk.Const;
import com.novv.dzdesk.R;
import com.novv.dzdesk.http.DefaultScheduler;
import com.novv.dzdesk.http.ServerApi;
import com.novv.dzdesk.http.SimpleObserver;
import com.novv.dzdesk.http.callback.DialogCallback;
import com.novv.dzdesk.http.utils.Urls;
import com.novv.dzdesk.res.model.BaseResult;
import com.novv.dzdesk.res.model.UploadImage;
import com.novv.dzdesk.res.model.UserModel;
import com.novv.dzdesk.res.user.LoginContext;
import com.novv.dzdesk.util.PermissionsUtils;
import com.novv.dzdesk.util.ToastUtil;
import com.novv.dzdesk.util.UriUtils;
import com.umeng.analytics.MobclickAgent;
import com.yalantis.ucrop.UCrop;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserInfoEditActivity extends XAppCompatActivity implements
    View.OnClickListener, TextWatcher {

  private static final int REQUEST_PHOTO_CODE = 111;
  private static final int REQUEST_CROP_CODE = 222;
  private EditText etInputNickname;
  private EditText etInputSign;
  private ImmersionBar immersionBar;
  private UserModel userModel;
  private View btnBack;
  private View llRight;
  private ImageView mImageView;
  private boolean userInfoChanged;

  public static void launch(FragmentActivity context) {
    Intent intent = new Intent(context, UserInfoEditActivity.class);
    context.startActivity(intent);
  }

  @Override
  public int getLayoutId() {
    return R.layout.activity_user_info_edit;
  }

  @Nullable
  @Override
  public Object newP() {
    return null;
  }

  @Override
  public void setUpView(@NonNull View rootView) {
    super.setUpView(rootView);
    btnBack = findViewById(R.id.btn_back);
    llRight = findViewById(R.id.ll_right);
    etInputNickname = findViewById(R.id.et_input_nickname);
    etInputSign = findViewById(R.id.et_input_sign);
    mImageView = findViewById(R.id.imageView);
  }

  @Override
  public void initData(@Nullable Bundle savedInstanceState) {
    immersionBar = ImmersionBar.with(this)
        .titleBar(findViewById(R.id.detail_top_rl), false)
        .transparentBar();
    immersionBar.init();
    mImageView.setOnClickListener(this);
    btnBack.setOnClickListener(this);
    llRight.setOnClickListener(this);
    userModel = LoginContext.getInstance().getUser();
    Glide.with(this)
        .load(userModel.getImg())
        .apply(new RequestOptions().centerCrop()
            .placeholder(mImageView.getDrawable())
            .error(R.drawable.user_avatar_default))
        .into(mImageView);
    if (userModel != null) {
      etInputNickname.setText(userModel.getNickname());
      etInputSign.setText(userModel.getDesc());
      etInputNickname.requestFocus();
      etInputNickname.setSelection(etInputNickname.getText().length());
    } else {
      ToastUtil.showToast(this, "登陆后才可以修改个人资料");
      finish();
    }
    etInputNickname.addTextChangedListener(this);
    etInputSign.addTextChangedListener(this);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_back:
        finish();
        break;
      case R.id.ll_right:
        if (TextUtils.isEmpty(etInputNickname.getText().toString().trim())) {
          ToastUtil.showToast(this, "昵称不能为空");
          return;
        }
        editComplete();
        break;
      case R.id.imageView:
        PermissionsUtils.checkStorage(this, new PermissionsUtils.OnPermissionBack() {
          @Override public void onResult(boolean success) {
            if (success) {
              try {
                String action;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                  action = Intent.ACTION_OPEN_DOCUMENT;
                } else {
                  action = Intent.ACTION_PICK;
                }
                Intent intent = new Intent(action);
                intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, REQUEST_PHOTO_CODE);
              } catch (Exception e) {
                Toast.makeText(context, "打开系统相册失败", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
              }
            }
          }
        });
        break;
      default:
        break;
    }
  }

  private void editComplete() {
    ServerApi.modifyUserInfo(etInputNickname.getText().toString().trim(),
        etInputSign.getText().toString().trim())
        .compose(DefaultScheduler.<BaseResult>getDefaultTransformer())
        .subscribe(new SimpleObserver() {
          @Override
          public void onSuccess() {
            userModel.setNickname(etInputNickname.getText().toString().trim());
            userModel.setDesc(etInputSign.getText().toString().trim());
            notifyEdit();
          }

          @Override
          public void onFailure(int code, String message) {
            ToastUtil.showToast(UserInfoEditActivity.this, message);
          }

          @Override
          public void onLogout() {
            super.onLogout();
            ToastUtil.showToast(UserInfoEditActivity.this, "个人资料修改失败，登录失效");
          }
        });
  }

  private void notifyEdit() {
    Intent intent = new Intent();
    intent.putExtra("userModel", userModel);
    setResult(RESULT_OK, intent);
    finish();
  }

  private void uploadFile(final File file) {
    OkGo.<BaseResult<UploadImage>>post(Urls.defaultUrl + "v2/user/info?type=img")
        .tag(this)
        .params("file", file)
        .isMultipart(true)
        .execute(new DialogCallback<BaseResult<UploadImage>>(this) {

          @Override
          public void onSuccess(Response<BaseResult<UploadImage>> response) {
            BaseResult<UploadImage> result = response.body();
            if (result != null) {
              UploadImage uploadImage = result.getRes();
              if (uploadImage != null) {
                UserModel userModel = LoginContext.getInstance().getUser();
                if (userModel != null) {
                  userModel.setImg(uploadImage.getImg());
                  LoginContext.getInstance().login(userModel);
                }
                if (isFinishing()) {
                  return;
                }
                Glide.with(context)
                    .load(file)
                    .apply(new RequestOptions().centerCrop()
                        .placeholder(R.drawable.user_avatar_default)
                        .error(R.drawable.user_avatar_default)
                        .skipMemoryCache(true))
                    .listener(new RequestListener<Drawable>() {
                      @Override
                      public boolean onLoadFailed(
                          @Nullable GlideException e, Object o,
                          Target<Drawable> target, boolean b) {
                        changePicEnd();
                        return false;
                      }

                      @Override
                      public boolean onResourceReady(Drawable drawable,
                          Object o, Target<Drawable> target,
                          DataSource dataSource, boolean b) {
                        changePicEnd();
                        return false;
                      }
                    })
                    .into(mImageView);
              }
            }
          }

          @Override
          public void onError(Response<BaseResult<UploadImage>> response) {
            super.onError(response);
            ToastUtil.showGeneralToast(UserInfoEditActivity.this, "上传失败");
          }
        });
  }

  private void changePicEnd() {
    if (!isFinishing()) {
      if (userInfoChanged) {
        editComplete();
      } else {
        finish();
      }
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (data != null) {
      if (requestCode == REQUEST_PHOTO_CODE) {
        File dir = new File(Const.Dir.CACHE);
        if (!dir.exists()) {
          dir.mkdirs();
        }
        File file = new File(dir, getDefaultCropPhotoFileName(Bitmap.CompressFormat.JPEG));
        try {
          file.createNewFile();
        } catch (IOException e) {
          e.printStackTrace();
        }
        if (data.getData() != null) {
          UCrop.Options options = new UCrop.Options();
          options.setToolbarCancelDrawable(R.mipmap.ic_close);
          options.setHideBottomControls(true);
          options.setFreeStyleCropEnabled(false);
          options.setStatusBarColor(ContextCompat.getColor(this, R.color.color_primary_dark));
          UCrop.of(data.getData(), Uri.fromFile(file))
              .withAspectRatio(1, 1)
              .withOptions(options)
              .start(this, REQUEST_CROP_CODE);
        }
      } else if (requestCode == REQUEST_CROP_CODE) {
        Uri uri;
        if ((uri = UCrop.getOutput(data)) == null) {
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
        uploadFile(new File(UriUtils.getImagePathByUri(this, uri)));
      }
    }
  }

  private String getDefaultCropPhotoFileName(@NonNull Bitmap.CompressFormat outputFormat) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
    return "CROP_IMG_" + dateFormat.format(new Date()) + "." + outputFormat.toString();
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
  protected void onDestroy() {
    super.onDestroy();
    if (immersionBar != null) {
      immersionBar.destroy();
    }
  }

  @Override
  public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

  }

  @Override
  public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

  }

  @Override
  public void afterTextChanged(Editable editable) {
    userInfoChanged = true;
  }
}
