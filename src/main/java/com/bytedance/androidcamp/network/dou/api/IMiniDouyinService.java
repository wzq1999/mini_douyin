package com.bytedance.androidcamp.network.dou.api;

import android.database.Observable;
import android.service.media.MediaBrowserService;

import com.bytedance.androidcamp.network.dou.model.Video;
import com.bytedance.androidcamp.network.dou.model.videoList;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IMiniDouyinService {
    // TODO 7: Define IMiniDouyinService
    String BASE_URL ="http://test.androidcamp.bytedance.com/mini_douyin/invoke/";
    String PATH="video";
    @GET(PATH)
    Call<videoList> getVideos();

    @GET("video")
    Call<videoList>getFavorites(@Query ("image_w" )int imw);

    @Multipart
    @POST(PATH)
    Call<ResponseBody>uploadVideo(@Query("student_id") String studentId,@Query("user_name") String userName,
                                  @Part MultipartBody.Part coverImagePart, @Part MultipartBody.Part videoPart);



}
