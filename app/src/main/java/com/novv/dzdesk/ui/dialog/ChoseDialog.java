package com.novv.dzdesk.ui.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.novv.dzdesk.R;
import com.novv.dzdesk.util.ToastUtil;

import java.util.ArrayList;

/**
 * 上传信息填写，cids Created by lijianglong on 2017/8/29.
 */

public class ChoseDialog extends DialogFragment implements AdapterView.OnItemSelectedListener {

    private static final String tag = DialogFragment.class.getSimpleName();
    private String spinnerName;
    private String spinnerId;
    private ArrayList<String> idList;
    private ArrayList<String> nameList;
    private CallBack callBack;
    private LayoutInflater inflater;
    private String choseTypeTitle;

    public static ChoseDialog launch(FragmentActivity activity, ArrayList<String> idList,
            ArrayList<String> nameList) {
        return launch(activity, idList, nameList, "");
    }

    public static ChoseDialog launch(FragmentActivity activity, ArrayList<String> idList,
            ArrayList<String> nameList, String choseTypeTitle) {
        FragmentManager fm = activity.getSupportFragmentManager();
        ChoseDialog fragment = new ChoseDialog();
        Bundle bundle = new Bundle();
        bundle.putString("choseTypeTitle", choseTypeTitle);
        bundle.putStringArrayList("idList", idList);
        bundle.putStringArrayList("nameList", nameList);
        fragment.setArguments(bundle);//把参数传递给该DialogFragment
        fragment.show(fm, tag);
        return fragment;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        spinnerId = idList.get(i);
        spinnerName = nameList.get(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        spinnerId = "-1";
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppDialogDark);
        setCancelable(true);
        inflater = LayoutInflater.from(getContext());
        nameList = getArguments().getStringArrayList("nameList");
        idList = getArguments().getStringArrayList("idList");
        choseTypeTitle = getArguments().getString("choseTypeTitle");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View customView = inflater.inflate(
                R.layout.dialog_chose_layout, container, false);
        customView.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));

        TextView dialog_chose_title_tv = customView.findViewById(R.id.dialog_chose_title_tv);
        if (!TextUtils.isEmpty(choseTypeTitle)) {
            dialog_chose_title_tv.setText(choseTypeTitle);
        }

        TextView mBtnSure = customView.findViewById(R.id.confirm_button);
        TextView mBtnCancel = customView.findViewById(R.id.cancel_button);
        Spinner mSpinner = customView.findViewById(R.id.spinner);
        mBtnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(spinnerId) || TextUtils.equals("-1", spinnerId)) {
                    ToastUtil.showToast(getActivity(), "请选择一个选项");
                } else {
                    dismissAllowingStateLoss();
                    if (callBack != null) {
                        callBack.onSureClick(spinnerId, spinnerName);
                    }
                }
            }
        });
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissAllowingStateLoss();
            }
        });
        ListAdapter arrayAdapter = new ListAdapter(nameList);
        mSpinner.setOnItemSelectedListener(this);
        mSpinner.setAdapter(arrayAdapter);
        return customView;
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public interface CallBack {

        void onSureClick(String spinnerId, String spinnerName);
    }

    private class ListAdapter extends BaseAdapter implements SpinnerAdapter {

        ArrayList<String> nameList;

        ListAdapter(ArrayList<String> nameList) {
            this.nameList = nameList;
        }

        @Override
        public int getCount() {
            return nameList == null ? 0 : nameList.size();
        }

        @Override
        public Object getItem(int position) {
            return nameList == null ? null : nameList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.dialog_item_select, parent, false);
            }
            TextView text = convertView.findViewById(R.id.tv_text);
            text.setText(nameList.get(position));
            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.dialog_item_drop, parent, false);
            }
            TextView text = convertView.findViewById(R.id.tv_text);
            text.setText(nameList.get(position));
            return convertView;
        }
    }
}
