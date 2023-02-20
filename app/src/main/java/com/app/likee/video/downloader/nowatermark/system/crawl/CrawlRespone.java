package com.app.likee.video.downloader.nowatermark.system.crawl;

import com.app.likee.video.downloader.nowatermark.models.DownloadModel;

public interface CrawlRespone {
    void onComplate(DownloadModel data);
    void onError(Exception e);
}
