package com.karbide.bluoh.viewadapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.google.gson.Gson;
import com.karbide.bluoh.DeckDetailActivity;
import com.karbide.bluoh.R;
import com.karbide.bluoh.database.AppDatabaseHelper;
import com.karbide.bluoh.datatypes.Card;
import com.karbide.bluoh.datatypes.Content;
import com.karbide.bluoh.ui.CustomTextView;
import com.karbide.bluoh.util.AppUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

// TODO: Auto-generated Javadoc
/**
 * The Class VerticalPagerAdapter.
 */
public class HomePagerAdapter extends PagerAdapter {

	/** The Constant TAG. */
	private static final String TAG = HomePagerAdapter.class.getSimpleName();

	private ArrayList<Content> _allDecks;
	/** The _context. */
	private Context _context = null;

	/** The _on click listener. */
	private OnClickListener _onClickListener = null;
	/** The _on checked change listener. */
	private CompoundButton.OnCheckedChangeListener _onCheckedChangeListener = null;
	private String adPlacementId = "800960263379262_814380178703937";
	public static NativeAd nativeAd = null;
	public static Ad loadedAd = null;
	/**
	 * Instantiates a new vertical pager adapter.
	 * 
	 * @param context the context
	 * @paramarticleSummaryList the article summary list
	 * @param onClickListener the on click listener
	 */
	public HomePagerAdapter(Context context, OnClickListener onClickListener, ArrayList<Content> allDecks, CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
		_context = context;
		_onClickListener = onClickListener;
		_onCheckedChangeListener = onCheckedChangeListener;
		_allDecks = allDecks;
//		showNativeAd();
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bluoh.ui.PagerAdapter#getCount()
	 */
	@Override
	public int getCount() {
//		AppUtil.LogMsg(TAG, "Item++ count: " + _allDecks.size());
		if (null != _allDecks)
		{
			if(nativeAd != null && loadedAd!= null)
				return _allDecks.size()+((_allDecks.size()/6)+1);
			else
				return _allDecks.size();
		}
		else {
			return 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bluoh.ui.PagerAdapter#instantiateItem(android.view.ViewGroup ,
	 * int)
	 */
	@Override
	public Object instantiateItem(ViewGroup container, int position)
	{
		AppUtil.LogMsg(TAG, "instantiateItem++ position: " + position+" ALl Deck size:- "+_allDecks.size());
		View view = null;


		if(nativeAd != null && loadedAd != null)
		{
//			if(position > 0 && position%6 == 0)
			if(position%6 == 0)
			{
				view = initializeAdView(loadedAd);
			}
			else
			{
//				int index = position - (int)Math.floor(position/6);
				int index = position - ((int)Math.floor(position/6)+1);
				AppUtil.LogMsg(TAG, "Index here++"+(int)Math.floor(position/6));
				if (_allDecks.get(index).getType() != null && _allDecks.get(index).getType().equalsIgnoreCase("deck"))
				{
					view = initializeLandscapeImageLayoutDeck(index);
				} else
				{
					if (_allDecks.get(index).getCards().get(0).getTemplate().equalsIgnoreCase("Full"))
						view = initializePortraitImageLayout(index);
					else if (_allDecks.get(index).getCards().get(0).getTemplate().equalsIgnoreCase("70_30"))
						view = initializeSeventyTirtyLayout(index);
					else
						view = initializeLandscapeImageLayout(index);
				}
			}
		}
		else
		{
			if (_allDecks.get(position).getType() != null && _allDecks.get(position).getType().equalsIgnoreCase("deck")) {
				view = initializeLandscapeImageLayoutDeck(position);
			} else {
				if (_allDecks.get(position).getCards().get(0).getTemplate().equalsIgnoreCase("Full"))
					view = initializePortraitImageLayout(position);
				else if (_allDecks.get(position).getCards().get(0).getTemplate().equalsIgnoreCase("70_30"))
					view = initializeSeventyTirtyLayout(position);
				else
					view = initializeLandscapeImageLayout(position);
			}
		}
		if (null != view) {
			container.addView(view, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
			view.setTag(position);
		}

		return view;
	}

	/**
	 * Initialize landscape image layout.
	 *
	 * @param position the position
	 * @return the view
	 */
	private View initializeLandscapeImageLayout(int position) {
		View view = LayoutInflater.from(_context).inflate(R.layout.category_full_view_layout_landscape_img, null);
		setCommonData(position, view, _allDecks.get(position).getCards().get(0));
		loadArticleImage(view, _allDecks.get(position).getCards().get(0).getMedia().getUrl());
		return view;
	}

	private View initializeSeventyTirtyLayout(int position) {
		View view = LayoutInflater.from(_context).inflate(R.layout.layout_seventy_thirty, null);
		setCommonData(position, view, _allDecks.get(position).getCards().get(0));
		loadArticleImage(view, _allDecks.get(position).getCards().get(0).getMedia().getUrl());
		return view;
	}
	/**
	 * Initialize landscape image layout Deck.
	 *
	 * @param position the position
	 * @return the view
	 */
	private View initializeLandscapeImageLayoutDeck(int position) {
		View view = LayoutInflater.from(_context).inflate(R.layout.full_view_layout_portrait_img_deck, null);
		setCommonDataDeck(position, view, _allDecks.get(position).getCards().get(0));
		loadArticleImage(view, _allDecks.get(position).getCards().get(0).getMedia().getUrl());
		return view;
	}

	/**
	 * Initialize portrait image layout.
	 * 
	 * @paramsummary the summary
	 * @param position the position
	 * @return the view
	 */
	private View initializePortraitImageLayout(int position) {
		View view = LayoutInflater.from(_context).inflate(R.layout.category_full_view_layout_portrait_img, null);
		setCommonData(position, view, _allDecks.get(position).getCards().get(0));
		loadArticleImage(view, _allDecks.get(position).getCards().get(0).getMedia().getUrl());
		return view;
	}


	private void setCommonData(final int position, View view, final Card summary)
	{
		if (null != view && null != summary)
		{
			CustomTextView tvHeadline = (CustomTextView) view.findViewById(R.id.textViewArticleHeadline);
			CustomTextView tvSummary = (CustomTextView) view.findViewById(R.id.textViewArticleSummary);
			CustomTextView tvSource = (CustomTextView) view.findViewById(R.id.tvNewsSource);
			LinearLayout llSourceImage = (LinearLayout)view.findViewById(R.id.llSourceImage);
			ImageView ivSourceImage = (ImageView)view.findViewById(R.id.ivSourceImage);
			CustomTextView tvAuthor = (CustomTextView) view.findViewById(R.id.tvAuthorName);
			CustomTextView tvMediaCopyRight = (CustomTextView) view.findViewById(R.id.tvMediaCopyRight);
			CircleImageView ivAuthorImage = (CircleImageView)view.findViewById(R.id.ivAuthorImage);

			if (null != _onClickListener)
			{
				CheckBox buttonLike = (CheckBox) view.findViewById(R.id.buttonLike);
				CheckBox buttonBookmark = (CheckBox) view.findViewById(R.id.buttonBookmark);
				ImageButton buttonShare = (ImageButton) view.findViewById(R.id.imageButtonShare);
				buttonShare.setTag(Integer.valueOf(position));
				buttonShare.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						AppUtil.shareData(_context, ""+summary.getTitle()+"\n"+summary.getUrl());
					}
				});


				buttonLike.setTag(Integer.valueOf(position));
				buttonBookmark.setTag(Integer.valueOf(position));
				buttonShare.setTag(Integer.valueOf(position));

				buttonBookmark.setChecked(AppDatabaseHelper.getInstance(_context).isBookMarked(_allDecks.get(position).getDeckId()));
				buttonBookmark.setOnCheckedChangeListener(_onCheckedChangeListener);
				buttonLike.setOnCheckedChangeListener(_onCheckedChangeListener);
				tvSource.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view)
					{

//						AppUtil.openNativeWebView(_context, summary.getUrl());
						AppUtil.openNativeWebView(_context, getBundle(position));
					}
				});

