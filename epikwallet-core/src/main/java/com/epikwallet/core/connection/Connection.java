package com.epikwallet.core.connection;

import android.content.*;
import android.app.*;
import org.json.*;
import java.util.*;
import com.epikwallet.core.RequestNetwork;
import com.epikwallet.core.RequestNetworkController;
import com.epikwallet.core.Server;

public class Connection {
	private static Connection mInstance = null;
	private final RequestNetwork net;
	public static boolean isConnected = false;
	
	public Connection(Context context) {
		net = new RequestNetwork((Activity) context);
	}
	
	public static Connection getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new Connection(context);
		}
		return mInstance;
	}
	
	public void initializeConnection(String apiKey) {
		HashMap<String, Object> map = new HashMap<>();
		map.put("apiKey", apiKey);
		net.setParams(map, RequestNetworkController.REQUEST_PARAM);
		net.startRequestNetwork(RequestNetworkController.POST, Server.DOMAIN_NAME + Server.END_POINT_WEB_API, "WebAPI", new RequestNetwork.RequestListener() {
			@Override
			public void onResponse(String _param1, String _param2, HashMap<String, Object> _param3) {

				try {
					JSONObject json = new JSONObject(_param2);
					if (json.getString("status").equals("200")) {
						isConnected = true;
					} else {
						isConnected = false;
					}
				} catch (JSONException e) {
					isConnected = false;
				}
			}
			
			@Override
			public void onErrorResponse(String _param1, String _param2) {
				isConnected = false;
			}
		});
	}
}
