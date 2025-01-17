package com.samridhi.musicPlanet.loaders;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.samridhi.musicPlanet.models.Album;
import com.samridhi.musicPlanet.models.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class AlbumProvider {

    @NonNull
    static List<Album> retrieveAlbums(@Nullable final List<Song> songs) {
        List<Album> albums = new ArrayList<>();
        if (songs != null) {
            for (Song song : songs) {
                getAlbum(albums, song.albumName).songs.add(song);
            }
        }
        if (albums.size() > 1) {
            sortAlbums(albums);
        }
        return albums;
    }

    private static void sortAlbums(List<Album> albums) {
        Collections.sort(albums, new Comparator<Album>() {
            public int compare(Album obj1, Album obj2) {
                return Integer.compare(obj1.getYear(), obj2.getYear());
            }
        });
    }

    private static Album getAlbum(List<Album> albums, String albumName) {
        for (Album album : albums) {
            if (!album.songs.isEmpty() && album.songs.get(0).albumName.equals(albumName)) {
                return album;
            }
        }
        Album album = new Album();
        albums.add(album);
        return album;
    }
}
