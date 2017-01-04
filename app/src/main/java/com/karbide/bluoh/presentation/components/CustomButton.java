package com.karbide.bluoh.presentation.components;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.karbide.bluoh.R;
import com.karbide.bluoh.util.AppUtil;

import java.util.HashMap;
import java.util.Map;

// TODO: Auto-generated Javadoc

/**
 * The Class Button
 */
public class CustomButton extends Button {

	/**
	 * Instantiates a new custom text view.
	 * 
	 * @param context
	 *            the context
	 */
	private static Map<String, Typeface> mTypefaces;
	public CustomButton(Context context) {
		super(context);
	}

	/**
	 * Instantiates a new custom text view.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 */
	public CustomButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (mTypefaces == null) {
			mTypefaces = new HashMap<String, Typeface>();
		}

		// prevent exception in Android Studio / ADT interface builder
		if (this.isInEditMode()) {
			return;
		}

		final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
		if (array != null) {
			final String typefaceAssetPath = array.getString(R.styleable.CustomTextView_typeface);
			if (typefaceAssetPath != null)
			{
				Typeface typeface = null;
				try {
					if (mTypefaces.containsKey(typefaceAssetPath)) {
						typeface = mTypefaces.get(typefaceAssetPath);
					} else {
						AssetManager assets = context.getAssets();
						typeface = Typeface.createFromAsset(assets, typefaceAssetPath);
						mTypefaces.put(typefaceAssetPath, typeface);
					}
					setTypeface(typeface);
					AppUtil.LogMsg("AKD FONT NAME", typefaceAssetPath );
				}
				catch (Exception ex)
				{
					AppUtil.LogMsg("AKD FONT NAME", typefaceAssetPath+" Not Found");
				}
			}
			array.recycle();
		}
	}

	/**
	 * Instantiates a new custom text view.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 * @param defStyle
	 *            the def style
	 */
	public CustomButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		if (mTypefaces == null) {
			mTypefaces = new HashMap<String, Typeface>();
		}

		// prevent exception in Android Studio / ADT interface builder
		if (this.isInEditMode()) {
			return;
		}

		final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
		if (array != null) {
			final String typefaceAssetPath = array.getString(R.styleable.CustomTextView_typeface);
			if (typefaceAssetPath != null)
			{
				Typeface typeface = null;
				try {
					if (mTypefaces.containsKey(typefaceAssetPath)) {
						typeface = mTypefaces.get(typefaceAssetPath);
					} else {
						AssetManager assets = context.getAssets();
						typeface = Typeface.createFromAsset(assets, typefaceAssetPath);
						mTypefaces.put(typefaceAssetPath, typeface);
					}
					setTypeface(typeface);
					AppUtil.LogMsg("AKD FONT NAME", typefaceAssetPath );
				}
				catch (Exception ex)
				{
					AppUtil.LogMsg("AKD FONT NAME", typefaceAssetPath+" Not Found");
				}
			}
			array.recycle();
		}

	}
}
