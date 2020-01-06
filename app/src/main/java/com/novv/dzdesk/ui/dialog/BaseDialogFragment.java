package com.novv.dzdesk.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.novv.dzdesk.util.LogUtil;

/**
 * DialogFragment封装 Created by lijianglong on 2017/9/6.
 */

public abstract class BaseDialogFragment extends DialogFragment {

    public FragmentActivity mContext;
    protected boolean isViewPrepared; // 标识fragment视图已经初始化完毕
    protected View mRootView;
    private boolean hasFetchData; // 标识已经触发过懒加载数据

    public BaseDialogFragment() {
    }

    /**
     * 请不要使用 bug：IllegalStateException: Can not perform this action after onSaveInstanceState
     *
     * @param manager FragmentManager
     * @param tag     tag
     */
    @Deprecated
    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

    /**
     * 请不要使用 bug：IllegalStateException: Can not perform this action after onSaveInstanceState
     *
     * @param transaction FragmentTransaction
     * @param tag         tag
     */
    @Deprecated
    @Override
    public int show(FragmentTransaction transaction, String tag) {
        return super.show(transaction, tag);
    }

    public void show(FragmentActivity activity, String tag) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        LogUtil.d("TAG", " setUserVisibleHint() --> isVisibleToUser = " + isVisibleToUser);
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            lazyFetchDataIfPrepared();
        }
    }

    /**
     * 如果布局已加载初始化完成则在加载数据，用于可见状态
     */
    private void lazyFetchDataIfPrepared() {
        if (getUserVisibleHint() && !hasFetchData && isViewPrepared) {
            hasFetchData = true;
            pullData();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        setStyle(DialogFragment.STYLE_NORMAL, setFragmentStyle());
        setHasOptionsMenu(true);
        handleArgument(getArguments());
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            DisplayMetrics dm = new DisplayMetrics();
            mContext.getWindowManager().getDefaultDisplay().getMetrics(dm);
            Window window = dialog.getWindow();
            setWindowSize(window);
        }
    }

    public abstract void setWindowSize(Window window);

    public abstract
    @StyleRes
    int setFragmentStyle();

    /**
     * 视图创建
     *
     * @param inflater           LayoutInflater
     * @param container          ViewGroup
     * @param savedInstanceState Bundle
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(getLayoutId(), container, false);
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        initView(mRootView);
        initData();
        return mRootView;
    }

    /**
     * 布局文件的加载
     *
     * @return 布局文件的layoutRes
     */
    protected abstract
    @LayoutRes
    int getLayoutId();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewPrepared = true;
        lazyFetchDataIfPrepared();
    }

    public abstract void initView(View view);

    public abstract void initData();

    /**
     * 懒加载的数据，用于网络请求耗时操作等
     */
    public abstract void pullData();

    /**
     * 用于强制刷新，通知刷新，逻辑自行实现
     */
    public void reloadData() {

    }

    /**
     * 获取传值 Bundle 判空
     *
     * @param argument Bundle
     */
    public void handleArgument(Bundle argument) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hasFetchData = false;
        isViewPrepared = false;
    }
}
