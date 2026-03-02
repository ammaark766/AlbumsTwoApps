package com.example.appb.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.appb.data.AlbumsRepository;
import com.example.appb.model.Album;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


// view model for appb's albums screen

// provides album data to the UI via LiveData
// Runs content resolver/provides calls of the main thread
//exposes simple methods for UI actions such as load,add,update,delete
public class AlbumsViewModel extends AndroidViewModel {

    //the repository wraps the contentresolver calls (query, insert,update,delete)
    private final AlbumsRepository repo;

    //liveData is observed by the fragments. But, when it changes the recylerview updates automatically
    private final MutableLiveData<List<Album>> albums = new MutableLiveData<>();

    //single background thread to avoid doing provider work on the UI thread
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public AlbumsViewModel(@NonNull Application application) {
        super(application);

        //contentResolver is the API appb uses to communicate with appa's contentProvider
        repo = new AlbumsRepository(application.getContentResolver());
    }

    public LiveData<List<Album>> getAlbums() {
        return albums;
    }

    //query all abumbs from appa and then publish results to the liveData
    public void loadAlbums() {
        executor.execute(() -> albums.postValue(repo.fetchAll()));
    }


    //inserts a new album via provider then refrshes the list
    public void addAlbum(String title, String artist) {
        executor.execute(() -> {
            repo.add(title, artist);
            albums.postValue(repo.fetchAll());
        });
    }


    //updates an existing album via provider then refreshes the list
    public void updateAlbum(long id, String title, String artist) {
        executor.execute(() -> {
            repo.update(id, title, artist);
            albums.postValue(repo.fetchAll());
        });
    }

    //deletes an album via provider and then refresh list
    public void deleteAlbum(long id) {
        executor.execute(() -> {
            repo.delete(id);

            //cleans up the eexecutor when VM is destroyed
            albums.postValue(repo.fetchAll());
        });
    }


    //shuts down the background theread when ViewModel goes away so you have wasted threads
    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}