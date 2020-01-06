package com.novv.dzdesk.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.novv.dzdesk.R;
import com.novv.dzdesk.util.LogUtil;

/**
 * Created by lingyfh on 15/7/10.
 */
public class NavTabTitlePersionBar extends LinearLayout
        implements
        View.OnClickListener {

    private static final String tag = NavTabTitlePersionBar.class.getSimpleName();
    private View mFirstView;
    private View mSecondView;
    private TextView mNavFirstTV;
    private TextView mNavSecondTV;
    private OnItemClickListener mListener;

    public NavTabTitlePersionBar(Context context) {
        super(context);
    }

    public NavTabTitlePersionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public NavTabTitlePersionBar(Context context, AttributeSet attrs, int defStyle) {
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

        mNavFirstTV = findViewById(R.id.nav_first_tv);
        mNavSecondTV = findViewById(R.id.nav_second_tv);

        mFirstView.setOnClickListener(this);
        mSecondView.setOnClickListener(this);
    }

    private void toggleSelected(View view) {
        mFirstView.setSelected(false);
        mSecondView.setSelected(false);

        view.setSelected(true);
    }

    public void setNavTabTitle(String firstTitle, String secondTitle) {
        mNavFirstTV.setText(firstTitle);
        mNavSecondTV.setText(secondTitle);
    }

    public void setNavTabTitle(int firstTitleResId, int secondTitleResId) {
        mNavFirstTV.setText(getResources().getString(firstTitleResId));
        mNavSecondTV.setText(getResources().getString(secondTitleResId));
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
        }
    }

    public void setNavTitleSelected(NavType type) {
        LogUtil.i(tag, "setNavTitleSelected type = " + type);
        mFirstView.setSelected(false);
        mSecondView.setSelected(false);

        if (type == NavType.First) {
            mFirstView.setSelected(true);
        } else if (type == NavType.Second) {
            mSecondView.setSelected(true);
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
        }
    }

    public boolean isFirstSelected() {
        return mFirstView.isSelected();
    }

    public enum NavType {
        First, Second
    }

    public interface OnItemClickListener {

        void onFirstClick(View view);

        void onSecondClick(View view);
    }
}
