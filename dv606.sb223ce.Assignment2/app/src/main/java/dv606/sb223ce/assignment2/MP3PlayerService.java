package dv606.sb223ce.assignment2;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MP3PlayerService extends Service {

    private final MediaPlayer mediaPlayer = new MediaPlayer();
    private int NOTIFICATION_ID = 1;
    private boolean isPaused = false;
    private Song currentSong = null;
    private Binder binder = new MP3PlayerBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    // reset view when clicked the notification
    void resetView(TextView title, Button btn) {
        title.setText(currentSong.getName());
        btn.setBackgroundResource(R.mipmap.ic_pause);
        stopForeground(true);
    }

    // reset all when exit the activity and song is not playing
    void reset() {
        isPaused = false;
        currentSong = null;
        mediaPlayer.stop();
        mediaPlayer.reset();
    }

    // stop everything
    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        stopForeground(true);
    }

    // set notification
    void createNotification() {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher_background).setTicker("Music").setAutoCancel(true);
        builder.setContentTitle("MP3 Player");
        Intent intent = new Intent(this, MP3PlayerActivity.class);
        PendingIntent notifIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(notifIntent);
        Notification notification = builder.build();
        startForeground(NOTIFICATION_ID, notification);
    }

    // play next/prev song
    void handleNextAndPreviousButton(Button[] buttons, final TextView title, final ArrayList<Song> songs) {
        for (final Button btn : buttons) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentSong == null && !songs.isEmpty()) currentSong = songs.get(0);
                    else
                        currentSong = btn.getTransitionName().equals("Next") ? currentSong.getNext() : currentSong.getPrevious();
                    if (mediaPlayer.isPlaying()) play(currentSong, title);
                    else title.setText(currentSong.getName());
                }
            });
        }
    }

    // play/pause song
    void handlePlayButton(final Button btn, final TextView title, final ArrayList<Song> songs) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPaused) {
                    mediaPlayer.start();
                    isPaused = false;
                    btn.setBackgroundResource(R.mipmap.ic_pause);
                } else if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    isPaused = true;
                    btn.setBackgroundResource(R.mipmap.ic_play);
                    stopForeground(true);
                } else if (!songs.isEmpty()) {
                    play(currentSong == null ? songs.get(0) : currentSong, title);
                    btn.setBackgroundResource(R.mipmap.ic_pause);
                }
            }
        });
    }

    // play clicked song
    void handleListView(ListView listView, final ArrayList<Song> songs, final TextView title) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                play(songs.get(i), title);
            }
        });
    }

    void play(final Song song, final TextView title) {
        if (song == null) return;
        try {
            currentSong = song;
            title.setText(song.getName());
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(this, Uri.parse(song.getPath()));
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    play(song.getNext(), title);
                }
            });
            mediaPlayer.start();
        } catch (Exception e) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    class MP3PlayerBinder extends Binder {
        MP3PlayerService getMP3PlayerBinder() {
            return MP3PlayerService.this;
        }
    }
}