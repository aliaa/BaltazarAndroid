package com.mybaltazar.baltazar2.webservices;

import com.mybaltazar.baltazar2.models.Answer;
import com.mybaltazar.baltazar2.models.Question;
import com.mybaltazar.baltazar2.models.Student;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Services
{
    @GET("Common")
    Call<DataResponse<CommonData>> getCommonData();

    @POST("Student/Register")
    Call<DataResponse<Student>> registerStudent(@Body Student student);

    @POST("Student/Update")
    Call<DataResponse<Student>> updateStudent(@Header("token") String token,
                                              @Body Student update);

    @GET("Student/Login")
    Call<DataResponse<Student>> login(@Query("phone") String phone,
                                      @Query("password") String password);

    @POST("Question/Publish")
    Call<DataResponse<Question>> publishQuestion(@Header("token") String token,
                                                 @Body Question question);

    @Multipart
    @POST("Question/UploadImage/{id}")
    Call<CommonResponse> uploadQuestionImage(@Header("token") String token,
                                             @Path("id") String id,
                                             @Part MultipartBody.Part image);

    @GET("Question/List")
    Call<DataResponse<List<Question>>> questionList(@Header("token") String token,
                                                    @Query("grade") Integer grade,
                                                    @Query("courseId") String courseId,
                                                    @Query("sectionId") String sectionId,
                                                    @Query("page") Integer page);

    @GET("Question/Mine")
    Call<DataResponse<List<Question>>> myQuestions(@Header("token") String token);

    @POST("Answer/Publish")
    Call<DataResponse<Answer>> publishAnswer(@Header("token") String token,
                                             @Body Answer answer);

    @POST("Answer/UploadImage/{id}")
    Call<CommonResponse> uploadAnswerImage(@Header("token") String token,
                                           @Path("id") String id,
                                           @Part MultipartBody.Part image);

    @GET("Answer/SetResponse")
    Call<CommonResponse> setAnswerResponse(@Header("token") String token,
                                           @Query("questionId") String questionId,
                                           @Query("answerId") String answerId,
                                           @Query("response") Answer.QuestionerResponseEnum response);
}