package com.example.mobilemultimediacontents_timecapsule;

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
import android.text.format.Time;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class NFCActivity extends Activity implements CreateNdefMessageCallback, OnNdefPushCompleteCallback {
	NfcAdapter mNfcAdapter;
	TextView mInfoText;
	private static final int MESSAGE_SENT = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfc);
		
		mInfoText = (TextView)findViewById(R.id.nfc_textView);
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mNfcAdapter == null) {
			mInfoText.setText("NFC 미지원 디바이스");
		}
		mNfcAdapter.setNdefPushMessageCallback(this, this);
		mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
	}
	
	@Override
	public void onNdefPushComplete(NfcEvent event) {
		mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
		
	}

	private final Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_SENT :
				Toast.makeText(getApplicationContext(), "메시지 전송 완료!", Toast.LENGTH_LONG).show();
				break;
			}
		}
		
	};
	
	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
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
		mInfoText.setText(new String(msg.getRecords()[0].getPayload()));
	}
	
	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		Time time = new Time();
		time.setToNow();
		String text = ("Beam me up!\n\n" + "Beam Time : " + time.format("%H : %M : %S"));
		NdefMessage msg = new NdefMessage(new NdefRecord[] {
			createMimeRecord("application/com.appsutdio.sample.android.beam", text.getBytes())
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
		getMenuInflater().inflate(R.menu.nfc, menu);
		return true;
	}
}
