package com.example.photocapsule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class ReceiveActivity extends Activity implements LocationListener {
	ImageButton receive_imagebutton_ok, receive_imagebutton_location, receive_imagebutton_sms, receive_imagebutton_email;
	ImageView receive_imageView_photo;
	AlertDialog alertDialog;
	Intent intent;
	int result_intent;
	byte[] data;
	private LocationManager locationManager;
	private String bestProvider;
	public static String s = "";
	public static double lat;
	public static double lng;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_receive);
		
		receive_imagebutton_ok = (ImageButton)findViewById(R.id.receive_imagebutton_ok);
		receive_imagebutton_ok.setOnClickListener(imageButtonClickListener);
		receive_imageView_photo = (ImageView)findViewById(R.id.receive_imageView_photo);
		receive_imageView_photo.setRotation(90);
		intent = getIntent();
		result_intent = intent.getIntExtra("Receive", 0);
		if (result_intent == 1) {
			receive_imageView_photo.setImageBitmap(CameraActivity.photo_left_bitmap);
			data = CameraActivity.photo_left;
		}
		else if (result_intent == 2) {
			receive_imageView_photo.setImageBitmap(NFCActivity.photo_bit);
			data = NFCActivity.photo_byte;
		}
		
		receive_imagebutton_location = (ImageButton)findViewById(R.id.receive_imagebutton_location);
		receive_imagebutton_location.setOnClickListener(imageButtonClickListener);
		receive_imagebutton_sms = (ImageButton)findViewById(R.id.receive_imagebutton_sms);
		receive_imagebutton_sms.setOnClickListener(imageButtonClickListener);
		receive_imagebutton_email = (ImageButton)findViewById(R.id.receive_imagebutton_email);
		receive_imagebutton_email.setOnClickListener(imageButtonClickListener);
		
		alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("PhotoCapsule");
		alertDialog.setMessage("����� ������� �Ǳ� �������� ������ ��ĥ �� �����ϴ�!");
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Ȯ��", new DialogInterface.OnClickListener() {
			
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
				
				Intent intent_main = new Intent(ReceiveActivity.this, MainActivity.class);
				startActivity(intent_main);
				return;
			}
		});
		
		locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		bestProvider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(bestProvider);
		if (location != null) {
			s += getAddress(location.getLatitude(), location.getLongitude());
			lat = location.getLatitude();
			lng = location.getLongitude();
		}
	}
	
	String getAddress(double lat, double lon) {
		Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
		String result = "";
		List<Address> list;
		try {
			list = geocoder.getFromLocation(lat, lon, 1);
			if (list != null && list.size() > 0) {
				Address address = list.get(0);
				result = address.getAddressLine(0) + ", " + address.getLocality();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	ImageButton.OnClickListener imageButtonClickListener = new ImageButton.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.receive_imagebutton_ok :
				alertDialog.show();
				break;
			case R.id.receive_imagebutton_location : 
				Intent intent_location = new Intent(ReceiveActivity.this, LocationActivity.class);
				startActivity(intent_location);
				break;
			case R.id.receive_imagebutton_sms :
				TelephonyManager telManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
				String phoneNum = telManager.getLine1Number();
				SmsManager sms = SmsManager.getDefault();
				sms.sendTextMessage(phoneNum, null, "<PhotoCapsule>\n�������� ������ü �����ü��", null, null);
				Toast.makeText(getApplicationContext(), "���� �Ϸ�!", Toast.LENGTH_LONG).show();
				break;
			case R.id.receive_imagebutton_email :
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

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
