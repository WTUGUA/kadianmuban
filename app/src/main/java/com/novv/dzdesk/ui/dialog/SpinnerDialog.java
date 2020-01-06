package com.novv.dzdesk.ui.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.novv.dzdesk.R;
import com.novv.dzdesk.util.ToastUtil;

import java.util.ArrayList;

/**
 * 上传信息填写，cids name Created by lijianglong on 2017/8/29.
 */

public class SpinnerDialog extends DialogFragment implements AdapterView.OnItemSelectedListener {

    private static final String tag = DialogFragment.class.getSimpleName();
    private EditText mInput;
    private String spinnerId;
    private ArrayList<String> idList;
    private ArrayList<String> nameList;
    private CallBack callBack;
    private LayoutInflater inflater;

    public static SpinnerDialog launch(FragmentActivity activity, ArrayList<String> idList,
            ArrayList<String> nameList) {
        FragmentManager fm = activity.getSupportFragmentManager();
        SpinnerDialog fragment = new SpinnerDialog();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("idList", idList);
        bundle.putStringArrayList("nameList", nameList);
        fragment.setArguments(bundle);//把参数传递给该DialogFragment
        fragment.show(fm, tag);
        return fragment;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        spinnerId = idList.get(i);
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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View customView = inflater.inflate(
                R.layout.dialog_spinner_layout, container, false);
        customView.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
        TextView mBtnSure = customView.findViewById(R.id.confirm_button);
        TextView mBtnCancel = customView.findViewById(R.id.cancel_button);
        Spinner mSpinner = customView.findViewById(R.id.spinner);
        mInput = customView.findViewById(R.id.et_input);
        mInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)}); //最大输入长度
        mBtnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = mInput.getText().toString();
                if (TextUtils.isEmpty(input)) {
                    ToastUtil.showToast(getActivity(), "名称不能为空！");
                } else if (TextUtils.isEmpty(spinnerId) || TextUtils.equals("-1", spinnerId)) {
                    ToastUtil.showToast(getActivity(), "分类不能为空！");
                } else {
                    dismissAllowingStateLoss();
                    if (callBack != null) {
                        callBack.onSureClick(input, spinnerId);
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

        void onSureClick(String inputText, String spinnerId);
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
