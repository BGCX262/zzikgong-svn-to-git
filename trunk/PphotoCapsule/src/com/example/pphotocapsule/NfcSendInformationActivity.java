package com.example.pphotocapsule;

import java.nio.charset.Charset;

import android.app.Activity;
import android.content.Intent;
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

public class NfcSendInformationActivity extends Activity implements CreateNdefMessageCallback, OnNdefPushCompleteCallback {
	NfcAdapter mNfcAdapter;
	private static final int NFC_MESSAGE_SENT = -1;
	public static String info;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfc_send_information);
		
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		mNfcAdapter.setNdefPushMessageCallback(this, this);
		mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nfc_send_information, menu);
		return true;
	}

	private final Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NFC_MESSAGE_SENT :
				Toast.makeText(getApplicationContext(), "전송 완료!", Toast.LENGTH_LONG).show();
				Intent intent_nfc_image = new Intent(NfcSendInformationActivity.this, NfcSendImageActivity.class);
				startActivity(intent_nfc_image);
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
		info = new String(msg.getRecords()[0].getPayload());
		Intent intent_receive = new Intent(NfcSendInformationActivity.this, NfcSendImageActivity.class);
		startActivity(intent_receive);
		finish();
	}
	
	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		String information = CameraActivity.mYear + "-" + CameraActivity.mMonth + "-" + CameraActivity.mDay + "-" + CameraActivity.mHour + "-" + 
						CameraActivity.mMinute + "-" + CameraActivity.lat + "-" + CameraActivity.lng + "-" + CameraActivity.s;
		info = information;
		NdefMessage msg = new NdefMessage(new NdefRecord[] {
				createMimeRecord("text/info", information.getBytes())
		});
		return msg;
	}

	public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
		byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
		NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
		return mimeRecord;
	}
}
