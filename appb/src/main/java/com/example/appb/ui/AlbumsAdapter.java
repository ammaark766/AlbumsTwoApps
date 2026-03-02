package com.example.appb.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appb.R;
import com.example.appb.model.Album;

import java.util.ArrayList;
import java.util.List;


//the recyler view adapter for displaying albumbs in appb
//converts album objects into visible rows with title and artists
//forwards user interactions (tap/longpress) back to the fragment via the listener

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.VH> {

    //UI callbacks that the fragment implements
    //the adapter detects the interaction and the fragment deciddes what to do (edit dialog, or confirm delete)
    public interface Listener {
        void onEdit(Album album); //tapp = edit

        //for deletion
        void onLongPress(Album album);//long press the row = confirm delete
    }

    private final Listener listener;

    //backs up list for recylerview then updated with the submit() when the LiveData changed
    private final List<Album> data = new ArrayList<>();

    public AlbumsAdapter(Listener listener) {
        this.listener = listener;
    }


    //this below replaced the current list with a new list and refreshes the UI (use DiffUtill for larger lists)
    public void submit(List<Album> albums) {
        data.clear();
        if (albums != null) data.addAll(albums);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //inflates one row layout (in item_album.xml)
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new VH(row);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {

        //binds the current album into the row UI
        Album album = data.get(position);
        holder.tvTitle.setText(album.getTitle());
        holder.tvArtist.setText(album.getArtist());


        // tap then edit album (fragment will show the edit dialog)
        holder.itemView.setOnClickListener(v -> listener.onEdit(album));

        //long press = start the delete flow (fragment handles the confirmation and deletion)
        holder.itemView.setOnLongClickListener(v -> {
            listener.onLongPress(album);
            return true; // consumes the long press event
        });
    }


    //viewHolder caches view references so that the recylervieew can reuse row views more efficjently
    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvArtist;

        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvArtist = itemView.findViewById(R.id.tvArtist);
        }
    }
}