package com.app.likee.video.downloader.nowatermark.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DownloadModel extends RealmObject {
    @PrimaryKey
    long id;
    String username;
    String avatar;
    String caption;
    String thumb;
    String filePath;
    String deeplink;
    String url;
    String urlShare;

    public String getUrlShare() {
        return urlShare;
    }

    public void setUrlShare(String urlShare) {
        this.urlShare = urlShare;
    }

    public String getDeeplink() {
        return deeplink;
    }

    public void setDeeplink(String deeplink) {
        this.deeplink = deeplink;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
