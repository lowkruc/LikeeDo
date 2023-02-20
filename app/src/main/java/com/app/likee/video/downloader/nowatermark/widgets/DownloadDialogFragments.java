package com.app.likee.video.downloader.nowatermark.widgets;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.app.likee.video.downloader.nowatermark.R;
import com.app.likee.video.downloader.nowatermark.ads.AdsDownloadDialog;
import com.app.likee.video.downloader.nowatermark.database.RealmViewModel;
import com.app.likee.video.downloader.nowatermark.fragments.home.HomeViewModel;
import com.app.likee.video.downloader.nowatermark.models.DownloadModel;
import com.app.likee.video.downloader.nowatermark.system.UiUtils;
import com.app.likee.video.downloader.nowatermark.system.download.DownloadModule;
import com.app.likee.video.downloader.nowatermark.system.download.DownloadResponse;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

public class DownloadDialogFragments extends DialogFragment {

    private Dialog dialog;
    private TextView username, caption, downloadPercent;
    private ImageView avatar;
    private ProgressBar progressSize;
    private HomeViewModel homeViewModel;
    private RealmViewModel realmViewModel;
    private DownloadModule downloadModule;
    private UiUtils uiUtils;
    private FrameLayout adsLayout;
    private AdsDownloadDialog adsDownloadDialog;
    private AdRequest adRequest;
    private ResponseDownload responseDownload;

    public void setResponseDownload(ResponseDownload rs){
        this.responseDownload = rs;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.widget_download_dialog_fragment, container, false);
        dialog = getDialog();
        downloadModule = new DownloadModule();
        downloadModule.setDownloadResponse(downloadResponse);
        uiUtils = new UiUtils(getActivity());
        setUpUI(view);
        setViewModel();
        adLoad();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
        }
    }


    private void setViewModel() {
        homeViewModel = ViewModelProviders.of(requireActivity()).get(HomeViewModel.class);
        realmViewModel = ViewModelProviders.of(requireActivity()).get(RealmViewModel.class);

        if(homeViewModel.getData() != null){
            caption.setText(homeViewModel.getData().getValue().getCaption());
            Glide.with(getContext()).asBitmap().load(homeViewModel.getData().getValue().getAvatar()).into(avatar);
            username.setText("@"+homeViewModel.getData().getValue().getUsername());
            downloadModule.execute(homeViewModel.getData().getValue().getUrl());

        }
        homeViewModel.getIsDownloaded().observe(getParentFragment().getViewLifecycleOwner(), isDownloaded -> {
            if(isDownloaded){
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);

            }
        });
        realmViewModel.getDownloadData().observe(getParentFragment().getViewLifecycleOwner(), data -> {
            Log.d("DownloadDialog", ""+data.size());
        });
    }


    private DownloadResponse downloadResponse = new DownloadResponse() {
        @Override
        public void onProgress(long max, long progress) {
            Log.d("DownloadDialog", ""+progress);
            progressSize.setMax((int) max);
            progressSize.setProgress((int) progress);
            int persen = (int) ((progress*100)/max);
            if(persen == max){
                downloadPercent.setText("Downloading complated.");
            }else{
                downloadPercent.setText("Downloading.. "+persen+"%");
            }

        }
        @Override
        public void onError(Exception e) {
            Log.d("DownloadDialog", e.getMessage());
            uiUtils.ToastUI(e.getMessage()).show();
        }
        @Override
        public void onComplate(String filePath, long max) {
            homeViewModel.setIsDownloaded(true);
            homeViewModel.setFilePath(filePath);
            DownloadModel d = homeViewModel.getObjectData();
            d.setFilePath(filePath);
            realmViewModel.addData(d);
            homeViewModel.setObserveData(d);
            Log.d("Dialog", d.toString());
            responseDownload.onComplate(max);
            uiUtils.ToastUI("Downloading Complated").show();
        }
    };

    private void setUpUI(View view) {
        username = view.findViewById(R.id.usernameProgress);
        caption = view.findViewById(R.id.captionProgress);
        avatar = view.findViewById(R.id.avatarDownProgress);
        progressSize = view.findViewById(R.id.progressSize);
        downloadPercent = view.findViewById(R.id.progressPercent);
        adsLayout = view.findViewById(R.id.adsLayout);
        adsDownloadDialog = view.findViewById(R.id.adView);
    }

    private void adLoad(){
        AdLoader adLoader = new AdLoader.Builder(getContext(), getResources().getString(R.string.ads_native))
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        adsLayout.setVisibility(View.VISIBLE);
                        adsDownloadDialog.setupNativeAd(unifiedNativeAd);
                    }
                })
                .withAdListener(new AdListener(){
                    @Override
                    public void onAdFailedToLoad(int i) {
                        super.onAdFailedToLoad(i);
                        Log.d("Load Failed", String.valueOf(i));
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        .build())
                .build();
        adRequest = new AdRequest.Builder().build();
        adLoader.loadAd(adRequest);
    }


}
