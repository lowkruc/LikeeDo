package com.app.likee.video.downloader.nowatermark.system.download;

public interface DownloadResponse {
    void onProgress(long max, long progress);
    void onError(Exception e);
    void onComplate(String filePath, long max);
}
