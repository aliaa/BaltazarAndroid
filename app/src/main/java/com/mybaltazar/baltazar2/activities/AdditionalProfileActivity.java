package com.mybaltazar.baltazar2.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.models.BaseEntity;
import com.mybaltazar.baltazar2.models.City;
import com.mybaltazar.baltazar2.models.Province;
import com.mybaltazar.baltazar2.models.Student;
import com.mybaltazar.baltazar2.utils.DataListener;
import com.mybaltazar.baltazar2.utils.JalaliCalendar;
import com.mybaltazar.baltazar2.utils.StringUtils;
import com.mybaltazar.baltazar2.webservices.CommonData;
import com.mybaltazar.baltazar2.webservices.DataResponse;
import com.mybaltazar.baltazar2.webservices.RetryableCallback;
import com.mybaltazar.baltazar2.webservices.Services;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import eu.inmite.android.lib.validations.form.annotations.MaxValue;
import eu.inmite.android.lib.validations.form.annotations.MinValue;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import retrofit2.Call;
import retrofit2.Response;

public class AdditionalProfileActivity extends BaseActivity
{
    @NotEmpty(messageId = R.string.is_empty)
    @MinValue(value = 1300, messageId = R.string.value_incorrect)
    @MaxValue(value = 1410, messageId = R.string.value_incorrect)
    @BindView(R.id.txtBirthYear)
    EditText txtBirthYear;

    @NotEmpty(messageId = R.string.is_empty)
    @MinValue(value = 1, messageId = R.string.value_incorrect)
    @MaxValue(value = 12, messageId = R.string.value_incorrect)
    @BindView(R.id.txtBirthMonth)
    EditText txtBirthMonth;

    @NotEmpty(messageId = R.string.is_empty)
    @MinValue(value = 1, messageId = R.string.value_incorrect)
    @MaxValue(value = 31, messageId = R.string.value_incorrect)
    @BindView(R.id.txtBirthDay)
    EditText txtBirthDay;

    @BindView(R.id.spinnerProvince) Spinner spinnerProvince;
    @BindView(R.id.spinnerCity)     Spinner spinnerCity;
    @BindView(R.id.spinnerGender)   Spinner spinnerGender;

    @NotEmpty(messageId = R.string.is_empty)
    @BindView(R.id.txtSchoolName)
    EditText txtSchoolName;

    @NotEmpty(messageId = R.string.is_empty)
    @BindView(R.id.txtSchoolPhone)
    EditText txtSchoolPhone;

    @NotEmpty(messageId = R.string.is_empty)
    @BindView(R.id.txtSchoolAddress)
    EditText txtSchoolAddress;

    private CommonData commonData;
    private Student profile;

    private String[] GENDERS;

    public AdditionalProfileActivity() {
        super(R.layout.activity_additional_profile, true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GENDERS = new String[] { getString(R.string.boy), getString(R.string.girl) };
    }

    @Override
    protected void onStart() {
        super.onStart();

        final ProgressDialog progress = showProgress();
        loadCommonData(false, new DataListener<CommonData>() {
            @Override
            public void onCallBack(CommonData data) {
                profile = loadCache(BaseActivity.PREF_PROFILE, Student.class);
                commonData = data;
                loadUI();
                progress.dismiss();
            }

            @Override
            public void onFailure() {
                progress.dismiss();
                Toast.makeText(AdditionalProfileActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadUI()
    {
        spinnerProvince.setAdapter(new ArrayAdapter<Province>(this,
                android.R.layout.simple_spinner_dropdown_item,
                commonData.provinces));
        if(profile.cityId != null && !profile.cityId.equals("") && !profile.cityId.equals(BaseEntity.EMPTY_ID))
        {
            City city = null;
            for(City c : commonData.cities) {
                if (c.id.equals(profile.cityId)) {
                    city = c;
                    break;
                }
            }

            if(city != null) {
                int provinceIndex = 0;
                for (; provinceIndex < commonData.provinces.size(); provinceIndex++) {
                    if (commonData.provinces.get(provinceIndex).id.equals(city.provinceId))
                        break;
                }
                spinnerProvince.setSelection(provinceIndex);
            }
        }

        spinnerGender.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, GENDERS));
        switch (profile.gender) {
            case Male:
                spinnerGender.setSelection(0);
                break;
            case Female:
                spinnerGender.setSelection(1);
                break;
            default:
                break;
        }

        if(profile.birthDate != null && !profile.birthDate.equals("")) {
            JalaliCalendar.YearMonthDate date = StringUtils.getPersianDate(profile.birthDate);
            if(date != null && date.getYear() > 0) {
                txtBirthYear.setText(String.valueOf(date.getYear()));
                txtBirthMonth.setText(String.valueOf(date.getMonth()+1));
                txtBirthDay.setText(String.valueOf(date.getDate()));
            }
        }

        if(profile.schoolName != null)
            txtSchoolName.setText(profile.schoolName);
        if(profile.schoolPhone != null)
            txtSchoolPhone.setText(profile.schoolPhone);
        if(profile.address != null)
            txtSchoolAddress.setText(profile.address);
    }

    @OnItemSelected(R.id.spinnerProvince)
    protected void spinnerProvince_itemSelected()
    {
        Province selectedProvince = (Province)spinnerProvince.getSelectedItem();
        List<City> cities = new ArrayList<>();
        int selectedCityIndex = 0;
        int i=0;
        for(City c : commonData.cities) {
            if(c.provinceId.equals(selectedProvince.id)) {
                cities.add(c);
                if(c.id.equals(profile.cityId))
                    selectedCityIndex = i;
                i++;
            }
        }
        spinnerCity.setAdapter(new ArrayAdapter<City>(this,
                android.R.layout.simple_spinner_dropdown_item, cities));
        spinnerCity.setSelection(selectedCityIndex);
    }

    @OnClick(R.id.btnSave)
    protected void btnSave_Click()
    {
        if(!validateForm())
            return;

        final ProgressDialog progress = showProgress();
        Student update = new Student();

        int year = Integer.parseInt(txtBirthYear.getText().toString());
        int month = Integer.parseInt(txtBirthMonth.getText().toString());
        int day = Integer.parseInt(txtBirthDay.getText().toString());
        JalaliCalendar.YearMonthDate georgian = JalaliCalendar.jalaliToGregorian(new JalaliCalendar.YearMonthDate(year, month-1, day));
        update.birthDate = georgian.toString();

        update.cityId = ((City)spinnerCity.getSelectedItem()).id;
        update.gender = Student.GenderEnum.values()[spinnerGender.getSelectedItemPosition()+1];
        update.schoolName = txtSchoolName.getText().toString();
        update.schoolPhone = txtSchoolPhone.getText().toString();
        update.address = txtSchoolAddress.getText().toString();

        Call<DataResponse<Student>> call = createWebService(Services.class).updateStudent(getToken(), update);
        call.enqueue(new RetryableCallback<DataResponse<Student>>(call) {
            @Override
            public void onFinalFailure(Call<DataResponse<Student>> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(AdditionalProfileActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call<DataResponse<Student>> call, Response<DataResponse<Student>> response)
            {
                progress.dismiss();
                DataResponse<Student> resp = response.body();
                if(resp == null)
                {
                    onFinalFailure(call, null);
                    return;
                }
                if(resp.message != null)
                    Toast.makeText(AdditionalProfileActivity.this, resp.message, Toast.LENGTH_LONG).show();
                if(resp.data != null)
                {
                    cacheItem(resp.data, BaseActivity.PREF_PROFILE);
                    profile = resp.data;
                }
                if(resp.success)
                    finish();
            }
        });
    }
}
