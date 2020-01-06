package com.novv.dzdesk.ui.fragment.picker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.ark.baseui.XFragment;
import com.novv.dzdesk.R;
import com.novv.dzdesk.ui.activity.local.VideoLocalListActivity;
import com.novv.dzdesk.ui.adapter.local.AdapterLocalVideoPicker;
import com.novv.dzdesk.util.NetUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import java.util.ArrayList;
import java.util.List;

public class LocalVideosFragment extends XFragment<LocalVideosPresenter> {

  private AdapterLocalVideoPicker mAdapter;
  private ArrayList<String> inputList = new ArrayList<>();
  private RecyclerView mRecyclerView;
  private SmartRefreshLayout mRefreshLayout;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle bundle = getArguments();
    if (bundle != null) {
      ArrayList<String> data = bundle.getStringArrayList("data");
      if (data != null) {
        this.inputList.addAll(data);
      }
    }
  }

  @Nullable
  @Override
  public LocalVideosPresenter newP() {
    return new LocalVideosPresenter();
  }

  @Override
  public int getLayoutId() {
    return R.layout.layout_refresh_list;
  }

  @Override
  public void setUpView(@NonNull View rootView) {
    super.setUpView(rootView);
    mRecyclerView = rootView.findViewById(R.id.recyclerView);
    mRefreshLayout = rootView.findViewById(R.id.refreshLayout);
  }

  @Override
  public void initData(@Nullable Bundle savedInstanceState) {
    mRecyclerView.setLayoutManager(new GridLayoutManager(context, 3));
    mAdapter = new AdapterLocalVideoPicker(new ArrayList<String>(), inputList);
    mAdapter.setOnItemClickListener(new AdapterLocalVideoPicker.OnItemClickListener() {
      @Override
      public void onVideoClick(int position, List<String> selectVideo) {
        VideoLocalListActivity activity = (VideoLocalListActivity) getActivity();
        if (activity != null) {
          activity.onLocalVideosSelected(selectVideo);
        }
      }
    });
    mRecyclerView.setAdapter(mAdapter);
    mAdapter.setEnableLoadMore(false);
    mRefreshLayout.setEnableLoadMore(false);
    mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
      @Override public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        getP().loadLocalVideos(true);
      }

      @Override public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getP().loadLocalVideos(false);
      }
    });
    getP().loadLocalVideos(false);
  }

  public void showProgress() {
    if (context instanceof VideoLocalListActivity) {
      ((VideoLocalListActivity) context).onShowProgress(true);
    }
  }

  public void hideProgress() {
    if (context instanceof VideoLocalListActivity) {
      ((VideoLocalListActivity) context).onShowProgress(false);
    }
  }

  public void setError(boolean isLoadMore) {
    if (isLoadMore) {
      mRefreshLayout.finishLoadMore(0, false, false);
    } else {
      mRefreshLayout.finishRefresh();
      mAdapter.replaceData(new ArrayList<String>());
      View view = mAdapter.getEmptyView();
      if (view == null) {
        view = LayoutInflater.from(context).inflate(R.layout.empty_view, null);
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(
            NetUtil.isNetworkAvailable(context) ? R.mipmap.ic_empty_favor :
                R.mipmap.ic_empty_nonet);
        TextView textView = view.findViewById(R.id.tvEmpty);
        textView.setText("本机没有更多视频资源~");
        mAdapter.setEmptyView(view);
        view.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mRefreshLayout.autoRefresh();
          }
        });
      }
    }
  }

  public void setNewData(List<String> newData) {
    mAdapter.replaceData(newData);
    mRefreshLayout.finishRefresh();
    if (newData.isEmpty()) {
      setError(false);
    }
  }

  public void addMoreData(List<String> moreData) {
    mAdapter.addData(moreData);
    if (moreData.isEmpty()) {
      mRefreshLayout.finishLoadMore(0, true, true);
    } else {
      mRefreshLayout.finishLoadMore();
    }
  }
}
