package com.mybaltazar.baltazar2.activities;

import android.app.ProgressDialog;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.models.ContactUsMessage;
import com.mybaltazar.baltazar2.webservices.CommonResponse;
import com.mybaltazar.baltazar2.webservices.RetryableCallback;
import com.mybaltazar.baltazar2.webservices.Services;

import butterknife.BindView;
import butterknife.OnClick;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import retrofit2.Call;

public class ContactUsActivity extends BaseActivity
{
    @NotEmpty(messageId = R.string.is_empty)
    @BindView(R.id.txtName)
    EditText txtName;

    @BindView(R.id.txtEmail)
    EditText txtEmail;

    @NotEmpty(messageId = R.string.is_empty)
    @BindView(R.id.txtMessage)
    EditText txtMessage;

    public ContactUsActivity() {
        super(R.layout.activity_contact_us, true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @OnClick(R.id.btnSend)
    protected void btnSend_Click()
    {
        if(!validateForm())
            return;
        ContactUsMessage msg = new ContactUsMessage();
        msg.email = txtEmail.getText().toString();
        msg.name = txtName.getText().toString();
        msg.message = txtMessage.getText().toString();

        ProgressDialog progress = showProgress();
        Call<CommonResponse> call = createWebService(Services.class).submitContactUsMessage(getToken(), msg);
        call.enqueue(new RetryableCallback<CommonResponse>(this, progress) {
            @Override
            public void onFinalSuccess(CommonResponse response) {
                if(response.message != null)
                    Toast.makeText(ContactUsActivity.this, response.message, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
