package com.redoct.blackboard.util;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

public class DeviceUtil {
	
	public static String getRawDeviceId(Context ctx) {
		TelephonyManager tm = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);

		return tm.getDeviceId();
	}

	public static String getDeviceOS() {
		String osVersion="android:"+android.os.Build.MODEL + "," + Build.VERSION.CODENAME + ","
				+ Build.VERSION.RELEASE;
		
		Log.e("osVersion", osVersion);
		return osVersion;
	}
}
