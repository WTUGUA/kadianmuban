package com.novv.dzdesk.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.novv.dzdesk.live.PrefUtil;
import com.umeng.analytics.MobclickAgent;


/**
 * Created by lingyfh on 2018/4/16.
 */
public class AcceptUserProtocol {

    public static final String UserProtocolURL = "http://s.adesk.com/web_html/videowp_service_protocol.html";
    private static final String tag = AcceptUserProtocol.class.getSimpleName();
    private static final String KEY_ACCEPT = "key_user_protocol_accept";

    public static boolean needShowUserProtocol(Context context) {
        String needShowUserProtocol = AdeskOnlineConfigUtil.getConfigParams(
                context, "need_show_user_protocol");
        com.novv.dzdesk.util.LogUtil.i(tag, "needShowUserProtocol = " + needShowUserProtocol);
        if (!"true".equalsIgnoreCase(needShowUserProtocol)) {
            return false;
        }
        return !PrefUtil.getBoolean(context, KEY_ACCEPT, false);
    }

    public static boolean showUserProtocol(final Context context,
            final OperationListener listener) {

        if (PrefUtil.getBoolean(context, KEY_ACCEPT, false)) {
            LogUtil.i(tag, "showUserProtocol KEY_ACCEPT = true");
            if (listener != null) {
                listener.accept();
            }
            return false;
        }

        try {
            LogUtil.i(tag, "showUserProtocol");

            SweetAlertDialog dialog = new SweetAlertDialog(context);
            dialog.setTitleText("提示");
            dialog.setContentText(Html
                    .fromHtml("继续该操作，表示您同意<font color=\"#366aa4\">用户协议</font>"));
            dialog.setContentClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    com.novv.dzdesk.util.LogUtil.i(tag, "onClick");
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri
                            .parse(AcceptUserProtocol.UserProtocolURL));
                    context.startActivity(intent);
                }
            });

            dialog.setConfirmText("继续");
            dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog dialog) {
                    MobclickAgent.onEvent(context, "user_protocol_accept");
                    PrefUtil.putBoolean(context, KEY_ACCEPT, true);
                    if (listener != null) {
                        listener.accept();
                    }
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
            dialog.setCancelText("放弃");
            dialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog dialog) {
                    MobclickAgent.onEvent(context, "user_protocol_reject");
                    PrefUtil.putBoolean(context, KEY_ACCEPT, false);
                    if (listener != null) {
                        listener.reject();
                    }
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
            dialog.show();
            MobclickAgent.onEvent(context, "user_protocol_show");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
        return false;
    }

    public interface OperationListener {

        public void accept();

        public void reject();
    }

}
