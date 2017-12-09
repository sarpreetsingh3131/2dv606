package dv606.sb223ce.assignment2;

import android.app.ListActivity;;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.*;

public class MainActivity extends ListActivity {

    private List<String> activities = new ArrayList<String>();
    private Map<String, Class> nameToClass = new HashMap<String, Class>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (Class element : Arrays.asList(MyCountriesActivity.class, MyCountriesPreferenceActivity.class,
                AlarmClockActivity.class, MP3PlayerActivity.class)) {
            activities.add(element.getSimpleName());
            nameToClass.put(element.getSimpleName(), element);
        }
        setListAdapter(new ArrayAdapter<String>(this, R.layout.activity_main, activities));
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String activityName = activities.get(i);
                Class className = nameToClass.get(activityName);
                Intent intent = new Intent(MainActivity.this, className);
                startActivity(intent);
            }
        });
    }
}