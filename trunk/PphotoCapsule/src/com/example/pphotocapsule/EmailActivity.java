package com.example.pphotocapsule;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class EmailActivity extends Activity {
	EditText email_editText_address;
	ImageButton email_imageButton_send;
	String email_address;
	Mail m;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		setContentView(R.layout.activity_email);
		
		email_editText_address = (EditText)findViewById(R.id.email_editText_address);
		email_imageButton_send = (ImageButton)findViewById(R.id.email_imageButton_send);
		email_imageButton_send.setOnClickListener(imageButtonClickListener);
	}

	ImageButton.OnClickListener imageButtonClickListener = new ImageButton.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.email_imageButton_send :
				email_address = email_editText_address.getText().toString();
				m = new Mail("somiohh@gmail.com", "tstort0100");
				String[] toArr = {email_address};
				m.setTo(toArr); 
			    m.setFrom("somiohh@gmail.com"); 
			    m.setSubject("<PhotoCapsule>"); 
			    m.setBody("잊지말고 사진합체 사랑합체♥\n\n" + "기념일 : " + ReceiveActivity.mYear + "-" + ReceiveActivity.mMonth + "-"
			    		+ ReceiveActivity.mDate + "\nPhotoCapsule 앱에서 기념일에 사진을 완성시켜주세요! 기념일이 지나면 사진을 더이상 볼 수 없어요 ^^");
			    
			    try {
					if(m.send()) { 
					      Toast.makeText(EmailActivity.this, "전송 완료!", Toast.LENGTH_LONG).show(); 
					} else { 
					      Toast.makeText(EmailActivity.this, "전송 실패!", Toast.LENGTH_LONG).show(); 
					}
				} catch (Exception e) {
					e.printStackTrace();
				} 
				break;
			}
			
		}
		
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.email, menu);
		return true;
	}

}
