package com.example.photocapsule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends Activity {
	ImageView main_imageButton_camera, main_imageButton_album;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		main_imageButton_camera = (ImageView)findViewById(R.id.main_imageButton_camera);
		main_imageButton_album = (ImageView)findViewById(R.id.main_imageButton_album);
		main_imageButton_camera.setOnClickListener(imageButtonClickListener);
		main_imageButton_album.setOnClickListener(imageButtonClickListener);
	}

	ImageView.OnClickListener imageButtonClickListener = new ImageView.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.main_imageButton_camera:
				Intent intent_camera = new Intent(MainActivity.this, CameraActivity.class);
				startActivity(intent_camera);
				break;
			case R.id.main_imageButton_album:
				Intent intent_album = new Intent(MainActivity.this, AlbumActivity.class);
				startActivity(intent_album);
				break;
			}
		}
		
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
