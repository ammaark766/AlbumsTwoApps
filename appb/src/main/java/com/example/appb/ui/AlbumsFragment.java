package com.example.appb.ui;

import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appb.R;
import com.example.appb.model.Album;
import com.example.appb.provider.AlbumsContract;

public class AlbumsFragment extends Fragment {

    //The viewmodel owns data and runs provider calls based of the UI thread
    private AlbumsViewModel vm;

    //adapter here displays albums in the recylerview and forwards the row interactions ack to this fragment
    private AlbumsAdapter adapter;

    //content observer lets appb reach when app a calls notifychange() on the albums URI
    private final ContentObserver observer = new ContentObserver(new Handler(Looper.getMainLooper())) {
        @Override
        public void onChange(boolean selfChange) {
            vm.loadAlbums(); // refresh when App A notifies changes
        }
    };

    public AlbumsFragment() {
        //this fragment shows the albums list and allows add/edit/delete via  dialougs
        super(R.layout.fragment_albums);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        //scoped viewmodel for this screen which is a fragment
        vm = new ViewModelProvider(this).get(AlbumsViewModel.class);

        //setup for the recyclerview ( lists UI only no buisness logic)
        RecyclerView recycler = view.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));


        //callbacks for adapters tap (for editing album ,and long press for deleting)
        adapter = new AlbumsAdapter(new AlbumsAdapter.Listener() {
            @Override
            public void onEdit(Album album) {

                //shows dialog which is prefilled with the selected album
                AddEditAlbumDialog.newEdit(album).show(getParentFragmentManager(), "edit");
            }


            //deletion for album
            @Override
            public void onLongPress(Album album) {

                //confirm before deleting to avoid accidental long press UI)
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Delete album?")
                        .setMessage(album.getTitle() + " - " + album.getArtist())
                        .setPositiveButton("Delete", (d, which) -> vm.deleteAlbum(album.getId()))
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        recycler.setAdapter(adapter);

        //observes the albumbs list. SO when data changes update the RecyclerView
        vm.getAlbums().observe(getViewLifecycleOwner(), adapter::submit);

        //the add button shows empty dialog for creating a new album
        view.findViewById(R.id.btnAdd).setOnClickListener(v ->
                AddEditAlbumDialog.newAdd().show(getParentFragmentManager(), "add")
        );

        // Register the content observer so appb reloads when appa changes albums
        //also the true flag here means we also observe child URIS like /albums/123
        requireContext().getContentResolver().registerContentObserver(
                AlbumsContract.CONTENT_URI,
                true,
                observer
        );

        //initial load when the screen appears
        vm.loadAlbums();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        //avoids any leaks of this fragment by unregistering the oberver when the view is destroyed
        requireContext().getContentResolver().unregisterContentObserver(observer);
    }
}