package com.epikwallet.core.wallet;

import android.app.*;
import com.epikwallet.core.*;
import com.epikwallet.core.wallet.EpikWalletListener;
import com.epikwallet.core.auth.*;
import com.epikwallet.core.app.EpikApp;
import android.content.*;
import java.util.HashMap;
import org.json.*;
import android.text.*;
import android.widget.Toast;

public class EpikWallet {
	private static EpikWallet instance = null;
	private RequestNetwork net;
	private SharedPreferences pref;
	private Context context;
	private EpikWalletListener listener;
	private int balance;
	private EpikAuth mAuth;
	
	public EpikWallet(Context context) {
		net = new RequestNetwork((Activity) context);
		pref = context.getSharedPreferences("epik_banks", Context.MODE_PRIVATE);
		this.context = context;
		this.listener = null;
		mAuth = EpikAuth.getInstance(context);
	}
	
	public static EpikWallet getInstance(Context context) {
		if (instance == null) {
			instance = new EpikWallet(context);
		}
		return instance;
	}
	
	public void init() {
		if (EpikApp.isReady()) {
			HashMap<String, Object> map = new HashMap<>();
			map.put("authToken", mAuth.getToken());
			net.setParams(map, RequestNetworkController.REQUEST_PARAM);
			net.startRequestNetwork(RequestNetworkController.POST, Server.DOMAIN_NAME + Server.END_POINT_BALANCE, "Balance", new RequestNetwork.RequestListener() {
				@Override
				public void onResponse(String _param1, String _param2, HashMap<String, Object> _param3) {
					final String _tag = _param1;
					final String _response = _param2;
					final HashMap<String, Object> _responseHeaders = _param3;
					
					try {
						JSONObject json = new JSONObject(_response);
						if (json.getString("status").equals("200")) {
							JSONObject data = new JSONObject(json.getString("data"));
							pref.edit().putString("balance", data.getString("balance")).apply();
							pref.edit().putString("accountNumber", data.getString("accountNumber")).apply();
						}
					} catch (JSONException e) {
						Toast.makeText(context, "An unknown error occurred.", Toast.LENGTH_LONG).show();
					}
					
				}
				
				@Override
				public void onErrorResponse(String _param1, String _param2) {
					final String _tag = _param1;
					final String _message = _param2;
					Toast.makeText(context, _message, Toast.LENGTH_LONG).show();
				}
			});
		} else {
            
        }
	}
	
	public String getBalance() {
		return pref.getString("balance", "");
	}
	
	public String getAccountNumber() {
		return pref.getString("accountNumber", "");
	}
	
	public void reset() {
		pref.edit().clear().apply();
	}
	
	public void updateBalance(String bal) {
		pref.edit().putString("balance", bal).apply();
	}
}
