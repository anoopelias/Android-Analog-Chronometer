package com.timtak.widget;

import android.app.Activity;
import android.os.Bundle;

public class ChronometerActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		AnalogChronometer chronometer = (AnalogChronometer) findViewById(R.id.chronometer);
		chronometer.start();
	}
}