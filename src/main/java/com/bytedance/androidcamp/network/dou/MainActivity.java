package com.bytedance.androidcamp.network.dou;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bytedance.androidcamp.network.dou.api.IMiniDouyinService;
import com.bytedance.androidcamp.network.dou.model.Video;
import com.bytedance.androidcamp.network.dou.model.videoList;
import com.bytedance.androidcamp.network.lib.util.ImageHelper;
import com.bytedance.androidcamp.network.dou.util.ResourceUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private static final int PICK_VIDEO = 2;
    private static final String TAG = "MainActivity";
    private RecyclerView mRv;
    private List<Video> mVideos = new ArrayList<>();
    public Uri mSelectedImage;
    private Uri mSelectedVideo;
    public Button mBtn;
    private Button mBtnRefresh;
    private Button userInfoPageBtn;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.RecycledViewPool pool=new RecyclerView.RecycledViewPool();
    private SwipeRefreshLayout mSwipeRefreshWidget;

    // TODO 8: initialize retrofit & miniDouyinService
    private Retrofit retrofit;
    private IMiniDouyinService miniDouyinService;


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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initRecyclerView();
        initBtns();
        loginOrNot();
        bindActivity(R.id.favorites,FavoriteActivity.class );
        bindActivity(R.id.about_me, UserActivity.class);
        refresh();
        mSwipeRefreshWidget = findViewById(R.id.swipe_refresh_widget);
        mSwipeRefreshWidget.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                mSwipeRefreshWidget.setRefreshing(false);
            }
        });
    }

    private void initBtns() {
        Log.d("iB", "Start int btns");
        mBtn = findViewById(R.id.post);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mBtn.getText().toString();
                if (getString(R.string.select_an_image).equals(s)) {
                    chooseImage();
                } else if (getString(R.string.select_a_video).equals(s)) {
                    chooseVideo();
                } else if (getString(R.string.post_it).equals(s)) {
                    if (mSelectedVideo != null && mSelectedImage != null) {
                        postVideo();
                    } else {
                        throw new IllegalArgumentException("error data uri, mSelectedVideo = "
                                + mSelectedVideo
                                + ", mSelectedImage = "
                                + mSelectedImage);
                    }
                } else if ((getString(R.string.success_try_refresh).equals(s))) {
                    mBtn.setText(R.string.select_an_image);
                }
            }
        });

       // mBtnRefresh = findViewById(R.id.btn_refresh);
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

    private void initRecyclerView() {
        Log.d("intRV", "start init RV");
        mRv = findViewById(R.id.rv);
        mLayoutManager=new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRv.setLayoutManager(mLayoutManager);
        mRv.setItemViewCacheSize(100);
        mRv.setRecycledViewPool(pool);


        mRv.setAdapter(new RecyclerView.Adapter<MyViewHolder>() {
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new MyViewHolder(
                       LayoutInflater.from(MainActivity.this)
                               .inflate(R.layout.video_item_view, viewGroup, false));
            }

            @Override
            public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
                final Video video = mVideos.get(i);
                viewHolder.bind(MainActivity.this, video);

            }

            @Override
            public int getItemCount() {
                return mVideos.size();
            }
        });
    }

    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE);
    }

    public void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult() called with: requestCode = ["
                + requestCode
                + "], resultCode = ["
                + resultCode
                + "], data = ["
                + data
                + "]");

        if (resultCode == RESULT_OK && null != data) {
            if (requestCode == PICK_IMAGE) {
                mSelectedImage = data.getData();
                Log.d(TAG, "selectedImage = " + mSelectedImage);
                mBtn.setText(R.string.select_a_video);
            } else if (requestCode == PICK_VIDEO) {
                mSelectedVideo = data.getData();
                Log.d(TAG, "mSelectedVideo = " + mSelectedVideo);
                mBtn.setText(R.string.post_it);
            }
        }
    }

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        File f = new File(ResourceUtils.getRealPath(MainActivity.this, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }

    private void postVideo() {
        Log.v("PostVideo", "Start");
        mBtn.setText("POSTING...");
        mBtn.setEnabled(false);
        MultipartBody.Part coverImagePart = getMultipartFromUri("cover_image", mSelectedImage);
        MultipartBody.Part videoPart = getMultipartFromUri("video", mSelectedVideo);

        // TODO 9: post video & update buttons
        Call<ResponseBody> responseBodyCall=getDouyinService().uploadVideo("3170104719","qqq" ,coverImagePart,videoPart );
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Log.e("Upload error:", throwable.getMessage());
            }
        });
        mBtn.setText("POSTED");
        Toast.makeText(this, "TODO 9: post video & update buttons", Toast.LENGTH_SHORT).show();
    }

    public void fetchFeed(View view) {
        mBtnRefresh.setText("requesting...");
        mBtnRefresh.setEnabled(false);
        // TODO 10: get videos & update recycler list
        Call<videoList> call=getDouyinService().getVideos();
        call.enqueue(new Callback<videoList>() {
            @Override
            public void onResponse(Call<videoList> call, Response<videoList> response) {
                Log.d("oR", " onRespond");
                if (response.body() != null && response.body().getVideoList() != null) {

                    mVideos.addAll(response.body().getVideoList());
                    //seperateVideos();
                    mRv.setItemAnimator(null);
                    mRv.getAdapter().notifyDataSetChanged();
                }
                else
                    Log.d("oR", " onRespond body null");
                mBtnRefresh.setText(R.string.refresh_feed);
                mBtnRefresh.setEnabled(true);

            }
            @Override
            public void onFailure(Call<videoList> call, Throwable throwable) {

            }
        });


        Toast.makeText(this, "TODO 10: get videos & update recycler list", Toast.LENGTH_SHORT).show();
    }
    void refresh(){
        Call<videoList> call=getDouyinService().getVideos();
        call.enqueue(new Callback<videoList>() {
            @Override
            public void onResponse(Call<videoList> call, Response<videoList> response) {
                Log.d("oR", " onRespond");
                if (response.body() != null && response.body().getVideoList() != null) {

                    mVideos.addAll(response.body().getVideoList());
                    mRv.setItemAnimator(null);
                    mRv.getAdapter().notifyDataSetChanged();
                }
                else
                    Log.d("oR", " onRespond body null");

            }
            @Override
            public void onFailure(Call<videoList> call, Throwable throwable) {

            }
        });
    }
    private void bindActivity(final int btnId, final Class<?> activityClass) {
        findViewById(btnId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, activityClass));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }
    private void loginOrNot(){
        SharedPreferences preferences=getSharedPreferences("user_info",Activity.MODE_PRIVATE);
        int loginSituation=preferences.getInt("loginbool", 0);
        Log.d("loginOrNot", ""+loginSituation);
        if(loginSituation==0){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }
}






