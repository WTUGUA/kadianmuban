package com.novv.dzdesk.ui.activity.vwp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.ark.baseui.XAppCompatActivity;
import com.gyf.barlibrary.ImmersionBar;
import com.novv.dzdesk.R;
import com.novv.dzdesk.ui.fragment.vwp.TabVwpResFragment;
import com.umeng.analytics.MobclickAgent;

public class VwpResListActivity extends XAppCompatActivity {

    private static final String KEY_CATEGORY_ID = "key_category_id";
    private static final String KEY_TITLE = "key_title";
    private TextView tvTitle;
    private View btnBack;

    public static void launch(Context context, String categoryId, String title) {
        if (TextUtils.isEmpty(categoryId)) {
            return;
        }
        Intent intent = new Intent(context, VwpResListActivity.class);
        intent.putExtra(KEY_CATEGORY_ID, categoryId);
        intent.putExtra(KEY_TITLE, title);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_content;
    }

    @Nullable
    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void setUpView(@NonNull View rootView) {
        super.setUpView(rootView);
        tvTitle = findViewById(R.id.tv_title);
        btnBack = findViewById(R.id.btn_back);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        ImmersionBar.with(this)
                .titleBar(findViewById(R.id.fl_top), false)
                .transparentStatusBar()
                .init();
        String categoryId = getIntent().getStringExtra(KEY_CATEGORY_ID);
        String title = getIntent().getStringExtra(KEY_TITLE);
        tvTitle.setText(title);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (!TextUtils.isEmpty(categoryId)) {
            TabVwpResFragment fragment = TabVwpResFragment
                    .newInstance(TabVwpResFragment.TYPE_CATEGORY, categoryId);
            addFirstFragment(fragment);
        } else {
            getVDelegate().toastShort("无效分类");
            finish();
        }
    }

    private void addFirstFragment(Fragment fragment) {
        FragmentManager mFM = getSupportFragmentManager();
        int count = mFM.getBackStackEntryCount();
        while (count-- > 0) {
            mFM.popBackStack();
        }
        FragmentTransaction ft = mFM.beginTransaction();
        ft.replace(R.id.fragment, fragment, fragment.getClass().getSimpleName());
        ft.commitAllowingStateLoss();
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
        ImmersionBar.with(this).destroy();
    }
}
