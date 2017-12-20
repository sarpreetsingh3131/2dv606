package dv606.sb223ce.assignment3;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class CityMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<City> cities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        readFile();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        for (City city : cities) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(city.lat, city.lng)).title(city.name));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.7321273, 13.5194958), 8));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        final Marker dotMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_01)).anchor(0.5f, 0.5f).position(mMap.getCameraPosition().target));
        final Toast toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                dotMarker.setPosition(mMap.getCameraPosition().target);
                float[] distance = new float[1];
                for (City city : cities) {
                    Location.distanceBetween(city.lat, city.lng, mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude, distance);
                    city.distance = distance[0];
                }
                Collections.sort(cities);
                toast.setText(cities.get(0).name + " is closet (" + String.format("%.1f", cities.get(0).distance / 1000) + " Km)");
                toast.show();
            }
        });
    }

    void readFile() {
        Scanner scan = new Scanner(getApplicationContext().getResources().openRawResource(R.raw.cities));
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            cities.add(new City(line.split(";")[0], line.split(";")[1], line.split(";")[2]));
        }
    }

    class City implements Comparable<City> {

        String name;
        double lat, lng;
        float distance;

        City(String name, String lat, String lng) {
            this.name = name;
            this.lat = Double.valueOf(lat);
            this.lng = Double.valueOf(lng);
        }

        @Override
        public int compareTo(City city) {
            return (int) (distance - city.distance);
        }
    }
}