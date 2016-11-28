package com.karbide.bluoh.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.karbide.bluoh.R;
import com.karbide.bluoh.datatypes.FeedbackData;
import com.karbide.bluoh.util.AppConstants;
import com.karbide.bluoh.util.AppSharedPreference;
import com.karbide.bluoh.util.AppUtil;
import com.karbide.bluoh.util.HttpUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


public class InviteFragment extends BaseFragment implements View.OnClickListener {

    private Button btnTestNotification, btnSendFeedback;
    private EditText etEmail, etName, etNumber, etFeedback;
    private Spinner spFeedbackType;
    private String feedbackType = "", emailAddress="", name="", number="", feedback="";
    @Override
    public void onResume()
    {
        super.onResume();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_invite,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initCustomSpinner(view);
        etEmail = (EditText)view.findViewById(R.id.etEmail);
        etName = (EditText)view.findViewById(R.id.etName);
        etNumber = (EditText)view.findViewById(R.id.etNumber);
        etFeedback = (EditText)view.findViewById(R.id.etFeedback);

        btnSendFeedback = (Button)view.findViewById(R.id.btnSendFeedback);
        btnTestNotification = (Button)view.findViewById(R.id.btnTestNotification);
        btnSendFeedback.setOnClickListener(this);
        btnTestNotification.setOnClickListener(this);
    }


    private void initCustomSpinner(View view) {

        spFeedbackType= (Spinner)view.findViewById(R.id.spFeedbackType);
        // Spinner Drop down elements
        ArrayList<String> languages = new ArrayList<String>();
        languages.add("Query");
        languages.add("Complain type");
        languages.add("Suggestion");
        CustomSpinnerAdapter customSpinnerAdapter=new CustomSpinnerAdapter(getActivity(),languages);
        spFeedbackType.setAdapter(customSpinnerAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btnSendFeedback:
                if(isValidInput())
                {
                    FeedbackData feedbackData = new FeedbackData(feedback, emailAddress, name, (number.equals(""))?0:Integer.valueOf(number), feedbackType);
                    try {
                        sendFeedback(feedbackData);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btnTestNotification:
                testNotification();
                break;
        }
    }
    private void sendFeedback(FeedbackData feedback) throws UnsupportedEncodingException
    {
        showProgressDialog(R.string.please_wait);
        AppUtil.LogMsg("RESPONSE", "TRAFIC JSON"+new Gson().toJson(feedback));
        StringEntity entity = new StringEntity(new Gson().toJson(feedback));
        HttpUtils.postWithJson(getActivity(), AppConstants.FEEDBACK, entity,new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                hideProgressDialog();
                try
                {
                    String str = new String(responseBody, "utf-8");
                    AppUtil.LogMsg("RESPONSE", "RESPONSE  ERROR"+statusCode+str);
                    if(statusCode == 200)
                    {
                        clearValue();
                        AppUtil.showToast(getActivity(), "Feedback sent");
                    }
                } catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                error.printStackTrace();
                hideProgressDialog();
                try
                {
                    if(error!= null)
                    {
                        AppUtil.showToast(getActivity(), "STATUS CODE"+statusCode+error.getMessage()+error.getLocalizedMessage());
                    }
                    else
                    {
                        AppUtil.LogMsg("RESPONSE", "RESPONSE  ERROR" + statusCode + error.getMessage());
                        String str = new String(responseBody, "utf-8");
                        AppUtil.LogMsg("RESPONSE", "RESPONSE  ERROR" + statusCode + str);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void testNotification()
    {
        if(!AppSharedPreference.getInstance(getActivity()).getGcmId().equals(""))
        {
            HttpUtils.testNotification(getActivity(), new AsyncHttpResponseHandler()
            {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
                {
                    try
                    {
                        String str = new String(responseBody, "utf-8");
                        AppUtil.showToast(getActivity(), str);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    AppUtil.showToast(getActivity(), "Some Error");
                }
            });
        }
    }

    private boolean isValidInput()
    {
        feedback = spFeedbackType.getSelectedItem().toString();
        name = etName.getText().toString();
        number = etNumber.getText().toString();
        emailAddress = etEmail.getText().toString();
        feedback = etFeedback.getText().toString();
        if(name.equals(""))
        {
            AppUtil.showToast(getActivity(), "Please enter your name");
            return false;
        }
        else if(emailAddress.equals(""))
        {
            AppUtil.showToast(getActivity(), "Please enter your email");
            return false;
        }
        else if(feedback.equals(""))
        {
            AppUtil.showToast(getActivity(), "Please write something in feedback box");
            return false;
        }
        else
            return true;
    }

    public class CustomSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

        private final Context activity;
        private ArrayList<String> asr;

        public CustomSpinnerAdapter(Context context,ArrayList<String> asr) {
            this.asr=asr;
            activity = context;
        }



        public int getCount()
        {
            return asr.size();
        }

        public Object getItem(int i)
        {
            return asr.get(i);
        }

        public long getItemId(int i)
        {
            return (long)i;
        }



        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(getActivity());
            txt.setPadding(20, 20, 20, 20);
            txt.setTextSize(18);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(asr.get(position));
            txt.setTextColor(Color.parseColor("#000000"));
            return  txt;
        }

        public View getView(int i, View view, ViewGroup viewgroup) {
            TextView txt = new TextView(getActivity());
            txt.setGravity(Gravity.CENTER);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(16);
            txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
            txt.setText(asr.get(i));
            txt.setTextColor(Color.parseColor("#000000"));
            return  txt;
        }

    }

    private void clearValue()
    {
        etNumber.setText("");
        etName.setText("");
        etEmail.setText("");
        etFeedback.setText("");
    }
}