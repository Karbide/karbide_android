package com.karbide.bluoh.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.karbide.bluoh.R;
import com.karbide.bluoh.util.AppConstants;
import com.karbide.bluoh.util.AppUtil;

import java.util.ArrayList;
import java.util.List;

public class ShareFragment extends BaseFragment
{
    private static String TAG = ShareFragment.class.getSimpleName();
    private List<Integer> mAppsIcons;
    private List<String> mAppsName;
    public ShareDialog mShareDialog;
    private GridView shareAppsGrid;
    private ScrollView parentView;

    public ShareFragment()
    {

    }

    private ArrayList<ResolveInfo> queryShareIntent()
    {
        Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        ArrayList<ResolveInfo> appList = new ArrayList<>();
        List activities = getActivity().getPackageManager().queryIntentActivities(sendIntent, 0);
        for(int i=0; i<activities.size(); i++)
        {
            String packageName =((ResolveInfo) activities.get(i)).activityInfo.applicationInfo
                    .packageName.toString();
            if(packageName.equals("com.whatsapp") || packageName.equals("com.twitter.android")
                    || packageName.equals("com.facebook.orca") || packageName.equals("com.facebook.katana")
                    || packageName.equals("com.google.android.talk") || packageName.equals("com.google.android.apps.plus")
                    || packageName.equals("com.google.android.apps.messaging"))
            {
                if(appList!= null && appList.size()>0)
                {
                    boolean addToList = true;
                    for(int j=0; j<appList.size(); j++)
                    {
                        String addedpackageName =(appList.get(j)).activityInfo.applicationInfo
                                .packageName.toString();
                        if(addedpackageName.equals(packageName))
                        {
                            addToList = false;
                            AppUtil.LogMsg(" NAME"," PACKAGE:- "+addedpackageName);
                            break;
                        }
                    }
                    if (addToList)
                        appList.add((ResolveInfo) activities.get(i));
                }
                else {
                    appList.add((ResolveInfo) activities.get(i));
                }
            }
        }
        return appList;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        menu.clear();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View layout = inflater.inflate(R.layout.fragment_referal, container, false);
        initView(layout);
        setHasOptionsMenu(false);
        this.mShareDialog = new ShareDialog(getActivity());
        final ShareAdapter adapter=new ShareAdapter(getActivity(), queryShareIntent());
        this.shareAppsGrid.setAdapter(adapter);
        return layout;
    }

    private void initView(View view)
    {
        parentView = (ScrollView)view.findViewById(R.id.parentView);
        shareAppsGrid = (GridView)view.findViewById(R.id.shareApps);
    }


    @Override
    public void onResume()
    {
        super.onResume();
    }

    public class ShareAdapter extends BaseAdapter {
        ArrayList<ResolveInfo> items;
        private LayoutInflater mInflater;
        Context context;

        public ShareAdapter(Context context, ArrayList<ResolveInfo> items) {
            this.mInflater = LayoutInflater.from(context);
            this.items = items;
            this.context = context;
        }

        public int getCount() {
            return items.size()+1;
        }

        public ResolveInfo getItem(int position) {
            return items.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_share, null);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.shareAppName);
                holder.logo = (ImageView) convertView.findViewById(R.id.shareAppIcon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if(position==items.size())
            {
                holder.name.setText("More");
                holder.logo.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle_more));
            }
            else {
                holder.name.setText(items.get(position).activityInfo.applicationInfo
                                .loadLabel(getActivity().getPackageManager()).toString());
                holder.logo.setImageDrawable(items.get(position).activityInfo.applicationInfo
                                .loadIcon(getActivity().getPackageManager()));
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    if(position == items.size())
                    {
                        AppUtil.shareData(getActivity(), getResources().getString(R.string.invite_text)+"\n"+AppConstants.BLUOH_PROFILE_PLAYSTORE_WEB_URL);
                    }
                    else {
                        if (items.get(position).activityInfo.packageName.contains("facebook")) {
                            if (ShareDialog.canShow(ShareLinkContent.class)) {
                                mShareDialog.show(((ShareLinkContent.Builder) new ShareLinkContent.Builder().setContentTitle("Try Bluoh").setContentDescription(getResources().getString(R.string.invite_text)).setContentUrl(Uri.parse(AppConstants.BLUOH_PROFILE_PLAYSTORE_WEB_URL))).build());
                            }
//                        new PostToFacebookDialog(context, body).show();
                            //here u can write your own code to handle the particular social media networking apps.
                            Toast.makeText(getActivity(), "FaceBook", Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                            intent.setClassName(items.get(position).activityInfo.packageName, items.get(position).activityInfo.name);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_SUBJECT, "BLUOH");
                            intent.putExtra(Intent.EXTRA_TEXT, ""+getResources().getString(R.string.invite_text)+"\n "+AppConstants.BLUOH_PROFILE_PLAYSTORE_WEB_URL);
                            getActivity().startActivity(intent);
                        }
                    }
                }
            });
            return convertView;
        }

        class ViewHolder
        {
            TextView name;
            ImageView logo;
        }}
}
