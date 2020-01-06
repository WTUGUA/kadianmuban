package com.novv.dzdesk.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.adesk.tool.plugin.PAppUtils;
import com.adesk.tool.plugin.PluginManager;
import com.ark.baseui.XAppCompatActivity;
import com.novv.dzdesk.R;
import com.novv.dzdesk.ui.HomeActivity;
import com.novv.dzdesk.ui.activity.local.VideoLocalListActivity;
import com.novv.dzdesk.ui.adapter.plugin.AdapterPlayList;
import com.novv.dzdesk.ui.adapter.plugin.ItemDragCallback;
import com.novv.dzdesk.util.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import java.util.ArrayList;

public class VideoPlayListActivity extends XAppCompatActivity implements
    View.OnClickListener {

  private ImageView btnBack;
  private TextView tvList, btnAction;
  private ArrayList<String> selectedList = new ArrayList<>();
  private RecyclerView mRecyclerView;
  private AdapterPlayList mAdapterPlayList;
  private View llAdd, btnApply, flAdd, btnSave;
  private PluginManager pluginManager;

  @Override
  public int getLayoutId() {
    return R.layout.activity_video_play_list;
  }

  @Override
  public void setUpView(@NonNull View rootView) {
    super.setUpView(rootView);
    btnBack = findViewById(R.id.btn_back);
    mRecyclerView = findViewById(R.id.recycler_view);
    btnApply = findViewById(R.id.btn_apply);
    tvList = findViewById(R.id.tv_video_list);
    llAdd = findViewById(R.id.ll_add);
    btnAction = findViewById(R.id.btn_action);
    flAdd = findViewById(R.id.fl_add);
    btnSave = findViewById(R.id.btn_save);
  }

  @Nullable
  @Override
  public Object newP() {
    return null;
  }

  @Override
  public void initData(@Nullable Bundle bundle) {
    btnBack.setOnClickListener(this);
    btnApply.setOnClickListener(this);
    llAdd.setOnClickListener(this);
    btnAction.setOnClickListener(this);
    btnSave.setOnClickListener(this);
    Intent intent = getIntent();
    if (intent != null) {
      Bundle extras = intent.getExtras();
      if (extras != null) {
        ArrayList<String> list = extras.getStringArrayList("data");
        if (list != null) {
          selectedList = list;
        }
      }
    }
    mRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
    mAdapterPlayList = new AdapterPlayList(this, selectedList,
        new AdapterPlayList.ChannelListener() {
          @Override
          public void onEdit(int pos) {
            refreshUi();
          }

          @Override
          public void onDelete(int pos) {

          }
        });
    mRecyclerView.setAdapter(mAdapterPlayList);
    ItemDragCallback callback = new ItemDragCallback(mAdapterPlayList);
    ItemTouchHelper helper = new ItemTouchHelper(callback);
    helper.attachToRecyclerView(mRecyclerView);
    refreshUi();
    pluginManager = new PluginManager(this);
    pluginManager.bindPluginService(null);
  }

  @Override
  public void onClick(View view) {
    int id = view.getId();
    if (id == R.id.btn_back) {
      startActivity(new Intent(this, HomeActivity.class));
      finish();
    } else if (id == R.id.btn_apply) {
      if (selectedList.isEmpty()) {
        ToastUtil.showGeneralToast(this, "您还没有选择任何视频");
        return;
      }
      if (!PAppUtils.isServiceRunning(this, "com.adesk.wpplugin.service.RemoteService")) {
        pluginManager.startPluginActivity(selectedList);
      } else {
        pluginManager.startSetWallpaperList(selectedList);
      }
    } else if (id == R.id.ll_add) {
      ArrayList<String> list = mAdapterPlayList.getData();
      Intent intent = new Intent(this, VideoLocalListActivity.class);
      intent.putStringArrayListExtra("data", list);
      startActivityForResult(intent, 100);
    } else if (id == R.id.btn_action) {
      boolean isEdit = mAdapterPlayList.isEdit;
      if (isEdit) {
        mAdapterPlayList.cancelEdit(selectedList);
      } else {
        mAdapterPlayList.setEditState(true);
      }
      refreshUi();
    } else if (id == R.id.btn_save) {
      mAdapterPlayList.setEditState(false);
      selectedList = mAdapterPlayList.getData();
      refreshUi();
      ToastUtil.showGeneralToast(this, "点击“应用到桌面”后列表方可生效");
    }
  }

  private void refreshUi() {
    boolean isEmpty = selectedList.isEmpty();
    boolean isEdit = mAdapterPlayList.isEdit;
    tvList.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    btnApply.setVisibility(isEdit || isEmpty ? View.GONE : View.VISIBLE);
    btnAction.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    btnAction.setText(isEdit ? "取消" : "编辑");
    btnSave.setVisibility(isEdit ? View.VISIBLE : View.GONE);
    tvList.setText(isEdit ? "长按拖动可排序" : "视频列表");
    llAdd.setEnabled(!isEdit);
    flAdd.setBackground(ContextCompat
        .getDrawable(this,
            isEdit ? R.drawable.bgr_add_videos : R.drawable.bgr_add_videos_light));
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (data != null && requestCode == 100 && resultCode == RESULT_OK) {
      ArrayList<String> list = data.getStringArrayListExtra("data");
      if (list != null) {
        selectedList = list;
        mAdapterPlayList.setNewData(list);
      }
      refreshUi();
    }
    if (requestCode == 666) {
      PAppUtils.launchHome(this);
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
  protected void onDestroy() {
    super.onDestroy();
    pluginManager.unBindPluginService();
  }
}
