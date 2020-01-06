package com.novv.dzdesk.ui.fragment.vwp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.ark.baseui.XLazyFragment;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.novv.dzdesk.R;
import com.novv.dzdesk.http.BaseObserver;
import com.novv.dzdesk.http.DefaultScheduler;
import com.novv.dzdesk.http.ServerApi;
import com.novv.dzdesk.res.model.BaseResult;
import com.novv.dzdesk.res.model.CategoryBean;
import com.novv.dzdesk.ui.activity.vwp.VwpResListActivity;
import com.novv.dzdesk.ui.adapter.vwp.AdapterVwpCat;
import com.novv.dzdesk.util.*;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import io.reactivex.Observer;

import java.util.ArrayList;
import java.util.List;

/**
 * 视频壁纸分类栏目
 */
public class TabVwpCatFragment extends XLazyFragment {

    private static final String tag = TabVwpCatFragment.class.getSimpleName();

    private TextView mNetworkView;
    private SmartRefreshLayout refreshLayout;
    private AdapterVwpCat mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    public int getLayoutId() {
        return R.layout.nav_category_fragment;
    }

    @Nullable
    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void setUpView(@NonNull View rootView) {
        super.setUpView(rootView);
        refreshLayout = rootView
                .findViewById(R.id.refreshLayout);
        mNetworkView = rootView.findViewById(R.id.network_tv);
        mRecyclerView = rootView.findViewById(R.id.recyclerView);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mNetworkView.setVisibility(View.GONE);
        mNetworkView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.i(tag, "v == " + v);
                if (!NetUtil.isNetworkAvailable(v.getContext())) {
                    try {
                        Intent intent = new Intent(
                                Settings.ACTION_SETTINGS);
                        v.getContext().startActivity(intent);
                    } catch (Exception e) {
                        ToastUtil.showToast(context,
                                R.string.op_failed);
                        e.printStackTrace();
                    }
                } else {
                    refreshLayout.autoRefresh();
                }
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new AdapterVwpCat(new ArrayList<CategoryBean>());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (position >= mAdapter.getData().size()) {
                    LogUtil.i(tag,
                            "on item click position out of array size = "
                                    + position);
                    return;
                }
                LogUtil.i(tag, "on item click position = " + position);
                CategoryBean categoryBean = mAdapter.getData().get(position);
                AnaUtil.anaClick(context, categoryBean);
                UmUtil.anaCategoryOp(context, categoryBean.getName());
                VwpResListActivity.launch(context, categoryBean.getId(), categoryBean.getName());
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                requestData(true);
            }
        });
        requestData(true);
    }

    /**
     * 请求资源数据
     *
     * @param isPullRefresh 是否是下拉刷新,区分分页加载
     */
    private void requestData(final boolean isPullRefresh) {
        LogUtil.i(tag, "requestData---->" + "isPullRefresh:" + isPullRefresh);
        ServerApi.getCategory()
                .compose(
                        DefaultScheduler.<BaseResult<List<CategoryBean>>>getDefaultTransformer())
                .subscribe(getDefaultObserver(isPullRefresh));
    }

    private Observer<BaseResult<List<CategoryBean>>> getDefaultObserver(
            final boolean isPullRefresh) {
        return new BaseObserver<List<CategoryBean>>() {
            @Override
            public void onSuccess(@NonNull final List<CategoryBean> mList) {
                if (isPullRefresh) {
                    refreshLayout.finishRefresh();
                }
                mAdapter.replaceData(mList);
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        mNetworkView.setVisibility(mList.size() <= 0 ? View.VISIBLE : View.GONE);
                    }
                });
            }

            @Override
            public void onFailure(int code, @NonNull String message) {
                if (isPullRefresh) {
                    refreshLayout.finishRefresh();
                }
            }
        };
    }
}