package com.app.likee.video.downloader.nowatermark.database;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app.likee.video.downloader.nowatermark.R;
import com.app.likee.video.downloader.nowatermark.models.DownloadModel;

import java.io.File;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class RealmViewModel extends ViewModel {

    private final MutableLiveData<List<DownloadModel>> observerData = new MutableLiveData<List<DownloadModel>>();
    private final String TAG = RealmViewModel.class.getSimpleName();

    Realm realm;
    RealmResults<DownloadModel> results;
    RealmChangeListener<RealmResults<DownloadModel>> realmChangeListener = (results) -> {
      if(results.isLoaded() && results.isValid()){
          observerData.postValue(results);
      }
    };


    public RealmViewModel(){
        realm = Realm.getDefaultInstance();
        results = realm.where(DownloadModel.class).findAll().sort("id", Sort.DESCENDING);
        observerData.postValue(results);
        results.addChangeListener(realmChangeListener);
    }

    public LiveData<List<DownloadModel>> getDownloadData(){
        return observerData;
    }

    public void addData(DownloadModel data){
        Number currentnum= realm.where(DownloadModel.class).max("id");
        int nextId;
        if(currentnum==null){
            nextId=1;
        }
        else{
            nextId=currentnum.intValue()+1;
        }
        data.setId(nextId);
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d(TAG, data.getFilePath());
                realm.copyToRealm(data);
            }
        }, error -> {
            Log.d(TAG, error.getMessage());
        });
    }

    public DownloadModel getDataByDeeplink(String deeplink){
        DownloadModel data = realm.where(DownloadModel.class).equalTo("deeplink", deeplink).findFirst();
        return data;
    }

    public DownloadModel getDataById(long id){
        DownloadModel data = realm.where(DownloadModel.class).equalTo("id", id).findFirst();
        return data;
    }

    public void bulkRemoData(List<Long> id){

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmQuery<DownloadModel> query = realm.where(DownloadModel.class);
                int i = 0;
                for(Long d : id){
                    if (i++ > 0) query = query.or();
                    query = query.equalTo("id", d);

                }
                RealmResults<DownloadModel> fileX = query.findAll();
                for(DownloadModel dx : fileX){
                    String filePath = dx.getFilePath();
                    deleteFile(filePath);
                }

                fileX.deleteAllFromRealm();
            }
        }, error -> {Log.d(TAG, error.getMessage());});
    }

    public void singleRemoveData(long id){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DownloadModel data = realm.where(DownloadModel.class).equalTo("id", id).findFirst();
                deleteFile(data.getFilePath());
                data.deleteFromRealm();
            }
        }, error -> {Log.d(TAG, error.getMessage());});
    }

    private boolean deleteFile(String file){
        File fileDelete = new File(file);
        if(fileDelete.exists()){
            fileDelete.delete();
            Log.d("GaleryFragments", "Delete file success FILE_PATH : "+fileDelete);
            return true;
        }else{
            Log.d("GaleryFragments", "Delete file error FILE_PATH : "+fileDelete);
            return false;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        results.removeAllChangeListeners();
        realm.close();
        realm = null;
    }
}
