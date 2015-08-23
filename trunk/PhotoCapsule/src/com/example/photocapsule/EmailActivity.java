package com.example.photocapsule;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
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
			    m.setBody(CameraActivity.date + "\n�������� ������ü �����ü��\n\n" + "PhotoCapsule �ۿ��� ����Ͽ� ������ �ϼ������ּ���! ������� ������ ������ ���̻� �� �� ����� ^^");
			    
			    try {
					if(m.send()) { 
					      Toast.makeText(EmailActivity.this, "���� �Ϸ�!", Toast.LENGTH_LONG).show(); 
					} else { 
					      Toast.makeText(EmailActivity.this, "���� ����!", Toast.LENGTH_LONG).show(); 
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
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
