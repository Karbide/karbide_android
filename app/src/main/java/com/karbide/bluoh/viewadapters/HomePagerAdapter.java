package com.karbide.bluoh.viewadapters;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.karbide.bluoh.DeckDetailActivity;
import com.karbide.bluoh.R;
import com.karbide.bluoh.database.AppDatabaseHelper;
import com.karbide.bluoh.datatypes.Card;
import com.karbide.bluoh.datatypes.Content;
import com.karbide.bluoh.ui.CustomTextView;
import com.karbide.bluoh.ui.PagerAdapter;
import com.karbide.bluoh.util.AppUtil;

import java.util.ArrayList;

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
	private String adPlacementId = "893127754073705_909205119132635";
	
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
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bluoh.ui.PagerAdapter#getCount()
	 */
	@Override
	public int getCount() {
//		AppUtil.LogMsg(TAG, "Item++ count: " + _allDecks.size());
		if (null != _allDecks) {
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
//		AppUtil.LogMsg(TAG, "instantiateItem++ position: " + position);
		View view = null;
		if(_allDecks.get(position).getType()!= null && _allDecks.get(position).getType().equalsIgnoreCase("deck"))
		{
			view = initializeLandscapeImageLayoutDeck(position);
		}
		else
		{
			if(_allDecks.get(position).getCards().get(0).getTemplate().equalsIgnoreCase("Full"))
				view = initializePortraitImageLayout(position);
			else if(_allDecks.get(position).getCards().get(0).getTemplate().equalsIgnoreCase("70_30"))
				view = initializeSeventyTirtyLayout(position);
			else
				view = initializeLandscapeImageLayout(position);
		}
		if (null != view) {
//			AppUtil.LogError("BLUOH ", " VIEW IS NOT NULL");
			container.addView(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
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
			CustomTextView tvAuthor = (CustomTextView) view.findViewById(R.id.tvAuthorName);
			CustomTextView tvMediaCopyRight = (CustomTextView) view.findViewById(R.id.tvMediaCopyRight);

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
					public void onClick(View view) {
//						AppUtil.openUrlInWeb(_context,summary.getUrl());
						AppUtil.openNativeWebView(_context, summary.getUrl());
					}
				});
			}
			view.setOnClickListener(null);
			tvHeadline.setText(summary.getTitle());
			tvSummary.setText(summary.getContent());
			if(summary.getSource() != null)
				tvSource.setText("@"+summary.getSource());
			else
				tvSource.setText("@rediff.com");
			tvAuthor.setText(_allDecks.get(position).getDisplayName());
			if(summary.getMedia().getSource() != null && !summary.getMedia().getSource().equals(""))
				tvMediaCopyRight.setText(_context.getResources().getString(R.string.copyright_symbol)+" "+summary.getMedia().getSource());
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
						AppUtil.openNativeWebView(_context, summary.getUrl());
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
			tvHeadline.setText(summary.getTitle());
			tvAuthor.setText(_allDecks.get(position).getDisplayName());
			if(summary.getMedia().getSource() != null && !summary.getMedia().getSource().equals(""))
				tvMediaCopyRight.setText(_context.getResources().getString(R.string.copyright_symbol)+" "+summary.getMedia().getSource());
			if(summary.getSource() != null)
				tvSource.setText("@"+summary.getSource());
			else
				tvSource.setText("@rediff.com");
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
		Glide.with(_context).load(imageUrl).into(imageView);
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


}
