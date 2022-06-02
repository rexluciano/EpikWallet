package com.epikwallet.core.ui.auth;

import android.annotation.SuppressLint;
import android.content.*;
import android.widget.*;
import android.view.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.app.*;
import android.os.*;
import com.epikwallet.core.R;
import com.epikwallet.core.auth.EpikAuth;
import com.epikwallet.core.auth.EpikAuthListener;
import com.epikwallet.core.app.EpikApp;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class EpikAuthUI {
	@SuppressLint("StaticFieldLeak")
	private static EpikAuthUI mInstance = null;
	private static EpikAuthUICompleteListener listener;
	@SuppressLint("StaticFieldLeak")
	private static Context context;
	private static AlertDialog dialog;
	private static AlertDialog loading;
	private static EpikAuth mAuth;
	
	public EpikAuthUI() {
	}
	
	public static void initialize(Context mContext) {
		context = mContext;
		mAuth = EpikAuth.getInstance(mContext);
	}
	
	public static EpikAuthUI getInstance() {
		if (mInstance == null) {
			mInstance = new EpikAuthUI();
		}
		return mInstance;
	}
	
	public static EpikAuthUI signinWithEpik() {
		if (EpikApp.isReady()) {
			if (context != null) {
				showLoadingDialog();
				new Handler(Looper.getMainLooper()).postDelayed(() -> {
					showLoginDialog();
					loading.dismiss();
				}, 3000);
			}
		} else {
			listener.onCompleted(false, "EpikApp is not yet inilialize.");
		}
		return getInstance();
	}
	
	public void addOnCompleteListener(EpikAuthUICompleteListener mListener) {
		listener = mListener;
	}
	
	private static void showLoginDialog() {
		dialog = new AlertDialog.Builder(context).create();
		final View dialogView = LayoutInflater.from(context).inflate(R.layout.epik_wallet_login_dialog, null);
		dialog.setView(dialogView);
		final LinearLayout login_button = dialogView.findViewById(R.id.login_button);
		final LinearLayout email = dialogView.findViewById(R.id.email);
		final LinearLayout password = dialogView.findViewById(R.id.password);
		final CircularProgressIndicator progressbar1 = dialogView.findViewById(R.id.progressbar1);
		final EditText edittext1 = dialogView.findViewById(R.id.edittext1);
		final EditText edittext2 = dialogView.findViewById(R.id.edittext2);
		final TextView textview2 = dialogView.findViewById(R.id.textview2);
		email.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b, int c, int d) { this.setCornerRadius(a); this.setStroke(b, c); this.setColor(d); return this; } }.getIns((int)10, (int)1, 0xFFBDBDBD, 0xFFFFFFFF));
		password.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b, int c, int d) { this.setCornerRadius(a); this.setStroke(b, c); this.setColor(d); return this; } }.getIns((int)10, (int)1, 0xFFBDBDBD, 0xFFFFFFFF));
		rippleRoundStroke(login_button);
		login_button.setOnClickListener((v) -> {
			progressbar1.setVisibility(View.VISIBLE);
			textview2.setVisibility(View.GONE);
			mAuth.signinUserWithEmailAndPassword(edittext1.getText().toString().trim(), edittext2.getText().toString().trim(), new EpikAuthListener() {
				@Override
				public void onSigninUser(final String message, final boolean isSuccess) {
					if (listener != null) {
						if (isSuccess) {
							listener.onCompleted(true, "");
						} else {
							listener.onCompleted(false, message);
						}
						progressbar1.setVisibility(View.GONE);
						textview2.setVisibility(View.VISIBLE);
                        dialog.dismiss();
					}
				}
				@Override
				public void onCreateUser(String message, boolean isSuccess) {
					
				}
				@Override
				public void onEmailVerificationSent(String message, boolean isSuccess) {
					
				}
			});
		});
		dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		dialog.show();
	}
	
	private static void showLoadingDialog() {
		
		loading = new AlertDialog.Builder(context).create();
		View view = LayoutInflater.from(context).inflate(R.layout.epik_wallet_loading_dialog, null);
		loading.setView(view);
		loading.setCancelable(false);
		loading.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		loading.show();
		
	}
	
	private static void rippleRoundStroke(final View _view) {
		final android.graphics.drawable.GradientDrawable GG = new android.graphics.drawable.GradientDrawable();
		GG.setColor(Color.parseColor("#2B323C"));
		GG.setCornerRadius((float) (double) 10);
		GG.setStroke((int) (double) 0,
		Color.parseColor("#" + "#FFFFFF".replace("#", "")));
		final android.graphics.drawable.RippleDrawable RE = new android.graphics.drawable.RippleDrawable(new android.content.res.ColorStateList(new int[][]{new int[]{}}, new int[]{ Color.parseColor("#FFFFFF")}), GG, null);
		_view.setBackground(RE);
	}
}
