package com.mybaltazar.baltazar2.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.events.CoinChangedEvent;
import com.mybaltazar.baltazar2.models.Student;
import com.mybaltazar.baltazar2.utils.DataListener;
import com.mybaltazar.baltazar2.webservices.CommonData;
import com.mybaltazar.baltazar2.webservices.DataResponse;
import com.mybaltazar.baltazar2.webservices.Services;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import co.ronash.pushe.Pushe;
import eu.inmite.android.lib.validations.form.FormValidator;
import eu.inmite.android.lib.validations.form.callback.SimpleErrorPopupCallback;
import khangtran.preferenceshelper.PrefHelper;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class BaseActivity extends AppCompatActivity {
    private static Retrofit retrofit = null;
    protected final int layoutId;
    protected final boolean liveValidation;

    private final static String PREF_TOKEN = "token";
    private final static String PREF_COIN = "coin";
    private final static String PREF_COMMON = "common";
    private final static String PREF_PROFILE = "profile";

    public BaseActivity(int layoutId, boolean liveValidation) {
        this.layoutId = layoutId;
        this.liveValidation = liveValidation;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId);
        ButterKnife.bind(this);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        if (liveValidation)
            FormValidator.startLiveValidation(this, findViewById(android.R.id.content), new SimpleErrorPopupCallback(this));

        //TODO uncomment this
//            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//                @Override
//                public void uncaughtException(Thread thread, Throwable t) {
//                }
//            });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (liveValidation)
            FormValidator.stopLiveValidation(this);
    }

    public static <T> void cacheItem(Context context, T item, String name) {
        Gson gson = new Gson();
        String json = gson.toJson(item);
        File file = new File(name);
        if (file.exists())
            file.delete();
        try {
            OutputStreamWriter writer = new OutputStreamWriter(context.openFileOutput(name, Context.MODE_PRIVATE));
            writer.write(json);
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String loadJson(Context context, String fileName) {
        try {
            InputStream input = context.openFileInput(fileName);
            if (input == null)
                return null;
            InputStreamReader reader = new InputStreamReader(input);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((receiveString = bufferedReader.readLine()) != null) {
                stringBuilder.append(receiveString);
            }

            input.close();
            return stringBuilder.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static <T> T loadCache(Context context, String name, Type type) {
        String json = loadJson(context, name);
        if (json == null)
            return null;
        return new Gson().fromJson(json, type);
    }

    public  <T> void cacheItem(T item, String name) {
        cacheItem(this, item, name);
    }

    public <T> T loadCache(String name, Type type) {
        return loadCache(this, name, type);
    }

    public Retrofit getWebServiceClient() {
        return getWebServiceClient(this);
    }

    public <T> T createWebService(final Class<T> service) {
        return getWebServiceClient().create(service);
    }

    public static Retrofit getWebServiceClient(Context context) {
        if (retrofit == null) {
            //TODO remove tracing
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).readTimeout(60, TimeUnit.SECONDS).connectTimeout(60, TimeUnit.SECONDS).build();

            Gson gson = new GsonBuilder().setLenient().create();
            retrofit = new Retrofit.Builder().baseUrl(context.getString(R.string.base_url))
                    .client(client).addConverterFactory(GsonConverterFactory.create(gson)).build();
        }
        return retrofit;
    }

    protected void showOkDialog(int title, int content) {
        showOkDialog(title, getString(content), null);
    }

    protected void showOkDialog(int title, String content) {
        showOkDialog(title, content, null);
    }

    protected void showOkDialog(int title, String content, Dialog.OnClickListener callback) {
        try {
            new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(content)
                    .setPositiveButton(R.string.ok, callback)
                    .show();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public boolean validateForm() {
        return FormValidator.validate(this, new SimpleErrorPopupCallback(this));
    }

    public static void loadImage(final String url, final ImageView target, boolean forceOffline) {
        if (forceOffline) {
            Picasso.get().load(url).networkPolicy(NetworkPolicy.OFFLINE).into(target, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError(Exception e) {
                    //Try again online if cache failed
                    loadImage(url, target);
                }
            });
        } else
            loadImage(url, target);
    }

    public static void loadImage(final String url, final ImageView target) {
        try {
            Picasso.get().load(url).error(R.drawable.ic_error).into(target);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public String getImageUrlById(String id) {
        return getString(R.string.media_base_url) + getString(R.string.image_dir) + id + ".jpg";
    }

    public static String getToken() {
        return PrefHelper.getStringVal(PREF_TOKEN, null);
    }

    public static void setToken(String token) {
        PrefHelper.setVal(PREF_TOKEN, token);
    }

    public static int getCoinCount() {
        return PrefHelper.getIntVal(PREF_COIN, 0);
    }

    private static void setCoinCount(int val) {
        PrefHelper.setVal(PREF_COIN, val);
        EventBus.getDefault().post(new CoinChangedEvent(val));
    }

    public void setProfile(Student profile)
    {
        cacheItem(profile, PREF_PROFILE);
        setCoinCount(profile.coins);
    }

    public Student getProfile()
    {
        return loadCache(PREF_PROFILE, Student.class);
    }

    public ProgressDialog showProgress() {
        return showProgress(R.string.connecting_to_server);
    }

    public ProgressDialog showProgress(int messageId) {
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(messageId));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();
        return progress;
    }

    private static CommonData commonDataCache = null;

    public CommonData loadCommonData(boolean forceNetwork, final DataListener<CommonData> callback) {
        if (forceNetwork) {
            loadCommonDataFromNetwork(callback);
            return null;
        }
        else {
            if(commonDataCache == null) {
                commonDataCache = loadCache(PREF_COMMON, CommonData.class);
                if (commonDataCache == null)
                    loadCommonDataFromNetwork(callback);
            }
            if (callback != null && commonDataCache != null)
                callback.onCallBack(commonDataCache);
            return commonDataCache;
        }
    }

    private void loadCommonDataFromNetwork(final DataListener<CommonData> callback) {
        int appVersion = 0;
        try {
            PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
            appVersion = info.versionCode;
        } catch (Exception ignored) { }

        String uuid = null;
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= 26)
                    uuid = getSystemService(TelephonyManager.class).getImei();
                else {
                    TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    uuid = tManager.getDeviceId();
                }
            }
        }
        catch (Exception ignored) { }

        String pusheId = null;
        try {
            pusheId = Pushe.getPusheId(BaseActivity.this);
        }
        catch (Throwable ignored){}

        Call<DataResponse<CommonData>> call = createWebService(Services.class).getCommonData(getToken(),
                appVersion, Build.VERSION.SDK_INT, uuid, pusheId);
        call.enqueue(new retrofit2.Callback<DataResponse<CommonData>>()
        {
            @Override
            public void onResponse(Call<DataResponse<CommonData>> call, Response<DataResponse<CommonData>> response)
            {
                if(response.code() == 401) // unauthorized
                {
                    PrefHelper.removeKey(PREF_TOKEN);
                    if(callback != null)
                        callback.onFailure();
                    return;
                }
                DataResponse<CommonData> resp = response.body();
                if(resp == null) {
                    onFailure(call, new Exception("null body!"));
                    return;
                }
                if(!resp.success)
                {
                    showOkDialog(R.string.error, resp.message);
                    if(callback != null)
                        callback.onFailure();
                    return;
                }
                if(resp.data != null) {
                    commonDataCache = resp.data;
                    cacheItem(resp.data, PREF_COMMON);
                    if(resp.data.me != null) {
                        setProfile(resp.data.me);
                    }
                    if(callback != null)
                        callback.onCallBack(resp.data);
                }
                else
                    onFailure(call, null);
            }

            @Override
            public void onFailure(Call<DataResponse<CommonData>> call, Throwable t) {
                Toast.makeText(BaseActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                if(callback != null)
                    callback.onFailure();
            }
        });
    }



    public void setupSwipe(SwipeRefreshLayout swipe)
    {
        swipe.setColorSchemeColors(
                getResources().getColor(R.color.blue),
                getResources().getColor(R.color.green),
                getResources().getColor(R.color.red));
    }
}
