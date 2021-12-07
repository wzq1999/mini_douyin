package com.bytedance.androidcamp.network.dou.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class videoList {
    @SerializedName("feeds")
    private List<Video> videos;

    public List<Video> getVideoList() {
        return videos;
    }

    public void setVideoList(List<Video> VideoList) {
        this.videos = VideoList;
    }
}