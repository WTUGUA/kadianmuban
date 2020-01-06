package com.novv.dzdesk.ui.fragment.rings;

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
import com.novv.dzdesk.res.model.RingsCategoryBean;
import com.novv.dzdesk.res.model.RingsRespons;
import com.novv.dzdesk.ui.activity.rings.RingsListActivity;
import com.novv.dzdesk.ui.adapter.rings.AdapterRingsCat;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import java.util.ArrayList;
import java.util.List;

public class TabRingsCatFragment extends XLazyFragment {

  private AdapterRingsCat mAdapter;
  private SmartRefreshLayout refreshLayout;
  private RecyclerView recyclerView;
  private boolean isLoading;

  @Override
  public int getLayoutId() {
    return R.layout.fragment_category_rings;
  }

  @Override
  public void setUpView(@NonNull View rootView) {
    super.setUpView(rootView);
    refreshLayout = rootView.findViewById(R.id.refreshLayout);
    recyclerView = rootView.findViewById(R.id.recyclerView);
  }

  @Nullable
  @Override
  public Object newP() {
    return null;
  }

  @Override
  public void initData(@Nullable Bundle savedInstanceState) {
    mAdapter = new AdapterRingsCat(new ArrayList<RingsCategoryBean>());
    recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
    recyclerView.setAdapter(mAdapter);
    refreshLayout.setEnableLoadMore(true);
    mAdapter.setEnableLoadMore(false);
    refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
      @Override public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        loadDatas(false);
      }

      @Override public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        isLoading = false;
        loadDatas(true);
      }
    });
    mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        ArrayList<RingsCategoryBean> currentData = new ArrayList<>(mAdapter.getData());
        if (position < currentData.size()) {
          try {
            String url = currentData.get(position).getCateurl();
            String cid = url
                .substring(url.indexOf("cid=") + 4, url.indexOf("&copyright"));
            RingsListActivity
                .launch(context, cid, currentData.get(position).getName());
          } catch (Exception e) {
            //
          }
        }
      }
    });
    loadDatas(true);
  }

  private void loadDatas(final boolean isPull) {
    if (isLoading) {
      return;
    }
    isLoading = true;
    ServerApi.getRingsCategory()
        .compose(DefaultScheduler.<RingsRespons>getDefaultTransformer())
        .subscribe(new DefaultDisposableObserver<RingsRespons>() {
          @Override
          public void onFailure(Throwable throwable) {
            isLoading = false;
          }

          @Override
          public void onSuccess(RingsRespons result) {
            isLoading = false;
            List<RingsCategoryBean> categorays = new ArrayList<>();
            if (result != null && result.getResp() != null) {
              if (result.getResp().getCategory() != null) {
                categorays.addAll(result.getResp().getCategory());
              }
            }
            if (isPull) {
              onRefresh(categorays);
            } else {
              onLoadMore(categorays);
            }
          }
        });
  }

  private void onRefresh(@NonNull List<RingsCategoryBean> newData) {
    mAdapter.replaceData(newData);
    refreshLayout.finishRefresh();
  }

  private void onLoadMore(@NonNull List<RingsCategoryBean> newData) {
    mAdapter.addData(newData);
    if (newData.isEmpty()) {
      refreshLayout.finishLoadMore(0, true, true);
    } else {
      refreshLayout.finishLoadMore();
    }
  }
}
