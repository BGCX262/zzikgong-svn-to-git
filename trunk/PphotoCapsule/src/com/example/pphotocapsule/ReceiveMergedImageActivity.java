package com.example.pphotocapsule;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ReceiveMergedImageActivity extends Activity {
	ImageView receive_merged_imageView_photo;
	ImageButton receive_merged_imagebutton_send;
	Intent intent;
	int result_intent;
	public static byte[] photo_byte;
	Bitmap photo_bit;
	Bitmap resized;
	private final int imgWidth = 480;
	private final int imgHeight = 500;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_receive_merged_image);
		
		receive_merged_imageView_photo = (ImageView)findViewById(R.id.receive_merged_imageView_photo);
		receive_merged_imageView_photo.setRotation(90);
		receive_merged_imagebutton_send = (ImageButton)findViewById(R.id.receive_merged_imagebutton_send);
		receive_merged_imagebutton_send.setOnClickListener(imageButtonClickListener);
		
		intent = getIntent();
		result_intent = intent.getIntExtra("Receive", 0);
		if (result_intent == 1) {
			Intent intent_nfc_receive_mergedImage = new Intent(ReceiveMergedImageActivity.this, NfcReceiveMergedImageActivity.class);
			startActivity(intent_nfc_receive_mergedImage);
		}
		else if (result_intent == 2) {
			resized = Bitmap.createScaledBitmap(NfcMergeImageActivity.result_photo_bit, imgWidth, imgHeight, true);
			receive_merged_imageView_photo.setImageDrawable(new BitmapDrawable(getResources(), resized));
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			resized.compress(CompressFormat.JPEG, 100, stream);
			photo_byte = stream.toByteArray();
		}
	}

	ImageButton.OnClickListener imageButtonClickListener = new ImageButton.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.receive_merged_imagebutton_send :
				Intent intent_nfc_receive_mergedImage = new Intent(ReceiveMergedImageActivity.this, NfcReceiveMergedImageActivity.class);
				startActivity(intent_nfc_receive_mergedImage);
				break;
			}
		}
		
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.receive_merged_image, menu);
		return true;
	}

}
