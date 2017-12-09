package dv606.sb223ce.assignment2;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyCountriesActivity extends ListActivity implements CalendarProviderClient {

    private List<String> countries = new ArrayList<>();
    private List<Integer> eventsIds = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private long id = -1;
    private final String SORT_ORDER = "sortOrder";
    private final String SORT_PREF = "sortPref";
    private final int EDIT_CONTEXT_MENU = 1;
    private final int DELETE_CONTEXT_MENU = 2;
    private int clickedItemIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getMyCountriesCalendarId();
        applyUIPreferences();
        setListAdapter(adapter);
        registerForContextMenu(getListView());
        addEventToListViewOnLongClick();
        applySortPreference();
    }

    void applyUIPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        switch (preferences.getString("background", "Transparent")) {
            case "White":
                getListView().setBackgroundColor(Color.WHITE);
                break;
            case "Yellow":
                getListView().setBackgroundColor(Color.YELLOW);
                break;
            case "Transparent":
                getListView().setBackgroundColor(Color.TRANSPARENT);
        }

        int temp = 0;
        switch (preferences.getString("fontStyle", "Normal")) {
            case "Bold":
                temp = Typeface.BOLD;
                break;
            case "Italic":
                temp = Typeface.ITALIC;
                break;
            case "Bold Italic":
                temp = Typeface.BOLD_ITALIC;
                break;
            case "Normal":
                temp = Typeface.NORMAL;
        }
        final int style = temp;
        final float size = Float.valueOf(preferences.getString("fontSize", "20"));
        adapter = new ArrayAdapter<String>(this, R.layout.activity_my_countries, countries) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view.findViewById(R.id.myCountriesTextView)).setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
                ((TextView) view.findViewById(R.id.myCountriesTextView)).setTypeface(null, style);
                return view;
            }
        };
    }

    void applySortPreference() {
        Bundle bundle = new Bundle();
        bundle.putString(SORT_ORDER, getSharedPreferences(SORT_PREF, MODE_PRIVATE).getString(SORT_ORDER, null));
        getLoaderManager().restartLoader(LOADER_MANAGER_ID, bundle, this);
    }

    void addEventToListViewOnLongClick() {
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                clickedItemIndex = i;
                return false;
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Options");
        menu.add(0, EDIT_CONTEXT_MENU, 0, "Edit");
        menu.add(0, DELETE_CONTEXT_MENU, 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_CONTEXT_MENU:
                deleteEvent(eventsIds.get(clickedItemIndex));
                return super.onContextItemSelected(item);
            case EDIT_CONTEXT_MENU:
                Intent intent = new Intent(this, AddCountryActivity.class);
                intent.putExtra("year", countries.get(clickedItemIndex).split(" ")[0]);
                intent.putExtra("country", countries.get(clickedItemIndex).split(" ")[1]);
                startActivityForResult(intent, 0);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_countries, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String order = "";
        switch (item.getItemId()) {
            case R.id.myCountriesAddCountryItem:
                startActivityForResult(new Intent(this, AddCountryActivity.class), 1);
                return true;
            case R.id.myCountriesByCountryASCItem:
                order = CalendarContract.Events.TITLE + " ASC";
                break;
            case R.id.myCountriesByCountryDESCItem:
                order = CalendarContract.Events.TITLE + " DESC";
                break;
            case R.id.myCountriesByYearASCItem:
                order = CalendarContract.Events.DTSTART + " ASC";
                break;
            case R.id.myCountriesByYearDESCItem:
                order = CalendarContract.Events.DTSTART + " DESC";
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        Bundle bundle = new Bundle();
        bundle.putString(SORT_ORDER, order);
        saveSortingOrderInPreferences(order);
        getLoaderManager().restartLoader(LOADER_MANAGER_ID, bundle, this);
        return super.onOptionsItemSelected(item);
    }

    void saveSortingOrderInPreferences(String order) {
        SharedPreferences.Editor editor = getSharedPreferences(SORT_PREF, MODE_PRIVATE).edit();
        editor.putString(SORT_ORDER, order);
        editor.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            addNewEvent(Integer.valueOf(data.getStringExtra("year")), data.getStringExtra("country"));
        } else if (resultCode == Activity.RESULT_OK && requestCode == 0) {
            updateEvent(eventsIds.get(clickedItemIndex), Integer.valueOf(data.getStringExtra("year")), data.getStringExtra("country"));
        }
    }

    @Override
    public long getMyCountriesCalendarId() {
        Cursor cursor = getContentResolver().query(CALENDARS_LIST_URI, CALENDARS_LIST_PROJECTION, CALENDARS_LIST_SELECTION,
                CALENDARS_LIST_SELECTION_ARGS, null);
        long id = cursor.moveToFirst() ? cursor.getLong(PROJ_CALENDARS_LIST_ID_INDEX) : createCountriesCalendar();
        cursor.close();
        return id;
    }

    long createCountriesCalendar() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CalendarContract.Calendars.NAME, CALENDAR_TITLE);
        contentValues.put(CalendarContract.Calendars.ACCOUNT_NAME, ACCOUNT_TITLE);
        contentValues.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDAR_TITLE);
        contentValues.put(CalendarContract.Calendars.OWNER_ACCOUNT, ACCOUNT_TITLE);
        contentValues.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        contentValues.put(CalendarContract.Calendars.VISIBLE, 1);
        contentValues.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        contentValues.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_READ);
        Uri uri = asSyncAdapter(CALENDARS_LIST_URI, ACCOUNT_TITLE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        Uri calendarUri = getContentResolver().insert(uri, contentValues);
        return ContentUris.parseId(calendarUri);
    }

    //https://developer.android.com/guide/topics/providers/calendar-provider.html#sync-adapter
    static Uri asSyncAdapter(Uri uri, String account, String accountType) {
        return uri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, account)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, accountType).build();
    }


    ContentValues createEvent(int year, String country) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, CalendarUtils.getEventStart(year));
        values.put(CalendarContract.Events.DTEND, CalendarUtils.getEventEnd(year));
        values.put(CalendarContract.Events.TITLE, country);
        values.put(CalendarContract.Events.CALENDAR_ID, this.id);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, CalendarUtils.getTimeZoneId());
        return values;
    }

    @Override
    public void addNewEvent(int year, String country) {
        getContentResolver().insert(CalendarContract.Events.CONTENT_URI, createEvent(year, country));
    }

    @Override
    public void updateEvent(int eventId, int year, String country) {
        getContentResolver().update(ContentUris.withAppendedId(EVENTS_LIST_URI, eventId), createEvent(year, country), null, null);
    }

    @Override
    public void deleteEvent(int eventId) {
        getContentResolver().delete(ContentUris.withAppendedId(EVENTS_LIST_URI, eventId), null, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_MANAGER_ID:
                return new CursorLoader(this, EVENTS_LIST_URI, EVENTS_LIST_PROJECTION,
                        CalendarContract.Events.CALENDAR_ID + "=" + this.id, null, args.getString(SORT_ORDER));
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        countries.clear();
        eventsIds.clear();
        while (data.moveToNext()) {
            countries.add(CalendarUtils.getEventYear(data.getLong(PROJ_EVENTS_LIST_DTSTART_INDEX))
                    + " " + data.getString(PROJ_CALENDARS_LIST_NAME_INDEX));
            eventsIds.add((int) data.getLong(PROJ_CALENDARS_LIST_ID_INDEX));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}