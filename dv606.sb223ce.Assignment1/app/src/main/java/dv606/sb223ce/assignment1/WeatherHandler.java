package dv606.sb223ce.assignment1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Xml;

public class WeatherHandler extends DefaultHandler {

    private static final String NAME = "name";
    private static final String COUNTRY = "country";
    private static final String TIME = "time";
    private static final String LOCATION = "location";
    private static final String SYMBOL = "symbol";
    private static final String RAIN = "precipitation";
    private static final String WIND = "windDirection";
    private static final String SPEED = "windSpeed";
    private static final String TEMP = "temperature";
    private static final String LAST_UPDATE = "lastupdate";
    private static final String NEXT_UPDATE = "nextupdate";

    public static WeatherReport getWeatherReport(URL url) {
        WeatherHandler handler = new WeatherHandler();
        try {
            URLConnection urlConnection = url.openConnection();
            InputStream input = urlConnection.getInputStream();
            //print_input(input);
            Xml.parse(input, Xml.Encoding.UTF_8, handler);
            input.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return handler.getReport();
    }

    private WeatherReport report = null;
    private WeatherForecast forecast = null;

    public WeatherReport getReport() {
        return report;
    }

    private StringBuilder builder = new StringBuilder();

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        super.characters(ch, start, length);
        builder.append(ch, start, length);
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        report = new WeatherReport("Vaxjo", "Sweden");
        //System.out.println("StartDocument");
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        super.endElement(uri, localName, name);

//			if (localName.equals(NAME)){
//				System.out.println(builder.toString());   // Växjö
//			} else if (localName.equals(COUNTRY)){
//				System.out.println(builder.toString());   // Sverige
//			} else
        if (localName.equals(LAST_UPDATE)) {
            //System.out.println("Last:"+builder.toString());
            report.setLastUpdated(builder.toString().trim()); // Last updated
        } else if (localName.equals(NEXT_UPDATE)) {
            //System.out.println("Next:"+builder.toString());
            report.setNextUpdate(builder.toString().trim());  // Next updated
        } else if (localName.equals(TIME)) {
            //System.out.println(forecast);            // One forecast completed
            report.addForecast(forecast);            // Add forecast to report
        }
        builder.setLength(0);
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, name, attributes);
        //System.out.println(localName);
        if (localName.equals(TIME)) {          // New forecast started
            forecast = new WeatherForecast();
            int sz = attributes.getLength();
            String from = null, to = null, period = null;
            for (int i = 0; i < sz; i++) {
                String attr = attributes.getLocalName(i);
                if (attr.equals("from"))
                    from = attributes.getValue(i);
                else if (attr.equals("to"))
                    to = attributes.getValue(i);
                else if (attr.equals("period"))
                    period = attributes.getValue(i);
            }
            forecast.setPeriod(from, to, period);
        } else if (localName.equals(LOCATION)) {  // Once for each report
            int sz = attributes.getLength();
            if (sz > 0) {
                String alt = null, lat = null, lng = null;
                for (int i = 0; i < sz; i++) {
                    String attr = attributes.getLocalName(i);
                    if (attr.equals("altitude"))
                        alt = attributes.getValue(i);
                    else if (attr.equals("longitude"))
                        lng = attributes.getValue(i);
                    else if (attr.equals("latitude"))
                        lat = attributes.getValue(i);
                }
                report.setLocation(lat, lng, alt);
            }
        } else if (localName.equals(SYMBOL)) {
            int sz = attributes.getLength();
            String num = null, wname = null;
            for (int i = 0; i < sz; i++) {
                String attr = attributes.getLocalName(i);
                if (attr.equals("number"))
                    num = attributes.getValue(i);
                else if (attr.equals("name"))
                    wname = attributes.getValue(i);
            }
            forecast.setWeather(num, wname);
        } else if (localName.equals(RAIN)) {  // Precipitation, nederbörd
            int sz = attributes.getLength();
            String rain = null;
            for (int i = 0; i < sz; i++) {
                String attr = attributes.getLocalName(i);
                if (attr.equals("value"))
                    rain = attributes.getValue(i);
            }
            forecast.setRain(rain);
        } else if (localName.equals(WIND)) {
            int sz = attributes.getLength();
            String num = null, wname = null;
            for (int i = 0; i < sz; i++) {
                String attr = attributes.getLocalName(i);
                if (attr.equals("code"))
                    num = attributes.getValue(i);
                else if (attr.equals("name"))
                    wname = attributes.getValue(i);
            }
            forecast.setWindDirection(num, wname);
        } else if (localName.equals(SPEED)) {
            int sz = attributes.getLength();
            String num = null, wname = null;
            for (int i = 0; i < sz; i++) {
                String attr = attributes.getLocalName(i);
                if (attr.equals("mps"))
                    num = attributes.getValue(i);
                else if (attr.equals("name"))
                    wname = attributes.getValue(i);
            }
            forecast.setWindSpeed(num, wname);
        } else if (localName.equals(TEMP)) {
            int sz = attributes.getLength();
            String num = null;
            for (int i = 0; i < sz; i++) {
                String attr = attributes.getLocalName(i);
                if (attr.equals("value"))
                    num = attributes.getValue(i);
            }
            forecast.setTemperature(num);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        //System.out.println("EndDocument");
    }

    private static void print_input(InputStream input) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            System.out.println(inputLine);
    }
}