package com.example.pphotocapsule;

import java.io.IOException;
import java.nio.charset.Charset;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
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

public class NfcMergeImageActivity extends Activity implements CreateNdefMessageCallback, OnNdefPushCompleteCallback {
	NfcAdapter mNfcAdapter;
	private static final int NFC_MERGE_MESSAGE_SENT = 1;
	public static Bitmap photo_bit;
	public static byte[] photo_byte;
	public static Bitmap result_photo_bit;
	public static Database db;
	static Cursor result = null;
	String filename;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfc_merge_image);
		
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		mNfcAdapter.setNdefPushMessageCallback(this, this);
		mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
		
		db = new Database(this);
		try {
			db.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		db.openDataBase();
	}

	private final Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NFC_MERGE_MESSAGE_SENT :
				Toast.makeText(getApplicationContext(), "전송 완료!", Toast.LENGTH_LONG).show();
				Intent intent_receive = new Intent(NfcMergeImageActivity.this, ReceiveMergedImageActivity.class);
				intent_receive.putExtra("Receive", 1);
				startActivity(intent_receive);
				finish();
				break;
			}
		}
		
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nfc_merge_image, menu);
		return true;
	}

	@Override
	public void onNdefPushComplete(NfcEvent event) {
		mHandler.obtainMessage(NFC_MERGE_MESSAGE_SENT).sendToTarget();
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
		
		result = db.getFileName();
		if (result.moveToFirst())
			filename = result.getString(0);

		BitmapFactory.Options bfo = new BitmapFactory.Options();
		bfo.inSampleSize = 2;
		Bitmap bm = BitmapFactory.decodeFile(filename, bfo);
		Bitmap temp = Bitmap.createScaledBitmap(bm, 480, 500, true);
		
		MergeImage merge = new MergeImage();
		result_photo_bit = merge.mergedImage(temp, NfcMergeImageActivity.photo_bit);
		Intent intent_receive = new Intent(NfcMergeImageActivity.this, ReceiveMergedImageActivity.class);
		intent_receive.putExtra("Receive", 2);
		startActivity(intent_receive);
	}
	
	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		NdefMessage msg = new NdefMessage(new NdefRecord[] {
				createMimeRecord("text/merge", MergeActivity.image)
		});
		return msg;
	}

	public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
		byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
		NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
		return mimeRecord;
	}
}
