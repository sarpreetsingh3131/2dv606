package dv606.sb223ce.assignment2;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MP3PlayerActivity extends Activity {

    private MP3PlayerService service;
    private MP3PlayerActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp3_player);
        Intent intent = new Intent(this, MP3PlayerService.class);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        activity = this;
    }

    // create notification when exit the activity with menu back button
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (service != null && service.isPlaying())
            service.createNotification();
        else if (service != null) service.reset();
        return super.onMenuItemSelected(featureId, item);
    }

    // create notification when exit the activity with back button
    @Override
    public void onBackPressed() {
        if (service != null && service.isPlaying())
            service.createNotification();
        else if (service != null) service.reset();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    class GetSongs extends AsyncTask<Integer, Integer, ArrayList<Song>> {

        boolean isStorageAvailable() {
            return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        }

        // fetch songs
        @Override
        protected ArrayList<Song> doInBackground(Integer... integers) {
            ArrayList<Song> songs = new ArrayList<Song>();
            if (!isStorageAvailable()) {
                return songs;
            }

            Cursor music = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{
                            MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
                            MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA},
                    MediaStore.Audio.Media.IS_MUSIC + " > 0 ", null, null);

            if (music.getCount() > 0) {
                music.moveToFirst();
                Song prev = null;
                do {
                    Song song = new Song(music.getString(0), music.getString(1), music.getString(2), music.getString(3));
                    if (prev != null) {
                        prev.setNext(song);
                        song.setPrevious(prev);
                    }
                    prev = song;
                    songs.add(song);
                } while (music.moveToNext());
                prev.setNext(songs.get(0));
                songs.get(0).setPrevious(songs.get(songs.size() - 1));
            }
            music.close();
            return songs;
        }

        // add events and display songs
        @Override
        protected void onPostExecute(ArrayList<Song> songs) {
            TextView title = (TextView) findViewById(R.id.mp3PlayerSongTitleTextView);
            ListView listView = (ListView) findViewById(R.id.mp3PlayerListView);
            listView.setAdapter(new PlayListAdapter(activity, songs));
            service.handleListView(listView, songs, title);
            service.handlePlayButton((Button) findViewById(R.id.mp3PlayerPlayButton), title, songs);
            service.handleNextAndPreviousButton(new Button[]{
                            (Button) findViewById(R.id.mp3PlayerNextButton), (Button) findViewById(R.id.mp3PlayerPreviousButton)}
                    , title, songs);
            if (service.isPlaying()) {
                service.resetView(title, ((Button) findViewById(R.id.mp3PlayerPlayButton)));
            }
        }
    }


    class PlayListAdapter extends ArrayAdapter<Song> {

        public PlayListAdapter(Context context, ArrayList<Song> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View row, ViewGroup parent) {
            Song data = getItem(position);
            row = getLayoutInflater().inflate(R.layout.layout_row, parent, false);
            TextView name = (TextView) row.findViewById(R.id.mp3PlayerTextView);
            name.setText(String.valueOf(data));
            row.setTag(data);
            return row;
        }
    }

    // connect to service and fetch songs
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = ((MP3PlayerService.MP3PlayerBinder) iBinder).getMP3PlayerBinder();
            new GetSongs().execute(0);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            service = null;
        }
    };
}