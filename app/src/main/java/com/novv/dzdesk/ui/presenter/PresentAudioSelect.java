package com.novv.dzdesk.ui.presenter;

import android.support.annotation.NonNull;
import com.ark.baseui.XPresent;
import com.ark.tools.medialoader.AudioItem;
import com.ark.tools.medialoader.InterfaceContract;
import com.ark.tools.medialoader.MediaSearch;
import com.novv.dzdesk.ui.activity.local.AudioSelectActivity;

import java.util.ArrayList;
import java.util.List;

public class PresentAudioSelect extends XPresent<AudioSelectActivity> {

    public void loadLocalAudios(boolean isLoadMore) {
        if (hasV()) {
            if (isLoadMore) {
                getV().addMoreData(new ArrayList<AudioItem>());
                return;
            }
            new MediaSearch(getV())
                    .setLoadingCallback(new InterfaceContract.LoadingCallBack() {
                        @Override
                        public void showLoading() {
                            if (hasV()) {
                                getV().showProgress();
                            }
                        }

                        @Override
                        public void hideLoading() {
                            if (hasV()) {
                                getV().hideProgress();
                            }
                        }
                    })
                    .setErrorCallback(new InterfaceContract.ErrorCallBack() {
                        @Override
                        public void dealError(@NonNull String message) {
                            if (hasV()) {
                                getV().getVDelegate().toastShort(message);
                            }
                        }
                    })
                    .searchAudios(new InterfaceContract.DataImpl<AudioItem>() {

                        @Override
                        public void onFinish(@NonNull List<AudioItem> list) {
                            if (hasV()) {
                                getV().setNewData(list);
                            }
                        }
                    });
        }
    }
}
