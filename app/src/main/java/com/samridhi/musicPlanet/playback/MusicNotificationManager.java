package com.samridhi.musicPlanet.playback;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.VectorDrawable;
import android.media.session.MediaSession;
import android.os.Build;
import android.text.Spanned;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.samridhi.musicPlanet.MainActivity;
import com.samridhi.musicPlanet.R;
import com.samridhi.musicPlanet.models.Song;
import com.samridhi.musicPlanet.Utils;

public class MusicNotificationManager {

    public static final int NOTIFICATION_ID = 101;
    static final String PLAY_PAUSE_ACTION = "com.samridhi.musicPlanet.PLAYPAUSE";
    static final String NEXT_ACTION = "com.samridhi.musicPlanet.NEXT";
    static final String PREV_ACTION = "com.samridhi.musicPlanet.PREV";
    private final String CHANNEL_ID = "com.samridhi.musicPlanet.CHANNEL_ID";
    private final int REQUEST_CODE = 100;
    private final NotificationManager mNotificationManager;
    private final MusicService mMusicService;
    private NotificationCompat.Builder mNotificationBuilder;
    private int mAccent;
    private MediaSession mediaSession;

    MusicNotificationManager(@NonNull final MusicService musicService) {
        mMusicService = musicService;
        mNotificationManager = (NotificationManager) mMusicService.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void setAccentColor(int color) {
        mAccent = ContextCompat.getColor(mMusicService, color);
    }

    public NotificationManager getNotificationManager() {
        return mNotificationManager;
    }

    public NotificationCompat.Builder getNotificationBuilder() {
        return mNotificationBuilder;
    }

    private PendingIntent playerAction(String action) {

        Intent pauseIntent = new Intent();
        pauseIntent.setAction(action);

        return PendingIntent.getBroadcast(mMusicService, REQUEST_CODE, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public Notification createNotification() {

        Song song = mMusicService.getMediaPlayerHolder().getCurrentSong();

        mNotificationBuilder = new NotificationCompat.Builder(mMusicService, CHANNEL_ID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        Intent openPlayerIntent = new Intent(mMusicService, MainActivity.class);
        openPlayerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(mMusicService, REQUEST_CODE,
                openPlayerIntent, 0);

        String artist = song.artistName;
        String songTitle = song.title;

        Spanned spanned = Utils.buildSpanned(mMusicService.getString(R.string.playing_song, artist, songTitle));

        mNotificationBuilder
                .setShowWhen(false)
                .setSmallIcon(R.drawable.music_notification)
                .setLargeIcon(getLargeIcon())
                .setColor(mAccent)
                .setContentTitle(spanned)
                .setContentText(song.albumName)
                .setContentIntent(contentIntent)
                .addAction(notificationAction(PREV_ACTION))
                .addAction(notificationAction(PLAY_PAUSE_ACTION))
                .addAction(notificationAction(NEXT_ACTION))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        mNotificationBuilder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2));
        return mNotificationBuilder.build();
    }

    @NonNull
    private NotificationCompat.Action notificationAction(String action) {

        int icon;

        switch (action) {
            default:
            case PREV_ACTION:
                icon = R.drawable.ic_skip_previous_notification;
                break;
            case PLAY_PAUSE_ACTION:

                icon = mMusicService.getMediaPlayerHolder().getState() != PlaybackInfoListener.State.PAUSED ? R.drawable.ic_pause_notification : R.drawable.ic_play_notification;
                break;
            case NEXT_ACTION:
                icon = R.drawable.ic_skip_next_notification;
                break;
        }
        return new NotificationCompat.Action.Builder(icon, action, playerAction(action)).build();
    }

    @RequiresApi(26)
    private void createNotificationChannel() {

        if (mNotificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            NotificationChannel notificationChannel =
                    new NotificationChannel(CHANNEL_ID,
                            mMusicService.getString(R.string.app_name),
                            NotificationManager.IMPORTANCE_LOW);

            notificationChannel.setDescription(
                    mMusicService.getString(R.string.app_name));

            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setShowBadge(false);

            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private Bitmap getLargeIcon() {

        VectorDrawable vectorDrawable = (VectorDrawable) mMusicService.getDrawable(R.drawable.music_notification);

        int largeIconSize = mMusicService.getResources().getDimensionPixelSize(R.dimen.notification_large_dim);
        Bitmap bitmap = Bitmap.createBitmap(largeIconSize,
                largeIconSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        if (vectorDrawable != null) {
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.setTint(mAccent);
            vectorDrawable.setAlpha(100);
            vectorDrawable.draw(canvas);
        }

        return bitmap;
    }
}
