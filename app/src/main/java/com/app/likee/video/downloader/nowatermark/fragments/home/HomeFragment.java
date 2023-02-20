package com.app.likee.video.downloader.nowatermark.fragments.home;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.likee.video.downloader.nowatermark.MainActivity;
import com.app.likee.video.downloader.nowatermark.R;
import com.app.likee.video.downloader.nowatermark.ads.AdsDownloadDialog;
import com.app.likee.video.downloader.nowatermark.ads.AdsHomeFragment;
import com.app.likee.video.downloader.nowatermark.fragments.player.VideoPlayerActivity;
import com.app.likee.video.downloader.nowatermark.models.DownloadModel;
import com.app.likee.video.downloader.nowatermark.widgets.DownloadDialogFragments;
import com.app.likee.video.downloader.nowatermark.widgets.ResponseDownload;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

public class HomeFragment extends Fragment {
    private static final String TAG = HomeFragment.class.getSimpleName();
    private String URL = "url";
    private HomeViewModel homeViewModel;

    private TextView username, caption;
    private ImageView thumb, avatar, imageBtnPreview, how_to_1, how_to_2, how_to_3;
    private EditText editTextValue;
    private FrameLayout downloadItemPreview, btnItemPreview, pasteLink, downloadLink, howToWrapper, adsLayout;
    private AdsHomeFragment adsHomeFragment;
    private AdRequest adRequest;
    private InterstitialAd mInterstitialAd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        setUpUI(view);
        setupViewModel();
        hendleButtonClick();
        adLoad();
        setupInteristial();
        return view;
    }

    private void setupInteristial(){
        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId(getString(R.string.ads_interistial));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                Log.d("Interisial", "Is Loaded");
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                Log.d("Interisial", "Is Failed to Load | Error :" + adError.getMessage());
            }

            @Override
            public void onAdOpened() {
                Log.d("Interisial", "Is Opened");
            }

            @Override
            public void onAdClicked() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                Log.d("Interisial", "Clicked");
            }

            @Override
            public void onAdLeftApplication() {
            }

            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                Log.d("Interisial", "is Closed");
            }
        });
    }

    private void setupViewModel(){
        homeViewModel = ViewModelProviders.of(requireActivity()).get(HomeViewModel.class);

        homeViewModel.getData().observe(getParentFragment().getViewLifecycleOwner(), data -> {
            if(data != null){
                setupLayout(data);
                downloadItemPreview.setVisibility(View.VISIBLE);
                howToWrapper.setVisibility(View.GONE);
            }else{
                downloadItemPreview.setVisibility(View.GONE);
                howToWrapper.setVisibility(View.VISIBLE);
            }
        });

        homeViewModel.isLoading().observe(getParentFragment().getViewLifecycleOwner(), isLoading -> {
            ((MainActivity) getActivity()).toggleLoading(isLoading);
            if(isLoading == false){
                if(homeViewModel.isError()){
                    Toast.makeText(getContext(), homeViewModel.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    homeViewModel.setIsError(false);
                }
            }
        });

        homeViewModel.getUrlDownload().observe(getParentFragment().getViewLifecycleOwner(), url -> {
            editTextValue.setText(url);
        });

        homeViewModel.getIsDownloaded().observe(getParentFragment().getViewLifecycleOwner(), isDownloaded ->{
            if(isDownloaded){
                btnItemPreview.setOnClickListener(prviewListener);
                imageBtnPreview.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
            }else{
                imageBtnPreview.setImageDrawable(getResources().getDrawable(R.drawable.ic_download));
                btnItemPreview.setOnClickListener(downloadListener);
            }
        });
    }

    private void setUpUI(View view){
        username = view.findViewById(R.id.usernamePreview);
        caption = view.findViewById(R.id.captionPreview);
        thumb = view.findViewById(R.id.thumbPreview);
        avatar = view.findViewById(R.id.avatarPreview);
        editTextValue = view.findViewById(R.id.editTextValue);
        downloadItemPreview = view.findViewById(R.id.downloadItemPreview);
        btnItemPreview = view.findViewById(R.id.btnItemPreview);
        imageBtnPreview = view.findViewById(R.id.imageBtnPreview);
        pasteLink = view.findViewById(R.id.pasteLink);
        howToWrapper = view.findViewById(R.id.howToWrapper);
        downloadLink = view.findViewById(R.id.downloadLink);
        adsLayout = view.findViewById(R.id.adsLayout);
        adsHomeFragment = view.findViewById(R.id.adView);
        how_to_1 = view.findViewById(R.id.how_to_1);
        how_to_2 = view.findViewById(R.id.how_to_2);
        how_to_3 = view.findViewById(R.id.how_to_3);

        Glide.with(getContext()).asBitmap().load("https://i.ibb.co/K0HhXK1/how-to-1.png").into(how_to_1);
        Glide.with(getContext()).asBitmap().load("https://i.ibb.co/XWZ957C/how-to-2.png").into(how_to_2);
        Glide.with(getContext()).asBitmap().load("https://i.ibb.co/QPVzJxJ/how-to-3.png").into(how_to_3);
    }

    private View.OnClickListener downloadListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DownloadDialogFragments dialog = new DownloadDialogFragments();
            ResponseDownload rd = size -> {
                Log.d(TAG, ""+size);
                if(size >= 7000000){
                    Log.d(TAG, "Show interisitial");
                    showInteristial();
                }
            };
            dialog.setResponseDownload(rd);
            dialog.show(getChildFragmentManager(), "Download Dialog Fragments");
        }
    };

    private View.OnClickListener prviewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "Download Listener");
            Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
            intent.putExtra("avatar", homeViewModel.getData().getValue().getAvatar());
            intent.putExtra("filePath", homeViewModel.getData().getValue().getFilePath());
            intent.putExtra("username", homeViewModel.getData().getValue().getUsername());
            intent.putExtra("deeplink", homeViewModel.getData().getValue().getDeeplink());
            intent.putExtra("caption", homeViewModel.getData().getValue().getCaption());
            getActivity().startActivity(intent);
        }
    };

    private void setupLayout(DownloadModel data){
        username.setText("@"+data.getUsername());
        caption.setText(data.getCaption());
        Glide.with(getActivity()).asBitmap().load(data.getThumb()).into(thumb);
        Glide.with(getActivity()).asBitmap().load(data.getAvatar()).into(avatar);
    }

    private void hendleButtonClick(){
        pasteLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager)  getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                if(!(clipboardManager.hasPrimaryClip())){
                    Toast.makeText(getContext(), "Empty Clipboard!", Toast.LENGTH_SHORT).show();
                }else if(!(clipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))){
                    Toast.makeText(getContext(), "No URL Likee in Clipboard!", Toast.LENGTH_SHORT).show();
                }else{
                    ClipData.Item item = clipboardManager.getPrimaryClip().getItemAt(0);
                    String data = item.getText().toString();
                    URL = data;
                    editTextValue.setText(data);
                }
            }
        });

        downloadLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editTextValue.getText().equals("") && editTextValue.getText().length() > 0){
                    homeViewModel.setUrlDownload(editTextValue.getText().toString());
                    homeViewModel.execute(editTextValue.getText().toString());
                    downloadItemPreview.setVisibility(View.GONE);
                }else{
                    Toast.makeText(getContext(), "Please insert url share", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void adLoad(){
        AdLoader adLoader = new AdLoader.Builder(getContext(), getResources().getString(R.string.ads_native))
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        adsLayout.setVisibility(View.VISIBLE);
                        adsHomeFragment.setupNativeAd(unifiedNativeAd);
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

    public void showInteristial(){
        if(mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
        }else{
            Log.d("Interistial", "The interstitial wasn't loaded yet.");
        }
    }


}