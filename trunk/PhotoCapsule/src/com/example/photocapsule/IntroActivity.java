package com.example.photocapsule;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;

public class IntroActivity extends Activity {
	ImageButton intro_imageButton_ok;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);
		
		intro_imageButton_ok = (ImageButton)findViewById(R.id.intro_imageButton_ok);
		intro_imageButton_ok.setOnClickListener(imageButtonClickListener);
	}

	ImageButton.OnClickListener imageButtonClickListener = new ImageButton.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()) {
			case R.id.intro_imageButton_ok :
				Intent intent_main = new Intent(IntroActivity.this, MainActivity.class);
				startActivity(intent_main);
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