				llSourceImage.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
//						AppUtil.openNativeWebView(_context, summary.getUrl());
						AppUtil.openNativeWebView(_context, getBundle(position));
					}
				});
			}
			view.setOnClickListener(null);
			tvHeadline.setText(summary.getTitle());
			tvSummary.setText(summary.getContent());
//			Glide.with(_context).load(_allDecks.get(position).getAuthorImage()).into(ivAuthorImage);
			ImageLoader.getInstance().displayImage(_allDecks.get(position).getAuthorImage(), ivAuthorImage);
			tvAuthor.setText(_allDecks.get(position).getDisplayName());
//			if(summary.getMedia().getSource() != null && !summary.getMedia().getSource().equals(""))
//				tvMediaCopyRight.setText(_context.getResources().getString(R.string.copyright_symbol)+" "+summary.getMedia().getSource());
			if(summary.getMedia().getSource() != null && !summary.getMedia().getSource().equals(""))
			{
				if(summary.getMedia().getSource().contains("newsapi"))
				{
					tvMediaCopyRight.setText(Html.fromHtml(_context.getResources().getString(R.string.powered_by_text)));
					tvMediaCopyRight.setMovementMethod(LinkMovementMethod.getInstance());
				}
				else
					tvMediaCopyRight.setText(_context.getResources().getString(R.string.copyright_symbol) + " " + summary.getMedia().getSource());
			}
			if(summary.getSource()!= null && (summary.getSource().contains("http")
					|| summary.getSource().contains(".png") || summary.getSource().contains(".jpg")))
			{
				Log.e("IMG", "SOURCE : "+summary.getSource());
				tvSource.setVisibility(View.GONE);
				llSourceImage.setVisibility(View.VISIBLE);
				Glide.with(_context).load(summary.getSource()).into(ivSourceImage);
			}
			else
			{
				tvSource.setVisibility(View.VISIBLE);
				llSourceImage.setVisibility(View.GONE);
				tvSource.setText("@"+summary.getSource());
			}
		}
	}

	private void setCommonDataDeck(final int position, View view, final Card summary)
	{
		if (null != view && null != summary)
		{
			CustomTextView tvHeadline = (CustomTextView) view.findViewById(R.id.textViewArticleHeadline);
			CustomTextView tvAuthor = (CustomTextView) view.findViewById(R.id.tvAuthorName);
			CustomTextView tvSource = (CustomTextView) view.findViewById(R.id.tvNewsSource);
			CustomTextView tvMediaCopyRight = (CustomTextView) view.findViewById(R.id.tvMediaCopyRight);
			CircleImageView ivAuthorImage = (CircleImageView)view.findViewById(R.id.ivAuthorImage);
			ImageView ivSourceImage = (ImageView)view.findViewById(R.id.ivSourceImage);
			LinearLayout llSourceImage = (LinearLayout)view.findViewById(R.id.llSourceImage);
			Button btnOpenDeck = (Button) view.findViewById(R.id.btnOpenDeck);
			if (null != _onClickListener)
			{
				CheckBox buttonLike = (CheckBox) view.findViewById(R.id.buttonLike);
				CheckBox buttonBookmark = (CheckBox) view.findViewById(R.id.buttonBookmark);
				ImageButton buttonShare = (ImageButton) view.findViewById(R.id.imageButtonShare);
				buttonShare.setTag(Integer.valueOf(position));
				buttonShare.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						AppUtil.shareData(_context, ""+summary.getTitle()+"\n"+summary.getUrl());
					}
				});


				buttonLike.setTag(Integer.valueOf(position));
				buttonBookmark.setTag(Integer.valueOf(position));
				buttonShare.setTag(Integer.valueOf(position));

				buttonBookmark.setChecked(AppDatabaseHelper.getInstance(_context).isBookMarked(_allDecks.get(position).getDeckId()));
				buttonBookmark.setOnCheckedChangeListener(_onCheckedChangeListener);
				buttonLike.setOnCheckedChangeListener(_onCheckedChangeListener);
				buttonShare.setOnClickListener(_onClickListener);
				tvSource.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
