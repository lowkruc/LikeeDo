package com.app.likee.video.downloader.nowatermark.system.download;

import android.os.AsyncTask;
import android.os.Environment;
import android.webkit.URLUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadModule extends AsyncTask<String, Long, String> {
    private final File FILE_PATH;
    private DownloadResponse downloadResponse;
    private long max = 0;

    public DownloadModule() {
        this.FILE_PATH = new File(Environment.getExternalStorageDirectory().toString() + "/LikeeDown/");;
    }

    public void setDownloadResponse(DownloadResponse response){
        this.downloadResponse = response;
    }


    @Override
    protected String doInBackground(String... strings) {
        OkHttpClient client = new OkHttpClient();
        String filePath = "";

        Call call = client.newCall(new Request.Builder().url(strings[0]).get().build());

        try {
            Response response = call.execute();
            if(response.code() == 200 || response.code() == 201){
                if(!FILE_PATH.exists()){
                    FILE_PATH.mkdirs();
                }
                InputStream inputStream = null;
                try {
                    inputStream = response.body().byteStream();
                    byte[] buff = new byte[1024 * 4];
                    long downloaded = 0;
                    long target = response.body().contentLength();
                    String filename = URLUtil.guessFileName(strings[0], null, null);
                    File x = new File(FILE_PATH, filename);
                    OutputStream output = new FileOutputStream(x);
                    filePath = x.getAbsolutePath();
                    while (true){
                        int readed = inputStream.read(buff);
                        if(readed == -1){
                            break;
                        }
                        output.write(buff, 0, readed);
                        downloaded += readed;
                        this.max = target;
                        publishProgress(downloaded, target);
                    }
                    if(isCancelled()){
                        return null;
                    }
                    output.flush();
                    output.close();
                }catch (IOException e){
                    downloadResponse.onError(e);
                    return null;
                }finally {
                    if(inputStream != null){
                        inputStream.close();
                        call.cancel();
                    }
                }
            }
        } catch (IOException e) {
            downloadResponse.onError(e);
            return null;
        }
        return filePath;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        downloadResponse.onComplate(s, this.max);
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
        downloadResponse.onProgress(values[1].intValue(), values[0].intValue());
    }
}
