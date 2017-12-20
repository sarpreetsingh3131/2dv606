package dv606.sb223ce.assignment3;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class RoadMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_road_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_road_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String url = "";
        switch (item.getItemId()) {
            case R.id.roadMapVaxToStkItem:
                url = "http://cs.lnu.se/android/VaxjoToStockholm.kml";
                break;
            case R.id.roadMapVaxToCphItem:
                url = "http://cs.lnu.se/android/VaxjoToCopenhagen.kml";
                break;
            case R.id.roadMapVaxToOdsItem:
                url = "http://cs.lnu.se/android/VaxjoToOdessa.kml";
        }

        if (WeatherWidgetService.isNetworkAvailable(getApplicationContext())) {
            try {
                new RoadMapRetriever().execute(new URL(url));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();

        return super.onOptionsItemSelected(item);
    }

    class RoadMapRetriever extends AsyncTask<URL, Void, RoadMap> {

        @Override
        protected RoadMap doInBackground(URL... urls) {
            return new RoadMapHandler().getRoadMap(urls[0]);
        }

        @Override
        protected void onPostExecute(RoadMap roadMap) {
            mMap.clear();
            mMap.addPolyline(new PolylineOptions().width(5).color(Color.RED).addAll(roadMap.coordinates));
            mMap.addMarker(new MarkerOptions().position(roadMap.startLatLng).title(roadMap.start));
            mMap.addMarker(new MarkerOptions().position(roadMap.endLatLng).title(roadMap.end));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(roadMap.cameraLtnLng, roadMap.zoom));
        }
    }


    class RoadMapHandler extends DefaultHandler {

        private static final String NAME = "name", COORDINATES = "coordinates";
        private StringBuilder builder = new StringBuilder();
        private RoadMap roadMap = null;

        RoadMap getRoadMap(URL url) {
            RoadMapHandler handler = new RoadMapHandler();
            try {
                URLConnection urlConnection = url.openConnection();
                InputStream input = urlConnection.getInputStream();
                Xml.parse(input, Xml.Encoding.UTF_8, handler);
                input.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return handler.roadMap;
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            builder.append(ch, start, length);
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            roadMap = new RoadMap();
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if (localName.equals(NAME) && !roadMap.coordinates.isEmpty()) {
                if (roadMap.start == null) roadMap.start = builder.toString().trim();
                else roadMap.setEnd(builder.toString().trim());
            } else if (localName.equals(COORDINATES) && roadMap.start == null) {
                for (String coordinate : builder.toString().trim().split(" ")) {
                    LatLng latLng = new LatLng(Double.valueOf(coordinate.split(",")[1]), Double.valueOf(coordinate.split(",")[0]));
                    roadMap.coordinates.add(latLng);
                }
            } else if (localName.equals(COORDINATES) && roadMap.start != null) {
                LatLng latLng = new LatLng(Double.valueOf(builder.toString().trim().split(",")[1]), Double.valueOf(builder.toString().trim().split(",")[0]));
                if (roadMap.startLatLng == null) roadMap.startLatLng = latLng;
                else roadMap.endLatLng = latLng;
            }
            builder.setLength(0);
        }
    }

    class RoadMap {

        String start, end;
        LatLng startLatLng, endLatLng, cameraLtnLng = null;
        float zoom = 0;
        ArrayList<LatLng> coordinates = new ArrayList<>();

        void setEnd(String name) {
            if (name.equalsIgnoreCase("Stockholm")) {
                cameraLtnLng = new LatLng(58.3732614468956, 16.254681833088398);
                zoom = 7;
            } else if (name.equalsIgnoreCase("Copenhagen")) {
                cameraLtnLng = new LatLng(56.60960076605178, 13.87255433946848);
                zoom = 7;
            } else {
                cameraLtnLng = new LatLng(53.01266670217281, 18.891438469290733);
                zoom = 4;
            }
            end = name;
        }
    }
}
