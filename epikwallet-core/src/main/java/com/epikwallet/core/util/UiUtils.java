package com.epikwallet.core.util;

import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import java.text.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.ArrayList;
import java.util.regex.*;

public class UiUtils {
	
	public static void rippleRoundStroke(final View _view, final String _focus, final String _pressed, final double _round, final double _stroke, final String _strokeclr) {
		final android.graphics.drawable.GradientDrawable GG = new android.graphics.drawable.GradientDrawable();
		GG.setColor(Color.parseColor(_focus));
		GG.setCornerRadius((float)_round);
		GG.setStroke((int) _stroke,
		Color.parseColor("#" + _strokeclr.replace("#", "")));
		final android.graphics.drawable.RippleDrawable RE = new android.graphics.drawable.RippleDrawable(new android.content.res.ColorStateList(new int[][]{new int[]{}}, new int[]{ Color.parseColor(_pressed)}), GG, null);
		_view.setBackground(RE);
	}
	
	public static void formatText(final TextView _textview, final double _number) {
		ArrayList<String> list = new ArrayList<>();
		double num = 0;
		String format = "";
		list.add("");
		list.add("K");
		list.add("M");
		list.add("B");
		list.add("T");
		num = Math.floor((String.valueOf((long)(_number)).length() - 1) / 3);
		if (num > 4) {
			num = 4;
		}
		format = "###,###.##".concat(list.get((int)(num)));
		_textview.setText(new DecimalFormat(format).format(_number / Math.pow(1000, num)));
	}
	
}
