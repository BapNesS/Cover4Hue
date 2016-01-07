package com.baptistecarlier.android.cover4hue;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import com.baptistecarlier.android.cover4hue.activity.MainActivity;

import java.util.Locale;

/**
 * Created by BapNesS on 06/01/2016.
 */
public class CoverHueApplication extends Application {

	public Context mCtx;

	private String mRequestedURL;

	private static CoverHueApplication instance = null;

	public static CoverHueApplication getInstance() {
		if(instance != null) {
			return instance;
		} else {
			return new CoverHueApplication();
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;

		String version;
		try {
			version = "/" + CoverHueApplication.getInstance().getPackageManager().getPackageInfo(MainActivity.PACKAGE, 0).versionName;
		} catch (Exception e) {
			version = "";
		}

		// Populate our Session object
		SharedPreferences settings = getSharedPreferences(MainActivity.PREFS, 0);
		String bridgeForExample = settings.getString("cover4hue_bridge", "");

		version = "0.1";
		try {
			version = getPackageManager().getPackageInfo(MainActivity.PACKAGE, 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {
		}
	}


	@Override
	public void onTerminate() {
		instance = null;
		super.onTerminate();
	}
}
