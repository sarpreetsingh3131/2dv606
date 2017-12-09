package dv606.sb223ce.assignment1;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.util.*;

public class MainActivity extends ListActivity {

    private List<String> activities = new ArrayList<String>();
    private Map<String, Class> nameToClass = new HashMap<String, Class>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // add activities
        for (Class element : Arrays.asList(RandomActivity.class, BMIActivity.class, MyCountriesActivity.class,
                WeatherActivity.class, BeerPagerActivity.class)) {
            activities.add(element.getSimpleName());
            nameToClass.put(element.getSimpleName(), element);
        }

        setListAdapter(new ArrayAdapter<String>(this, R.layout.activity_main, activities));

        // open clicked activity
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String activityName = activities.get(i);
                Class className = nameToClass.get(activityName);
                Intent intent = new Intent(MainActivity.this, className);
                MainActivity.this.startActivity(intent);
            }
        });
    }
}
