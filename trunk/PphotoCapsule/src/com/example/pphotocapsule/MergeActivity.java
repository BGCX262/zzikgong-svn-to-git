package com.example.pphotocapsule;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MergeActivity extends Activity {
	ImageView merge_imageView;
	ImageButton merge_imageButton_merge, merge_imageButton_location;
	Intent intent;
	String filename;
	private final int imgWidth = 480;
	private final int imgHeight = 500;
	public static byte[] image;
	public static Bitmap resized;
	public static Database db;
	static Cursor result = null;
	double mLat, mLong;
	String s;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_merge);
		
		db = new Database(this);
		try {
			db.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		db.openDataBase();
		
		intent = getIntent();
		filename = intent.getStringExtra("filename");
		result = db.getInformationThroughFileName(filename);
		
		if(result.moveToFirst()){
			mLat = result.getDouble(7);
			mLong = result.getDouble(8);
			s = result.getString(9);
		}	
		
		BitmapFactory.Options bfo = new BitmapFactory.Options();
		bfo.inSampleSize = 2;
		Bitmap bm = BitmapFactory.decodeFile(filename, bfo);
		resized = Bitmap.createScaledBitmap(bm, imgWidth, imgHeight, true);
		merge_imageView = (ImageView)findViewById(R.id.merge_imageView_photo);
		merge_imageView.setImageBitmap(resized);
		merge_imageView.setRotation(90);
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		resized.compress(CompressFormat.JPEG, 100, stream);
		image = stream.toByteArray();
		
		merge_imageButton_merge = (ImageButton)findViewById(R.id.merge_imageButton_merge);
		merge_imageButton_merge.setOnClickListener(imageButtonClickListener);
		merge_imageButton_location = (ImageButton)findViewById(R.id.merge_imageButton_location);
		merge_imageButton_location.setOnClickListener(imageButtonClickListener);
	}

	ImageButton.OnClickListener imageButtonClickListener = new ImageButton.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.merge_imageButton_merge :
				db.updateRegisterMergedImageToOne(filename);
				Intent intent_nfc = new Intent(MergeActivity.this, NfcMergeImageActivity.class);
				startActivity(intent_nfc);
				break;
			case R.id.merge_imageButton_location :
				Intent intent_location = new Intent(MergeActivity.this, LocationActivity.class);
				intent_location.putExtra("lat", mLat);
				intent_location.putExtra("lng", mLong);
				intent_location.putExtra("s", s);
				startActivity(intent_location);
				break;
			}
		}
		
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.merge, menu);
		return true;
	}

}
