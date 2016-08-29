package jp.co.smbc.gridbeacon;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

/**
 * Created by amrizal.zainuddin on 12/8/2016.
 */
public class NetworkUtility {
    /**
     * Check for internet connection *
     */
    public static boolean isConnectedToInternet(ConnectivityManager cm) {
        NetworkInfo networkInfo;
        if (cm != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network[] networks = cm.getAllNetworks();
                if (networks != null) {
                    for (Network network : networks) {
                        networkInfo = cm.getNetworkInfo(network);

                        if (networkInfo != null && networkInfo.isConnected()) {
                            return true;
                        }
                    }
                }
            } else {
                NetworkInfo[] networks = cm.getAllNetworkInfo();
                if (networks != null) {
                    int i = networks.length - 1;
                    while (i > -1) {
                        networkInfo = networks[i];
                        if (networkInfo != null && networkInfo.isConnected()) {
                            return true;
                        }
                        i--;
                    }
                }
            }
        }

        return false;
    }
}
