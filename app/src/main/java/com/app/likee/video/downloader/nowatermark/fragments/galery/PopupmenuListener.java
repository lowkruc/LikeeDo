package com.app.likee.video.downloader.nowatermark.fragments.galery;

import com.app.likee.video.downloader.nowatermark.models.DownloadModel;

public interface PopupmenuListener {
    void onDelete(long id);
    void onShare(String filePath);
    void onOpenLikee(String deeplink);
    void onClickedItem(DownloadModel data);
}
