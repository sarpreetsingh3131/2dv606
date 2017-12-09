package dv606.sb223ce.assignment1;

import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class WeatherActivity extends ListActivity {

    public static String TAG = "dv606.weather";
    private InputStream input;
    private WeatherReport report = null;
    private WeatherAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new WeatherAdapter(this);
        setListAdapter(adapter);

        if (isNetworkAvailable()) {
            try {
                URL url = new URL("http://www.yr.no/sted/Sverige/Kronoberg/V%E4xj%F6/forecast.xml");
                AsyncTask task = new WeatherRetriever().execute(url);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
        }
    }

    //https://stackoverflow.com/questions/33929760/detect-if-android-device-is-connected-to-the-internet
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

    private class WeatherRetriever extends AsyncTask<URL, Void, WeatherReport> {

        protected WeatherReport doInBackground(URL... urls) {
            try {
                return WeatherHandler.getWeatherReport(urls[0]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        protected void onProgressUpdate(Void... progress) {
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
            ((ImageView) convertView.findViewById(R.id.weatherImageView)).setImageResource(getImageId(forecast.getWeatherCode()));
            return convertView;
        }

        int getImageId(int weatherCode) {
            return getResources().getIdentifier("ic_" + (weatherCode < 10 ? "0" + weatherCode : weatherCode), "mipmap", getPackageName());
        }
    }
}