package dv606.sb223ce.assignment2;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class AlarmClockActivity extends Activity {

    private final String SET_ALARM = "Set Alarm";
    private final String UPDATE_ALARM = "Update Alarm";
    private final String CANCEL = "Cancel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_clock);
        handleTimer();

        // set/update alarm
        for (final Button btn : Arrays.asList((Button) findViewById(R.id.alarmClockSetAlarmButton),
                (Button) findViewById(R.id.alarmClockCancelAlarmButton))) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String hour = ((Spinner) findViewById(R.id.alarmClockHourSpinner)).getSelectedItem().toString();
                    String min = ((Spinner) findViewById(R.id.alarmClockMinSpinner)).getSelectedItem().toString();

                    // check default values
                    if (!hour.equals("HH") && !min.equals("MM")) {
                        handleAlarm(Integer.valueOf(hour), Integer.valueOf(min), btn);
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid Selection", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    void handleAlarm(int hour, int min, Button button) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        switch (button.getText().toString()) {
            case CANCEL:
                am.cancel(alarmIntent);
                ((Spinner) findViewById(R.id.alarmClockHourSpinner)).setSelection(0);
                ((Spinner) findViewById(R.id.alarmClockMinSpinner)).setSelection(0);
                ((Button) findViewById(R.id.alarmClockSetAlarmButton)).setText(SET_ALARM);
                Toast.makeText(this, "Alarm is cancelled", Toast.LENGTH_SHORT).show();
                return;
            case UPDATE_ALARM:
                am.cancel(alarmIntent);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        Toast.makeText(this, "Alarm is " + (button.getText().equals(SET_ALARM) ? "on" : "updated"),
                Toast.LENGTH_SHORT).show();
        button.setText(UPDATE_ALARM);
    }

    void handleTimer() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.alarmClockTimeTextView)).setText(new SimpleDateFormat("HH:mm:ss")
                        .format(System.currentTimeMillis()));
                handler.postDelayed(this, 5000);

            }
        }, 10);
    }
}