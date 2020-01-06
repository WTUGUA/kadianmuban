package com.novv.dzdesk.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.novv.dzdesk.R;
import com.novv.dzdesk.util.LogUtil;

/**
 * Created by lingyfh on 15/7/10.
 */
public class NavTabTitleHomeBar extends LinearLayout
        implements
        View.OnClickListener {

    private static final String tag = NavTabTitleHomeBar.class.getSimpleName();
    private View mFirstView;
    private View mSecondView;
    private View mThirdView;
    private View mForthView;
    private OnItemClickListener mListener;

    public NavTabTitleHomeBar(Context context) {
        super(context);
    }

    public NavTabTitleHomeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public NavTabTitleHomeBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    private void initView() {
        mFirstView = findViewById(R.id.nav_first_rl);
        mSecondView = findViewById(R.id.nav_second_rl);
        mThirdView = findViewById(R.id.nav_third_rl);
        mForthView = findViewById(R.id.nav_forth_rl);

        mFirstView.setOnClickListener(this);
        mSecondView.setOnClickListener(this);
        mThirdView.setOnClickListener(this);
        mForthView.setOnClickListener(this);
    }

    private void toggleSelected(View view) {
        mFirstView.setSelected(false);
        mSecondView.setSelected(false);
        mThirdView.setSelected(false);
        mForthView.setSelected(false);
        view.setSelected(true);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void performClickNav(NavType type) {
        if (type == NavType.First) {
            if (mFirstView == null) {
                return;
            }
            onClick(mFirstView);
        } else if (type == NavType.Second) {
            if (mSecondView == null) {
                return;
            }
            onClick(mSecondView);
        } else if (type == NavType.Third) {
            if (mThirdView == null) {
                return;
            }
            onClick(mThirdView);
        } else if (type == NavType.Forth) {
            if (mForthView == null) {
                return;
            }
            onClick(mForthView);
        }
    }

    public void setNavTitleSelected(NavType type) {
        LogUtil.i(tag, "setNavTitleSelected type = " + type);
        mFirstView.setSelected(false);
        mSecondView.setSelected(false);
        mThirdView.setSelected(false);
        mForthView.setSelected(false);
        if (type == NavType.First) {
            mFirstView.setSelected(true);
        } else if (type == NavType.Second) {
            mSecondView.setSelected(true);
        } else if (type == NavType.Third) {
            mThirdView.setSelected(true);
        } else if (type == NavType.Forth) {
            mForthView.setSelected(true);
        }
    }

    @Override
    public void onClick(View v) {
        LogUtil.i(tag, "v = " + v);
        toggleSelected(v);
        if (mListener == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.nav_first_rl:
                mListener.onFirstClick(v);
                break;
            case R.id.nav_second_rl:
                mListener.onSecondClick(v);
                break;
            case R.id.nav_third_rl:
                mListener.onThirdClick(v);
                break;
            case R.id.nav_forth_rl:
                mListener.onForthClick(v);
                break;
        }
    }

    public boolean isFirstSelected() {
        return mFirstView.isSelected();
    }

    public enum NavType {
        First, Second, Third, Forth
    }

    public interface OnItemClickListener {

        void onFirstClick(View view);

        void onSecondClick(View view);

        void onThirdClick(View view);

        void onForthClick(View view);
    }
}
