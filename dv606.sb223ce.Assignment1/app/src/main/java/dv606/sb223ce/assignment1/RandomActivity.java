package dv606.sb223ce.assignment1;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class RandomActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);

        // handles the click and display random number
        ((Button) findViewById(R.id.randomButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TextView) findViewById(R.id.randomTextView)).setText(String.valueOf(new Random().nextInt(100) + 1));
            }
        });
    }
}
