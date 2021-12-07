package com.bytedance.androidcamp.network.dou;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserActivity extends AppCompatActivity {
    RecyclerView uRv;
    Retrofit retrofit;
    TextView user_studenId;
    TextView studentName;
    IMiniDouyinService miniDouyinService;
    LinkedList<Video> allVideos=new LinkedList<Video>();
    LinkedList<Video> userVideos=new LinkedList<Video>();
    RecyclerView.LayoutManager uLayoutManager;
    RecyclerView.RecycledViewPool pool;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        uRv=findViewById(R.id.urv);
        initRecyclerView();
       // addTosharedpreference("user_info", "1120161939","qqq");
        Log.d("UserActivityOncreat", "addTosharedperference");
        Log.d("UserActivityOncreat", "findview");
        Log.d("UserActivityOncreat", "initRecyclerView");
        user_studenId = findViewById(R.id.user_studentId);
        studentName= findViewById(R.id.user_name);

        Log.d("UserActivityOncreat", "findview2");
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
                    UserPostFliter();
                    Log.d("refresh", "in onRespond/addAll");
                    uRv.setItemAnimator(null);
                    Log.d("refresh", "in onRespond/setItemAnimator");
                    uRv.getAdapter().notifyDataSetChanged();

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
    public void addTosharedpreference(String SPname, String studentId, String studentName){

        SharedPreferences.Editor editor=getSharedPreferences(SPname, MODE_PRIVATE).edit();
        editor.putString("studentId", studentId);
        editor.putString("studentName",studentName );
        editor.commit();
    }
    private void initRecyclerView() {
        Log.d("intRV", "start init RV");
        uLayoutManager=new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        uRv.setLayoutManager(uLayoutManager);
        uRv.setItemViewCacheSize(100);
        uRv.setRecycledViewPool(pool);


        uRv.setAdapter(new RecyclerView.Adapter<UserActivity.MyViewHolder>() {
            @NonNull
            @Override
            public UserActivity.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new UserActivity.MyViewHolder(
                        LayoutInflater.from(UserActivity.this)
                                .inflate(R.layout.video_item_view, viewGroup, false));
            }

            @Override
            public void onBindViewHolder(@NonNull UserActivity.MyViewHolder viewHolder, int i) {
                final Video video = userVideos.get(i);
                viewHolder.bind(UserActivity.this, video);

            }

            @Override
            public int getItemCount() {
                return userVideos.size();
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


        }
    }

    void UserPostFliter(){
        SharedPreferences preferences=getSharedPreferences("user_info",Activity.MODE_PRIVATE);
        Log.d("FavoriteFliter", "getSharedPreferences");

        String StudentId;
        String StudentName;
        StudentName=preferences.getString("studentName", "");
        StudentId=preferences.getString("studentId","");
        user_studenId.setText("StudentID:"+StudentId);
        studentName.setText("StudentName:"+StudentName);
            for(int j=0;j<allVideos.size();j++)
            {

                if((allVideos.get(j).getStudentId().equals(StudentId)))
                {
                    Log.d("FavoriteFliter", "in if, j="+j);
                    userVideos.add(allVideos.get(j));
                    break;
                }
            }
    }
}
