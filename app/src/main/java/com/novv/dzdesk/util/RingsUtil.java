package com.novv.dzdesk.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import com.novv.dzdesk.res.model.RingsBean;

import java.io.File;

public class RingsUtil {

    public static void setCalling(Context context, RingsBean ring) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        values.put(MediaStore.Audio.Media.IS_ALARM, false);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);
        try {
            Uri newUri = getUri(context, ring, values);
            RingtoneManager.setActualDefaultRingtoneUri(context,
                    RingtoneManager.TYPE_RINGTONE, newUri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Uri getUri(Context context, RingsBean ring, ContentValues values) {
        if (values == null) {
            values = new ContentValues();
        }
        String path = ring.getFilePath();
        File sdfile = new File(path);
        Uri newUri = null;
        int ringtoneID = getAudioId(context, path);
        values.put(MediaStore.MediaColumns.DATA, sdfile.getAbsolutePath());
        //values.put(MediaStore.MediaColumns.TITLE, FileUtils.getFileNameNoSuffix(path));
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, sdfile.getName());

        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
        values.put(MediaStore.Audio.Media.DURATION, ring.getDuring());
        values.put(MediaStore.Audio.Media.SIZE, ring.getSize());
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(path);
        if (ringtoneID == -1) {
            newUri = context.getContentResolver().insert(uri, values);
        } else {
            newUri = Uri.withAppendedPath(uri, ringtoneID + "");
            context.getContentResolver().update(uri, values, MediaStore.Audio.Media.DATA + "=?",
                    new String[]{path});
        }
        return newUri;
    }

    public static int getAudioId(Context context, String path) {
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(path);

        Cursor cursor = context.getContentResolver().query(uri,
                new String[]{MediaStore.Audio.Media._ID}, MediaStore.Audio.Media.DATA + "=?",
                new String[]{path}, null);
        int ringtoneID = -1;
        while (cursor != null && cursor.moveToNext()) {
            ringtoneID = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
        }
        //DataBaseUtils.closeCursor(cursor);
        return ringtoneID;
    }

    public static boolean deleteAudioId(Context context, String path) {
        try {
            Uri uri = MediaStore.Audio.Media.getContentUriForPath(path);
            context.getContentResolver().delete(uri, MediaStore.Audio.Media.DATA + "=?",
                    new String[]{path});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 设置--提示音的具体实现方法
    public static void setNotification(Context context, RingsBean ring) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Media.IS_RINGTONE, false);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        values.put(MediaStore.Audio.Media.IS_ALARM, false);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);
        try {
            Uri newUri = getUri(context, ring, values);
            RingtoneManager.setActualDefaultRingtoneUri(context,
                    RingtoneManager.TYPE_NOTIFICATION, newUri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println("setMyNOTIFICATION-----提示音");
    }

    // 设置--闹铃音的具体实现方法
    public static void setAlarm(Context context, RingsBean ring) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Media.IS_RINGTONE, false);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        values.put(MediaStore.Audio.Media.IS_ALARM, true);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);
        try {
            Uri newUri = getUri(context, ring, values);
            RingtoneManager.setActualDefaultRingtoneUri(context,
                    RingtoneManager.TYPE_ALARM, newUri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println("setMyNOTIFICATION------闹铃音");
    }
}
