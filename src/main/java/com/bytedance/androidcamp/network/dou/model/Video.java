package com.bytedance.androidcamp.network.dou.model;

import com.google.gson.annotations.SerializedName;

public class Video {

    @SerializedName("student_id") private String studentId;
    @SerializedName("user_name") private String userName;
    @SerializedName("image_url") private String imageUrl;
    @SerializedName("video_url") private String videoUrl;
    @SerializedName("image_w") private int imageWidth;
    @SerializedName("image_h") private int imageHeight;
    @SerializedName("_id") private String videoId;

    public Video(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getImageWidth(){return imageWidth; }

    public int getImageHeight(){return imageHeight;}
    public String getVideoId(){return videoId;}
}
