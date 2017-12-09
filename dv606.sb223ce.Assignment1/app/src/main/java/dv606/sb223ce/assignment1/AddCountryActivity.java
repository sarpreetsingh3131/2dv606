package dv606.sb223ce.assignment1;

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

        // Save selected country and year if they are not default
        ((Button) findViewById(R.id.addCountryAddBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String country = ((Spinner) findViewById(R.id.addCountryCountrySpinner)).getSelectedItem().toString();
                String year = ((Spinner) findViewById(R.id.addCountryYearSpinner)).getSelectedItem().toString();

                // check the default values
                if (!country.equals("Select Country") && !year.equals("Select Year")) {
                    setResult(Activity.RESULT_OK, new Intent().putExtra("data", year + "  " + country));
                    finish();
                }
            }
        });
    }
}
