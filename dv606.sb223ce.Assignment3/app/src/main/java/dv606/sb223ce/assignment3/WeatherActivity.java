package dv606.sb223ce.assignment3;

import android.app.ListActivity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;

public class WeatherActivity extends ListActivity {

    public static String TAG = "dv606.WeatherActivity";
    private WeatherReport report = null;
    private WeatherAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new WeatherAdapter(this);
        setListAdapter(adapter);
        String city = getIntent().getStringExtra(AppWidgetManager.EXTRA_CUSTOM_INFO);
        setTitle(city + " Weather");
        if (WeatherWidgetService.isNetworkAvailable(getApplicationContext()))
            new WeatherRetriever().execute(WeatherWidgetService.getCityURL(city));
        else
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
    }

    private void printReportToLog() {
        if (this.report != null) {
            for (WeatherForecast forecast : report) {
                adapter.add(forecast);
            }
        } else {
            Log.e(TAG, "Weather report has not been loaded.");
        }
    }

    class WeatherRetriever extends AsyncTask<URL, Void, WeatherReport> {

        protected WeatherReport doInBackground(URL... urls) {
            try {
                return WeatherHandler.getWeatherReport(urls[0]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        protected void onPostExecute(WeatherReport result) {
            Toast.makeText(getApplicationContext(), "WeatherRetriever task finished", Toast.LENGTH_LONG).show();
            report = result;
            printReportToLog();
        }
    }

    class WeatherAdapter extends ArrayAdapter<WeatherForecast> {

        WeatherAdapter(Context context) {
            super(context, R.layout.activity_weather);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.activity_weather, parent, false);
            }
            // add data
            WeatherForecast forecast = getItem(position);
            ((TextView) convertView.findViewById(R.id.weatherDateTextView)).setText(forecast.getStartYYMMDD());
            ((TextView) convertView.findViewById(R.id.weatherTimeTextView)).setText(forecast.getStartHHMM() + " - " + forecast.getEndHHMM());
            ((TextView) convertView.findViewById(R.id.weatherTempTextView)).setText("" + forecast.getTemperature() + "° C");
            ((TextView) convertView.findViewById(R.id.weatherWindTextView)).setText(forecast.getWindSpeed() + "m/s, " + forecast.getWindDirection());
            ((TextView) convertView.findViewById(R.id.weatherRainTextView)).setText(forecast.getRain() + "mm/h");
            ((ImageView) convertView.findViewById(R.id.weatherImageView)).setImageResource(WeatherWidgetService.getImageId(forecast.getWeatherCode(), getResources(), getPackageName()));
            return convertView;
        }
    }
}