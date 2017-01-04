package com.karbide.bluoh.presentation.viewadapters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build.VERSION;
import android.provider.Telephony.Sms;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.BuildConfig;
import com.facebook.CallbackManager;
import com.facebook.CallbackManager.Factory;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareLinkContent.Builder;
import com.facebook.share.widget.ShareDialog;
import com.karbide.bluoh.R;

import java.util.ArrayList;
import java.util.List;

public class ShareAppsAdapter extends BaseAdapter {
    private static final String EXTRA_APP_ID = "com.facebook.orca.extra.APPLICATION_ID";
    private static final String EXTRA_PROTOCOL_VERSION = "com.facebook.orca.extra.PROTOCOL_VERSION";
    private static final int PROTOCOL_VERSION = 20150314;
    private static final int SHARE_TO_MESSENGER_REQUEST_CODE = 1;
    private static LayoutInflater inflater;
    private String FACEBOOK_APP_ID;
    CallbackManager callbackManager;
    Context context;
    String default_msg;
    String email_body;
    String email_subject;
    String fb_messenger;
    List<Integer> imageId;
    List<String> result;
    ShareDialog shareDialog;
    String str_email;
    String str_fb;
    String str_more;
    String str_sms;
    String str_twitter;
    String str_whatsapp;
    String twitter;

    class ClickListener implements OnClickListener {
        final int position;

        ClickListener(int i) {
            this.position = i;
        }

        public void onClick(View v) {
            if (((String) ShareAppsAdapter.this.result.get(this.position)).equalsIgnoreCase("whatsapp")) {
                ShareAppsAdapter.this.shareOnWhatsApp();
            } else if (((String) ShareAppsAdapter.this.result.get(this.position)).equalsIgnoreCase("messaging")) {
                ShareAppsAdapter.this.shareOnMessaging();
            } else if (((String) ShareAppsAdapter.this.result.get(this.position)).equalsIgnoreCase("email")) {
                ShareAppsAdapter.this.shareOnEmail();
            } else if (((String) ShareAppsAdapter.this.result.get(this.position)).equalsIgnoreCase("facebook")) {
                ShareAppsAdapter.this.shareOnFacebook();
            } else if (((String) ShareAppsAdapter.this.result.get(this.position)).equalsIgnoreCase("twitter")) {
                ShareAppsAdapter.this.shareOnTwitter();
            } else {
                ShareAppsAdapter.this.shareIntent();
            }
        }
    }

    public class ViewHolder {
        ImageView shareAppIcon;
        TextView shareAppName;
    }

    static {
        inflater = null;
    }

    public ShareAppsAdapter(Context contextMain, List<String> prgmNameList, List<Integer> prgmImages, ShareDialog shareDialog, String default_msg, String fb_messenger, String email_body, String email_subject, String twitter) {
        this.result = prgmNameList;
        this.context = contextMain;
        this.imageId = prgmImages;
        this.default_msg = default_msg;
        this.twitter = twitter;
        this.fb_messenger = fb_messenger;
        this.email_body = email_body;
        this.email_subject = email_subject;
        this.shareDialog = shareDialog;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.FACEBOOK_APP_ID = "";//this.context.getString(R.string.facebook_app_id);
        FacebookSdk.sdkInitialize(this.context);
        this.callbackManager = Factory.create();
    }

    public int getCount() {
        return this.result.size();
    }

    public Object getItem(int position) {
        return Integer.valueOf(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        this.str_fb = this.context.getResources().getString(R.string.refer_facebook);
        this.str_email = this.context.getResources().getString(R.string.refer_email);
        this.str_more = this.context.getResources().getString(R.string.refer_more);
        this.str_sms = this.context.getResources().getString(R.string.refer_message);
        this.str_twitter = this.context.getResources().getString(R.string.refer_twitter);
        this.str_whatsapp = this.context.getResources().getString(R.string.refer_whatsapp);
        View rowView = inflater.inflate(R.layout.item_share, null);
        holder.shareAppName = (TextView) rowView.findViewById(R.id.shareAppName);
        holder.shareAppIcon = (ImageView) rowView.findViewById(R.id.shareAppIcon);
        holder.shareAppName.setText((CharSequence) this.result.get(position));
        holder.shareAppIcon.setImageResource(this.imageId.get(position));
//        Picasso.with(this.context).load(((Integer) this.imageId.get(position)).intValue()).into(holder.shareAppIcon);
        rowView.setOnClickListener(new ClickListener(position));
        return rowView;
    }

    public void shareOnWhatsApp() {
        try {
            PackageInfo info = this.context.getPackageManager().getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            Intent sendIntent = new Intent();
            sendIntent.setAction("android.intent.action.SEND");
            sendIntent.setPackage("com.whatsapp");
            sendIntent.putExtra("android.intent.extra.TEXT", this.default_msg);
            sendIntent.setType("text/plain");
            this.context.startActivity(sendIntent);
        } catch (Exception e) {
            Toast.makeText(this.context, "Please install WhatsApp", Toast.LENGTH_LONG).show();
        }
    }

    public void shareOnMessaging() {
        try {
            Intent sendIntent;
            if (VERSION.SDK_INT >= 19) {
                String defaultSmsPackageName = Sms.getDefaultSmsPackage(this.context);
                sendIntent = new Intent("android.intent.action.SEND");
                sendIntent.setType("text/plain");
                sendIntent.putExtra("android.intent.extra.TEXT", this.default_msg);
                if (defaultSmsPackageName != null) {
                    sendIntent.setPackage(defaultSmsPackageName);
                }
                this.context.startActivity(sendIntent);
            } else {
                sendIntent = new Intent("android.intent.action.VIEW");
                sendIntent.setData(Uri.parse("sms:"));
                sendIntent.putExtra("sms_body", this.default_msg);
                this.context.startActivity(sendIntent);
            }
        } catch (Exception e) {
            Toast.makeText(this.context, "Messaging client not found!", Toast.LENGTH_LONG).show();
        }
    }

    public void shareOnEmail() {
        try {
            Resources resources = this.context.getResources();
            PackageManager pm = this.context.getPackageManager();
            new Intent("android.intent.action.SEND").setType("text/plain");
            Intent emailIntent = new Intent();
            emailIntent.setAction("android.intent.action.VIEW");
            emailIntent.putExtra("android.intent.extra.TEXT", this.email_body);
            emailIntent.putExtra("android.intent.extra.SUBJECT", this.email_subject);
            emailIntent.setData(Uri.parse("mailto:"));
            this.context.startActivity(Intent.createChooser(emailIntent, "Choose an email client"));
        } catch (Exception e) {
            Toast.makeText(this.context, "Please install an email client", Toast.LENGTH_LONG).show();
        }
    }

    public void shareOnFacebook() {
        String fbLink = BuildConfig.FLAVOR;
        if (!this.fb_messenger.equals(BuildConfig.FLAVOR)) {
            String[] fbMessageArray = this.fb_messenger.split(" ");
            int length = fbMessageArray.length;
            for (int i = 0; i < length; i += SHARE_TO_MESSENGER_REQUEST_CODE) {
                String s = fbMessageArray[i];
                if (s.startsWith("http://")) {
                    fbLink = s;
                }
            }
        }
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            this.shareDialog.show(((Builder) new Builder().setContentTitle("Try DriveU").setContentDescription(this.fb_messenger).setContentUrl(Uri.parse(fbLink))).build());
        }
    }

