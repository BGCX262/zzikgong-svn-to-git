package com.example.photocapsule;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ImagePopupActivity extends Activity {
	ImageView imagepopup_imageView;
	ImageButton imagepopup_imageButton_merge;
	Intent intent;
	String filename;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_popup);
		
		intent = getIntent();
		filename = intent.getStringExtra("filename");
		
		BitmapFactory.Options bfo = new BitmapFactory.Options();
		bfo.inSampleSize = 2;
		Bitmap bm = BitmapFactory.decodeFile(filename, bfo);
		
		imagepopup_imageView = (ImageView)findViewById(R.id.imagepopup_imageView);
		imagepopup_imageView.setImageBitmap(bm);
		
		imagepopup_imageButton_merge = (ImageButton)findViewById(R.id.imagepopup_imageButton_merge);
		imagepopup_imageButton_merge.setOnClickListener(imageButtonClickListener);
	}

	ImageButton.OnClickListener imageButtonClickListener = new ImageButton.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.imagepopup_imageButton_merge :
				break;
			}
		}
		
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_popup, menu);
		return true;
	}

}
