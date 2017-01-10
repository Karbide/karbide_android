package com.karbide.bluoh.presentation.components;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;

import com.karbide.bluoh.R;

import java.util.HashMap;
import java.util.Map;

// TODO: Auto-generated Javadoc

/**
 * The Class CustomTextView.
 */
public class CustomTextView extends TextView {

	/**
	 * Instantiates a new custom text view.
	 * 
	 * @param context
	 *            the context
	 */
	private static Map<String, Typeface> mTypefaces;
	public CustomTextView(Context context) {
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
	public CustomTextView(Context context, AttributeSet attrs) {
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
//					AppUtil.LogMsg("BLUOH APP FONT NAME", typefaceAssetPath );
				}
				catch (Exception ex)
				{
//					AppUtil.LogMsg("BLUOH APP FONT NAME", typefaceAssetPath+" Not Found");
				}
			}
			array.recycle();
		}

//		applyCustomFont(context, attrs);
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
	public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
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
//					AppUtil.LogMsg("FONT NAME", typefaceAssetPath );
				}
				catch (Exception ex)
				{
//					AppUtil.LogMsg("FONT NAME", typefaceAssetPath+" Not Found");
				}
			}
			array.recycle();
		}
		/*applyCustomFont(context, attrs);*/
	}

	/**
	 * Sets the text and max lines.
	 *
	 * @param text the new text and max lines
	 */
	public void setTextAndMaxLines(CharSequence text){
		setText(text);
		
		ViewTreeObserver vto = getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				ViewTreeObserver vto = getViewTreeObserver();
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
					vto.removeGlobalOnLayoutListener(this);
				}
				else {
					vto.removeOnGlobalLayoutListener(this);
				}

				int height = getHeight();
				int lineHeight = getLineHeight();

				int maxlines = height / lineHeight;
				setMaxLines(maxlines);
			}
		});		
	}
}
