package com.mybaltazar.baltazar2.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mybaltazar.baltazar2.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import eu.inmite.android.lib.validations.form.FormValidator;
import eu.inmite.android.lib.validations.form.callback.SimpleErrorPopupCallback;
import khangtran.preferenceshelper.PrefHelper;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class BaseActivity extends AppCompatActivity
{
    private static Retrofit retrofit = null;
    protected final int layoutId;
    protected final boolean liveValidation;

    public final static String PREF_SESSION_ID = "sessionId";

    public BaseActivity(int layoutId, boolean liveValidation)
    {
        this.layoutId = layoutId;
        this.liveValidation = liveValidation;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(layoutId);
        ButterKnife.bind(this);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        if(liveValidation)
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
        if(liveValidation)
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

    protected <T> void cacheItem(T item, String name) {
        cacheItem(this, item, name);
    }

    protected <T> T loadCache(String name, Type type) {
        return loadCache(this, name, type);
    }

    public Retrofit getWebServiceClient() {
        return getWebServiceClient(this);
    }

    public <T> T createWebService(final Class<T> service) {
        return getWebServiceClient().create(service);
    }

    public static Retrofit getWebServiceClient(Context context)
    {
        if(retrofit == null)
        {
            //TODO remove tracing
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).readTimeout(60, TimeUnit.SECONDS).connectTimeout(60,TimeUnit.SECONDS).build();

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
        }
        catch (Throwable e) { e.printStackTrace(); }
    }

    protected boolean validateForm()
    {
        return FormValidator.validate(this, new SimpleErrorPopupCallback(this));
    }

    public void loadImage(final String url, final ImageView target, boolean forceOffline)
    {
        if(forceOffline)
        {
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
        }
        else
            loadImage(url, target);
    }

    public void loadImage(final String url, final ImageView target)
    {
        try {
            Picasso.get().load(url).into(target);
        }
        catch (Throwable t){
            t.printStackTrace();
        }
    }

    public static String getSessionId()
    {
        return PrefHelper.getStringVal(PREF_SESSION_ID, null);
    }

    protected ProgressDialog showProgress()
    {
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.connecting_to_server));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();
        return progress;
    }
}
