package com.epikwallet.core.ui.button;

import android.widget.*;
import android.content.*;
import android.view.*;
import android.app.*;
import android.content.res.*;
import android.graphics.*;
import android.util.*;
import android.os.*;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.epikwallet.core.R;
import com.epikwallet.core.payment.EpikPay;
import com.epikwallet.core.app.EpikApp;

public class EpikPayButton extends RelativeLayout {
	
	private boolean isLightModeEnabled;
	private TextView textview2;
	private ImageView epikIcon;
	private LinearLayout epikButton;
	private CircularProgressIndicator progressbar1;
	private EpikPay mPay;
	private String itemName;
	private int itemPrice;
	
	public EpikPayButton(Context context) {
		super(context, null);
		init(context, null, -1);
	}
	
	public EpikPayButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, -1);
	}
	
	
	public EpikPayButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr);
	}
	
	private void init(Context context, AttributeSet attrs, int defStyleAttr) {
		View mLayout = View.inflate(context, R.layout.epik_pay_button_layout, null);
		addView(mLayout);
		
		mPay = EpikPay.getInstance(context);
		
		ViewGroup viewGroup = (ViewGroup) mLayout.getParent();
		viewGroup.setClipChildren(false);
		
		textview2 = mLayout.findViewById(R.id.textview2);
		epikButton = mLayout.findViewById(R.id.epikButton);
		progressbar1 = mLayout.findViewById(R.id.progressbar1);
		epikIcon = mLayout.findViewById(R.id.epikIcon);
		
		if (isLightModeEnabled) {
			rippleRoundStroke(epikButton, "#F5F5F5", "#E0E0E0", 10, 0, "#FFFFFF");
			textview2.setTextColor(0xFF2B323C);
			epikIcon.setColorFilter(0xFF2B323C);
            progressbar1.getIndeterminateDrawable().setTint(0xFF2B323C);
		} else {
			rippleRoundStroke(epikButton, "#2B323C", "#FFFFFF", 10, 0, "#FFFFFF");
			textview2.setTextColor(0xFFFFFFFF);
			epikIcon.setColorFilter(0xFFFFFFFF);
            progressbar1.getIndeterminateDrawable().setTint(0xFFFFFFFF);
		}
		
		if (attrs != null && defStyleAttr != -1) {
			TypedArray a = context.getTheme().obtainStyledAttributes(
			attrs,
			R.styleable.EpikPayButton,
			0, 0);
			try {
				isLightModeEnabled = a.getBoolean(R.styleable.EpikPayButton_enableLightMode, false);
			} finally {
				a.recycle();
			}
		}
		
		new Handler(Looper.getMainLooper()).postDelayed(() -> {
			if (EpikApp.isReady()) {
				textview2.setVisibility(View.VISIBLE);
				progressbar1.setVisibility(View.GONE);
				epikIcon.setVisibility(View.VISIBLE);
				epikButton.setEnabled(true);
			} else {
				textview2.setVisibility(View.GONE);
				progressbar1.setVisibility(View.VISIBLE);
				epikIcon.setVisibility(View.GONE);
				epikButton.setEnabled(false);
			}
		}, 2000);
		
		epikButton.setOnClickListener(v -> {
			mPay.setName(itemName).setValue(itemPrice).launch();
		});
	}
	
	public void setItemName(String value) {
		this.itemName = value;
	}
	
	public void setItemValue(int value) {
		this.itemPrice = value;
	}
	
	public void enableLightMode(boolean enabled) {
		this.isLightModeEnabled = enabled;
		invalidate();
		requestLayout();
	}
	
	private void rippleRoundStroke(final View _view, final String _focus, final String _pressed, final double _round, final double _stroke, final String _strokeclr) {
		android.graphics.drawable.GradientDrawable GG = new android.graphics.drawable.GradientDrawable();
		GG.setColor(Color.parseColor(_focus));
		GG.setCornerRadius((float)_round);
		GG.setStroke((int) _stroke,
		Color.parseColor("#" + _strokeclr.replace("#", "")));
		android.graphics.drawable.RippleDrawable RE = new android.graphics.drawable.RippleDrawable(new android.content.res.ColorStateList(new int[][]{new int[]{}}, new int[]{ Color.parseColor(_pressed)}), GG, null);
		_view.setBackground(RE);
	}
}
