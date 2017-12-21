package dv606.sb223ce.news;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

public class MainActivity extends ListActivity {

    private ArticleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ArticleAdapter(getLayoutInflater());
        setListAdapter(adapter);
        fetch(NewsPreferences.loadSortPref(getApplicationContext()), null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchView.clearFocus();
                fetch("", s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.subscription:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.refresh:
                fetch(NewsPreferences.loadSortPref(getApplicationContext()), null);
                return true;
            case R.id.topNews:
                fetch("top", null);
                return true;
            case R.id.latestNews:
                fetch("latest", null);
                return true;
            case R.id.popularNews:
                fetch("popular", null);
                return true;
            default:
                return super.onMenuItemSelected(featureId, item);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(adapter.getItem(position).url)));
    }

    void fetch(String sortBy, String search) {
        try {
            adapter.clear();
            if (search != null) new ArticleRetriever(adapter).execute("", "", search);
            else {
                NewsPreferences.saveSortPref(getApplicationContext(), sortBy);
                for (String source : NewsPreferences.loadSourcePref(getApplicationContext())) {
                    new ArticleRetriever(adapter).execute(sortBy, source, "");
                }
            }
        } catch (UnsupportedOperationException e) {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
        }
    }
}