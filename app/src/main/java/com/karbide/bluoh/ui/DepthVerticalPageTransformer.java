package com.karbide.bluoh.ui;

import android.view.View;


// TODO: Auto-generated Javadoc
/**
 * The Class DepthVerticalPageTransformer.
 */
public class DepthVerticalPageTransformer implements VerticalViewPager.PageTransformer {
	
	/** The Constant TAG. */
	@SuppressWarnings("unused")
	private static final String TAG = DepthVerticalPageTransformer.class.getSimpleName();

	/** The min scale. */
	private static float MIN_SCALE = 0.80f;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.view.ViewPager.PageTransformer#transformPage(android
	 * .view.View, float)
	 */
	/**
	 * Transform page.
	 * 
	 * @param view
	 *            the view
	 * @param position
	 *            the position
	 */
	public void transformPage(View view, float position) {
		// AppUtil.LogMsg(TAG, "position: " + position);
		int pageHeight = view.getHeight();

		if (position < -1) { // [-Infinity,-1)
			// This page is way off-screen to the left.
		}
		else if (position <= 0) { // [-1,0]

			// Scale the page down (between MIN_SCALE and 1)
			float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));

			if (null != view) {
				// Fade the page out.
				view.setAlpha(1 - (-position));

				// Counteract the default slide transition
				view.setTranslationY(pageHeight * -position);

				view.setScaleX(scaleFactor);
				view.setScaleY(scaleFactor);
			}
		}
		else if (position <= 1) { // (0,1]

		}
		else { // (1,+Infinity]
				// This page is way off-screen to the right.
		}
	}

}
