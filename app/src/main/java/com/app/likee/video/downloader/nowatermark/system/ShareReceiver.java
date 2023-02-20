package com.app.likee.video.downloader.nowatermark.system;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.app.likee.video.downloader.nowatermark.MainActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShareReceiver extends AppCompatActivity {
    private static final String TAG = ShareReceiver.class.getSimpleName();
    private static final String URL_PATTERN_1 = "^https://l.likee.video/v/.+";
    private static final String URL_PATTERN_2 = "^https://lite.likeevideo.com/i/.+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UiUtils uiUtils = new UiUtils(this);
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if ("android.intent.action.SEND".equals(action) && type != null && "text/plain".equals(type)) {
            String url = intent.getStringExtra("android.intent.extra.TEXT");
            Pattern one = Pattern.compile(URL_PATTERN_1);
            Pattern two = Pattern.compile(URL_PATTERN_2);
            String u = url.split("https://")[1];
            String urlx = "https://"+u;
            Matcher matcherOne = one.matcher(urlx);
            Matcher matcherTwo = two.matcher(urlx);
            if(matcherOne.find() || matcherTwo.find()){
                Intent x = new Intent(this, MainActivity.class);
                x.putExtra("sharedUrl", urlx);
                startActivity(x);
                finish();
            }else {
                Log.d(TAG, "URL Not Valid!");
                uiUtils.ToastUI("URL Not valid!").show();
                Intent x = new Intent(this, MainActivity.class);
                startActivity(x);
                finish();
            }
        }
    }

}
