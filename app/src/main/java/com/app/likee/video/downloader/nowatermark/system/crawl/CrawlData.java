package com.app.likee.video.downloader.nowatermark.system.crawl;

import android.os.AsyncTask;
import android.util.Log;

import com.app.likee.video.downloader.nowatermark.models.DownloadModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class CrawlData extends AsyncTask<String, Void, Void> {
    private CrawlRespone crawlRespone;
    private DownloadModel downloadModel;
    private boolean isError = false;
    private String errorMasagge;

    public void setResponse(CrawlRespone crawlRespone) {
        this.crawlRespone = crawlRespone;
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            Document document = Jsoup.connect(strings[0]).userAgent("Mozilla/5.0").get();
            Elements elements = document.select("script");
            JSONObject val = null;
            for (Element el : elements){
                String scriptText = el.data();
                if (scriptText.startsWith("window.data")) {
                    String jsonText = scriptText.replaceAll("[^{]*([{].*)[^}]*", "$1");
                    val = new JSONObject(jsonText);
                }
            }
            if(val != null){
                downloadModel = new DownloadModel();
                downloadModel.setUrl(val.getString("video_url").replace("_4", ""));
                downloadModel.setThumb(val.getString("image2"));
                downloadModel.setUsername(val.getString("nick_name"));
                downloadModel.setAvatar(val.getString("uid_avatar"));
                downloadModel.setCaption(val.getString("msg_text"));
                downloadModel.setFilePath("");
                downloadModel.setUrlShare(strings[0]);
                downloadModel.setDeeplink(val.getString("deeplink"));
                Log.d("CrawlData", val.toString());
            }else{
                isError = true;
                errorMasagge = "Error parsing data";
            }
        }catch (IOException | JSONException e){
            isError = true;
            errorMasagge = e.getMessage();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if(isError){
            crawlRespone.onError(new Exception(errorMasagge));
            Log.d("CrawlData", errorMasagge);
        }else{
            crawlRespone.onComplate(downloadModel);
        }
        super.onPostExecute(aVoid);
    }

}
