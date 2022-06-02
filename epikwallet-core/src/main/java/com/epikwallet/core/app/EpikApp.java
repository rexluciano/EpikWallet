package com.epikwallet.core.app;

import android.content.*;
import android.os.*;
import com.epikwallet.core.connection.Connection;
import com.epikwallet.core.ui.auth.EpikAuthUI;

public class EpikApp {
	
	public static void initializeApp(Context context, String key) {
		Connection.getInstance(context).initializeConnection(key);
		new Handler(Looper.getMainLooper()).postDelayed(() -> {
			if (Connection.isConnected) {
				EpikAuthUI.initialize(context);
			}
		}, 2000);
	}
	
	public static boolean isReady() {
		return Connection.isConnected;
	}
}
