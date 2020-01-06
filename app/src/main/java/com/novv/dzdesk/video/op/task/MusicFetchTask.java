package com.novv.dzdesk.video.op.task;

import android.content.Context;
import android.os.AsyncTask;

import com.novv.dzdesk.util.FileUtil;
import com.novv.dzdesk.util.LogUtil;
import com.novv.dzdesk.util.NetUtil;
import com.novv.dzdesk.video.op.model.MusicItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MusicFetchTask extends AsyncTask<Void, Void, ArrayList> {

    private static final String MUSIC_URL = "http://service.rt.adesk.com/music";
    private static final String tag = MusicFetchTask.class.getSimpleName();
    public static ArrayList<MusicItem> sMusicItems = new ArrayList<>();
    private Context mContext;

    public MusicFetchTask(Context context) {
        this.mContext = context;
    }

    public static void executeTask(Context context) {
        MusicFetchTask task = new MusicFetchTask(context);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected ArrayList doInBackground(Void... voids) {
        Object object = FileUtil.unSerializableFromFile(mContext, "video_edit_music");
        if (object instanceof ArrayList) {
            sMusicItems = (ArrayList) object;
        }

        String rs = NetUtil.requestData(mContext, MUSIC_URL);
        ArrayList list = new ArrayList();
        try {
            JSONObject jsonObject = new JSONObject(rs);
            JSONArray array = jsonObject.optJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                list.add(new MusicItem(array.optJSONObject(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list != null && !list.isEmpty()) {
            FileUtil.serializableToFile(mContext, "video_edit_music", list);
        }

        LogUtil.i(tag, "list size = " + list.size());
        return list;
    }

    @Override
    protected void onPostExecute(ArrayList arrayList) {
        super.onPostExecute(arrayList);
        if (sMusicItems == null || sMusicItems.isEmpty()) {
            sMusicItems = arrayList;
        }
    }
}
