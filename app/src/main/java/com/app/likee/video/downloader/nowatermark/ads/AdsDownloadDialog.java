package com.app.likee.video.downloader.nowatermark.ads;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.app.likee.video.downloader.nowatermark.R;
import com.google.android.gms.ads.MediaContent;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

public class AdsDownloadDialog extends FrameLayout {

    private UnifiedNativeAd nativeAd;
    private UnifiedNativeAdView nativeAdView;

    ImageView image;
    MediaView mediaView;
    CardView frameImage;
    TextView headline, secondary, store, action;

    public AdsDownloadDialog(@NonNull Context context) {
        super(context);
    }

    public AdsDownloadDialog(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public AdsDownloadDialog(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attributeSet) {

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.ads_template_dialog_download, this);
    }

    private boolean adHasOnlyStore(UnifiedNativeAd nativeAd) {
        String store = nativeAd.getStore();
        String advertiser = nativeAd.getAdvertiser();
        return !TextUtils.isEmpty(store) && TextUtils.isEmpty(advertiser);
    }

    public UnifiedNativeAdView getNativeAdView() {
        return nativeAdView;
    }

    public void destroyNativeAd() {
        nativeAd.destroy();
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        mediaView = (MediaView) findViewById(R.id.mediaView);
        nativeAdView = (UnifiedNativeAdView) findViewById(R.id.nativeAds);
        image = (ImageView) findViewById(R.id.image);
        headline = (TextView) findViewById(R.id.headline);
        secondary = (TextView) findViewById(R.id.secondary);
        action = (TextView) findViewById(R.id.btnAction);
        frameImage = (CardView) findViewById(R.id.wraperIconAdsHome);
    }

    public void setupNativeAd(UnifiedNativeAd nativeAd){
        this.nativeAd = nativeAd;

        String headlineVal = nativeAd.getHeadline();
        String second = nativeAd.getBody();
        String cta = nativeAd.getCallToAction();
        MediaContent mediaContent = nativeAd.getMediaContent();
        NativeAd.Image icon = nativeAd.getIcon();

        nativeAdView.setCallToActionView(action);
        headline.setText(headlineVal);
        secondary.setText(second);
        nativeAdView.setMediaView(mediaView);
        mediaView.setMediaContent(mediaContent);
        mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
        action.setText(cta);
        headline.setText(headlineVal);
        if(icon != null){
            frameImage.setVisibility(VISIBLE);
            image.setImageDrawable(icon.getDrawable());
        }else{
            frameImage.setVisibility(GONE);
        }

        nativeAdView.setNativeAd(nativeAd);
    }
}
