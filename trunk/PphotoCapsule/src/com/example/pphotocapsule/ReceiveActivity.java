package com.example.pphotocapsule;

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
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class ReceiveActivity extends Activity {
	ImageView receive_imageView_photo, receive_imagebutton_location, receive_imagebutton_sms, receive_imagebutton_email;
	ImageButton receive_imagebutton_ok;
	AlertDialog alertDialog;
	Intent intent;
	int result_intent;
	byte[] data;
	private final int imgWidth = 480;
	private final int imgHeight = 500;
	public static Database db;
	static Cursor result = null;
	int maxId;
	public static int mYear, mMonth, mDate, mHour, mMinute;
	public static double mLat, mLong;
	public static String s = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_receive);
		
		db = new Database(this);
		try {
			db.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		db.openDataBase();
		
		result = db.getMaxId();
		if (result.moveToFirst())
			maxId = result.getInt(0);
		
		result = db.getInformation(maxId);
		if(result.moveToFirst()){
			mYear = result.getInt(2);
			mMonth = result.getInt(3);
			mDate = result.getInt(4);
			mHour = result.getInt(5);
			mMinute = result.getInt(6);
			mLat = result.getDouble(7);
			mLong = result.getDouble(8);
			s = result.getString(9);
		}	
		
		receive_imagebutton_ok = (ImageButton)findViewById(R.id.receive_imagebutton_ok);
		receive_imagebutton_ok.setOnClickListener(imageButtonClickListener);
		receive_imageView_photo = (ImageView)findViewById(R.id.receive_imageView_photo);
		receive_imageView_photo.setRotation(90);
		
		intent = getIntent();
		result_intent = intent.getIntExtra("Receive", 0);
		if (result_intent == 1) {
			Bitmap resized = Bitmap.createScaledBitmap(CameraActivity.photo_left_bitmap, imgWidth, imgHeight, true);
			receive_imageView_photo.setImageBitmap(resized);
			data = CameraActivity.photo_left;
		}
		else if (result_intent == 2) {
			Bitmap resized = Bitmap.createScaledBitmap(NfcSendImageActivity.photo_bit, imgWidth, imgHeight, true);
			receive_imageView_photo.setImageBitmap(resized);
			data = NfcSendImageActivity.photo_byte;
		}
		
		receive_imagebutton_location = (ImageView)findViewById(R.id.receive_imageView_location);
		receive_imagebutton_location.setOnClickListener(imageViewClickListener);
		receive_imagebutton_sms = (ImageView)findViewById(R.id.receive_imageView_sms);
		receive_imagebutton_sms.setOnClickListener(imageViewClickListener);
		receive_imagebutton_email = (ImageView)findViewById(R.id.receive_imageView_email);
		receive_imagebutton_email.setOnClickListener(imageViewClickListener);
		
		alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("PhotoCapsule");
		alertDialog.setMessage("등록한 기념일이 되기 전까지는 사진을 합칠 수 없습니다!");
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "확인", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				File pictureFile = CameraActivity.getOutputMediaFile(CameraActivity.MEDIA_TYPE_IMAGE);
				FileOutputStream fos;
				try {
					fos = new FileOutputStream(pictureFile);
					fos.write(data);
					fos.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			
				db.updateRegister(maxId, pictureFile.getPath());
				Intent intent_main = new Intent(ReceiveActivity.this, MainActivity.class);
				startActivity(intent_main);
				finish();
				return;
			}
		});
	}
	
	ImageButton.OnClickListener imageButtonClickListener = new ImageButton.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.receive_imagebutton_ok :
				alertDialog.show();
				break;
			}
		}
		
	};
	
	ImageView.OnClickListener imageViewClickListener = new ImageView.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.receive_imageView_location : 
				Intent intent_location = new Intent(ReceiveActivity.this, LocationActivity.class);
				intent_location.putExtra("lat", mLat);
				intent_location.putExtra("lng", mLong);
				intent_location.putExtra("s", s);
				startActivity(intent_location);
				break;
			case R.id.receive_imageView_sms :
				TelephonyManager telManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
				String phoneNum = telManager.getLine1Number();
				SmsManager sms = SmsManager.getDefault();
				sms.sendTextMessage(phoneNum, null, "<PhotoCapsule>\n" + mYear + "-" + mMonth + "-" + mDate
						+ "\n잊지말고 사진합체 사랑합체♥", null, null);
				Toast.makeText(getApplicationContext(), "전송 완료!", Toast.LENGTH_LONG).show();
				break;
			case R.id.receive_imageView_email :
				Intent intent_mail = new Intent(ReceiveActivity.this, EmailActivity.class);
				startActivity(intent_mail);
				break;
			}
		
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.receive, menu);
		return true;
	}
}
