package com.gmoonxs.www.orangehome;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by 威 on 2016/6/25.
 */
public class NetUtil {
    public static final int NETWORK_NONE = 0;
    public static final int NETWORK_WIFI = 1;
    public static final int NETWORK_MOBILE = 2;
    public static int getActiveNetworkInfo(Context contetx) {
        ConnectivityManager conManager = (ConnectivityManager) contetx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conManager.getActiveNetworkInfo();
        if (info == null)
            return NETWORK_NONE;
        //WIFI
        if (info.getType() == ConnectivityManager.TYPE_WIFI)
            return NETWORK_WIFI;
        //MOBILE
        if (info.getType() == ConnectivityManager.TYPE_MOBILE)
            return NETWORK_MOBILE;
        return NETWORK_NONE;
    }
}
