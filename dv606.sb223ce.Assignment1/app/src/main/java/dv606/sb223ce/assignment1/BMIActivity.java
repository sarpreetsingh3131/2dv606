package dv606.sb223ce.assignment1;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class BMIActivity extends Activity {

    private EditText weightEditText;
    private EditText heightEditText;
    private TextView bmiTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);

        weightEditText = (EditText) findViewById(R.id.bmiWeightEditText);
        heightEditText = (EditText) findViewById(R.id.bmiHeightEditText);
        bmiTextView = (TextView) findViewById(R.id.bmiTextView);

        // calculate BMI if the given values are in range
        ((Button) findViewById(R.id.bmiComputeBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    double weight = Double.valueOf(weightEditText.getText().toString());
                    double height = Double.valueOf(heightEditText.getText().toString());
                    bmiTextView.setText(weight > 0.0 && height > 0.0 && weight < 200.0 && height < 2.7
                            ? "BMI: " + String.valueOf((int) (Math.round(weight / Math.pow(height, 2))))
                            : "Values are out of range");
                } catch (NumberFormatException e) {
                    bmiTextView.setText("Please provide all values.");
                }
            }
        });

        // clear the values
        ((Button) findViewById(R.id.bmiResetBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weightEditText.setText("");
                heightEditText.setText("");
                bmiTextView.setText("");
            }
        });
    }
}