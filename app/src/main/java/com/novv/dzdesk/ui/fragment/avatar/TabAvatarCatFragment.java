package com.novv.dzdesk.ui.fragment.avatar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.ark.baseui.XLazyFragment;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.novv.dzdesk.R;
import com.novv.dzdesk.http.DefaultDisposableObserver;
import com.novv.dzdesk.http.DefaultScheduler;
import com.novv.dzdesk.http.ServerApi;
import com.novv.dzdesk.res.model.AvatarCategoryBean;
import com.novv.dzdesk.res.model.AvatarResult;
import com.novv.dzdesk.res.model.BaseResult;
import com.novv.dzdesk.ui.activity.avatar.AvatarListActivity;
import com.novv.dzdesk.ui.adapter.avatar.AdapterAvatarCat;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import java.util.ArrayList;
import java.util.List;

/**
 * 头像分类
 */
public class TabAvatarCatFragment extends XLazyFragment {

  private SmartRefreshLayout refreshLayout;
  private RecyclerView recyclerView;
  private AdapterAvatarCat mAdapter;
  private boolean isLoading;

  @Override
  public void setUpView(@NonNull View rootView) {
    super.setUpView(rootView);
    recyclerView = rootView.findViewById(R.id.recyclerView);
    refreshLayout = rootView.findViewById(R.id.refreshLayout);
  }

  @Override
  public int getLayoutId() {
    return R.layout.layout_refresh_list;
  }

  @Nullable
  @Override
  public Object newP() {
    return null;
  }

  @Override
  public void initData(@Nullable Bundle savedInstanceState) {
    recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
    mAdapter = new AdapterAvatarCat(new ArrayList<AvatarCategoryBean>());
    refreshLayout.setEnableLoadMore(true);
    mAdapter.setEnableLoadMore(false);
    recyclerView.setAdapter(mAdapter);
    mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        ArrayList<AvatarCategoryBean> currentData = new ArrayList<>(mAdapter.getData());
        if (position < currentData.size()) {
          AvatarListActivity.launch(context, currentData.get(position).getId(),
              currentData.get(position).getName());
        }
      }
    });
    refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
      @Override public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        loadData(false);
      }

      @Override public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        isLoading = false;
        loadData(true);
      }
    });
    loadData(true);
  }

  private void loadData(final boolean isPull) {
    if (isLoading) {
      return;
    }
    isLoading = true;
    ServerApi.getAvatarCategory()
        .compose(DefaultScheduler.<BaseResult<AvatarResult>>getDefaultTransformer())
        .subscribe(new DefaultDisposableObserver<BaseResult<AvatarResult>>() {
          @Override
          public void onFailure(Throwable throwable) {
            isLoading = false;
          }

          @Override
          public void onSuccess(BaseResult<AvatarResult> result) {
            isLoading = false;
            List<AvatarCategoryBean> mList = new ArrayList<>();
            if (result != null && result.getRes() != null) {
              List<AvatarCategoryBean> list = result.getRes().getCategoryList();
              if (list != null && !list.isEmpty()) {
                mList.addAll(list);
              }
            }
            if (isPull) {
              onRefresh(mList);
            } else {
              onLoadMore(mList);
            }
          }
        });
  }

  private void onRefresh(@NonNull List<AvatarCategoryBean> newData) {
    mAdapter.replaceData(newData);
    refreshLayout.finishRefresh();
  }

  private void onLoadMore(@NonNull List<AvatarCategoryBean> newData) {
    mAdapter.addData(newData);
    if (newData.isEmpty()) {
      refreshLayout.finishLoadMore(0, true, true);
    } else {
      refreshLayout.finishLoadMore();
    }
  }
}
