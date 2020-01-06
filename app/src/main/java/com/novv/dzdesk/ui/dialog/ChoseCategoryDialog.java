package com.novv.dzdesk.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.model.CategoryBean;
import com.novv.dzdesk.util.LogUtil;
import com.novv.dzdesk.util.ToastUtil;

import java.util.ArrayList;

public class ChoseCategoryDialog extends DialogFragment {

    private static final String tag = ChoseCategoryDialog.class.getSimpleName();
    private static final String KEY_ITEMS = "key_items";
    private ArrayList<CategoryBean> mItems;
    private View mSureView;
    private ListView mListView;
    private ItemAdapter mAdapter;
    private OnSelectedListener mListener;

    public static ChoseCategoryDialog showDialog(FragmentActivity activity,
            ArrayList<CategoryBean> items) {
        ChoseCategoryDialog dialog = new ChoseCategoryDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_ITEMS, items);
        dialog.setArguments(bundle);
        dialog.show(activity.getSupportFragmentManager(), tag);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar);
        mItems = (ArrayList<CategoryBean>) getArguments().getSerializable(KEY_ITEMS);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.chose_category_dialog, container, false);
        initView(view);
        return view;
    }

    public OnSelectedListener getOnSelectedListener() {
        return mListener;
    }

    public void setOnSelectedListener(OnSelectedListener listener) {
        this.mListener = listener;
    }

    public void initView(View view) {
        view.findViewById(R.id.ll_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mSureView = view.findViewById(R.id.sure_ll);
        mListView = view.findViewById(R.id.list_view);

        mSureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAdapter.selectedIndex == -1) {
                    ToastUtil.showGeneralToast(getContext(), "您还没有选择分类");
                    return;
                }
                mListener.onSelected(mAdapter.selectedIndex);
                dismiss();
            }
        });
        mAdapter = new ItemAdapter(getContext(), mItems);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            private View selectedView;

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (selectedView != null) {
                    selectedView.setBackgroundResource(R.color.transparent);
                }
                view.setBackgroundResource(R.color.color_accent);
                mAdapter.selectedIndex = i;
                selectedView = view;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i(tag, "onResume");
    }


    public interface OnSelectedListener {

        public void onSelected(int position);
    }

    public class ItemAdapter extends BaseAdapter {


        public int selectedIndex = -1;
        private Context mContext;
        private ArrayList<CategoryBean> mItems;

        public ItemAdapter(Context context, ArrayList<CategoryBean> items) {
            this.mContext = context;
            this.mItems = items;
        }

        @Override
        public int getCount() {
            return mItems == null ? 0 : mItems.size();
        }

        @Override
        public Object getItem(int i) {
            return mItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ItemViewHolder holder;
            if (view == null) {
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.chose_category_item_layout, null);
                holder = new ItemViewHolder();
                holder.textView = view.findViewById(R.id.item_tv);
                view.setTag(holder);
            } else {
                holder = (ItemViewHolder) view.getTag();
            }

            holder.textView.setText(mItems.get(i).getName());
            if (selectedIndex == i) {
                view.setBackgroundResource(R.color.color_accent);
            } else {
                view.setBackgroundResource(R.color.transparent);
            }
            return view;
        }

        public class ItemViewHolder {

            public TextView textView;
        }
    }
}
