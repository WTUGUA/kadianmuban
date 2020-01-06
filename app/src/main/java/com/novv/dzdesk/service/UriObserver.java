package com.novv.dzdesk.service;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import com.adesk.tool.plugin.PSetUtils;
import com.ark.adkit.basics.utils.LogUtils;
import com.novv.dzdesk.res.event.EventCode;
import com.novv.dzdesk.rxbus2.RxBus;

public class UriObserver extends ContentObserver {

    private Context context;

    public UriObserver(Context context, Handler handler) {
        super(handler);
        this.context = context;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        LogUtils.e("宿主监测到变化");
        RxBus.getDefault()
                .send(EventCode.VOICE_CODE, new Voice(selfChange, PSetUtils.isVolume(context)));
    }

    public static class Voice {

        public boolean isSelf;
        public boolean isOn;

        public Voice(boolean isSelf, boolean isOn) {
            this.isSelf = isSelf;
            this.isOn = isOn;
        }
    }
}
