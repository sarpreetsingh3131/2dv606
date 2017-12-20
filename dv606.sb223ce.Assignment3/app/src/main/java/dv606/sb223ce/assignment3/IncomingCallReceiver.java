package dv606.sb223ce.assignment3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import static android.content.Context.MODE_PRIVATE;

public class IncomingCallReceiver extends BroadcastReceiver {

    //Save the incoming number along with existing number in the preferences. Numbers are separated by semi-colon.
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            String state = bundle.getString(TelephonyManager.EXTRA_STATE);
            if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {
                String number = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER) + ";";
                SharedPreferences preferences = context.getSharedPreferences(IncomingCallHistoryActivity.PREFERENCES, MODE_PRIVATE);
                String data = number + preferences.getString(IncomingCallHistoryActivity.MY_PREF, "");
                SharedPreferences.Editor editor = context.getSharedPreferences(IncomingCallHistoryActivity.PREFERENCES, MODE_PRIVATE).edit();
                editor.putString(IncomingCallHistoryActivity.MY_PREF, data);
                editor.commit();
            }
        }
    }
}