package dv606.sb223ce.assignment2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

public class AddCountryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_country);

        // If updating, set the country and year
        if (getIntent().hasExtra("country") && getIntent().hasExtra("year")) {
            setEntry((Spinner) findViewById(R.id.addCountryCountrySpinner), getIntent().getStringExtra("country"));
            setEntry((Spinner) findViewById(R.id.addCountryYearSpinner), getIntent().getStringExtra("year"));
        }

        ((Button) findViewById(R.id.addCountryAddBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String country = ((Spinner) findViewById(R.id.addCountryCountrySpinner)).getSelectedItem().toString();
                String year = ((Spinner) findViewById(R.id.addCountryYearSpinner)).getSelectedItem().toString();

                // check the default values
                if (!country.equals("Select Country") && !year.equals("Select Year")) {
                    Intent intent = new Intent();
                    intent.putExtra("country", country);
                    intent.putExtra("year", year);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    void setEntry(Spinner spinner, String entry) {
        for (int i = 0; i < spinner.getAdapter().getCount(); i++) {
            if (spinner.getAdapter().getItem(i).equals(entry)) {
                spinner.setSelection(i);
                break;
            }
        }
    }
}