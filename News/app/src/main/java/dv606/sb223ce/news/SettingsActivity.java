package dv606.sb223ce.news;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;

public class SettingsActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new SourceAdapter(this, getLayoutInflater()));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }
}