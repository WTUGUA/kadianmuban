package com.novv.dzdesk.live;

import android.content.*;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;
import com.novv.dzdesk.util.LogUtil;

import java.util.Map;


/**
 * Created by lingyfh on 14/12/9.
 */
public class MultiProcessPreferences extends ContentProvider {

    private static final String tag = MultiProcessPreferences.class
            .getSimpleName();
    private static final String AUTHORITY = "com.novv.live.MultiProcessPreferences.PREFFERENCE_AUTHORITY";
    private static final String TYPE = "type";
    private static final String KEY = "key";
    private static final String INT_TYPE = "integer";
    private static final String LONG_TYPE = "long";
    private static final String FLOAT_TYPE = "float";
    private static final String BOOLEAN_TYPE = "boolean";
    private static final String STRING_TYPE = "string";
    private static final int MATCH_DATA = 0x010000;
    public static String PREFFERENCE_AUTHORITY;
    public static Uri BASE_URI;
    private static UriMatcher matcher;

    private static void init() {
        PREFFERENCE_AUTHORITY = AUTHORITY;

        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(PREFFERENCE_AUTHORITY, "*/*", MATCH_DATA);

        BASE_URI = Uri.parse("content://" + PREFFERENCE_AUTHORITY);
    }

    public static MultiProcessSharedPreferences getDefaultSharedPreferences(
            Context context) {
        return new MultiProcessSharedPreferences(context);
    }

    private static final Uri getContentUri(String key, String type) {
        if (BASE_URI == null) {
            init();
        }
        return BASE_URI.buildUpon().appendPath(key).appendPath(type).build();
    }

    private static String getStringValue(Cursor cursor, String def) {
        if (cursor == null) {
            return def;
        }
        String value = def;
        if (cursor.moveToFirst()) {
            value = cursor.getString(0);
        }
        cursor.close();
        return value;
    }

    private static boolean getBooleanValue(Cursor cursor, boolean def) {
        if (cursor == null) {
            return def;
        }
        boolean value = def;
        if (cursor.moveToFirst()) {
            value = cursor.getInt(0) > 0;
        }
        cursor.close();
        return value;
    }

    private static int getIntValue(Cursor cursor, int def) {
        if (cursor == null) {
            return def;
        }
        int value = def;
        if (cursor.moveToFirst()) {
            value = cursor.getInt(0);
        }
        LogUtil.i(tag, "getIntValue", "cursor size = " + cursor.getCount()
                + " value = " + value);
        cursor.close();
        return value;
    }

    private static long getLongValue(Cursor cursor, long def) {
        if (cursor == null) {
            return def;
        }
        long value = def;
        if (cursor.moveToFirst()) {
            value = cursor.getLong(0);
        }
        cursor.close();
        return value;
    }

    private static float getFloatValue(Cursor cursor, float def) {
        if (cursor == null) {
            return def;
        }
        float value = def;
        if (cursor.moveToFirst()) {
            value = cursor.getFloat(0);
        }
        cursor.close();
        return value;
    }

    /**
     * 如果存储失败，采用以前的方式存储
     */

    public synchronized static Cursor quary(Context context, Uri uri, String key) {
        MatrixCursor cursor = null;
        switch (matcher.match(uri)) {
            case MATCH_DATA:
                final String type = uri.getPathSegments().get(1);
                cursor = new MatrixCursor(new String[]{key});
                android.content.SharedPreferences sharedPreferences = PrefUtilPrivate
                        .getPref(context.getApplicationContext());
                if (!sharedPreferences.contains(key)) {
                    return cursor;
                }

                MatrixCursor.RowBuilder rowBuilder = cursor.newRow();
                Object object = null;
                if (type.equals(STRING_TYPE)) {
                    object = sharedPreferences.getString(key, null);
                    LogUtil.i(tag, "query", "STRING_TYPE object = " + object);
                } else if (type.equals(BOOLEAN_TYPE)) {
                    object = sharedPreferences.getBoolean(key, false) ? 1 : 0;
                    LogUtil.i(tag, "query", "BOOLEAN_TYPE object = " + object);
                } else if (type.equals(LONG_TYPE)) {
                    object = sharedPreferences.getLong(key, 0l);
                    LogUtil.i(tag, "query", "LONG_TYPE object = " + object);
                } else if (type.equals(INT_TYPE)) {
                    object = sharedPreferences.getInt(key, 0);
                    LogUtil.i(tag, "query", "INT_TYPE object = " + object);
                } else if (type.equals(FLOAT_TYPE)) {
                    object = sharedPreferences.getFloat(key, 0f);
                    LogUtil.i(tag, "query", "FLOAT_TYPE object = " + object);
                } else {
                    LogUtil.e(context, "Unsupported type " + uri);
                }
                rowBuilder.add(object);
                break;
            default:
                LogUtil.e(context, "Unsupported uri " + uri);
                break;
        }
        return cursor;
    }

