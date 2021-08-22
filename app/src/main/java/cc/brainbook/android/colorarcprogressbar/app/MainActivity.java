package cc.brainbook.android.colorarcprogressbar.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cc.brainbook.android.colorarcprogressbar.ColorArcProgressBar;

public class MainActivity extends AppCompatActivity {
    private Button button1;
    private ColorArcProgressBar bar1;
    private Button button2;
    private ColorArcProgressBar bar2;
    private Button button3;
    private ColorArcProgressBar bar3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        bar1 = (ColorArcProgressBar) findViewById(R.id.bar1);
        button1 = (Button) findViewById(R.id.button1);
        bar2 = (ColorArcProgressBar) findViewById(R.id.bar2);
        button2 = (Button) findViewById(R.id.button2);
        bar3 = (ColorArcProgressBar) findViewById(R.id.bar3);
        button3 = (Button) findViewById(R.id.button3);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar1.setProgressValue(100);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                bar2.setProgressValue(0);
                bar2.setProgressValue(bar2.getProgressMaxValue());
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                bar3.setProgressValue(0);
                bar3.setProgressValue(bar3.getProgressMaxValue());
            }
        });


    }
}