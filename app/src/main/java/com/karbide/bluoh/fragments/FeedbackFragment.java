package com.karbide.bluoh.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


public class FeedbackFragment extends BaseFragment implements View.OnClickListener {

    private Button btnTestNotification, btnSendFeedback;
    private EditText etEmail, etName, etNumber, etFeedback;
    private Spinner spFeedbackType;
    private String feedbackType = "";
    private String emailAddress="";
    private String name="";
    private String number="";
    private String feedback="";

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


    /**
     * Custom spinner created using attributes from feedback type array
     * @param view
     */
    private void initCustomSpinner(View view) {
        spFeedbackType= (Spinner)view.findViewById(R.id.spFeedbackType);
        // Spinner Drop down elements
        String [] complaintTypes = getResources().getStringArray(R.array.feedback_type);
        CustomSpinnerAdapter customSpinnerAdapter=new CustomSpinnerAdapter(getActivity(),complaintTypes);
        spFeedbackType.setAdapter(customSpinnerAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btnSendFeedback:
                if(isValidInput())
                {
                    FeedbackData feedbackData = new FeedbackData(feedback, emailAddress,name, number, feedbackType);
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
        name = etName.getText().toString().trim();
        number = etNumber.getText().toString().trim();
        emailAddress = etEmail.getText().toString().trim();
        feedback = etFeedback.getText().toString().trim();
        feedbackType = spFeedbackType.getSelectedItem().toString();

        Log.e("FEEDBACK TYPE SELECTED",feedbackType);

        if(name.isEmpty() || name.length() == 0 || name.equals("") || name == null) {
            AppUtil.showToast(getActivity(), getResources().getString(R.string.feedback_str_enter_name));
            return false;
        }

        if(number.isEmpty() || number.length() == 0 || number.equals("") || number == null) {
            AppUtil.showToast(getActivity(), getResources().getString(R.string.feedback_str_enter_number));
            return false;
        }

        if(emailAddress.isEmpty() || emailAddress.length() == 0 || emailAddress.equals("") || emailAddress == null) {
            AppUtil.showToast(getActivity(), getResources().getString(R.string.feedback_str_enter_email));
            return false;
        }else{
            // - @ missing
            if(!emailAddress.contains("@")){
                AppUtil.showToast(getActivity(), getResources().getString(R.string.feedback_str_enter_email));
                return false;
            }
        }

        if(feedback.isEmpty() || feedback.length() == 0 || feedback.equals("") || feedback == null) {
            AppUtil.showToast(getActivity(), getResources().getString(R.string.feedback_str_enter_feedback));
            return false;
        }

        return true;
    }

    public class CustomSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

        private final Context activity;
        private String [] complaintType;

        public CustomSpinnerAdapter(Context context,String args[]) {
            this.complaintType=args;
            activity = context;
        }

        public int getCount(){
            return complaintType.length;
        }

        public Object getItem(int i){
            return complaintType[i];
        }

        public long getItemId(int i){
            return (long)i;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(getActivity());
            txt.setPadding(20, 20, 20, 20);
            txt.setTextSize(18);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(complaintType[position]);
            txt.setTextColor(Color.parseColor("#000000"));
            return  txt;
        }

        public View getView(int i, View view, ViewGroup viewgroup) {
            TextView txt = new TextView(getActivity());
            txt.setGravity(Gravity.CENTER);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(16);
            txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
            txt.setText(complaintType[i]);
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