package com.example.pphotocapsule;

import java.io.IOException;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;

public class SplashActivity extends Activity {
	public static Database db;
	static Cursor result = null;
	int check = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	    // Cancel Notification
	    nm.cancel(1234);
	    
		db = new Database(this);
		try {
			db.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		db.openDataBase();
		
		result = db.getExplanation();
		if (result.moveToFirst())
			check = result.getInt(0);
		
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (check == 1) {
					Intent intent_main = new Intent(SplashActivity.this, MainActivity.class);
					startActivity(intent_main);
				}
				else {
					Intent intent_intro = new Intent(SplashActivity.this, IntroActivity.class);
					startActivity(intent_intro);
				}
				finish();
			}
			
		}, 4000);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}

}
