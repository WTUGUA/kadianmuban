package com.novv.dzdesk.ui.activity.rings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.ark.baseui.XAppCompatActivity;
import com.novv.dzdesk.R;
import com.novv.dzdesk.ui.fragment.rings.TabRingsDataFragment;
import com.umeng.analytics.MobclickAgent;

public class RingsListActivity extends XAppCompatActivity {

    private ImageView btnBack;
    private TextView tvTitle;
    private TabRingsDataFragment rdf;

    public static void launch(Context context, String cid, String title) {
        Intent intent = new Intent(context, RingsListActivity.class);
        intent.putExtra("cid", cid);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_avatar_list;
    }

    @Override
    public void setUpView(@NonNull View rootView) {
        super.setUpView(rootView);
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
    }

    @Nullable
    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void initData(@Nullable Bundle bundle) {
        Bundle arg = new Bundle();
        Intent intent = getIntent();
        String title = "铃声";
        if (intent != null) {
            arg.putString("cid", getIntent().getStringExtra("cid"));
            title = getIntent().getStringExtra("title");
        }
        if (bundle == null) {
            rdf = TabRingsDataFragment.getInstance(2, arg);
            rdf.setArguments(arg);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.mContent, rdf, "rdf")
                    .commitAllowingStateLoss();

        } else {
            rdf = (TabRingsDataFragment) getSupportFragmentManager().findFragmentByTag("rdf");
            rdf.setArguments(arg);
            showFragment(0);
        }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvTitle.setText(title);
    }

    private void showFragment(int index) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < getSupportFragmentManager().getFragments().size(); i++) {
            Fragment fragment = getSupportFragmentManager().getFragments().get(i);
            ft.hide(fragment);
            if (index == i) {
                ft.show(fragment);
            }
        }
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
}
