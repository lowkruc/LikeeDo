package com.app.likee.video.downloader.nowatermark.system;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.app.likee.video.downloader.nowatermark.BuildConfig;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class UiUtils {
    private Activity activity;

    public UiUtils(Activity activity) {
        this.activity = activity;
    }

    public Toast ToastUI(String msg){
        return Toast.makeText(activity.getApplicationContext(), msg, Toast.LENGTH_SHORT);
    }

    public void launchNewActivity(Context context, String packageName) {
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.CUPCAKE) {
            intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        }
        if (intent == null) {
            Toast toast = Toast.makeText(context, "Likee not installed", Toast.LENGTH_LONG);
            toast.show();
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public void shareFile(String file, Context context){
        String type;
        Intent intent;
        intent = new Intent(Intent.ACTION_SEND);
        if(getExtension(file).equals("jpg")){
            Log.d("File image", file);
            type = "image/*";
        }else if(getExtension(file).equals("mp4")){
            Log.d("File video", file);
            type = "video/*";
        }else{
            return;
        }
        intent.setType(type);
        File x = new File(file);
        Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+".provider", x);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(intent, "Share to"));
    }

    public String getExtension(String fileName) {
        String encoded;
        try {
            encoded = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            encoded = fileName;
        }

        return MimeTypeMap.getFileExtensionFromUrl(encoded).toLowerCase();
    }

    public void openLikee(String uri, Context context){
        Intent intent =  new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(uri));
        context.startActivity(intent);
    }

}
