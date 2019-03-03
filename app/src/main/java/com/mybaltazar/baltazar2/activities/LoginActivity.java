package com.mybaltazar.baltazar2.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.models.Student;
import com.mybaltazar.baltazar2.utils.DataListener;
import com.mybaltazar.baltazar2.webservices.CommonData;
import com.mybaltazar.baltazar2.webservices.DataResponse;
import com.mybaltazar.baltazar2.webservices.RetryableCallback;
import com.mybaltazar.baltazar2.webservices.Services;

import butterknife.BindView;
import butterknife.OnClick;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import retrofit2.Call;
import retrofit2.Response;

public class LoginActivity extends BaseActivity
{
    @NotEmpty(messageId = R.string.is_empty)
    @BindView(R.id.txtMobileNum)
    EditText txtMobileNum;

    @NotEmpty(messageId = R.string.is_empty)
    @BindView(R.id.txtMelliCode)
    EditText txtMelliCode;

    public LoginActivity()
    {
        super(R.layout.activity_login, true, false);
    }

    @OnClick(R.id.btnLogin)
    protected void btnLogin_Click()
    {
        if(!validateForm())
            return;

        final ProgressDialog progress = showProgress();
        String phone = txtMobileNum.getText().toString();
        String password = txtMelliCode.getText().toString();
        Call<DataResponse<Student>> response = createWebService(Services.class).login(phone, password);
        response.enqueue(new RetryableCallback<DataResponse<Student>>(this, progress)
        {
            @Override
            public void onFinalSuccess(DataResponse<Student> response)
            {
                setToken(response.data.token);
                setProfile(response.data);
                loadCommonData(true, new DataListener<CommonData>(LoginActivity.this) {
                    @Override
                    public void onCallBack(CommonData data) {
                        MainActivity.open(LoginActivity.this, data.notification, data.me.isTeacher);
                        finish();
                    }
                });
            }
        });

    }

    @OnClick(R.id.lblRegister)
    protected void lblRegister_Click()
    {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_item_contact_us:
                startActivity(new Intent(this, ContactUsActivity.class));
                return true;
        }
        return false;
    }
}
