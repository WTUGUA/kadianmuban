package com.novv.dzdesk.util;

import org.json.JSONObject;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrUtil {

    /**
     * 1.判断string是否为空 {@link #isEmpty(String string)} 2.获取在指定string之前的子string（不包含指定字符串） {@link
     * #getSubStrBefore(String source, String before)} 3.获取在指定string之后的子string（不包含指定字符串） {@link
     * #getSubStrAfter(String source, String after)} 4.按照指定的List<string>切割目标string {@link
     * #split(String source, String... tokens)} 5.判断源string是否在指定的string集合中 {@link #inter(String
     * source, String... targets)} 6.判断源string是否在指定的List<string>中 {@link #inter(String source,
     * List<String> targets)} 7.判断源string是否在指定的string集合中 {@link #interNoCase(String source,
     * String... targets)} 8.判断源string是否在指定的List<string>中 {@link #interNoCase(String source,
     * List<String> targets)} 9.null 转为 "" {@link #nullStrToEmpty(String str)} 10.utf-8编码 {@link
     * #utf8Encode(String str)} 11.utf-8编码 ，若异常返回自身 {@link #utf8Encode(String str, String
     * defultReturn)} 12.正则表达式,在source中获取 符合regex {@link #getMatcger(String regex, String source)}
     * 13.将一个InputStream转换成String {@link #parseInputStreamToString(InputStream)}
     * 14.将网络请求的json中获取code {@link #getCodeFromJSON(String json)} 14.将网络请求的json中获取msg {@link
     * #getMsgFromJSON(String json)}
     */

    private static final String TAG = StrUtil.class.getSimpleName();
    ;
    private static final String EMAIL_PATTERN = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";

    /**
     * 判断字符串是否为空
     *
     * @param string 字符串
     * @return 字符串是否为空
     */
    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

    /**
     * 获取在指定字符串之前的子字符串（不包含指定字符串）
     *
     * @param source 源字符串
     * @param before 指定的字符串
     * @return 指定的字符串之前的子字符串
     */
    public static String getSubStrBefore(String source, String before) {
        if (source == null || before == null) {
            LogUtil.w(TAG, "getSubStrBefore", "invalid argument");
            return null;
        }
        int index = source.indexOf(before);
        if (index < 0) {
            return null;
        }
        return source.substring(0, index);
    }

    public static String getSubStrBeforeLast(String source, String before) {
        if (source == null || before == null) {
            LogUtil.w(TAG, "getSubStrBefore", "invalid argument");
            return null;
        }
        int index = source.lastIndexOf(before);
        if (index < 0) {
            return null;
        }
        return source.substring(0, index);
    }

    /**
     * 获取在指定字符串之后的子字符串（不包含指定字符串）
     *
     * @param source 源字符串
     * @param after  指定的字符串
     */
    public static String getSubStrAfter(String source, String after) {
        if (source == null || after == null) {
            LogUtil.w(TAG, "getSubStrAfter", "invalid argument");
            return null;
        }
        int index = source.indexOf(after);
        if (index < 0) {
            return null;
        }
        return source.substring(index + after.length());
    }

    /**
     * 按照指定的字符串集合切割目标字符串
     *
     * @param source 源字符串
     * @param tokens 指定的字符串集合
     */
    public static List<String> split(String source, String... tokens) {
        if (source == null || tokens == null) {
            LogUtil.w(TAG, "split", "invalid argument");
            return null;
        }
        List<String> subStrings = new ArrayList<String>();
        int len = source.length();
        if (len == 0 || tokens.length == 0) {
            return subStrings;
        }
        int index = -1;
        int start = 0;
        while (start < len) {
            int minIndex = Integer.MAX_VALUE;
            String minToken = null;
            boolean found = false;
            for (String token : tokens) {
                if (token == null || token.length() == 0) {
                    continue;
                }
                index = source.indexOf(token, start);
                if (index >= 0) {
                    found = true;
                    if (index < minIndex) {
                        minToken = token;
                        minIndex = index;
                    }
                }
            }
            if (!found || minToken == null) {
                break;
            }
            String subStr = source.substring(start, minIndex);
            subStrings.add(subStr);
            start += minToken.length();
        }
        return subStrings;
    }

    /**
     * 判断源字符串是否在指定的字符串集合中
     *
     * @param source  源字符串
     * @param targets 指定的字符串集合
     */
    public static boolean inter(String source, String... targets) {
        if (source == null || targets == null) {
            LogUtil.w(TAG, "inter", "invalid argument");
            return false;
        }

        for (String target : targets) {
            if (source.equals(target)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断源字符串是否在指定的字符串集合中
     *
     * @param source  源字符串
     * @param targets 指定的字符串集合
     */
    public static boolean inter(String source, List<String> targets) {
        if (source == null || targets == null) {
            LogUtil.w(TAG, "inter", "invalid argument");
            return false;
        }

        for (String target : targets) {
            if (source.equals(target)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断源字符串是否在指定的字符串集合中
     *
     * @param source  源字符串
     * @param targets 指定的字符串集合
     */
    public static boolean interNoCase(String source, String... targets) {
        if (source == null || targets == null) {
            LogUtil.w(TAG, "inter", "invalid argument");
            return false;
        }

        for (String target : targets) {
            if (source.equalsIgnoreCase(target)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断源字符串是否在指定的字符串集合中
     *
     * @param source  源字符串
     * @param targets 指定的字符串集合
     */
    public static boolean interNoCase(String source, List<String> targets) {
        if (source == null || targets == null) {
            LogUtil.w(TAG, "inter", "invalid argument");
            return false;
        }

        for (String target : targets) {
            if (source.equalsIgnoreCase(target)) {
                return true;
            }
        }
        return false;
    }

    /**
     * null string to ""
     */
    public static String nullStrToEmpty(String str) {
        return (str == null ? "" : str);
    }

    /**
     * encoded in utf-8
     * <p>
     * <pre>
     * utf8Encode(null)        =   null
     * utf8Encode("")          =   "";
     * utf8Encode("aa")        =   "aa";
     * utf8Encode("啊啊啊啊")   = "%E5%95%8A%E5%95%8A%E5%95%8A%E5%95%8A";
     * </pre>
     *
     * @throws UnsupportedEncodingException if an error occurs
     */
    public static String utf8Encode(String str) {
        if (!isEmpty(str) && str.getBytes().length != str.length()) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(
                        "UnsupportedEncodingException occurred. ", e);
            }
        }
        return str;
    }

    /**
     * encoded in utf-8, if exception, return defultReturn
     */
    public static String utf8Encode(String str, String defultReturn) {
        if (!isEmpty(str) && str.getBytes().length != str.length()) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return defultReturn;
            }
        }
        return str;
    }

    /**
     * 正则表达式,在source 中获取 符合regex
     */
    public static String getMatcger(String regex, String source) {
        String result = null;
        try {
            if (regex != null && !regex.equals("") && source != null
                    && !source.equals("")) {
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(source);
                while (matcher.find()) {
                    result = matcher.group(1);
                }
            }
            if (result == null || result.equals("")) {
                result = null;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return result;
    }

    public static String parseInputStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
            System.gc();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static int getCodeFromJSON(String json) {
        int code = -1;
        try {
            JSONObject jsonObject = new JSONObject(json);
            code = jsonObject.optInt("code");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    public static String getMsgFromJSON(String json) {
        String msg = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            msg = jsonObject.optString("msg");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }

    public static boolean CheckEmailFormat(String email) {
        boolean validate = true;
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher mat = pattern.matcher(email);
        if (!mat.find()) {
            validate = false;
        }
        return validate;
    }

    public static boolean checkKeywordIllegal(String keyword) {

        LogUtil.i(TAG, "lastIndexOf ? = " + keyword.lastIndexOf("?"));
        LogUtil.i(TAG, "lastIndexOf % = " + keyword.lastIndexOf("%"));
        LogUtil.i(TAG, "lastIndexOf / = " + keyword.lastIndexOf("/"));
        LogUtil.i(TAG, "lastIndexOf & = " + keyword.lastIndexOf("&"));

        LogUtil.i(TAG, "indexOf ? = " + keyword.indexOf("?"));
        LogUtil.i(TAG, "indexOf % = " + keyword.indexOf("%"));
        LogUtil.i(TAG, "indexOf / = " + keyword.indexOf("/"));
        LogUtil.i(TAG, "indexOf & = " + keyword.indexOf("&"));

        if (keyword.contains("?") || keyword.contains("%")
                || keyword.contains("/") || keyword.contains("&")
                || keyword.contains("\\") || keyword.contains("<")
                || keyword.contains(">") || keyword.contains("`")) {
            return true;
        }

        return false;
    }
}
