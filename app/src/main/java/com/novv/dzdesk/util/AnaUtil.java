package com.novv.dzdesk.util;

import android.content.Context;
import android.os.AsyncTask;
import com.novv.dzdesk.res.model.CategoryBean;
import com.novv.dzdesk.res.model.ResourceBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lingyfh on 2017/6/2.
 */
public class AnaUtil {

    private static final String tag = AnaUtil.class.getSimpleName();

    private static final ArrayList<ResourceBean> AnaViewIds = new ArrayList<>();

    public static void anaClick(final Context context, final ResourceBean bean) {
        anaResource(context, bean, AnaType.Click);
    }

    public static void anaClick(final Context context, final CategoryBean bean) {
        anaResource(context, bean, AnaType.Click);
    }

    public static void anaSet(final Context context, final ResourceBean bean) {
        anaResource(context, bean, AnaType.Set);
    }

    public static void anaDownload(final Context context,
            final ResourceBean bean) {
        anaResource(context, bean, AnaType.Download);
    }

    public static void anaView(final Context context, final ResourceBean bean) {
        AnaViewIds.add(bean);
        // anaResource(context, bean, AnaType.View);
    }

    public static void anaPlay(final Context context, final ResourceBean bean) {
        anaResource(context, bean, AnaType.Play);
    }

    public static void anaFavorite(final Context context,
            final ResourceBean bean) {
        anaResource(context, bean, AnaType.Favorite);
    }

    public static void anaShare(final Context context, final ResourceBean bean) {
        anaResource(context, bean, AnaType.Share);
    }

    public static void anaViewResources(final Context context, boolean sync) {
        if (context == null || AnaViewIds == null || AnaViewIds.isEmpty()) {
            LogUtil.i(tag, "ana resource exception, context = " + context
                    + " bean = " + AnaViewIds);
            return;
        }

        if (AnaViewIds.size() < 50 && !sync) {
            return;
        }

        final ArrayList<ResourceBean> anaItems = new ArrayList<>(AnaViewIds);
        AnaViewIds.clear();

        final String anaURL = anaItems.get(0).getAnalyticViewURL();

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    HashMap<String, String> anaParams = new HashMap<String, String>();
                    String ids = "";
                    for (ResourceBean bean : anaItems) {
                        ids = ids + bean.get_id() + ",";
                    }
                    if (ids.length() > 1) {
                        ids = ids.substring(0, ids.length() - 1);
                    }
                    anaParams.put("id", ids);
                    LogUtil.i(tag, "anaViewResources ids = " + ids);
                    return NetUtil.requestPostData(context, anaURL, anaParams);
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (Error e) {
                    e.printStackTrace();
                }
                return "type not support";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                LogUtil.i(tag, "anaClick rs = " + s);
            }
        }.execute();
    }

    public static void anaResource(final Context context,
            final ResourceBean bean, final AnaType type) {
        if (context == null || bean == null) {
            LogUtil.i(tag, "ana resource exception, context = " + context
                    + " bean = " + bean);

            return;
        }
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    if (type == AnaType.Click) {
                        return NetUtil.requestData(context,
                                bean.getAnalyticClickURL());
                    } else if (type == AnaType.Download) {
                        return NetUtil.requestData(context,
                                bean.getAnalyticDownloadURL());
                    } else if (type == AnaType.Set) {
                        return NetUtil.requestData(context,
                                bean.getAnalyticSetURL());
                    } else if (type == AnaType.View) {
                        return NetUtil.requestData(context,
                                bean.getAnalyticViewURL());
                    } else if (type == AnaType.Play) {
                        return NetUtil.requestData(context,
                                bean.getAnalyticPlayURL());
                    } else if (type == AnaType.Favorite) {
                        return NetUtil.requestData(context,
                                bean.getAnalyticFavoriteURL());
                    } else if (type == AnaType.Share) {
                        return NetUtil.requestData(context,
                                bean.getAnalyticShareURL());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (Error e) {
                    e.printStackTrace();
                }
                return "type not support";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                LogUtil.i(tag, "anaClick rs = " + s);
            }
        }.execute();
    }

    public static void anaResource(final Context context,
            final CategoryBean bean, final AnaType type) {
        if (context == null || bean == null) {
            LogUtil.i(tag, "ana resource exception, context = " + context
                    + " bean = " + bean);

            return;
        }
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    if (type == AnaType.Click) {
                        return NetUtil.requestData(context, bean.getClickurl());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (Error e) {
                    e.printStackTrace();
                }
                return "type not support";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                LogUtil.i(tag, "anaClick category rs = " + s);
            }
        }.execute();
    }

    public enum AnaType {
        Click, Download, Set, View, Play, Favorite, Share
    }

}
