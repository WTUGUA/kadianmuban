package com.novv.dzdesk.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class VShareDialog extends Dialog {

    public VShareDialog(@NonNull Context context) {
        super(context);
    }

    public VShareDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected VShareDialog(@NonNull Context context, boolean cancelable,
            @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
