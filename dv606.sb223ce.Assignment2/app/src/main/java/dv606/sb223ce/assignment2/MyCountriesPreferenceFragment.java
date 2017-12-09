package dv606.sb223ce.assignment2;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class MyCountriesPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_my_countries_preference);
    }
}