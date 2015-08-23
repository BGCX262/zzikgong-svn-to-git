package com.example.pphotocapsule;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ShowMergedImageActivity extends Activity {
	ImageView show_merged_image_imageView_photo;
	ImageButton show_merged_image_imageButton_ok;
	Intent intent;
	int result_intent;
	AlertDialog alertDialog;
	public static Database db;
	static Cursor result = null;
	String filename;
	Bitmap data;
	byte[] final_data;
	int mYear, mMonth, mDate, mHour, mMinute;
	double lat, lng;
	String street;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_merged_image);
	
		show_merged_image_imageView_photo = (ImageView)findViewById(R.id.show_merged_image_imageView_photo);
		intent = getIntent();
		result_intent = intent.getIntExtra("Receive", 0);
		if (result_intent == 1) {
			show_merged_image_imageView_photo.setImageBitmap(NfcReceiveMergedImageActivity.receive_photo_bit);
			data = NfcReceiveMergedImageActivity.receive_photo_bit;
		}
		else if (result_intent == 2) {
			show_merged_image_imageView_photo.setImageBitmap(NfcReceiveMergedImageActivity.photo_bit);
			data = NfcReceiveMergedImageActivity.photo_bit;
		}
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		data.compress(CompressFormat.JPEG, 100, stream);
		final_data = stream.toByteArray();
		
		show_merged_image_imageView_photo.setRotation(90);
		
		show_merged_image_imageButton_ok = (ImageButton)findViewById(R.id.show_merged_image_imageButton_ok);
		show_merged_image_imageButton_ok.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				alertDialog.show();
			}
			
		});
		
		alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("PhotoCapsule");
		alertDialog.setMessage("행복한 기념일 보내세요!");
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "확인", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				File pictureFile = CameraActivity.getOutputMediaFile(CameraActivity.MEDIA_TYPE_IMAGE);
				FileOutputStream fos;
				try {
					fos = new FileOutputStream(pictureFile);
					fos.write(final_data);
					fos.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			
				result = db.getFileName();
				if (result.moveToFirst()) 
					filename = result.getString(0);
				db.updateRegisterMergedImageToZero(filename);
				
				result = db.getInformationThroughFileName(filename);
				if (result.moveToFirst()) {
					mYear = result.getInt(2);
					mMonth = result.getInt(3);
					mDate = result.getInt(4);
					mHour = result.getInt(5);
					mMinute = result.getInt(6);
					lat = result.getDouble(7);
					lng = result.getDouble(8);
					street = result.getString(9);
				}
				
				db.insertRegisterNewImage(filename, mYear, mMonth, mDate, mHour, mMinute, lat, lng, street);
				Intent intent_main = new Intent(ShowMergedImageActivity.this, MainActivity.class);
				startActivity(intent_main);
				finish();
				return;
			}
		});
		
		db = new Database(this);
		try {
			db.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		db.openDataBase();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_merged_image, menu);
		return true;
	}

}