    public void shareOnTwitter() {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction("android.intent.action.SEND");
            sendIntent.setPackage("com.twitter.android");
            sendIntent.putExtra("android.intent.extra.TEXT", this.twitter);
            sendIntent.setType("text/plain");
            this.context.startActivity(sendIntent);
        } catch (Exception e) {
            Toast.makeText(this.context, "Please install Twitter", Toast.LENGTH_LONG).show();
        }
    }

    public void shareIntent() {
        Resources resources = this.context.getResources();
        Intent emailIntent = new Intent();
        emailIntent.setAction("android.intent.action.SEND");
        String str = "android.intent.extra.TEXT";
        emailIntent.putExtra("r17", this.email_body);
        str = "android.intent.extra.SUBJECT";
        emailIntent.putExtra("r17", this.email_subject);
        emailIntent.setType("message/rfc822");
        PackageManager pm = this.context.getPackageManager();
        Intent sendIntent = new Intent("android.intent.action.SEND");
        sendIntent.setType("text/plain");
        Intent openInChooser = Intent.createChooser(emailIntent, "Choose an app");
        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList();
        for (int i = 0; i < resInfo.size(); i += SHARE_TO_MESSENGER_REQUEST_CODE) {
            boolean toAdd = true;
            ResolveInfo ri = (ResolveInfo) resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if (packageName.contains("android.email")) {
                str = "Channel";
                emailIntent.setPackage(packageName);
            } else {
                if (!packageName.contains("twitter")) {
                    if (!packageName.contains("facebook")) {
                        if (!packageName.contains("mms")) {
                            if (!packageName.contains("android.gm")) {
                            }
                        }
                    }
                }
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction("android.intent.action.SEND");
                intent.setType("text/plain");
                if (packageName.contains("twitter")) {
                    toAdd = false;
                    str = "Channel";
                    str = "android.intent.extra.TEXT";
                    intent.putExtra("r17", this.twitter);
                } else {
                    if (packageName.contains("facebook.katana")) {
                        toAdd = false;
                        str = "Channel";
                        str = "android.intent.extra.TEXT";
                        intent.putExtra("r17", this.fb_messenger);
                    } else {
                        if (packageName.contains("facebook.orca")) {
                            str = "Channel";
                            str = "android.intent.extra.TEXT";
                            intent.putExtra("r17", this.fb_messenger);
                            intent.putExtra(EXTRA_PROTOCOL_VERSION, PROTOCOL_VERSION);
                            intent.putExtra(EXTRA_APP_ID, this.FACEBOOK_APP_ID);
                        } else {
                            if (packageName.contains("mms")) {
                                toAdd = false;
                                str = "Channel";
                                str = "android.intent.extra.TEXT";
                                intent.putExtra("r17", this.default_msg);
                            } else {
                                if (packageName.contains("android.gm")) {
                                    toAdd = false;
                                    str = "android.intent.extra.TEXT";
                                    intent.putExtra("r17", this.email_body);
                                    str = "android.intent.extra.SUBJECT";
                                    intent.putExtra("r17", this.email_subject);
                                    intent.setType("message/rfc822");
                                    str = "Channel";
                                } else {
                                    if (packageName.contains("com.whatsapp")) {
                                        toAdd = false;
                                        str = "android.intent.extra.TEXT";
                                        intent.putExtra("r17", this.default_msg);
                                        str = "Channel";
                                    }
                                }
                            }
                        }
                    }
                }
                if (toAdd) {
                    intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
                }
            }
        }
        str = "android.intent.extra.INITIAL_INTENTS";
        openInChooser.putExtra(str, (LabeledIntent[]) intentList.toArray(new LabeledIntent[intentList.size()]));
        this.context.startActivity(openInChooser);
        str = "Channel";
    }
}
