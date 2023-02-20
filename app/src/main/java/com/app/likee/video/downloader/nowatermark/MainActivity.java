package com.app.likee.video.downloader.nowatermark;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.likee.video.downloader.nowatermark.database.RealmViewModel;
import com.app.likee.video.downloader.nowatermark.fragments.home.HomeViewModel;
import com.app.likee.video.downloader.nowatermark.models.DownloadModel;
import com.app.likee.video.downloader.nowatermark.system.UiUtils;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private int currentInt;
    private TextView titleToolbar;
    private FrameLayout openLikee;
    private BottomNavigationView bottomNavView;
    private NavController navController;
    private DownloadModel data;
    private FrameLayout toggleLoading;
    private HomeViewModel homeViewModel;
    private RealmViewModel realmViewModel;
    private UiUtils uiUtils;
    private int countBack = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        uiUtils = new UiUtils(this);
        Realm.init(this);
        setupView();
        setupNavigation();
        setupViewModel();
        hendleIntent();
        hendleButton();
        if(!checkPermission()){
            requestPermission();
        }
    }

    private void hendleButton() {
        openLikee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uiUtils.launchNewActivity(v.getContext(), "video.like");
            }
        });
    }

    private void hendleIntent(){
        Intent intent = getIntent();
        if(intent.getStringExtra("sharedUrl") != null && !intent.getStringExtra("sharedUrl").isEmpty()){
            String url = intent.getStringExtra("sharedUrl");
            homeViewModel.setUrlDownload(url);
            homeViewModel.execute(url);
            intent.removeExtra("sharedUrl");
            navController.navigate(R.id.homeFragment);
        }
    }

    @Override
    public void onBackPressed() {
        if(currentInt == 1){
            currentInt = 0;
            bottomNavView.setSelectedItemId(R.id.homeFragment);
            navController.navigate(R.id.homeFragment);
        }else{
            countBack++;
            if(countBack == 2){
                if(Build.VERSION.SDK_INT>=16 && Build.VERSION.SDK_INT<21){
                    finishAffinity();
                } else if(Build.VERSION.SDK_INT>=21){
                    finishAndRemoveTask();
                }
                super.onBackPressed();
            }else{
                Toast.makeText(this, "Press back again to EXIT!", Toast.LENGTH_SHORT).show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        countBack = 0;
                    }
                }, 2000);
            }
        }
    }

    public void setupViewModel(){
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        realmViewModel = new ViewModelProvider(this).get(RealmViewModel.class);
        homeViewModel.getData().observe(this, data -> {
            if(data != null){
                DownloadModel x = realmViewModel.getDataByDeeplink(data.getDeeplink());
                if(x != null){
                    homeViewModel.setObserveData(x);
                    homeViewModel.setIsDownloaded(true);
                }
            }
        });
    }

    private void setupNavigation(){
        navController = Navigation.findNavController(this, R.id.navFragment);
        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.homeFragment){
                    currentInt = 0;
                    navController.navigate(R.id.homeFragment);
                }else if(item.getItemId() == R.id.galeryFragment){
                    currentInt = 1;
                    navController.navigate(R.id.galeryFragment);
                }
                return true;
            }
        });
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                titleToolbar.setText(destination.getLabel());
            }
        });
    }

    private void setupView(){
        bottomNavView = findViewById(R.id.bottomBar);
        titleToolbar = findViewById(R.id.titleToolbar);
        toggleLoading = findViewById(R.id.toggleLoading);
        openLikee = findViewById(R.id.openLikee);
    }

    public void toggleLoading(boolean isLoading){
        if(isLoading){
            toggleLoading.setVisibility(View.VISIBLE);
        }else{
            toggleLoading.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(MainActivity.this, "Please Give Permission to Download File", Toast.LENGTH_SHORT).show();
        }
        else{
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        }
    }

    private boolean checkPermission(){
        int result= ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(result== PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 101:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(MainActivity.this, "Permission Successfull", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MainActivity.this, "Permission Failed", Toast.LENGTH_SHORT).show();
                }
        }
    }
}