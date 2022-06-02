package com.epikwallet.core.auth;

import android.app.*;
import com.epikwallet.core.*;
import android.content.*;
import java.util.HashMap;
import org.json.*;
import android.text.*;
import com.epikwallet.core.app.EpikApp;

public class EpikAuth {
	
	private EpikAuthListener listener;
	private HashMap<String, Object> map = new HashMap<>();
	private static EpikAuth sInstance = null;
	private RequestNetwork net;
	private SharedPreferences pref;
	
	private String fName;
	private String lName;
	
	public EpikAuth(Context context) {
		net = new RequestNetwork((Activity) context);
		pref = context.getSharedPreferences("epik_users", Context.MODE_PRIVATE);
		this.listener = null;
	}
	
	public static EpikAuth getInstance(Context context) {
		
		if (sInstance == null) {
			sInstance = new EpikAuth(context);
		}
		
		return sInstance;
	}
	
	public void signinUserWithEmailAndPassword(String email, String password, EpikAuthListener mListener) {
		this.listener = mListener;
		if (EpikApp.isReady()) {
			doSignin(email, password);
		} else {
			if (listener != null) {
				listener.onSigninUser("EpikApp is not yet initialize.", false);
			}
		}
	}
	
	public void setName(String fName, String lName) {
		this.fName = fName;
		this.lName = lName;
		if (TextUtils.isEmpty(fName)) {
			throw new NullPointerException("You must set an name.");
		} else if (TextUtils.isEmpty(lName)) {
			throw new NullPointerException("You must set an surname.");
		}
	}
	
	public void createUserWithEmailAndPassword(String email, String password, EpikAuthListener mListener) {
		this.listener = mListener;
		if (EpikApp.isReady()) {
			doSignup(email, password);
		} else {
			if (listener != null) {
				listener.onCreateUser("EpikApp is not yet initialize.", false);
			}
		}
	}
	
	public void sendEmailVerification() {
		
	}
	
	/*EpikAuth User Details*/
	
	public String getUid() {
		return pref.getString("userId", "");
	}
	
	public String getEmail() {
		return pref.getString("email", "");
	}
	
	public String getDisplayName() {
		return pref.getString("displayName", "");
	}
	
	public String getToken() {
		return pref.getString("authToken", "");
	}
	
	public boolean isLogin() {
		return pref.getBoolean("isLogin", false);
	}
	
	public void logout() {
		pref.edit().clear().apply();
	}
	
	private void doSignin(final String email, final String password) {
		if (TextUtils.isEmpty(email)) {
			throw new NullPointerException("Email field is cannot be empty.");
		}
		if (TextUtils.isEmpty(password)) {
			throw new NullPointerException("Password field is cannot be empty.");
		} else {
			map = new HashMap<>();
			map.put("email", email);
			map.put("password", password);
			net.setParams(map, RequestNetworkController.REQUEST_PARAM);
			net.startRequestNetwork(RequestNetworkController.POST, Server.DOMAIN_NAME + Server.END_POINT_LOGIN, "Login", new RequestNetwork.RequestListener() {
				@Override
				public void onResponse(String _param1, String _param2, HashMap<String, Object> _param3) {
					final String _tag = _param1;
					final String _response = _param2;
					final HashMap<String, Object> _responseHeaders = _param3;
					
					try {
						JSONObject json = new JSONObject(_response);
						if (listener != null) {
							if (json.getString("status").equals("200")) {
								JSONObject data = new JSONObject(json.getString("data"));
								listener.onSigninUser(json.getString("message"), true);
								pref.edit().putString("email", data.getString("userEmail")).apply();
								pref.edit().putString("authToken", data.getString("authToken")).apply();
								pref.edit().putString("userId", data.getString("userId")).apply();
								pref.edit().putBoolean("isLogin", true).apply();
							} else {
								listener.onSigninUser(json.getString("message"), false);
								pref.edit().putBoolean("isLogin", false).apply();
							}
						}
					} catch (JSONException e) {
						if (listener != null) {
							listener.onSigninUser("An error occurred.", false);
						}
						pref.edit().putBoolean("isLogin", false).apply();
					}
					
				}
				
				@Override
				public void onErrorResponse(String _param1, String _param2) {
					final String _tag = _param1;
					final String _message = _param2;
					if (listener != null) {
						listener.onSigninUser(_message, false);
					}
					pref.edit().putBoolean("isLogin", false).apply();
				}
			});
		}
	}
	
	private void doSignup(final String email, final String password) {
		if (TextUtils.isEmpty(email)) {
			throw new NullPointerException("Email field is cannot be empty.");
		}
		if (TextUtils.isEmpty(password)) {
			throw new NullPointerException("Password field ia cannot be empty.");
		} else {
			HashMap<String, Object> map = new HashMap<>();
			map.put("firstName", fName);
			map.put("lastName", lName);
			map.put("userEmail", email);
			map.put("userPassword", password);
			net.setParams(map, RequestNetworkController.REQUEST_PARAM);
			net.startRequestNetwork(RequestNetworkController.POST, Server.DOMAIN_NAME + Server.END_POINT_SIGNUP, "Signup", new RequestNetwork.RequestListener() {
				@Override
				public void onResponse(String _param1, String _param2, HashMap<String, Object> _param3) {
					final String _tag = _param1;
					final String _response = _param2;
					final HashMap<String, Object> _responseHeaders = _param3;
					
					try {
						JSONObject json = new JSONObject(_response);
						if (listener != null) {
							if (json.getString("status").equals("200")) {
								listener.onCreateUser(json.getString("message"), true);
							} else {
								listener.onCreateUser(json.getString("message"), false);
							}
						}
					} catch (JSONException e) {
						if (listener != null) {
							listener.onCreateUser("An error occurred.", false);
						}
					}
					
				}
				
				@Override
				public void onErrorResponse(String _param1, String _param2) {
					final String _tag = _param1;
					final String _message = _param2;
					if (listener != null) {
						listener.onCreateUser(_message, false);
					}
				}
			});
		}
	}
}