    public synchronized static void insert(Context context, Uri uri,
            ContentValues values) {
        switch (matcher.match(uri)) {
            case MATCH_DATA:
                android.content.SharedPreferences.Editor editor = PrefUtilPrivate
                        .getPref(context.getApplicationContext()).edit();

                for (Map.Entry<String, Object> entry : values.valueSet()) {
                    final Object value = entry.getValue();
                    final String key = entry.getKey();
                    if (value == null) {
                        editor.remove(key);
                        LogUtil.i(tag, "insert", "value == null");
                    } else if (value instanceof String) {
                        editor.putString(key, (String) value);
                        LogUtil.i(tag, "insert", "value == String");
                    } else if (value instanceof Boolean) {
                        LogUtil.i(tag, "insert", "value == Boolean");
                        editor.putBoolean(key, (Boolean) value);
                    } else if (value instanceof Long) {
                        LogUtil.i(tag, "insert", "value == Long");
                        editor.putLong(key, (Long) value);
                    } else if (value instanceof Integer) {
                        LogUtil.i(tag, "insert", "value == Integer");
                        editor.putInt(key, (Integer) value);
                    } else if (value instanceof Float) {
                        LogUtil.i(tag, "insert", "value == Float");
                        editor.putFloat(key, (Float) value);
                    } else {
                        LogUtil.i(tag, "insert", "value == Exception");
                        LogUtil.e(context, "Unsupported type " + uri);
                    }
                }
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
                    editor.commit();
                } else {
                    editor.commit();
                }
                break;
            default:
                LogUtil.e(context, "Unsupported uri " + uri);
                break;
        }
        ;
    }

    @Override
    public boolean onCreate() {
        if (matcher == null) {
            init();
        }
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        // LogUtil.i(tag, "query uri = " + uri);
        MatrixCursor cursor = null;
        switch (matcher.match(uri)) {
            case MATCH_DATA:
                final String key = uri.getPathSegments().get(0);
                final String type = uri.getPathSegments().get(1);
                cursor = new MatrixCursor(new String[]{key});
                SharedPreferences sharedPreferences = ContentProviderInternalPrefUtil
                        .getPref(getContext().getApplicationContext());
                if (!sharedPreferences.contains(key)) {
                    return cursor;
                }

                MatrixCursor.RowBuilder rowBuilder = cursor.newRow();
                Object object = null;
                if (type.equals(STRING_TYPE)) {
                    object = sharedPreferences.getString(key, null);
                    LogUtil.i(tag, "query", "STRING_TYPE object = " + object);
                } else if (type.equals(BOOLEAN_TYPE)) {
                    object = sharedPreferences.getBoolean(key, false) ? 1 : 0;
                    LogUtil.i(tag, "query", "BOOLEAN_TYPE object = " + object);
                } else if (type.equals(LONG_TYPE)) {
                    object = sharedPreferences.getLong(key, 0l);
                    LogUtil.i(tag, "query", "LONG_TYPE object = " + object);
                } else if (type.equals(INT_TYPE)) {
                    object = sharedPreferences.getInt(key, 0);
                    LogUtil.i(tag, "query", "INT_TYPE object = " + object);
                } else if (type.equals(FLOAT_TYPE)) {
                    object = sharedPreferences.getFloat(key, 0f);
                    LogUtil.i(tag, "query", "FLOAT_TYPE object = " + object);
                } else {
                    LogUtil.e(this, "Unsupported type " + uri);
                }
                rowBuilder.add(object);
                break;
            default:
                LogUtil.e(this, "Unsupported uri " + uri);
                break;
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd."
                + PREFFERENCE_AUTHORITY + ".item";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // LogUtil.i(tag, "insert uri = " + uri + " values = " + values);
        switch (matcher.match(uri)) {
            case MATCH_DATA:
                SharedPreferences.Editor editor = ContentProviderInternalPrefUtil
                        .getPref(getContext().getApplicationContext()).edit();
                for (Map.Entry<String, Object> entry : values.valueSet()) {
                    final Object value = entry.getValue();
                    final String key = entry.getKey();
                    if (value == null) {
                        editor.remove(key);
                        LogUtil.i(tag, "insert", "value == null");
                    } else if (value instanceof String) {
                        editor.putString(key, (String) value);
                        LogUtil.i(tag, "insert", "value == String");
                    } else if (value instanceof Boolean) {
                        LogUtil.i(tag, "insert", "value == Boolean");
                        editor.putBoolean(key, (Boolean) value);
                    } else if (value instanceof Long) {
                        LogUtil.i(tag, "insert", "value == Long");
                        editor.putLong(key, (Long) value);
                    } else if (value instanceof Integer) {
                        LogUtil.i(tag, "insert", "value == Integer");
                        editor.putInt(key, (Integer) value);
                    } else if (value instanceof Float) {
                        LogUtil.i(tag, "insert", "value == Float");
                        editor.putFloat(key, (Float) value);
                    } else {
                        LogUtil.i(tag, "insert", "value == Exception");
                        LogUtil.e(this, "Unsupported type " + uri);
                    }
                }
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
                    // editor.apply();
                    editor.commit();
                } else {
                    editor.commit();
                }
                break;
            default:
                LogUtil.e(this, "Unsupported uri " + uri);
                break;
        }
        ;
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        return 0;
    }

    public static class Editor {

        private Context context;
        private ContentValues values = new ContentValues();

        public Editor(Context context) {
            this.context = context;
        }

        public synchronized void apply() {
            try {
                context.getContentResolver().insert(getContentUri(KEY, TYPE),
                        values);
            } catch (Exception e) {
                e.printStackTrace();
                MultiProcessPreferences.insert(context,
                        getContentUri(KEY, TYPE), values);
            }
        }

        public synchronized void commit() {
            apply();
        }

        public synchronized Editor putString(String key, String value) {
            values.put(key, value);
            return this;
        }

        public synchronized Editor putLong(String key, long value) {
            values.put(key, value);
            return this;
        }

        public synchronized Editor putBoolean(String key, boolean value) {
            values.put(key, value);
            return this;
        }

        public synchronized Editor putInt(String key, int value) {
            values.put(key, value);
            return this;
        }

        public synchronized Editor putFloat(String key, float value) {
            values.put(key, value);
            return this;
        }

        public synchronized void remove(String key) {
            values.putNull(key);
        }

        public synchronized void clear() {
            context.getContentResolver().delete(getContentUri(KEY, TYPE), null,
                    null);
        }
    }

    public static class MultiProcessSharedPreferences {

        private Context context;

        public MultiProcessSharedPreferences(Context context) {
            this.context = context;
        }

        public Editor edit() {
            return new Editor(context);
        }

        public Cursor getCursor(Context context, Uri uri, String key) {

            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, null, null,
                        null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (cursor == null) {
                try {
                    cursor = quary(context, uri, key);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return cursor;
        }

        public synchronized String getString(String key, String def) {
            Uri uri = getContentUri(key, STRING_TYPE);
            Cursor cursor = getCursor(context, uri, key);
            return getStringValue(cursor, def);
        }

        public synchronized long getLong(String key, long def) {
            Uri uri = getContentUri(key, LONG_TYPE);
            Cursor cursor = getCursor(context, uri, key);
            return getLongValue(cursor, def);
        }

        public synchronized float getFloat(String key, float def) {
            Uri uri = getContentUri(key, FLOAT_TYPE);
            Cursor cursor = getCursor(context, uri, key);
            return getFloatValue(cursor, def);
        }

        public synchronized boolean getBoolean(String key, boolean def) {
            Uri uri = getContentUri(key, BOOLEAN_TYPE);
            Cursor cursor = getCursor(context, uri, key);
            return getBooleanValue(cursor, def);
        }

        public synchronized int getInt(String key, int def) {
            Uri uri = getContentUri(key, INT_TYPE);
            Cursor cursor = getCursor(context, uri, key);
            LogUtil.i(tag, "getInt", "cursor = " + cursor);
            return getIntValue(cursor, def);
        }
    }
}
