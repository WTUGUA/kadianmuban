package com.novv.dzdesk.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetUtil {

    public static final Uri URI_PREFERAPN = Uri
            .parse("content://telephony/carriers/preferapn");
    public static final String NETWORK_DISABLED = "DISABLED"; // 网络不可用
    public static final String NETWORK_WIFI = "WIFI"; // wifi网络
    public static final String NETWORK_MOBILE = "MOBILE"; // 电信,移动,联通,等mobile网络
    public static final String NETWORK_CMWAP = "CMWAP"; // 移动wap
    public static final String NETWORK_UNIWAP = "UNIWAP"; // 联通wap
    public static final String NETWORK_CTWAP = "CTWAP"; // 电信wap
    public static final String NETWORK_GPRS = "GPRS";// 2G/3G 网络
    public static final String NETWORK_OTHER = "OTHR"; // 其它未知网络
    private static final String TAG = NetUtil.class.getSimpleName();

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private static boolean is3G(int networkType) {
        if (networkType == TelephonyManager.NETWORK_TYPE_UMTS
                || networkType == TelephonyManager.NETWORK_TYPE_HSDPA
                || networkType == TelephonyManager.NETWORK_TYPE_EVDO_0
                || networkType == TelephonyManager.NETWORK_TYPE_EVDO_A) {
            return true;
        } else if (VerUtil.sdkSupport(VerUtil.GINGERBREAD)
                && networkType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
            return true;
        } else if (VerUtil.sdkSupport(VerUtil.HONEYCOMB_MR2)
                && networkType == TelephonyManager.NETWORK_TYPE_HSPAP) {
            return true;
        }
        return false;
    }

    private static boolean is2G(int networkType) {
        // NETWORK_TYPE_UMTS 网络类型为UMTS
        // NETWORK_TYPE_EVDO_0 网络类型为EVDO0
        // NETWORK_TYPE_EVDO_A 网络类型为EVDOA
        // NETWORK_TYPE_HSDPA 网络类型为HSDPA

        // NETWORK_TYPE_CDMA 网络类型为CDMA
        // NETWORK_TYPE_EDGE 网络类型为EDGE
        // NETWORK_TYPE_GPRS 网络类型为GPRS
        //
        // 联通的3G为UMTS、HSPAP或HSDPA，电信的3G为EVDO,EVDO_A,EVDO_B,
        // 移动和联通的2G为GPRS或EDGE，电信的2G为CDMA
        // 参考于 http://wyoojune.blog.163.com/blog/static/57093325201332105128248/
        // http://www.cnblogs.com/lee0oo0/archive/2013/05/20/3089906.html
        // http://hi.baidu.com/neverever888/item/0c37d436553ddc1d9cc65ee0
        // 移动的3G不知道什么值
        return (networkType == TelephonyManager.NETWORK_TYPE_GPRS
                || networkType == TelephonyManager.NETWORK_TYPE_EDGE
                || networkType == TelephonyManager.NETWORK_TYPE_CDMA);
    }

    /**
     * 获取网络的2G，3G类型
     *
     * @return 2G、3G、unknown
     */
    private static String getNetworkTypeGeneration(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);// 平板电脑可能获取不到
        if (telephonyManager == null) {
            return "unknown";
        }
        try {
            int networkType = telephonyManager.getNetworkType();
            if (is3G(networkType)) {
                return "3G";
            } else if (is2G(networkType)) {
                return "2G";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "unknown";
    }

    public static void closeDBCursor(Cursor cursor) {
        try {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 网络类型： 0.无网络 1.wifi 2.net网络 3.移动wap 4.联通wap 4.电信wap 5.未知网络
     **/
    @SuppressLint("DefaultLocale")
    public static String getNetworkType(Context context) {
        try {
            ConnectivityManager connect = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo actNetInfo = connect.getActiveNetworkInfo();
            if (actNetInfo == null) {
                LogUtil.w(TAG, "getNetworkType", "network info is null");
                return NETWORK_DISABLED;
            }
            if (!actNetInfo.isAvailable()) {
                return NETWORK_DISABLED;
            }
            int netType = actNetInfo.getType();
            if (netType == ConnectivityManager.TYPE_WIFI) {
                return NETWORK_WIFI;
            }
            if (netType != ConnectivityManager.TYPE_MOBILE) {
                return NETWORK_MOBILE;
            }
            Cursor cursor = context.getContentResolver().query(URI_PREFERAPN,
                    null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                String user = cursor.getString(cursor.getColumnIndex("user"));
                if (!TextUtils.isEmpty(user) && user.startsWith("ctwap")) {
                    return NETWORK_CTWAP;
                }
            }

            closeDBCursor(cursor);

            String extrainfo = actNetInfo.getExtraInfo();
            if (extrainfo != null) {
                extrainfo = extrainfo.toLowerCase();
                // 3gnet/3gwap/uninet/uniwap/cmnet/cmwap/ctnet/ctwap
                if (extrainfo.equals("cmwap")) {
                    return NETWORK_CMWAP;
                }
                if (extrainfo.equals("uniwap")) {
                    return NETWORK_UNIWAP;
                }
                if (extrainfo.equals("uninet")) {
                    return NETWORK_CTWAP;
                }
            }
            return NETWORK_MOBILE;
        } catch (Exception e) {
            LogUtil.w(TAG, "getNetworkType", "unknown network type");
            return NETWORK_OTHER;
        }
    }

    /**
     * 获取网络类型全称
     */
    public static String getWholeNetworkType(Context context) {
        return String.format("%s_%s", getNetworkType(context),
                getNetworkTypeGeneration(context));
    }

    /**
     * 是否有网络连接
     */
    public static boolean hasConnection(Context context) {
        try {
            final ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnectedOrConnecting();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 是否有可用网络
     */
    public static boolean isNetworkAvailable(Context context) {
        return isWifiConnected(context) || isMobileConnected(context);
    }

    /**
     * WIFI是否连接
     */
    public static boolean isWifiConnected(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifi = connectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return wifi != null && wifi.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 2G/3G是否连接
     */
    public static boolean isMobileConnected(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobile = connectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            return mobile != null && mobile.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 取URL中的参数
     */
    public static Map<String, List<String>> getQueryParams(String url) {
        try {
            Map<String, List<String>> params = new HashMap<String, List<String>>();
            String[] urlParts = url.split("\\?");
            if (urlParts.length > 1) {
                String query = urlParts[1];
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    String key = URLDecoder.decode(pair[0], "UTF-8");
                    String value = "";
                    if (pair.length > 1) {
                        value = URLDecoder.decode(pair[1], "UTF-8");
                    }

                    List<String> values = params.get(key);
                    if (values == null) {
                        values = new ArrayList<String>();
                        params.put(key, values);
                    }
                    values.add(value);
                }
            }

            return params;
        } catch (UnsupportedEncodingException ex) {
            throw new AssertionError(ex);
        }
    }

    /**
     * 得到HttpConnectionProxy 根据URL. 在此处增加了代理，防止wap网络取不到数据
     */
    public static URLConnection getHttpConnectionProxy(Context context,
            String urlString) throws IOException {
        URL url = new URL(urlString);
        String host = android.net.Proxy.getDefaultHost();
        boolean isWifiConnected = NetUtil.isWifiConnected(context);
        if (!isWifiConnected && !TextUtils.isEmpty(host)) {
            int port = android.net.Proxy.getDefaultPort();
            SocketAddress sa = new InetSocketAddress(host, port);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, sa);
            return url.openConnection(proxy);
        } else {
            return url.openConnection();
        }
    }

    /**
     * 根据文件地址下载文件为byte[]
     */
    public static byte[] downloadFile(Context context, String urlString) {
        InputStream is = null;
        ByteArrayOutputStream bos = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) getHttpConnectionProxy(
                    context, urlString);
            connection.setConnectTimeout(15 * 1000);// 超时设置为15
            connection.setRequestProperty("Charset", "UTF-8");
            is = connection.getInputStream();
            int fileSize = connection.getContentLength();
            bos = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len = -1;
            while ((len = is.read(buff)) != -1) {
                bos.write(buff, 0, len);
            }
            bos.flush();
            if (fileSize == bos.size()) {
                byte[] bytes = bos.toByteArray();
                return bytes;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 根据文件地址下载文件
     */
    public static String downloadFileToPath(Context context, String urlString,
            String filePath) {

        File file = new File(filePath);
        File parentFile = file.getParentFile();
        if (parentFile != null && !parentFile.exists()) {
            file.getParentFile().mkdirs();
        }

        String tempFilePath = filePath + ".temp";

        int IO_BUFFER_SIZE = 8 * 1024;
        LogUtil.d(TAG, "downloadUrlToStream", "urlString=" + urlString
                + " filePath = " + filePath);

        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        OutputStream outputStream = null;

        File tempFile = new File(tempFilePath);
        try {
            tempFile.createNewFile();
            outputStream = new FileOutputStream(tempFilePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        try {
            urlConnection = (HttpURLConnection) NetUtil.getHttpConnectionProxy(
                    context, urlString);
            int fileSize = urlConnection.getContentLength();
            in = new BufferedInputStream(urlConnection.getInputStream(),
                    IO_BUFFER_SIZE);
            out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);
            int b = -1;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            out.flush();
            LogUtil.i(TAG,
                    " len = " + b + " tempFile.size = " + tempFile.length()
                            + " fileSize = " + fileSize);
            if (tempFile.length() == fileSize) {
                File downloadFile = new File(filePath);
                tempFile.renameTo(downloadFile);
                LogUtil.i(TAG, " renamTo = " + filePath);
                return filePath;
            } else {
                tempFile.delete();
                LogUtil.i(TAG, " delete = ");
                return null;
            }
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 根据文件地址下载文件(先下载到零时文件)
     */
    public static String downloadFileToPath(Context context, String urlString,
            String filePath, String tmpPath) {
        File file = new File(filePath);
        File parentFile = file.getParentFile();
        if (parentFile != null && !parentFile.exists()) {
            parentFile.mkdirs();
        }

        File tmpFile = new File(tmpPath);
        File tmpParentFile = tmpFile.getParentFile();
        if (tmpParentFile != null && !tmpParentFile.exists()) {
            tmpParentFile.mkdirs();
        }
        InputStream is = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) getHttpConnectionProxy(
                    context, urlString);
            connection.setConnectTimeout(15 * 1000);// 超时设置为15
            connection.setRequestProperty("Charset", "UTF-8");
            is = connection.getInputStream();

            int fileSize = connection.getContentLength();
            byte[] buff = new byte[1024];
            int len = -1;
            int size = 0;
            OutputStream os = new FileOutputStream(tmpPath);
            while ((len = is.read(buff)) != -1) {
                size = size + len;
                os.write(buff, 0, len);
            }
            os.close();
            if (fileSize == size) {
                tmpFile.renameTo(file);
                return filePath;
            } else {
                tmpFile.delete();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 根据请求地址，返回请求内容
     */
    public static String requestData(Context context, String urlString) {
        if (TextUtils.isEmpty(urlString)) {
            LogUtil.i(TAG, "url string is null");
            return "";
        }
        String rs = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) getHttpConnectionProxy(context,
                    urlString);
            connection.setConnectTimeout(15 * 1000); // 超时设置为15
            connection.setRequestProperty("http.keepAlive", "true");
            connection.setRequestProperty("Charset", "UTF-8");
            rs = StrUtil.parseInputStreamToString(connection.getInputStream());
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return rs;
    }

    /**
     * 根据请求地址，返回请求内容
     */
    public static String[] requestDataWithLength(Context context, String urlString) {
        if (TextUtils.isEmpty(urlString)) {
            LogUtil.i(TAG, "url string is null");
            return new String[]{"", "0"};
        }
        String rs = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) getHttpConnectionProxy(context,
                    urlString);
            connection.setConnectTimeout(15 * 1000); // 超时设置为15
            connection.setRequestProperty("http.keepAlive", "true");
            connection.setRequestProperty("Charset", "UTF-8");
            rs = StrUtil.parseInputStreamToString(connection.getInputStream());
            long length = connection.getContentLength();
            LogUtil.i(TAG, "content length === " + length);
            return new String[]{rs, length + ""};
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return new String[]{"", "0"};
    }

    public static String requestPostData(Context context, String urlString,
            Map<String, String> params) {
        String rs = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) getHttpConnectionProxy(context,
                    urlString);

            connection.setRequestProperty("http.keepAlive", "true");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            byte[] paramsData = getRequestData(params).toString().getBytes();
            connection.setRequestProperty("Content-Length",
                    String.valueOf(paramsData.length));
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(paramsData);

            rs = StrUtil.parseInputStreamToString(connection.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return rs;
    }

    public static String requestPostData(Context context, String urlString,
            Map<String, String> params, boolean addSession) {
        String rs = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) getHttpConnectionProxy(context,
                    urlString);
            if (addSession) {
                connection.setRequestProperty("Session-Id", HeaderSpf.getSessionId());
            }
            connection.setRequestProperty("http.keepAlive", "true");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            byte[] paramsData = getRequestData(params).toString().getBytes();
            connection.setRequestProperty("Content-Length",
                    String.valueOf(paramsData.length));
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(paramsData);

            rs = StrUtil.parseInputStreamToString(connection.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return rs;
    }

    private static StringBuffer getRequestData(Map<String, String> params) {
        StringBuffer buffer = new StringBuffer();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                buffer.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(entry.getValue()))
                        .append("&");

            }
            buffer.deleteCharAt(buffer.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }

}
