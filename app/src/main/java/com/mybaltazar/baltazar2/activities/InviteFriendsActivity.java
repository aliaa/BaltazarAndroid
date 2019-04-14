package com.mybaltazar.baltazar2.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.models.Student;

import butterknife.BindView;
import butterknife.OnClick;

public class InviteFriendsActivity extends BaseActivity
{
    @BindView(R.id.btnInviteCode)   Button btnInviteCode;

    public InviteFriendsActivity() {
        super(R.layout.activity_invite_friends, false, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Student profile = getProfile();
        btnInviteCode.setText(profile.invitationCode);
    }

    @OnClick(R.id.btnInviteCode)
    protected void btnInviteCode_Click()
    {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("inviteCode", btnInviteCode.getText());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, R.string.invitation_copied, Toast.LENGTH_LONG).show();
    }

    private String getMessage()
    {
        return getString(R.string.invitation_share_message) + btnInviteCode.getText();
    }

    @OnClick(R.id.btnShareBySMS)
    protected void btnShareBySMS_Click()
    {
        Uri sms_uri = Uri.parse("smsto:");
        Intent intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("sms_body", getMessage());
        startActivity(intent);
    }

    @OnClick(R.id.btnShareByEmail)
    protected void btnShareByEmail_Click()
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.invite_email_subject));
        intent.putExtra(Intent.EXTRA_TEXT, getMessage());
        startActivity(Intent.createChooser(intent, getString(R.string.send_email)));
    }

    @OnClick(R.id.btnShareByTelegram)
    protected void btnShareByTelegram_Click()
    {
        final String appName = "org.telegram.messenger";
        final boolean isAppInstalled = isAppAvailable(getApplicationContext(), appName);
        if (isAppInstalled)
        {
            Intent myIntent = new Intent(Intent.ACTION_SEND);
            myIntent.setType("text/plain");
            myIntent.setPackage(appName);
            myIntent.putExtra(Intent.EXTRA_TEXT, getMessage());
            startActivity(Intent.createChooser(myIntent, "Share with"));
        }
        else
        {
            Toast.makeText(this, R.string.telegram_not_installed, Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isAppAvailable(Context context, String appName)
    {
        PackageManager pm = context.getPackageManager();
        try
        {
            pm.getPackageInfo(appName, PackageManager.GET_ACTIVITIES);
            return true;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }

    @OnClick(R.id.btnShareGeneral)
    protected void btnShareGeneral_Click()
    {
        Intent i = new Intent(android.content.Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(android.content.Intent.EXTRA_TEXT, getMessage());
        startActivity(Intent.createChooser(i, getString(R.string.share_using)));
    }
}
