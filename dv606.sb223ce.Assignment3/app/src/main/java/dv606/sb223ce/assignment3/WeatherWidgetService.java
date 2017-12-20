package dv606.sb223ce.assignment3;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;

public class WeatherWidgetService extends Service {

    static URL getCityURL(String city) {
        try {
            switch (city) {
                case "Stockholm":
                    return new URL("http://www.yr.no/place/Sweden/Stockholm/Stockholm/forecast.xml");
                case "Solna":
                    return new URL("http://www.yr.no/place/Sweden/Stockholm/Solna/forecast.xml");
                default:
                    return new URL("http://www.yr.no/sted/Sverige/Kronoberg/V%E4xj%F6/forecast.xml");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //https://stackoverflow.com/questions/33929760/detect-if-android-device-is-connected-to-the-internet
    static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    static int getImageId(int weatherCode, Resources res, String pack) {
        return res.getIdentifier("ic_" + (weatherCode < 10 ? "0" + weatherCode : weatherCode), "mipmap", pack);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        String city = WeatherWidgetConfigureActivity.loadTitlePref(getApplicationContext(), id);
        if (city != null) {
            if (isNetworkAvailable(getApplicationContext())) {
                WeatherRetriever retriever = new WeatherRetriever(id, city);
                retriever.execute(getCityURL(city));
            } else
                Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
        }
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    void registerForRefreshing(int id, RemoteViews views) {
        Intent in = new Intent(getApplicationContext(), WeatherWidgetService.class);
        in.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        in.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
        PendingIntent pen = PendingIntent.getService(getApplicationContext(), id, in, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.weatherWidgetRefreshBtn, pen);
    }

    void registerForGoingToWeatherActivity(int id, RemoteViews views, String city) {
        final Intent in = new Intent(getApplicationContext(), WeatherActivity.class);
        in.putExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, city);
        final PendingIntent pen = PendingIntent.getActivity(getApplicationContext(), id, in, 0);
        views.setOnClickPendingIntent(R.id.weatherWidget, pen);
    }

    class WeatherRetriever extends AsyncTask<URL, Void, WeatherReport> {

        private String city;
        private int id;

        WeatherRetriever(int id, String city) {
            this.id = id;
            this.city = city;
        }

        protected WeatherReport doInBackground(URL... urls) {
            try {
                return WeatherHandler.getWeatherReport(urls[0]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        protected void onPostExecute(WeatherReport result) {
            if (result != null) {
                RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.weather_widget);
                registerForGoingToWeatherActivity(id, views, city);
                registerForRefreshing(id, views);
                Toast.makeText(getApplicationContext(), "WeatherRetriever task finished", Toast.LENGTH_LONG).show();
                views.setTextViewText(R.id.weatherWidgetCityTextView, city);
                WeatherForecast forecast = result.getForecasts().get(0);
                views.setTextViewText(R.id.weatherWidgetTempTextView, forecast.getTemperature() + "° C");
                views.setImageViewResource(R.id.weatherWidgetImageView, getImageId(forecast.getWeatherCode(), getResources(), getPackageName()));
                AppWidgetManager.getInstance(getApplicationContext()).updateAppWidget(id, views);
            } else
                Toast.makeText(getApplicationContext(), "WeatherRetriever task failed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}