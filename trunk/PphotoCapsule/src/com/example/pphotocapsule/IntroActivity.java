package com.example.pphotocapsule;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;

public class IntroActivity extends Activity {
	public static Database db;
	ImageButton intro_imageButton_ok;
	CheckBox intro_checkBox_check;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);
		
		db = new Database(this);
		try {
			db.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		db.openDataBase();
		
		intro_imageButton_ok = (ImageButton)findViewById(R.id.intro_imageButton_ok);
		intro_imageButton_ok.setOnClickListener(imageButtonClickListener);
		intro_checkBox_check = (CheckBox)findViewById(R.id.intro_checkBox_check);
	}

	ImageButton.OnClickListener imageButtonClickListener = new ImageButton.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()) {
			case R.id.intro_imageButton_ok :
				if (intro_checkBox_check.isChecked())
					db.updateExplanation();
				Intent intent_main = new Intent(IntroActivity.this, MainActivity.class);
				startActivity(intent_main);
				finish();
				break;
			}
		}
		
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.intro, menu);
		return true;
	}

}
