package com.app.likee.video.downloader.nowatermark.fragments.player;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import com.app.likee.video.downloader.nowatermark.MainActivity;
import com.app.likee.video.downloader.nowatermark.R;
import com.app.likee.video.downloader.nowatermark.database.RealmViewModel;
import com.app.likee.video.downloader.nowatermark.system.UiUtils;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;

public class VideoPlayerActivity extends AppCompatActivity {
    private View mContentView;
    private ImageView avatarFile;
    private TextView username, caption;
    private PlayerView playerView;
    public SimpleExoPlayer exoPlayer;
    private View mControlsView;
    private Toolbar mToolbarView;
    private FrameLayout openLikeeVP, shareVP;
    private UiUtils uiUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        this.uiUtils = new UiUtils(this);
        avatarFile = findViewById(R.id.avatarFile);
        username = findViewById(R.id.usernameFile);
        caption = findViewById(R.id.captionFile);
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mToolbarView = findViewById(R.id.fullscreen_action_bar);
        mContentView = findViewById(R.id.fullscreen_content);
        playerView = findViewById(R.id.playerView);
        openLikeeVP = findViewById(R.id.openLikeeVP);
        shareVP = findViewById(R.id.shareVP);

        Intent intent = getIntent();
        String avatar = intent.getStringExtra("avatar");
        String usernameText = intent.getStringExtra("username");
        String captionText = intent.getStringExtra("caption");
        String filePath = intent.getStringExtra("filePath");
        String deeplink = intent.getStringExtra("deeplink");

        openLikeeVP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uiUtils.openLikee(deeplink, v.getContext());
            }
        });

        shareVP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uiUtils.shareFile(filePath, v.getContext());
            }
        });

        username.setText(usernameText);
        caption.setText(captionText);
        Glide.with(this).asBitmap().load(avatar).circleCrop().into(avatarFile);
        setupPlayer(filePath);
    }

    @Override
    protected void onPause() {
        if(exoPlayer.isPlaying()){
            exoPlayer.pause();
        }
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        if(!exoPlayer.isPlaying()){
            exoPlayer.play();
        }
        super.onPostResume();
    }

    private void setupPlayer(String filePath){
        exoPlayer = new SimpleExoPlayer.Builder(this).build();
        MediaItem mediaItem = MediaItem.fromUri(filePath);
        exoPlayer.setMediaItem(mediaItem);
        playerView.setPlayer(exoPlayer);
        playerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                if(visibility == View.VISIBLE){
                    show();
                }else {
                    hide();
                }
            }
        });
        exoPlayer.prepare();
        exoPlayer.play();
    }

    public void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        releasePlayer();
        super.onDestroy();
    }


    private void hide() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mToolbarView.setVisibility(View.GONE);
    }

    private void show() {
        mControlsView.setVisibility(View.VISIBLE);
        mToolbarView.setVisibility(View.VISIBLE);
    }

}