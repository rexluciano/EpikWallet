package com.epikwallet.core.payment;

import android.annotation.SuppressLint;
import android.content.*;
import android.widget.*;
import android.view.*;
import android.app.*;
import android.os.*;
import org.json.*;
import java.util.*;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.epikwallet.core.R;
import com.epikwallet.core.RequestNetwork;
import com.epikwallet.core.Server;
import com.epikwallet.core.auth.EpikAuth;
import com.epikwallet.core.ui.auth.*;
import com.epikwallet.core.wallet.*;
import com.epikwallet.core.merchant.*;
import com.epikwallet.core.app.EpikApp;
import com.epikwallet.core.exception.Cause;
import com.epikwallet.core.RequestNetworkController;
import com.epikwallet.core.util.UiUtils;

public class EpikPay {
	@SuppressLint("StaticFieldLeak")
	private static EpikPay mInstance = null;
	private String itemName;
	private int price = 0;
	private final BottomSheetDialog mDialog;
	private final Context context;
	private final EpikAuth mAuth;
	private AlertDialog loading;
	private final RequestNetwork net;
	private boolean isLoad = false;
	private String merchantId;
	private final EpikWallet mWallet;
	private final Merchant mMerchant;
	private String balance = "0";
	private final Cause cause;
	
	private EpikPayListener mEpikPayListener;
	
	private final EpikAuthUICompleteListener mEpikAuthListener = new EpikAuthUICompleteListener() {
		@Override
		public void onCompleted(boolean isSuccess, String error) {
			if (isSuccess) {
				mWallet.init();
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("Error");
				builder.setMessage(error);
				builder.setPositiveButton("Okay", (_dialog, _which) -> _dialog.dismiss()).show();
			}
		}
	};
	
	private EpikPay(Context context) {
		this.context = context;
		mAuth = EpikAuth.getInstance(context);
		net = new RequestNetwork((Activity) context);
		mDialog = new BottomSheetDialog(context);
		mWallet = EpikWallet.getInstance(context);
		if (mAuth.isLogin()) {
			mWallet.init();
		}
		mMerchant = Merchant.getInstance(context);
		cause = new Cause();
	}
	
