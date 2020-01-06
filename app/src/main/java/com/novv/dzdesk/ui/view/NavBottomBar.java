package com.novv.dzdesk.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.novv.dzdesk.R;
import com.novv.dzdesk.util.LogUtil;

/**
 * Created by lingyfh on 15/7/10.
 */
public class NavBottomBar extends RelativeLayout implements View.OnClickListener {

    private static final String tag = NavBottomBar.class.getSimpleName();
    private View mFirstView;
    private View mSecondView;
    private View mThirdView;
    private View mFouthView;
    private View mUploadView;
    private OnItemClickListener mListener;

    public NavBottomBar(Context context) {
        super(context);
    }

    public NavBottomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public NavBottomBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    private void initView() {
        mFirstView = findViewById(R.id.nav_first_ll);
        mSecondView = findViewById(R.id.nav_second_ll);
        mThirdView = findViewById(R.id.nav_third_ll);
        mFouthView = findViewById(R.id.nav_fourth_ll);
        mUploadView = findViewById(R.id.nav_upload_ll);

        mFirstView.setOnClickListener(this);
        mSecondView.setOnClickListener(this);
        mThirdView.setOnClickListener(this);
        mFouthView.setOnClickListener(this);
        mUploadView.setOnClickListener(this);

        initView(mFirstView, R.drawable.selector_nav_home,
                R.string.nav_tab_home_name);
        initView(mSecondView, R.drawable.selector_nav_avatar,
                R.string.nav_tab_avatar_name);
        initView(mThirdView, R.drawable.selector_nav_rings,
                R.string.nav_tab_rings_name);
        initView(mFouthView, R.drawable.selector_nav_person,
                R.string.nav_tab_person_name);

    }

    private void initView(View view, int bgResid, int nameResid) {
        ImageView tabIcon = view.findViewById(R.id.tab_icon);
        TextView tabName = view.findViewById(R.id.tab_name);
        tabIcon.setImageResource(bgResid);
        tabName.setText(nameResid);
    }

    private void initView(View view, int bgResid, String name) {
        ImageView tabIcon = view.findViewById(R.id.tab_icon);
        TextView tabName = view.findViewById(R.id.tab_name);
        tabIcon.setImageResource(bgResid);
        tabName.setText("" + name);
    }

    private void initView(View view, StateListDrawable drawable, int nameResid) {
        ImageView tabIcon = view.findViewById(R.id.tab_icon);
        TextView tabName = view.findViewById(R.id.tab_name);
        tabIcon.setImageDrawable(drawable);
        tabName.setText(nameResid);
    }

    private void toggleSelected(View view) {
        mFirstView.setSelected(false);
        mSecondView.setSelected(false);
        mThirdView.setSelected(false);
        mFouthView.setSelected(false);

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
        } else if (type == NavType.Fourth) {
            if (mFouthView == null) {
                return;
            }
            onClick(mFouthView);
        } else if (type == NavType.Upload) {
            if (mUploadView == null) {
                return;
            }
            onClick(mUploadView);
        }
    }

    @Override
    public void onClick(View v) {
        LogUtil.i(tag, "v = " + v);
        if (mListener == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.nav_first_ll:
                toggleSelected(v);
                mListener.onFirstClick(v);
                break;
            case R.id.nav_second_ll:
                toggleSelected(v);
                mListener.onSecondClick(v);
                break;
            case R.id.nav_third_ll:
                toggleSelected(v);
                mListener.onThirdClick(v);
                break;
            case R.id.nav_fourth_ll:
                toggleSelected(v);
                mListener.onFouthClick(v);
                break;
            case R.id.nav_upload_ll:
                mListener.onUploadClick(v);
                break;
        }
    }

    public boolean isFirstSelected() {
        return mFirstView.isSelected();
    }

    public enum NavType {
        First, Second, Third, Fourth, Upload
    }

    public interface OnItemClickListener {

        void onFirstClick(View view);

        void onSecondClick(View view);

        void onThirdClick(View view);

        void onFouthClick(View view);

        void onUploadClick(View view);
    }
}
