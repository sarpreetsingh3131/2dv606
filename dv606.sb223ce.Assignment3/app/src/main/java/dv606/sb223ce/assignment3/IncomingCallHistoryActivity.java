package dv606.sb223ce.assignment3;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class IncomingCallHistoryActivity extends ListActivity {

    final static String PREFERENCES = "incomingCallHistoryPref";
    final static String MY_PREF = "myPref";
    private int clickedItemIndex = -1;
    private ArrayList<String> phoneNumbers = new ArrayList<>();
    private final int CALL = 1;
    private final int SMS = 2;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        for (String str : preferences.getString(MY_PREF, ";").split(";")) phoneNumbers.add(str);
        adapter = new ArrayAdapter<String>(this, R.layout.activity_incoming_call_history, phoneNumbers);
        setListAdapter(adapter);
        registerForContextMenu(getListView());
        addEventToListViewOnLongClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        phoneNumbers.clear();
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        for (String str : preferences.getString(MY_PREF, ";").split(";")) phoneNumbers.add(str);
        adapter.notifyDataSetChanged();
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
        menu.add(0, CALL, 0, "Call");
        menu.add(0, SMS, 0, "SMS");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case CALL:
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumbers.get(clickedItemIndex))));
                return true;
            case SMS:
                Intent sms = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumbers.get(clickedItemIndex)));
                sms.putExtra("sms_body", phoneNumbers.get(clickedItemIndex));
                startActivity(Intent.createChooser(sms, "Send via"));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}