package com.novv.dzdesk.res.ae;

import android.support.annotation.NonNull;
import com.lansosdk.LanSongAe.LSOAeDrawable;
import com.lansosdk.videoeditor.DrawPadAEExecute;
import com.lansosdk.videoeditor.DrawPadAEPreview;

import java.io.Serializable;

public abstract class Layer implements Serializable {

    public abstract void preview(@NonNull LSOAeDrawable drawable, @NonNull DrawPadAEPreview padAEPreview);

    public abstract void execute(@NonNull LSOAeDrawable drawable, @NonNull DrawPadAEExecute padAEExecute);

    public abstract void release();
}
