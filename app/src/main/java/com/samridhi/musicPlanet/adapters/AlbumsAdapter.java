package com.samridhi.musicPlanet.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.samridhi.musicPlanet.R;
import com.samridhi.musicPlanet.models.Album;
import com.samridhi.musicPlanet.playback.PlayerAdapter;

import java.util.List;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.SimpleViewHolder> {

    private final Activity mActivity;
    private final AlbumSelectedListener mAlbumSelectedListener;
    private final PlayerAdapter mPlayerAdapter;
    private final int mAccent;
    private List<Album> mAlbums;
    private Album mSelectedAlbum;

    public AlbumsAdapter(@NonNull Activity activity, List<Album> albums, PlayerAdapter playerAdapter, int accent) {
        mActivity = activity;
        mAlbums = albums;
        mPlayerAdapter = playerAdapter;
        mAccent = accent;
        mAlbumSelectedListener = (AlbumSelectedListener) mActivity;
        updateAlbumsForArtist();
    }

    public void swapArtist(List<Album> albums) {
        mAlbums = albums;
        notifyDataSetChanged();
        updateAlbumsForArtist();
    }

    private void updateAlbumsForArtist() {
        mSelectedAlbum = mPlayerAdapter.getSelectedAlbum() != null ? mPlayerAdapter.getSelectedAlbum() : mAlbums.get(0);
        mAlbumSelectedListener.onAlbumSelected(mSelectedAlbum);
    }

    @Override
    @NonNull
    public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(mActivity)
                .inflate(R.layout.album_item, parent, false);

        return new SimpleViewHolder(itemView);
    }

    private String getYear(int year) {
        return year != 0 && year != -1 ? String.valueOf(year) : mActivity.getString(R.string.unknown_year);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleViewHolder holder, int position) {

        Album album = mAlbums.get(holder.getAdapterPosition());
        String albumTitle = album.getTitle();
        holder.title.setText(albumTitle);
        holder.year.setText(getYear(album.getYear()));
    }

    @Override
    public int getItemCount() {
        return mAlbums.size();
    }

    public interface AlbumSelectedListener {
        void onAlbumSelected(Album album);
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView title, year;

        SimpleViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.album);
            year = itemView.findViewById(R.id.year);
            year.setBackgroundColor(ColorUtils.setAlphaComponent(year.getCurrentTextColor(), 10));
            itemView.setBackgroundColor(ColorUtils.setAlphaComponent(mAccent, 10));
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            //update songs list only if the album is updated
            if (mAlbums.get(getAdapterPosition()) != mSelectedAlbum) {
                mSelectedAlbum = mAlbums.get(getAdapterPosition());
                mAlbumSelectedListener.onAlbumSelected(mSelectedAlbum);
            }
        }
    }
}