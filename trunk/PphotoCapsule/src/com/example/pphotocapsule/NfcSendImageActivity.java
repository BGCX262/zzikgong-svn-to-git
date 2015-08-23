package com.example.pphotocapsule;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Scanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.Menu;
import android.widget.Toast;

public class NfcSendImageActivity extends Activity implements CreateNdefMessageCallback, OnNdefPushCompleteCallback {
	NfcAdapter mNfcAdapter;
	private static final int NFC_MESSAGE_SENT = -1;
	public static Bitmap photo_bit;
	public static byte[] photo_byte;
	byte[] sending;
	Scanner scan;
	public static Database db;
	int mYear, mMonth, mDate, mHour, mMinute;
	double mLat, mLong;
	String street;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfc_send_image);
		
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		mNfcAdapter.setNdefPushMessageCallback(this, this);
		mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
		
		scan = new Scanner(NfcSendInformationActivity.info);
		scan.useDelimiter("-");
		
		db = new Database(this);
		try {
			db.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		db.openDataBase();
		
		mYear = Integer.parseInt(scan.next());
		mMonth = Integer.parseInt(scan.next());
		mDate = Integer.parseInt(scan.next());
		mHour = Integer.parseInt(scan.next());
		mMinute = Integer.parseInt(scan.next());
		mLat = Double.parseDouble(scan.next());
		mLong = Double.parseDouble(scan.next());
		street = scan.next();
		
		db.insertRegister(mYear, mMonth, mDate, mHour, mMinute, mLat, mLong, street);
	}
	
	private final Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NFC_MESSAGE_SENT :
				Toast.makeText(getApplicationContext(), "전송 완료!", Toast.LENGTH_LONG).show();
				Intent intent_receive = new Intent(NfcSendImageActivity.this, ReceiveActivity.class);
				intent_receive.putExtra("Receive", 1);
				startActivity(intent_receive);
				finish();
				break;
			}
		}
		
	};
	
	@Override
	public void onNdefPushComplete(NfcEvent arg0) {
		mHandler.obtainMessage(NFC_MESSAGE_SENT).sendToTarget();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			processIntent(getIntent());
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			processIntent(getIntent());
		}
	}

	void processIntent(Intent intent) {
		Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		NdefMessage msg = (NdefMessage)rawMsgs[0];
		photo_bit = BitmapFactory.decodeByteArray(msg.getRecords()[0].getPayload(), 0, msg.getRecords()[0].getPayload().length);
		photo_byte = msg.getRecords()[0].getPayload();
		Intent intent_receive = new Intent(NfcSendImageActivity.this, ReceiveActivity.class);
		intent_receive.putExtra("Receive", 2);
		startActivity(intent_receive);
		finish();
	}
	
	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		NdefMessage msg = new NdefMessage(new NdefRecord[] {
				createMimeRecord("text/image", CameraActivity.photo_right)
		});
		return msg;
	}

	public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
		byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
		NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
		return mimeRecord;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nfc_send_image, menu);
		return true;
	}
}
