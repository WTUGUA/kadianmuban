package com.novv.dzdesk.ui.activity.vwp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.ark.baseui.XAppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.novv.dzdesk.Const;
import com.novv.dzdesk.R;
import com.novv.dzdesk.http.DefaultDisposableObserver;
import com.novv.dzdesk.http.DefaultScheduler;
import com.novv.dzdesk.http.ServerApi;
import com.novv.dzdesk.live.PrefUtil;
import com.novv.dzdesk.live.VideoUploadUtil;
import com.novv.dzdesk.res.model.BaseResult;
import com.novv.dzdesk.res.model.CategoryBean;
import com.novv.dzdesk.res.model.ResourceBean;
import com.novv.dzdesk.ui.HomeActivity;
import com.novv.dzdesk.ui.dialog.ChoseCategoryDialog;
import com.novv.dzdesk.ui.dialog.ChoseDialog;
import com.novv.dzdesk.ui.dialog.LoginDialog;
import com.novv.dzdesk.util.CleanUtil;
import com.novv.dzdesk.util.FileUtil;
import com.novv.dzdesk.util.HeaderSpf;
import com.novv.dzdesk.util.LogUtil;
import com.novv.dzdesk.util.PathUtils;
import com.novv.dzdesk.util.ToastUtil;
import com.novv.dzdesk.util.UmConst;
import com.novv.dzdesk.util.UmUtil;
import com.novv.dzdesk.util.VideoFileUtils;
import com.novv.dzdesk.util.VideoNotifyUtils;
import com.umeng.analytics.MobclickAgent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VwpUploadActivity extends XAppCompatActivity implements
    View.OnClickListener {

  public static final String FILE_NAME_CATEGORY_CACHE = "cache_category";
  public static final String PRIVACY_PUB = "public";
  public static final String PRIVACY_PRI = "private";
  private static final String tag = VwpUploadActivity.class.getSimpleName();
  private static final String MP4_PATH = "MP4_PATH";
  private static final String MP4_PATH_COVER_TIME = "MP4_PATH_COVER_TIME";
  private String mp4FilePath;
  private int coverTime;
  private EditText mTitleEt;
  private View mPostV;
  private View mSaveLocalV;
  private ImageView mSelectedIV;
  private View mCategoryChoseV;
  private TextView mCategoryNameTv;
  private View mPrivacyChoseV;
  private TextView mPricacyTv;
  private ImageView mVideoCoverIv;
  private String mCateId;
  private String mCateName;
  private String mPrivacyId = PRIVACY_PUB;
  private String mPrivacyName = "公开";
  private ArrayList<CategoryBean> mItems = new ArrayList<>();

  public static void launch(Context context, String mp4Path, int coverTime) {
    Intent intent = new Intent(context, VwpUploadActivity.class);
    intent.putExtra(MP4_PATH, mp4Path);
    intent.putExtra(MP4_PATH_COVER_TIME, coverTime);
    context.startActivity(intent);
  }

  @Override
  public int getLayoutId() {
    return R.layout.activity_video_upload;
  }

  @Override
  public void setUpView(@NonNull View rootView) {
    super.setUpView(rootView);
    mTitleEt = findViewById(R.id.video_title);
    mPostV = findViewById(R.id.post_ll);
    mSaveLocalV = findViewById(R.id.save_local_ll);
    mSelectedIV = findViewById(R.id.save_local_iv);
    mCategoryChoseV = findViewById(R.id.chose_cateogry_ll);
    mPrivacyChoseV = findViewById(R.id.chose_privacy_ll);
    mCategoryNameTv = findViewById(R.id.category_chose_tv);
    mPricacyTv = findViewById(R.id.privacy_chose_tv);
    mVideoCoverIv = findViewById(R.id.video_cover_iv);

    mSaveLocalV.setOnClickListener(this);
    mPostV.setOnClickListener(this);
    mPrivacyChoseV.setVisibility(View.GONE);
    mPricacyTv.setText(mPrivacyName);
    mSaveLocalV.setSelected(true);
    mCategoryChoseV.setOnClickListener(this);
    mPrivacyChoseV.setOnClickListener(this);
    findViewById(R.id.back_imgv).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });
  }

  @Nullable
  @Override
  public Object newP() {
    return null;
  }

  @Override
  public void initData(@Nullable Bundle bundle) {
    mp4FilePath = getIntent().getStringExtra(MP4_PATH);
    coverTime = getIntent().getIntExtra(MP4_PATH_COVER_TIME, 0);
    Glide.with(this)
        .load(PathUtils.getVideoEditCoverFilePath())
        .apply(new RequestOptions()
            .skipMemoryCache(true)
            .centerCrop()
            .signature(new ObjectKey(System.currentTimeMillis())))
        .into(mVideoCoverIv);
    getCategory();
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.chose_cateogry_ll:
        chooseCategory();
        break;
      case R.id.chose_privacy_ll:
        choosePrivacy();
        break;
      case R.id.post_ll:
        UmUtil.anaEvent(this, UmConst.CLICK_VIDEO_POST);
        post(view.getContext());
        break;
      case R.id.save_local_ll:
        view.setSelected(!view.isSelected());
        mSelectedIV.setImageResource(
            view.isSelected() ? R.drawable.select : R.drawable.unselect);
        break;
      default:
        break;
    }
  }

  private String getVideoTitle() {
    return mTitleEt.getText().toString().trim();
  }

  private void post(final Context context) {

    if (TextUtils.isEmpty(HeaderSpf.getSessionId())) {
      UmUtil.anaLoginFrom(context, "上传");

      LoginDialog.launch(this, "登录后才可以上传哦~");
      return;
    }

    if (TextUtils.isEmpty(getVideoTitle())) {
      ToastUtil.showGeneralToast(this, "请输入标题");
      return;
    }

    if (TextUtils.isEmpty(mCateId) || TextUtils.isEmpty(mCateName)) {
      ToastUtil.showGeneralToast(this, "请选择一个分类");
      return;
    }
    if (TextUtils.isEmpty(mPrivacyId) || TextUtils.isEmpty(mPrivacyName)) {
      ToastUtil.showGeneralToast(this, "请选择谁可以看");
      return;
    }
    LogUtil.i(tag, "post cate id = " + mCateId + " cate name = " + mCateName);
    LogUtil.i(tag, "post privacy id = " + mPrivacyId + " privacy name = " + mPrivacyName);
    if (mSaveLocalV.isSelected()) {
      FileUtil
          .copyFileWithPath(mp4FilePath,
              VideoFileUtils.getLocalFinishVideoPath(getVideoTitle()));
      VideoNotifyUtils
          .notify(this,
              new File(VideoFileUtils.getLocalFinishVideoPath(getVideoTitle())));
      ToastUtil.showToast(this, getResources().getString(R.string.video_saved));
    }

    ToastUtil.showGeneralToast(this, "发布中");
    uploadVideo();
  }

  private void chooseCategory() {

    if (mItems == null || mItems.size() == 0) {
      ToastUtil.showToast(this, "暂未获取到分类列表，请稍后尝试！");
      getCategory();
      return;
    }

    findViewById(R.id.fragment).setVisibility(View.VISIBLE);
    ChoseCategoryDialog dialog = ChoseCategoryDialog.showDialog(this, mItems);
    dialog.setOnSelectedListener(new ChoseCategoryDialog.OnSelectedListener() {
      @Override
      public void onSelected(int position) {
        mCateId = mItems.get(position).getId();
        mCateName = mItems.get(position).getName();
        mCategoryNameTv.setText(mCateName + " >");
        mCategoryNameTv.setTextColor(getResources().getColor(R.color.white));
      }
    });
  }

  private void choosePrivacy() {

    ArrayList<String> idList = new ArrayList<>();
    ArrayList<String> nameList = new ArrayList<>();
    idList.add("-1");
    nameList.add("请选择");

    idList.add(PRIVACY_PUB);
    nameList.add("公开");
    idList.add(PRIVACY_PRI);
    nameList.add("私密");

    ChoseDialog fragment = ChoseDialog.launch(this, idList, nameList, "谁可以看");
    fragment.setCallBack(new ChoseDialog.CallBack() {
      @Override
      public void onSureClick(String type, String name) {
        mPrivacyId = type;
        mPrivacyName = name;
        LogUtil.i(tag, "onSureClick type = " + type + " name = " + name);
        mPricacyTv.setText(mPrivacyName);
      }
    });
  }

  private void getCategory() {
    String str = PrefUtil.getString(this, FILE_NAME_CATEGORY_CACHE, "");
    if (TextUtils.isEmpty(str)) {
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
                mItems.clear();
                mItems.addAll(mList);
                Gson gson = new Gson();
                String str = gson.toJson(mList);
                PrefUtil.putString(VwpUploadActivity.this,
                    FILE_NAME_CATEGORY_CACHE, str);
              }
            }
          });
    } else {
      List<CategoryBean> mList = new Gson()
          .fromJson(str, new TypeToken<List<CategoryBean>>() {
          }.getType());
      if (mList != null) {
        mItems.clear();
        mItems.addAll(mList);
      }
    }
  }

  private void uploadVideo() {
    ResourceBean mItem = new ResourceBean();
    mItem.setLinkMp4(mp4FilePath);
    mItem.setName(getVideoTitle());
    mItem.setCid(mCateId);
    VideoUploadUtil.getInstance()
        .uploadVideo(this, mItem, coverTime, mPrivacyId,
            new VideoUploadUtil.UploadListener() {
              @Override
              public void uploadFinish(boolean success) {
                try {
                  CleanUtil.deleteDir(new File(Const.Dir.VIDEO_ROOT_PATH));
                } catch (Exception e) {
                  e.printStackTrace();
                }
                Intent intent = new Intent(VwpUploadActivity.this,
                    HomeActivity.class);
                VwpUploadActivity.this.startActivity(intent);
                // VwpUploadActivity.this.finish();
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
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    LogUtil.i(tag, "onDestroy");
  }
}
