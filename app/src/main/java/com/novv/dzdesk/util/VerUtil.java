package com.novv.dzdesk.util;

import android.os.Build;

public class VerUtil {

    public static final int UNKNOWN = 0;

    public static final int BASE = Build.VERSION_CODES.BASE;
    public static final int BASE_1_1 = Build.VERSION_CODES.BASE_1_1;
    public static final int CUPCAKE = Build.VERSION_CODES.CUPCAKE;
    public static final int DONUT = Build.VERSION_CODES.DONUT;
    public static final int ECLAIR = Build.VERSION_CODES.ECLAIR;
    public static final int ECLAIR_0_1 = Build.VERSION_CODES.ECLAIR_0_1;
    public static final int ECLAIR_MR1 = Build.VERSION_CODES.ECLAIR_MR1;
    public static final int FROYO = Build.VERSION_CODES.FROYO;
    public static final int GINGERBREAD = Build.VERSION_CODES.GINGERBREAD;
    public static final int GINGERBREAD_MR1 = Build.VERSION_CODES.GINGERBREAD_MR1;
    public static final int HONEYCOMB = Build.VERSION_CODES.HONEYCOMB;
    public static final int HONEYCOMB_MR1 = Build.VERSION_CODES.HONEYCOMB_MR1;
    public static final int HONEYCOMB_MR2 = Build.VERSION_CODES.HONEYCOMB_MR2;
    public static final int ICE_CREAM_SANDWICH = Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    public static final int ICE_CREAM_SANDWICH_MR1 = Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
    public static final int JELLY_BEAN = Build.VERSION_CODES.JELLY_BEAN;
    public static final int JELLY_BEAN_MR1 = Build.VERSION_CODES.JELLY_BEAN_MR1;
    public static final int JELLY_BEAN_MR2 = Build.VERSION_CODES.JELLY_BEAN_MR2;
    public static final int KITKAT = Build.VERSION_CODES.KITKAT;

    public static final int V_1_0 = BASE;
    public static final int V_1_1 = BASE_1_1;
    public static final int V_1_5 = CUPCAKE;
    public static final int V_1_6 = DONUT;
    public static final int V_2_0 = ECLAIR;
    public static final int V_2_0_1 = ECLAIR_0_1;
    public static final int V_2_1 = ECLAIR_MR1;
    public static final int V_2_2 = FROYO;
    public static final int V_2_3 = GINGERBREAD;
    public static final int V_2_3_3 = GINGERBREAD_MR1;
    public static final int V_3_0 = HONEYCOMB;
    public static final int V_3_1 = HONEYCOMB_MR1;
    public static final int V_3_2 = HONEYCOMB_MR2;
    public static final int V_4_0 = ICE_CREAM_SANDWICH;
    public static final int V_4_0_3 = ICE_CREAM_SANDWICH_MR1;
    public static final int V_4_1 = JELLY_BEAN;
    public static final int V_4_2 = JELLY_BEAN_MR1;
    public static final int V_4_3 = JELLY_BEAN_MR2;
    public static final int V_4_4 = KITKAT;

    public static final String N_UNKNOWN = "Unknown";
    public static final String N_BASE = "1.0";
    public static final String N_BASE_1_1 = "1.1";
    public static final String N_CUPCAKE = "1.5";
    public static final String N_DONUT = "1.6";
    public static final String N_ECLAIR = "2.0";
    public static final String N_ECLAIR_0_1 = "";
    public static final String N_ECLAIR_MR1 = "2.1";
    public static final String N_FROYO = "2.2";
    public static final String N_GINGERBREAD = "2.3.0";
    public static final String N_GINGERBREAD_MR1 = "2.3.3";
    public static final String N_HONEYCOMB = "3.0";
    public static final String N_HONEYCOMB_MR1 = "3.1";
    public static final String N_HONEYCOMB_MR2 = "3.2";
    public static final String N_ICE_CREAM_SANDWICH = "4.0.0";
    public static final String N_ICE_CREAM_SANDWICH_MR1 = "4.0.3";
    public static final String N_JELLY_BEAN = "4.1";
    public static final String N_JELLY_BEAN_MR1 = "4.2";
    public static final String N_JELLY_BEAN_MR2 = "4.3";
    public static final String N_KITKAT = "4.4";

    public static int sdkVer() {
        return Build.VERSION.SDK_INT;
    }

    public static String sdkVerName() {
        return getSdkVerName(sdkVer());
    }

    public static boolean sdkSupport(int version) {
        return sdkVer() >= version;
    }

    public static String getSdkVerName(int version) {
        String result = "";
        switch (version) {
            case BASE:
                result = N_BASE;
                break;
            case BASE_1_1:
                result = N_BASE_1_1;
                break;
            case CUPCAKE:
                result = N_CUPCAKE;
                break;
            case DONUT:
                result = N_DONUT;
                break;
            case ECLAIR:
                result = N_ECLAIR;
                break;
            case ECLAIR_0_1:
                result = N_ECLAIR_0_1;
                break;
            case ECLAIR_MR1:
                result = N_ECLAIR_MR1;
                break;
            case FROYO:
                result = N_FROYO;
                break;
            case GINGERBREAD:
                result = N_GINGERBREAD;
                break;
            case GINGERBREAD_MR1:
                result = N_GINGERBREAD_MR1;
                break;
            case HONEYCOMB:
                result = N_HONEYCOMB;
                break;
            case HONEYCOMB_MR1:
                result = N_HONEYCOMB_MR1;
                break;
            case HONEYCOMB_MR2:
                result = N_HONEYCOMB_MR2;
                break;
            case ICE_CREAM_SANDWICH:
                result = N_ICE_CREAM_SANDWICH;
                break;
            case ICE_CREAM_SANDWICH_MR1:
                result = N_ICE_CREAM_SANDWICH_MR1;
                break;
            case JELLY_BEAN:
                result = N_JELLY_BEAN;
                break;
            case JELLY_BEAN_MR1:
                result = N_JELLY_BEAN_MR1;
                break;
            case JELLY_BEAN_MR2:
                result = N_JELLY_BEAN_MR2;
                break;
            case KITKAT:
                result = N_KITKAT;
                break;
            default:
                result = N_UNKNOWN;
                break;
        }
        return result;
    }

}
