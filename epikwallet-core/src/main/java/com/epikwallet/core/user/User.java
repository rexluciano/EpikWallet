package com.epikwallet.core.user;

import com.epikwallet.core.*;
import com.epikwallet.core.auth.*;
import com.epikwallet.core.user.UserListener;
import android.content.*;
import java.util.*;
import org.json.*;

public class User {
	private static User instance = null;
	private Context context;
	private SharedPreferences pref;
	private UserListener listener;
	private RequestNetwork net;
	
	private EpikAuth mAuth;
	
	public User(Context context) {
		this.context = context;
		pref = context.getSharedPreferences("epik_users", Context.MODE_PRIVATE);
		mAuth = EpikAuth.getInstance(context);
	}
	
	public static User getInstance(Context context) {
		if (instance != null) {
			instance = new User(context);
		}
		return instance;
	}
	
	public void retrieveUser(UserListener mListener) {
		this.listener = mListener;
		HashMap<String, Object> map = new HashMap<>();
		map.put("authToken", mAuth.getToken());
		net.setParams(map, RequestNetworkController.REQUEST_PARAM);
		net.startRequestNetwork(RequestNetworkController.POST, Server.DOMAIN_NAME + Server.END_POINT_USER, "User", new RequestNetwork.RequestListener() {
			@Override
			public void onResponse(String _param1, String _param2, HashMap<String, Object> _param3) {
				final String _tag = _param1;
				final String _response = _param2;
				final HashMap<String, Object> _responseHeaders = _param3;
				
				try {
					JSONObject json = new JSONObject(_response);
					if (listener != null) {
						if (json.getString("status").equals("200")) {
							JSONObject publicData = new JSONObject(json.getString("public"));
							JSONObject personalData = new JSONObject(json.getString("personal"));
							JSONObject privacyData = new JSONObject(json.getString("privacy"));
							listener.onRetrieveSuccess(json.getString("message"));
							/*Public Data*/
							pref.edit().putString("displayName", publicData.getString("displayName")).apply();
							pref.edit().putString("displayPhoto", publicData.getString("displayPhoto")).apply();
							/*Personal Data*/
							pref.edit().putString("firstName", personalData.getString("firstName")).apply();
							pref.edit().putString("lastName", personalData.getString("lastName")).apply();
							/*Privacy Data*/
							pref.edit().putInt("isAccountBanned", privacyData.getInt("isAccountBanned")).apply();
							pref.edit().putInt("isAccountVerified", privacyData.getInt("isAccountVerified")).apply();
						} else {
							listener.onRetrieveFailed(json.getString("message"));
						}
					}
				} catch (JSONException e) {
					listener.onRetrieveFailed("An error occurred.");
				}
				
			}
			
			@Override
			public void onErrorResponse(String _param1, String _param2) {
				final String _tag = _param1;
				final String _message = _param2;
				if (listener != null) {
					listener.onRetrieveFailed(_message);
				}
			}
		});
	}
	
	public String getDisplayName() {
		return pref.getString("displayName", "");
	}
	
	public String getDisplayPhoto() {
		return pref.getString("displayPhoto", "");
	}
	
	public String getName() {
		return pref.getString("firstName", "");
	}
	
	public String getSurname() {
		return pref.getString("lastName", "");
	}
	
	public int isAccountBanned() {
		return pref.getInt("isAccountBanned", 0);
	}
	
	public int isAccountVerified() {
		return pref.getInt("isAccountVerified", 0);
	}
}
