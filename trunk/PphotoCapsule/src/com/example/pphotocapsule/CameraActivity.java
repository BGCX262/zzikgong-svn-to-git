package com.example.pphotocapsule;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

public class CameraActivity extends Activity implements LocationListener {
	public static final int MEDIA_TYPE_IMAGE = 1;
	
	private Camera mCamera;
	private CameraPreview mPreview;
	
	private ImageView camera_imageView_capture, camera_imageView_date, camera_imageView_send;
	private FrameLayout preview;
	private LinearLayout camera_linearlayout_capture, camera_linearlayout_date, camera_linearlayout_send;
	
	public static int mYear, mMonth, mDay;
	static final int DATE_DIALOG_ID = 0;
	public static String date;
	public static int mHour, mMinute;
	static final int TIME_DIALOG_ID = 1;
	
	public static byte[] photo;
	private Bitmap photo_bitmap;
	public static byte[] photo_left;
	public static byte[] photo_right;
	public static Bitmap photo_left_bitmap;
	public static Bitmap photo_right_bitmap;
	
	private LocationManager locationManager;
	private String bestProvider;
	public static String s = "";
	public static double lat;
	public static double lng;
	public static String timeStamp;
	
	private AlarmManager alarmManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		
		mCamera = getCameraInstance();
		mPreview = new CameraPreview(this);
		preview = (FrameLayout)findViewById(R.id.camera_framelayout);
		preview.addView(mPreview);
		camera_imageView_capture = (ImageView)findViewById(R.id.camera_imageView_capture);
		camera_imageView_capture.setOnClickListener(new ImageView.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCamera.takePicture(new Camera.ShutterCallback() {
					
					@Override
					public void onShutter() {	
					}
				}, null, mPicture);
				
			}
		});
	
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinute = c.get(Calendar.MINUTE);
		
		camera_imageView_date = (ImageView)findViewById(R.id.camera_imageView_date);
		camera_imageView_date.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
			
		});
		
		camera_imageView_send = (ImageView)findViewById(R.id.camera_imageView_send);
		camera_imageView_send.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				GregorianCalendar gregorianCalendar = new GregorianCalendar(TimeZone.getTimeZone("GMT+09:00"));
				gregorianCalendar.set(mYear, mMonth, mDay, mHour, mMinute, 00);
				Intent intent = new Intent(CameraActivity.this, AlarmedTimeShowActivity.class);		
				PendingIntent pi = PendingIntent.getActivity(CameraActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, gregorianCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
				
				Intent intent_nfc = new Intent(CameraActivity.this, NfcSendInformationActivity.class);
				startActivity(intent_nfc);
			}
			
		});
		
		camera_linearlayout_capture = (LinearLayout)findViewById(R.id.camera_linearlayout_capture);
		camera_linearlayout_date = (LinearLayout)findViewById(R.id.camera_linearlayout_date);
		camera_linearlayout_send = (LinearLayout)findViewById(R.id.camera_linearlayout_send);
		
		locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		bestProvider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(bestProvider);
		if (location != null) {
			s += getAddress(location.getLatitude(), location.getLongitude());
			lat = location.getLatitude();
			lng = location.getLongitude();
		}
		
		alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
    	alarmManager.setTimeZone("GMT+09:00");
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear + 1;
			mDay = dayOfMonth;
			date = mYear + "-" + mMonth + "-" + mDay;
			showDialog(TIME_DIALOG_ID);
		}
	};
	
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mHour = hourOfDay;
			mMinute = minute;
			camera_linearlayout_date.setVisibility(LinearLayout.INVISIBLE);
			camera_linearlayout_send.setVisibility(LinearLayout.VISIBLE);
		}
	};
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
		case DATE_DIALOG_ID :
			DatePickerDialog date = new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
			DatePicker dp = date.getDatePicker();
			Calendar cal = Calendar.getInstance();
			cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
			long time = cal.getTimeInMillis();
			dp.setMinDate(time);
			return date;
		case TIME_DIALOG_ID :
			TimePickerDialog alarm = new TimePickerDialog(this, mTimeSetListener, mHour, mMinute, true);
			return alarm;
		}
		return null;
	}

	
	public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
		private SurfaceHolder mHolder;
		
		public CameraPreview(Context context) {
			super(context);
			mHolder = getHolder();
			mHolder.addCallback(this);
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			if(mHolder.getSurface() == null) {
				return;
			}
			
			try {
				mCamera.stopPreview();
			} catch (Exception e) {
				
			}
			
			try {
				mCamera.setPreviewDisplay(mHolder);
				mCamera.startPreview();
			} catch (Exception e) {
				e.getMessage();
			}
			
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				if (mCamera == null) {
					mCamera = getCameraInstance();
				}

				mCamera.setDisplayOrientation(90);
				mCamera.setPreviewDisplay(holder);
				mCamera.startPreview();
			} catch (IOException e) {
				e.getMessage();
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {		
		}
		
	}
	
	public static Camera getCameraInstance() {
		Camera c = null;
		
		try {
			c = Camera.open();
		} catch(Exception e) {
			
		}
		return c;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		releaseCamera();
	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (!checkCameraHardware(this)) {
			finish();
		}
	}

	private boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
			return true;
		else
			return false;
	}
	
	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, final Camera camera) {
			File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
			
			if (pictureFile == null) {
				return;
			}
			
			photo = data;
			photo_bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
			DivideImage divide = new DivideImage();
			divide.segmentImage(photo_bitmap);
			
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			DivideImage.croppedBmpright.compress(CompressFormat.JPEG, 100, stream);
			photo_right = stream.toByteArray();
			photo_right_bitmap = BitmapFactory.decodeByteArray(photo_right, 0, photo_right.length);
			
			ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
			DivideImage.croppedBmpLeft.compress(CompressFormat.JPEG, 100, stream1);
			photo_left = stream1.toByteArray();
			photo_left_bitmap = BitmapFactory.decodeByteArray(photo_left, 0, photo_left.length);

			
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					camera_linearlayout_capture.setVisibility(LinearLayout.INVISIBLE);
					camera_linearlayout_date.setVisibility(LinearLayout.VISIBLE);
				}
				
			}, 0);
		}
		
	};
	
	public static File getOutputMediaFile(int type) {
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "PhotoCapsule");
		
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}
		
		timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		
		if(type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		}
		else {
			return null;
		}
		return mediaFile;
	}
	
	public String getAddress(double lat, double lon) {
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
			e.printStackTrace();
		}
		
		return result;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera, menu);
		return true;
	}

	@Override
	public void onLocationChanged(Location arg0) {
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
