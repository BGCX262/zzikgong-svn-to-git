package com.example.photocapsule;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class CameraActivity extends Activity {
	private static final String TAG = "APPSTUDIO";
	public static final int MEDIA_TYPE_IMAGE = 1;
	
	private Camera mCamera;
	private CameraPreview mPreview;
	
	private ImageButton camera_imageButton_capture, camera_imageButton_date, camera_imageButton_send;
	private FrameLayout preview;
	private LinearLayout camera_linearlayout_capture, camera_linearlayout_date, camera_linearlayout_send;
	
	private int mYear, mMonth, mDay;
	static final int DATE_DIALOG_ID = 0;
	public static String date;
	
	public static byte[] photo;
	private Bitmap photo_bitmap;
	public static byte[] photo_left;
	public static byte[] photo_right;
	public static Bitmap photo_left_bitmap;
	public static Bitmap photo_right_bitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		
		mCamera = getCameraInstance();
		mPreview = new CameraPreview(this);
		preview = (FrameLayout)findViewById(R.id.camera_framelayout);
		preview.addView(mPreview);
		camera_imageButton_capture = (ImageButton)findViewById(R.id.camera_imageButton_capture);
		camera_imageButton_capture.setOnClickListener(new View.OnClickListener() {
			
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
		
		camera_imageButton_date = (ImageButton)findViewById(R.id.camera_imageButton_date);
		camera_imageButton_date.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
			
		});
		
		camera_imageButton_send = (ImageButton)findViewById(R.id.camera_imageButton_send);
		camera_imageButton_send.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent_nfc = new Intent(CameraActivity.this, NFCActivity.class);
				startActivity(intent_nfc);
			}
			
		});
		
		camera_linearlayout_capture = (LinearLayout)findViewById(R.id.camera_linearlayout_capture);
		camera_linearlayout_date = (LinearLayout)findViewById(R.id.camera_linearlayout_date);
		camera_linearlayout_send = (LinearLayout)findViewById(R.id.camera_linearlayout_send);
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear + 1;
			mDay = dayOfMonth;
			date = mYear + "-" + mMonth + "-" + mDay;
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
				Log.d(TAG, "Error starting camera preview : " + e.getMessage());
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
				Log.d(TAG, "Error camera preview : " + e.getMessage());
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
				Log.d(TAG, "파일쓰기 에러, 권한 확인!");
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
				Log.d(TAG, "failed to create directory");
				return null;
			}
		}
		
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		
		if(type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		}
		else {
			return null;
		}
		return mediaFile;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera, menu);
		return true;
	}

}
