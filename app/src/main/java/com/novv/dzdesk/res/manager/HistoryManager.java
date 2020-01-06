package com.novv.dzdesk.res.manager;

import android.content.Context;
import com.novv.dzdesk.util.FileUtil;

import java.util.ArrayList;

/**
 * Created by lingyfh on 2017/7/3.
 */
public class HistoryManager {

    private static final String KEY_HISTORY_FILE_NAME = "search_history";
    private static final int UNLIMIT_HISTORY = 0;

    private static void saveHistory2File(Context context, ArrayList keywords) {
        if (context == null) {
            return;
        }
        FileUtil.serializableToFile(context, KEY_HISTORY_FILE_NAME, keywords);
    }

    public static void saveHistory2File(Context context, String keyword) {
        saveHistory2File(context, keyword, true);
    }

    public static void saveHistory2File(Context context, String keyword,
            boolean allowRepeat) {
        saveHistory2File(context, keyword, allowRepeat, UNLIMIT_HISTORY);
    }

    public static void saveHistory2File(Context context, String keyword,
            boolean allowRepeat, int max) {
        ArrayList<String> history = getHistoryFromFile(context);
        if (history == null) {
            return;
        }
        if (!allowRepeat && history.contains(keyword)) {
            history.remove(keyword);
        }
        history.add(0, keyword);

        if (max != UNLIMIT_HISTORY && history.size() > max) {
            history = new ArrayList<>(history.subList(0, max));
        }
        saveHistory2File(context, history);
    }

    public static void clearHistory(Context context) {
        saveHistory2File(context, new ArrayList());
    }

    public static ArrayList<String> getHistoryFromFile(Context context) {
        Object obj = FileUtil.unSerializableFromFile(context,
                KEY_HISTORY_FILE_NAME);
        if (obj == null) {
            return new ArrayList<>();
        }
        if (obj instanceof ArrayList) {
            ArrayList temp = (ArrayList) obj;
            return temp;
        }
        return new ArrayList<>();
    }

}
