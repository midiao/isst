package cn.edu.zju.isst.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import cn.edu.zju.isst.util.J;

/**
 * 判断网络是否链接正常
 * 
 */
public class NetworkConnection {
	public static boolean isNetworkConnected(Context context) {
		if (!J.isNullOrEmpty(context)) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager
					.getActiveNetworkInfo();
			if (J.isNullOrEmpty(networkInfo)) {
				return false;
			} else if (networkInfo.isAvailable()) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
}