package dv606.sb223ce.assignment1;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyCountriesActivity extends ListActivity {

    private List<String> countries = new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ArrayAdapter<String>(this, R.layout.activity_my_countries, countries);
        setListAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_countries, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Open AddCountryActivity if the button is pressed
        switch (item.getItemId()) {
            case R.id.myCountriesAddCountryItem:
                Intent intent = new Intent(this, AddCountryActivity.class);
                startActivityForResult(intent, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Get data (Country and Year)
        if (resultCode == Activity.RESULT_OK) {
            countries.add(data.getStringExtra("data"));
            adapter.notifyDataSetChanged();
        }
    }
}