package com.karbide.bluoh.viewadapters;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.karbide.bluoh.DeckDetailActivity;
import com.karbide.bluoh.R;
import com.karbide.bluoh.database.AppDatabaseHelper;
import com.karbide.bluoh.datatypes.Card;
import com.karbide.bluoh.datatypes.Content;
import com.karbide.bluoh.datatypes.Deck;
import com.karbide.bluoh.datatypes.DeckDetailResponse;
import com.karbide.bluoh.ui.CustomTextView;
import com.karbide.bluoh.ui.PagerAdapter;
import com.karbide.bluoh.util.AppUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

// TODO: Auto-generated Javadoc

/**
 * The Class VerticalPagerAdapter.
 */
public class DeckVerticalPagerAdapter extends PagerAdapter
{
	/** The Constant TAG. */
	private static final String TAG = DeckVerticalPagerAdapter.class.getSimpleName();
	private List<Card> _allDecks;
	private DeckDetailResponse response;
	/** The _context. */
	private Context _context = null;
	/** The _on click listener. */
	private OnClickListener _onClickListener = null;

	private Content _content;
	/**
	 * Instantiates a new vertical pager adapter.
	 *
	 * @param context the context
	 * @paramarticleSummaryList the article summary list
	 * @param onClickListener the on click listener
	 */
	public DeckVerticalPagerAdapter(Context context, OnClickListener onClickListener, DeckDetailResponse allDecks, Content parentContent) {
		_context = context;
		_onClickListener = onClickListener;
		_content = parentContent;
		response = allDecks;
		_allDecks = allDecks.getCards();
		_allDecks.remove(0);
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bluoh.ui.PagerAdapter#getCount()
	 */
	@Override
	public int getCount() {
		AppUtil.LogMsg(TAG, "Item++ count: " + _allDecks.size());
		if (null != _allDecks) {
			return _allDecks.size()+2;
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
		AppUtil.LogMsg(TAG, "instantiateItem++ position: " + position);
		View view = null;
		if(position == _allDecks.size())
		{
			view = initializeDeckCompleteLayout();
		}
		else if(position == _allDecks.size()+1)
		{

		}
		else {
			if (_allDecks.get(position).getTemplate().equalsIgnoreCase("Full"))
				view = initializePortraitImageLayout(position);
			else if(_allDecks.get(position).getTemplate().equalsIgnoreCase("70_30"))
				view = initializeSeventyTirtyLayout(position);
			else
				view = initializeLandscapeImageLayout(position);
		}
		if (null != view) {
			AppUtil.LogError("BLUOH ", " VIEW IS NOT NULL");
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
		setCommonData(position, view, _allDecks.get(position));
		loadArticleImage(view, _allDecks.get(position).getMedia().getUrl());
		return view;
	}

	private View initializeSeventyTirtyLayout(int position) {
		View view = LayoutInflater.from(_context).inflate(R.layout.layout_seventy_thirty, null);
		setCommonData(position, view, _allDecks.get(position));
		loadArticleImage(view, _allDecks.get(position).getMedia().getUrl());
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
		setCommonData(position, view, _allDecks.get(position));
		loadArticleImage(view, _allDecks.get(position).getMedia().getUrl());
		return view;
	}

	private View initializeDeckCompleteLayout() {
		View view = LayoutInflater.from(_context).inflate(R.layout.layout_deck_completed, null);
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

				buttonBookmark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
				{
					@Override
					public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
					{
						if (isChecked)
							AppDatabaseHelper.getInstance(_context).addBookMark(_content, null);
					}
				});

				buttonLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

					}
				});
				tvSource.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						AppUtil.openNativeWebView(_context, summary.getUrl());
//						AppUtil.openUrlInWeb(_context,summary.getUrl());
					}
				});
			}


			tvHeadline.setText(summary.getTitle());
			tvSummary.setText(summary.getContent());
			if(summary.getSource() != null)
				tvSource.setText("@"+summary.getSource());
			else
				tvSource.setText("@rediff.com");
			tvAuthor.setText(response.getAuthor());
			if(summary.getMedia().getSource() != null && !summary.getMedia().getSource().equals(""))
				tvMediaCopyRight.setText(_context.getResources().getString(R.string.copyright_symbol)+" "+summary.getMedia().getSource());

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

	@Override
	public void destroyItem(ViewGroup container, int position, Object obj) {
		AppUtil.LogMsg(TAG, "destroyItem++ position: " + position);
		container.removeView((View) obj);
	}

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return view == obj;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}


}