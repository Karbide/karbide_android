package com.karbide.bluoh.presentation.components;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.karbide.bluoh.R;


public class CustomProgressDialog extends AlertDialog
{
	private TextView _tvLoadingText;
	String _progressMessage;
	ProgressBar progressBar;
	public CustomProgressDialog(Context context, String progressMessage)
	   {
			super(context);
			_progressMessage = progressMessage;
	   }
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_progress_dialog);
		progressBar = (ProgressBar)findViewById(R.id.imageView1);
		_tvLoadingText = (TextView)findViewById(R.id.tvLoadingText);
		_tvLoadingText.setText(_progressMessage);
		progressBar.getIndeterminateDrawable().setColorFilter(0xFF28387C, android.graphics.PorterDuff.Mode.MULTIPLY);

		setCanceledOnTouchOutside(false);
	}

	
	@Override
	public void setMessage(CharSequence message)
	{
		super.setMessage(message);
//		_tvLoadingText.setText(message);
	}
}