//						AppUtil.openUrlInWeb(_context,summary.getUrl());
						AppUtil.openNativeWebView(_context, getBundle(position));
					}
				});

				llSourceImage.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
//						AppUtil.openNativeWebView(_context, summary.getUrl());
						AppUtil.openNativeWebView(_context, getBundle(position));
					}
				});
			}
			btnOpenDeck.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent deckDetail = new Intent(_context, DeckDetailActivity.class);
					deckDetail.putExtra("deckId", _allDecks.get(position).getDeckId());
					deckDetail.putExtra("title", _allDecks.get(position).getCards().get(0).getTitle());
					deckDetail.putExtra("content", new Gson().toJson(_allDecks.get(position)));
					_context.startActivity(deckDetail);
					Toast.makeText(_context, "TAP CLICKED", Toast.LENGTH_LONG).show();
				}
			});

			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent deckDetail = new Intent(_context, DeckDetailActivity.class);
					deckDetail.putExtra("deckId", _allDecks.get(position).getDeckId());
					deckDetail.putExtra("title", _allDecks.get(position).getCards().get(0).getTitle());
					_context.startActivity(deckDetail);
					Toast.makeText(_context, "TAP CLICKED", Toast.LENGTH_LONG).show();
				}
			});
//			Glide.with(_context).load(_allDecks.get(position).getAuthorImage()).into(ivAuthorImage);
			ImageLoader.getInstance().displayImage(_allDecks.get(position).getAuthorImage(), ivAuthorImage);
			tvHeadline.setText(summary.getTitle());
			tvAuthor.setText(_allDecks.get(position).getDisplayName());
			if(summary.getMedia().getSource() != null && !summary.getMedia().getSource().equals(""))
			{
				if(summary.getMedia().getSource().contains("newsapi"))
				{
					if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
						tvMediaCopyRight.setText(Html.fromHtml(summary.getMedia().getSource(),Html.FROM_HTML_MODE_LEGACY));
					} else {
						tvMediaCopyRight.setText(Html.fromHtml(summary.getMedia().getSource()));
					}
				}
				else
					tvMediaCopyRight.setText(_context.getResources().getString(R.string.copyright_symbol) + " " + summary.getMedia().getSource());
			}
			if(summary.getSource()!= null && (summary.getSource().contains("http") || summary.getSource().contains(".png") || summary.getSource().contains(".jpg")))
			{
				Log.e("IMG", "SOURCE : "+summary.getSource());
				tvSource.setVisibility(View.GONE);
				llSourceImage.setVisibility(View.VISIBLE);
//				Glide.with(_context).load(summary.getSource()).into(ivSourceImage);
				ImageLoader.getInstance().displayImage(summary.getSource(), ivSourceImage);
			}
			else
			{
				tvSource.setVisibility(View.VISIBLE);
				llSourceImage.setVisibility(View.GONE);
				tvSource.setText("@"+summary.getSource());
			}
		}
	}

	/**
	 * Load article image.
	 * 
	 * @param view
	 *            the view
	 */
	private void loadArticleImage(View view, String imageUrl)
	{
		ImageView imageView = (ImageView) view.findViewById(R.id.imageViewArticleImage);
//		Glide.with(_context).load(imageUrl).into(imageView);
		ImageLoader.getInstance().displayImage(imageUrl, imageView);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bluoh.ui.PagerAdapter#destroyItem(android.view.ViewGroup,
	 * int, java.lang.Object)
	 */
	@Override
	public void destroyItem(ViewGroup container, int position, Object obj)
	{
//		AppUtil.LogMsg(TAG, "destroyItem++ position: " + position);
		container.removeView((View) obj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bluoh.ui.PagerAdapter#isViewFromObject(android.view.View,
	 * java.lang.Object)
	 */
	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return view == obj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bluoh.ui.PagerAdapter#getItemPosition(java.lang.Object)
	 */
	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	private Bundle getBundle(int position)
	{
		String data = new Gson().toJson(_allDecks.get(position).getCards().get(0), Card.class);
		Bundle bundle = new Bundle();
		bundle.putString("data", ""+data);
		bundle.putInt("deckId", _allDecks.get(position).getDeckId());
		return bundle;
	}


	private View initializeAdView(Ad ad)
	{
		LayoutInflater inflater = LayoutInflater.from(_context);
		LinearLayout parentView = (LinearLayout) inflater.inflate(R.layout.ad_container, null);
		LinearLayout nativeAdContainer = (LinearLayout)parentView.findViewById(R.id.native_ad_container);
		// Inflate the Ad view.  The layout referenced should be the one you created in the last step.
		LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.add_layout, nativeAdContainer, false);
		nativeAdContainer.addView(adView);

		// Create native UI using the ad metadata.
		ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.native_ad_icon);
		TextView nativeAdTitle = (TextView) adView.findViewById(R.id.native_ad_title);
		MediaView nativeAdMedia = (MediaView) adView.findViewById(R.id.native_ad_media);
		TextView nativeAdSocialContext = (TextView) adView.findViewById(R.id.native_ad_social_context);
		TextView nativeAdBody = (TextView) adView.findViewById(R.id.native_ad_body);
		Button nativeAdCallToAction = (Button) adView.findViewById(R.id.native_ad_call_to_action);

		// Set the Text.
		nativeAdTitle.setText(nativeAd.getAdTitle());
		nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
		nativeAdBody.setText(nativeAd.getAdBody());
		nativeAdCallToAction.setText(nativeAd.getAdCallToAction());

		// Download and display the ad icon.
		NativeAd.Image adIcon = nativeAd.getAdIcon();
		NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);

		// Download and display the cover image.
		nativeAdMedia.setNativeAd(nativeAd);

		// Add the AdChoices icon
		LinearLayout adChoicesContainer = (LinearLayout)adView.findViewById(R.id.ad_choices_container);
		AdChoicesView adChoicesView = new AdChoicesView(_context, nativeAd, true);
		adChoicesContainer.addView(adChoicesView);

		// Register the Title and CTA button to listen for clicks.
		List<View> clickableViews = new ArrayList<>();
		clickableViews.add(nativeAdTitle);
		clickableViews.add(nativeAdCallToAction);
		nativeAd.registerViewForInteraction(nativeAdContainer,clickableViews);
		return parentView;
	}



}
