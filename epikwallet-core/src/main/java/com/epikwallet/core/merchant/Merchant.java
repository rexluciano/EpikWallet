package com.epikwallet.core.merchant;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.epikwallet.core.RequestNetwork;
import com.epikwallet.core.RequestNetworkController;
import com.epikwallet.core.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Merchant {
    private static Merchant mInstance = null;
    private final RequestNetwork net;
    private final SharedPreferences pref;
    
    private Merchant(Context context) {
        pref = context.getSharedPreferences("epik_merchant", Context.MODE_PRIVATE);
        net = new RequestNetwork((Activity) context);
    }
    
    public static Merchant getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Merchant(context);
        }
        return mInstance;
    }
    
    public void initializeId(String accountNumber) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("accountNumber", accountNumber);
        net.setParams(map, RequestNetworkController.REQUEST_PARAM);
		net.startRequestNetwork(RequestNetworkController.POST, Server.DOMAIN_NAME + Server.END_POINT_MERCHANT, "Merchant", new RequestNetwork.RequestListener() {
			@Override
			public void onResponse(String _param1, String _param2, HashMap<String, Object> _param3) {

                try {
					JSONObject json = new JSONObject(_param2);
					if (json.getString("status").equals("200")) {
                        JSONObject data = new JSONObject(json.getString("data"));
                        pref.edit().putString("merchantBalance", data.getString("accountBalance")).apply();
					}
                } catch (JSONException ignored) {
				}
				
			}
			
			@Override
			public void onErrorResponse(String _param1, String _param2) {
            }
		});
    }
    
    public String getMerchantBalance() {
        return pref.getString("merchantBalance", "");
    }
}
