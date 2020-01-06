package com.novv.dzdesk.ui.fragment.ae;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.novv.dzdesk.R;

public class ShowAddressLoadingFragment extends DialogFragment {



    public interface LoadingCallback{

        void openVip();

        void closeDialog();

    }

    private LoadingCallback loadingCallback;

    public void setLoadingCallback(LoadingCallback loadingCallback){
        this.loadingCallback = loadingCallback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_loading_unity_ad,null,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.bt_open_vip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loadingCallback!=null){
                    loadingCallback.openVip();
                }

            }
        });

        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loadingCallback!=null){
                    loadingCallback.closeDialog();
                }
            }
        });
    }
}