	public static EpikPay getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new EpikPay(context);
		}
		return mInstance;
	}
	
	public void connect(String token, String merchantId) {
		this.merchantId = merchantId;
		if (merchantId != null) {
			mMerchant.initializeId(merchantId);
		}
		HashMap<String, Object> map = new HashMap<>();
		map.put("OAuthToken", token);
		net.setParams(map, RequestNetworkController.REQUEST_PARAM);
		net.startRequestNetwork(RequestNetworkController.POST, Server.DOMAIN_NAME + Server.END_POINT_APPLICATIONS, "Application", new RequestNetwork.RequestListener() {
			@Override
			public void onResponse(String _param1, String _param2, HashMap<String, Object> _param3) {
				final String _response = _param2;

				try {
					JSONObject json = new JSONObject(_response);
					if (json.getString("status").equals("200")) {
						isLoad = true;
						JSONObject data = new JSONObject(json.getString("data"));
						balance = mWallet.getBalance();
						if (loading != null) {
							loading.dismiss();
						}
						if (mEpikPayListener != null) {
							mEpikPayListener.onConnectionSuccess();
						}
					} else {
						isLoad = false;
						if (loading != null) {
							loading.dismiss();
						}
						cause.setCause("Connection failed.");
						if (mEpikPayListener != null) {
							mEpikPayListener.onConnectionFailed(cause);
						}
					}
				} catch (JSONException e) {
					isLoad = false;
					if (loading != null) {
						loading.dismiss();
					}
					cause.setCause("Connection failed.");
					if (mEpikPayListener != null) {
						mEpikPayListener.onConnectionFailed(cause);
					}
				}
				
			}
			
			@Override
			public void onErrorResponse(String _param1, String _param2) {
				isLoad = false;
				if (loading != null) {
					loading.dismiss();
				}
				cause.setCause("Connection failed.");
				if (mEpikPayListener != null) {
					mEpikPayListener.onConnectionFailed(cause);
				}
			}
		});
	}
	
	public EpikPay setName(String name) {
		this.itemName = name;
		return this;
	}
	
	public EpikPay setValue(int value) {
		this.price = value;
		return this;
	}
	
	public void launch() {
		if (mAuth.isLogin()) {
			if (isLoad) {
				if (EpikApp.isReady()) {
					View view = LayoutInflater.from(context).inflate(R.layout.epik_wallet_payment_gateway, null);
					mDialog.setContentView(view);
					final TextView name = view.findViewById(R.id.itemName);
					final TextView itemPrice = view.findViewById(R.id.itemPrice);
					final LinearLayout buyButton = view.findViewById(R.id.buyButton);
					final CircularProgressIndicator progressbar1 = view.findViewById(R.id.progressbar1);
					final TextView textview2 = view.findViewById(R.id.textview2);
					final TextView availableBalance = view.findViewById(R.id.availableBalance);
					
					name.setText(itemName);
					itemPrice.setText(String.valueOf(price));
					UiUtils.formatText(availableBalance, Double.parseDouble(balance));
					
					UiUtils.rippleRoundStroke(buyButton, "#2B323C", "#FFFFFF", 10, 0, "#FFFFFF");
					
					buyButton.setOnClickListener((v) -> {
						progressbar1.setVisibility(View.VISIBLE);
						textview2.setVisibility(View.GONE);
						new Handler(Looper.getMainLooper()).postDelayed(() -> {
							if (Integer.parseInt(mWallet.getBalance()) > price) {
								if (merchantId.equals(mWallet.getAccountNumber())) {
									cause.setCause("Invalid transaction.");
									if (mEpikPayListener != null) {
										mEpikPayListener.onPurchasedFailed(cause);
									}
								} else {
									makePurchase();
								}
							} else {
								cause.setCause("Insufficient balance.");
								if (mEpikPayListener != null) {
									mEpikPayListener.onPurchasedFailed(cause);
								}
							}
							progressbar1.setVisibility(View.GONE);
							textview2.setVisibility(View.VISIBLE);
						}, 3000);
					});
					mDialog.show();
				} else {
					cause.setCause("EpikApp is not yet inilialize.");
					if (mEpikPayListener != null) {
						mEpikPayListener.onPurchasedFailed(cause);
					}
				}
			} else {
				showLoadingDialog();
			}
		} else {
			EpikAuthUI.getInstance();
			EpikAuthUI.signinWithEpik().addOnCompleteListener(mEpikAuthListener);
		}
	}
	
	private void makePurchase() {
		if (!mMerchant.getMerchantBalance().equals("")) {
			HashMap<String, Object> map = new HashMap<>();
			map.put("accountNumber", merchantId);
			map.put("authToken", mAuth.getToken());
			map.put("balance", Integer.parseInt(mMerchant.getMerchantBalance()) + price);
			map.put("buyerBalance", Integer.parseInt(mWallet.getBalance()) - price);
			net.setParams(map, RequestNetworkController.REQUEST_PARAM);
			net.startRequestNetwork(RequestNetworkController.POST, Server.DOMAIN_NAME + Server.END_POINT_PURCHASE, "Purchase", new RequestNetwork.RequestListener() {
				@Override
				public void onResponse(String _param1, String _param2, HashMap<String, Object> _param3) {

					try {
						JSONObject json = new JSONObject(_param2);
						if (json.getString("status").equals("200")) {
							if (mEpikPayListener != null) {
								mEpikPayListener.onPurchasedSuccess();
							}
							mWallet.updateBalance(String.valueOf(Integer.parseInt(mWallet.getBalance()) - price));
							mDialog.dismiss();
						} else {
							cause.setCause(json.getString("message"));
							if (mEpikPayListener != null) {
								mEpikPayListener.onPurchasedFailed(cause);
							}
						}
					} catch (JSONException e) {
						cause.setCause(e.getLocalizedMessage());
						if (mEpikPayListener != null) {
							mEpikPayListener.onPurchasedFailed(cause);
						}
					}
				}
				
				@Override
				public void onErrorResponse(String _param1, String _param2) {
					cause.setCause(_param2);
					if (mEpikPayListener != null) {
						mEpikPayListener.onPurchasedFailed(cause);
					}
				}
			});
		} else {
			cause.setCause("Merchant Balance is empty.");
			if (mEpikPayListener != null) {
				mEpikPayListener.onPurchasedFailed(cause);
			}
		}
	}
	
	private void showLoadingDialog() {
		
		loading = new AlertDialog.Builder(context).create();
		View view = LayoutInflater.from(context).inflate(R.layout.epik_wallet_loading_dialog, null);
		loading.setView(view);
		loading.setCancelable(false);
		loading.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		loading.show();
		
	}
	
	public void release() {
		mAuth.logout();
		mWallet.reset();
	}
	
	public void setEpikPayListener(EpikPayListener mEpikPayListener) {
		this.mEpikPayListener = mEpikPayListener;
	}
}
