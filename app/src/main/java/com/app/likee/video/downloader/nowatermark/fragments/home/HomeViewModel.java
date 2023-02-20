package com.app.likee.video.downloader.nowatermark.fragments.home;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app.likee.video.downloader.nowatermark.models.DownloadModel;
import com.app.likee.video.downloader.nowatermark.system.crawl.CrawlData;
import com.app.likee.video.downloader.nowatermark.system.crawl.CrawlRespone;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeViewModel extends ViewModel {
    private static final String TAG = HomeViewModel.class.getSimpleName();
    private static final String URL_PATTERN_1 = "^https://l.likee.video/v/.+";
    private static final String URL_PATTERN_2 = "^https://lite.likeevideo.com/i/.+";

    private final MutableLiveData<DownloadModel> observeData = new MutableLiveData<>();
    private CrawlData crawlData;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> isDownloaded = new MutableLiveData<>();
    private MutableLiveData<String> urlDownload = new MutableLiveData<>();
    private boolean isError = false;
    private String eMsg;

    public void execute(String url){
        if(observeData.getValue() == null || !observeData.getValue().getUrl().equals(url)){
            Pattern one = Pattern.compile(URL_PATTERN_1);
            Pattern two = Pattern.compile(URL_PATTERN_2);
            Matcher mOne = one.matcher(url);
            Matcher mTwo = two.matcher(url);
            if(mOne.find() || mTwo.find()){
                crawlData = new CrawlData();
                isLoading.postValue(true);
                isDownloaded.postValue(false);
                crawlData.setResponse(new CrawlRespone() {
                    @Override
                    public void onComplate(DownloadModel data) {
                        observeData.postValue(data);
                        isError = false;
                        isLoading.postValue(false);
                    }
                    @Override
                    public void onError(Exception e) {
                        observeData.postValue(null);
                        eMsg = e.getMessage();
                        isError = true;
                        isLoading.postValue(false);
                    }
                });
                crawlData.execute(url);
            }else{
                observeData.postValue(null);
                eMsg = "URL not valid!";
                isError = true;
                isLoading.postValue(false);
            }
        }else {
            observeData.postValue(null);
            isLoading.postValue(false);
            eMsg = "Duplicate Content";
            isError = true;
        }
    }

    public LiveData<String> getUrlDownload(){
        return urlDownload;
    }
    public void setUrlDownload(String url){
        urlDownload.postValue(url);
    }


    public Boolean isError(){
        return isError;
    }
    public void setIsError(Boolean error){
        isError = error;
    }

    public String getErrorMsg(){
        return eMsg;
    }

    public void setErrorMsg(String msg){
        eMsg = msg;
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getIsDownloaded() {
        return isDownloaded;
    }

    public void setIsDownloaded(boolean isDownloaded) {
        this.isDownloaded.postValue(isDownloaded);
    }

    public void setFilePath(String filePath){
        DownloadModel d = new DownloadModel();
        d.setThumb(observeData.getValue().getThumb());
        d.setUrl(observeData.getValue().getUrl());
        d.setUsername(observeData.getValue().getUsername());
        d.setAvatar(observeData.getValue().getAvatar());
        d.setCaption(observeData.getValue().getCaption());
        d.setUrlShare(observeData.getValue().getUrlShare());
        d.setDeeplink(observeData.getValue().getDeeplink());
        d.setFilePath(filePath);
        observeData.postValue(d);
    }
    public void setObserveData(DownloadModel data){
        observeData.postValue(data);
    }

    public DownloadModel getObjectData(){
        return observeData.getValue();
    }
    public LiveData<DownloadModel> getData(){
        return observeData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
