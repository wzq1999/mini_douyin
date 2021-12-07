package com.bytedance.androidcamp.network.dou;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bytedance.androidcamp.network.dou.api.IMiniDouyinService;
import com.bytedance.androidcamp.network.dou.model.Video;
import com.bytedance.androidcamp.network.dou.model.videoList;
import com.bytedance.androidcamp.network.lib.util.ImageHelper;

import java.util.LinkedList;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FavoriteActivity extends AppCompatActivity {
    RecyclerView fRv;
    Retrofit retrofit;
    IMiniDouyinService miniDouyinService;
    LinkedList<Video> allVideos=new LinkedList<Video>();
    LinkedList<Video> favoriteVideos=new LinkedList<Video>();
    RecyclerView.LayoutManager fLayoutManager;
    RecyclerView.RecycledViewPool pool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("FA", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        addTosharedpreference("collect", "https://lf1-hscdn-tos.pstatp.com/obj/developer-baas/baas/tt7217xbo2wz3cem41/a52a80bb971382d0_1563351781176.mp4");
        addTosharedpreference("collect", "https://sf3-hscdn-tos.pstatp.com/obj/developer-baas/baas/tt7217xbo2wz3cem41/730d27a515d0cccd_1563350028097.mp4");

        fRv=findViewById(R.id.Frv);
        initRecyclerView();
        Log.d("onCreate", "initRecyclerView");
        refresh();

    }
    void refresh(){
        Call<videoList> call=getDouyinService().getVideos();
        Log.d("refresh", "call");
        call.enqueue(new Callback<videoList>() {

            @Override
            public void onResponse(Call<videoList> call, Response<videoList> response) {
                Log.d("oR", " onRespond");
                if (response.body() != null && response.body().getVideoList() != null) {
                    Log.d("refresh", "in onRespond");
                    allVideos.addAll(response.body().getVideoList());
                    FavoriteFliter();
                    Log.d("refresh", "in onRespond/addAll");
                    fRv.setItemAnimator(null);
                    Log.d("refresh", "in onRespond/setItemAnimator");
                    fRv.getAdapter().notifyDataSetChanged();

                }
                else
                    Log.d("refresh", " onRespond body null");

            }
            @Override
            public void onFailure(Call<videoList> call, Throwable throwable) {
                Log.d("refresh", " onFailure");
            }
        });
    }
    private IMiniDouyinService getDouyinService(){
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(IMiniDouyinService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        if (miniDouyinService == null) {
            miniDouyinService = retrofit.create(IMiniDouyinService.class);
        }
        return miniDouyinService;
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;
        public TextView studentId;
        public TextView userName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            studentId=itemView.findViewById(R.id.studentId);
            userName=itemView.findViewById(R.id.userName);
        }

        public void bind(final Activity activity, final Video video) {
            studentId.setText(video.getStudentId());
            userName.setText(video.getUserName());
            ImageHelper.displayWebImage(video.getImageUrl(), img);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoActivity.launch(activity, video.getVideoUrl());
                }
            });
            //img.getLayoutParams().height=video.getImageHeight();

        }
    }

    private void initRecyclerView() {
        Log.d("intRV", "start init RV");
        fLayoutManager=new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        fRv.setLayoutManager(fLayoutManager);
        fRv.setItemViewCacheSize(100);
        fRv.setRecycledViewPool(pool);


        fRv.setAdapter(new RecyclerView.Adapter<FavoriteActivity.MyViewHolder>() {
            @NonNull
            @Override
            public FavoriteActivity.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new FavoriteActivity.MyViewHolder(
                        LayoutInflater.from(FavoriteActivity.this)
                                .inflate(R.layout.video_item_view, viewGroup, false));
            }

            @Override
            public void onBindViewHolder(@NonNull FavoriteActivity.MyViewHolder viewHolder, int i) {
                final Video video = favoriteVideos.get(i);
                viewHolder.bind(FavoriteActivity.this, video);

            }

            @Override
            public int getItemCount() {
                return favoriteVideos.size();
            }
        });
    }
    public void addTosharedpreference(String name, String mUrl){
        SharedPreferences preferences=getSharedPreferences(name,MODE_PRIVATE);
        int curr=preferences.getInt("total", 0);
        curr++;
        SharedPreferences.Editor editor=getSharedPreferences(name, MODE_PRIVATE).edit();
        editor.putInt("total", curr);
        editor.putString(name+curr,mUrl );
        editor.commit();
    }

    void FavoriteFliter(){
        SharedPreferences preferences=getSharedPreferences("collect",Activity.MODE_PRIVATE);
        Log.d("FavoriteFliter", "getSharedPreferences");
        int count=preferences.getInt("total", 0);
        Log.d("FavoriteFliter", "getInt");
        for(int i=0;i<count;i++)
        {
            String Url;
            Url=preferences.getString("collect"+i,"");
            Log.d("FavoriteFliter", "getString");
            //favoriteVideoIdList.add(Url);
            for(int j=0;j<allVideos.size();j++)
            {
                Log.d("FavoriteFliter", "j="+j);
                if((allVideos.get(j).getVideoUrl()).equals(Url))
                {
                    Log.d("FavoriteFliter", "in if, j="+j);
                    favoriteVideos.add(allVideos.get(j));
                    break;
                }
            }
            Log.d("FavoriteFliter", "favoritevideo.size="+favoriteVideos.size());
        }


    }

}
