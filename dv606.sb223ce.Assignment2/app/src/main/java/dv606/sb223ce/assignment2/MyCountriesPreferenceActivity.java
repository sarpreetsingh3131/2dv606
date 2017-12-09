package dv606.sb223ce.assignment2;

import android.preference.PreferenceActivity;

import java.util.List;

public class MyCountriesPreferenceActivity extends PreferenceActivity {

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.activity_my_countries_preference_headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return MyCountriesPreferenceFragment.class.getName().equals(fragmentName);
    }
}