package com.example.appb.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.appb.R;
import com.example.appb.model.Album;

public class AddEditAlbumDialog extends DialogFragment {

    // arguments keys for passing the data into the dialog (edit mode)
    //if however no arguments are provided, dialog behaves as add only
    private static final String EXTRA_ID = "extra_id";
    private static final String EXTRA_TITLE = "extra_title";
    private static final String EXTRA_ARTIST = "extra_artist";

    // Saved state keys so the users typed texts survive during(rotation while user is typing)
    private static final String KEY_DRAFT_TITLE = "key_draft_title";
    private static final String KEY_DRAFT_ARTIST = "key_draft_artist";

    private EditText etTitle;
    private EditText etArtist;

    //method for creating dialog for adding a new album
    public static AddEditAlbumDialog newAdd() {
        return new AddEditAlbumDialog();
    }


    //method to create a dialog for editing an existing album via the pre filled fields via args
    public static AddEditAlbumDialog newEdit(Album album) {
        AddEditAlbumDialog d = new AddEditAlbumDialog();
        Bundle b = new Bundle();
        b.putLong(EXTRA_ID, album.getId());
        b.putString(EXTRA_TITLE, album.getTitle());
        b.putString(EXTRA_ARTIST, album.getArtist());
        d.setArguments(b);
        return d;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save what the user has typed so far
        if (etTitle != null) outState.putString(KEY_DRAFT_TITLE, etTitle.getText().toString());
        if (etArtist != null) outState.putString(KEY_DRAFT_ARTIST, etArtist.getText().toString());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        //infaltes a smple layout with two edittexts (title and artist)
        View v = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_edit_album, null);

        etTitle = v.findViewById(R.id.etTitle);
        etArtist = v.findViewById(R.id.etArtist);

        //if Extra_ID exists, we are editing otherwise we are adding
        Bundle extras = getArguments();
        long id = (extras != null) ? extras.getLong(EXTRA_ID, -1) : -1;

        // 1) First restore what the user was typing (if rotated during a mid edit)
        if (savedInstanceState != null) {
            etTitle.setText(savedInstanceState.getString(KEY_DRAFT_TITLE, ""));
            etArtist.setText(savedInstanceState.getString(KEY_DRAFT_ARTIST, ""));
        }
        // 2) Otherwise load the first values from the "extras" (edit mode)
        else if (extras != null) {
            etTitle.setText(extras.getString(EXTRA_TITLE, ""));
            etArtist.setText(extras.getString(EXTRA_ARTIST, ""));
        }


        //shared viewmodel (activity scope) so this dialog can trigger the add/update
        //an the list fragment will update via the LiveData
        AlbumsViewModel vm = new ViewModelProvider(requireActivity()).get(AlbumsViewModel.class);

        AlertDialog dlg = new AlertDialog.Builder(requireContext())
                .setTitle(id == -1 ? "Add Album" : "Edit Album")
                .setView(v)

                //below sets the positiv button listener to null so that the dialog does not auto dismiss
                //this now lets us validate the fields and also keep the dialog open if the input is invalid
                .setPositiveButton("Save", null)   // <-- change: null
                .setNegativeButton("Cancel", null)
                .create();

        dlg.setOnShowListener(d -> {
            //overrides the "save" click so we can validate input before dissmising it
            dlg.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(btn -> {
                String title = etTitle.getText().toString().trim();
                String artist = etArtist.getText().toString().trim();

                //validation to prevent blank/empty rows from being saved
                boolean ok = true;

                if (title.isEmpty()) {
                    etTitle.setError("Required");
                    ok = false;
                } else {
                    etTitle.setError(null);
                }

                if (artist.isEmpty()) {
                    etArtist.setError("Required");
                    ok = false;
                } else {
                    etArtist.setError(null);
                }

                if (!ok) return; // this keeps dialog open so you can see errors / so user can correct the input

                if (id == -1) vm.addAlbum(title, artist);
                else vm.updateAlbum(id, title, artist);

                dlg.dismiss(); // this closes only when valid
            });
        });

        return dlg;
    }
}