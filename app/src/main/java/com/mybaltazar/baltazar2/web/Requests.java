package com.mybaltazar.baltazar2.web;

import android.support.annotation.Nullable;

import com.mybaltazar.baltazar2.models.*;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface Requests
{
    @POST("tools/register")
    Call<ServerResponse> registerTools();

    @POST("register")
    Call<ServerResponse> register(@Body ServerRequest request);

    @POST("activation")
    Call<ServerResponse> activate(@Header("Authorization") String jwt, @Body ServerRequest request);

    @POST("login")
    Call<ServerResponse> login(@Body ServerRequest request);

    @GET("logout")
    Call<ServerResponse> logout(@Header("Authorization") String jwt);

    @GET("refresh")
    Call<ServerResponse> refreshToken(@Header("Authorization") String jwt);

    @GET("me")
    Call<User> myInfo(@Header("Authorization") String jwt);

    @GET("school/all")
    Call<ArrayList<School>> schoolList(@Header("Authorization") String jwt);

    @GET("level/all")
    Call<ArrayList<Level>> levelList(@Header("Authorization") String jwt);

    @GET("tools/provinces")
    Call<ArrayList<Province>> provinceList();

    @GET("tools/cities/{province_id}")
    Call<ArrayList<City>> cityList(@Path("province_id") Integer provinceId);

    @GET("avatar/all")
    Call<ArrayList<Avatar>> avatarList();
//
//    @GET("tools/register")
//    Call<RegisterTools>registerTools();

    @GET("market")
    Call<ServerResponse> shopItems(@Header("Authorization") String jwt);
    @GET("market/favorites")
    Call<ServerResponse> shopfavoritesItems(@Header("Authorization") String jwt);
    @GET("market/togglelike/{item_id}")
    Call<ServerResponse> toggleMarketItemLike(@Header("Authorization") String jwt, @Path(value = "item_id", encoded = true) Integer itemId);

    @GET("market/buy/{item_id}")
    Call<ShopItem> buyItem(@Header("Authorization") String jwt, @Path(value = "item_id", encoded = true) Integer itemId);

    @GET("post/all")
    Call<ServerResponse> blogList(@Header("Authorization") String jwt);

    @GET("question/list")
    Call<QuestionListResponse> questionList(@Header("Authorization") String jwt);
    @GET("question/my")
    Call<QuestionListResponse> myQuestions(@Header("Authorization") String jwt);

    @GET("question/get/{id}")
    Call<ServerResponse> getQuestion(@Header("Authorization") String jwt, @Path(value = "id", encoded = true) Integer questionId);

    @GET("answer/next/{question_id}")
    Call<ServerResponse> nextAnswerForQuestion(@Header("Authorization") String jwt, @Path(value = "question_id", encoded = true) Integer questionId);

    @POST("device/add")
    Call<ServerResponse> addDeviceToken(@Header("Authorization") String jwt, @Body ServerRequest request);

    @Multipart
    @POST("answer")
    Call<ServerResponse> answerQuestion(@Header("Authorization") String jwt, @Nullable @Part("context")
            RequestBody description,
                                        @Part("question_id")
                                                RequestBody questionId,
                                        @Nullable @Part MultipartBody.Part file);
    @POST("answer/rate/{answer_id}/{rate}")
    Call<ServerResponse> rateAnswer(@Header("Authorization") String jwt, @Path(value = "answer_id", encoded = true) Integer answer_id, @Path(value = "rate", encoded = true) Integer rate);

    @Multipart
    @POST("ask")
    Call<ServerResponse> askQuestion(@Header("Authorization") String jwt,
                                     @Part("title") RequestBody title,
                                     @Nullable @Part("context") RequestBody description,
                                     @Part("course_id") RequestBody course_id,
                                     @Nullable @Part MultipartBody.Part file,
                                     @Part("immediate") RequestBody immediate,
                                     @Part("prize") RequestBody prize);
}
