package com.novv.dzdesk.ui.activity.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.ark.baseui.XAppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.model.UserModel;
import com.novv.dzdesk.ui.adapter.user.AdapterUserDetail;
import com.novv.dzdesk.ui.view.NavTabTitlePersionBar;

public class UserDetailActivity extends XAppCompatActivity implements View.OnClickListener {

    private static final String EXTRA_USER_MODEL = "UserModel";
    private ViewPager mPager;
    private NavTabTitlePersionBar mTabTitleBar;
    private ImageView imgAvatar;
    private TextView tvLoginName;
    private TextView tvUserSign;
    private ImageView backView;
    private ImageView backView2;
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayoutState state;

    public static void start(@NonNull Context context, UserModel userModel) {
        Intent intent = new Intent(context, UserDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_USER_MODEL, userModel);
        context.startActivity(intent);
    }


    @Nullable
    @Override
    public Object newP() {
        return null;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_user_detail;
    }

    @Override
    public void setUpView(@NonNull View rootView) {
        super.setUpView(rootView);
        mPager = findViewById(R.id.view_pager);
        mTabTitleBar = findViewById(R.id.nav_tab_title_bar);
        imgAvatar = findViewById(R.id.img_avatar);
        tvLoginName = findViewById(R.id.tv_login_name);
        backView = findViewById(R.id.iv_back);
        backView2 = findViewById(R.id.iv_back2);
        tvUserSign = findViewById(R.id.tv_sign);
        mAppBarLayout = findViewById(R.id.appbar_layout);
        TextView mFirstTabTv = findViewById(R.id.nav_first_tv);
        TextView mSecondTabTv = findViewById(R.id.nav_second_tv);

        mFirstTabTv.setText(getResources().getString(R.string.nav_other_person_favorite));
        mSecondTabTv.setText(getResources().getString(R.string.nav_other_person_contribute));
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int maxScroll = appBarLayout.getTotalScrollRange();
                float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;
                if (verticalOffset == 0) {
                    if (state != CollapsingToolbarLayoutState.EXPANDED) {
                        state = CollapsingToolbarLayoutState.EXPANDED;//修改状态标记为展开
                    }
                    backView2.setVisibility(View.GONE);
                    backView.setVisibility(View.VISIBLE);
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    if (state != CollapsingToolbarLayoutState.COLLAPSED) {
                        state = CollapsingToolbarLayoutState.COLLAPSED;//修改状态标记为折叠
                    }
                    backView2.setVisibility(View.VISIBLE);
                } else {
                    if (state != CollapsingToolbarLayoutState.INTERNEDIATE) {
                        if (state == CollapsingToolbarLayoutState.COLLAPSED) {
                            //由折叠变为中间状态
                            backView2.setVisibility(View.GONE);
                        } //else if (state == CollapsingToolbarLayoutState.EXPANDED) {
                        //由展开变为中间状态
                        //}
                        state = CollapsingToolbarLayoutState.INTERNEDIATE;//修改状态标记为中间
                    }
                }
            }
        });
        backView.setOnClickListener(this);
        backView2.setOnClickListener(this);
        UserModel mUser = (UserModel) getIntent().getSerializableExtra(EXTRA_USER_MODEL);
        if (mUser == null) {
            finish();
            return;
        }
        tvLoginName.setText(mUser.getNickname());
        tvUserSign.setText(mUser.getDesc());
        int loadingResId = R.drawable.user_avatar_default;
        Glide.with(this)
                .load(mUser.getImg())
                .apply(new RequestOptions().placeholder(loadingResId).error(loadingResId))
                .into(imgAvatar);
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                changeNavTabState(position);
            }
        });
        mTabTitleBar.setOnItemClickListener(new NavTabTitlePersionBar.OnItemClickListener() {
            @Override
            public void onFirstClick(View view) {
                mPager.setCurrentItem(0, false);
            }

            @Override
            public void onSecondClick(View view) {
                mPager.setCurrentItem(1, false);
            }

        });
        AdapterUserDetail adapterUserDetail = new AdapterUserDetail(
                getSupportFragmentManager(), mUser.getUserId());
        mPager.setAdapter(adapterUserDetail);
        mTabTitleBar.setNavTitleSelected(NavTabTitlePersionBar.NavType.First);
    }

    private void changeNavTabState(int position) {
        if (position == 0) {
            mTabTitleBar.setNavTitleSelected(NavTabTitlePersionBar.NavType.First);
        } else if (position == 1) {
            mTabTitleBar.setNavTitleSelected(NavTabTitlePersionBar.NavType.Second);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
            case R.id.iv_back2:
                finish();
                break;
        }
    }

    private enum CollapsingToolbarLayoutState {
        EXPANDED,//展开状态
        COLLAPSED,//折叠状态
        INTERNEDIATE//中间状态
    }
}
