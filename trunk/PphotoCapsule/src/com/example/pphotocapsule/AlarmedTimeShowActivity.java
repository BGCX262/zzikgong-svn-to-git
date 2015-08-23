package com.example.pphotocapsule;

import android.os.Bundle;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.view.Menu;

public class AlarmedTimeShowActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarmed_time_show);
		
		// notificationManager 생성. 
		NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
				
		// pendingintent 생성. notification 알람 메세지를 클릭하면 NotificationMessage이 실행됨. (우리 앱에서는 이걸 메인 activity로 바꾸면 될듯)
		PendingIntent intent = PendingIntent.getActivity(AlarmedTimeShowActivity.this, 0, 
				new Intent(AlarmedTimeShowActivity.this, SplashActivity.class), 0);
				
		// notification 메세지에 나오는 문구들 설정.
		String ticker = "D-day! PhotoCapsule에서 사진을 병합해주세요 :)";
		String title = "포토캡슐 D-day";
		String text = "사진을 완성하는 날이에요";
											
		// 이건 왜이러는지 모르겠음... notification 설정. (사용할 이미지, 메세지, 실행될 시간)
		Notification notification = new Notification(R.drawable.ic_launcher, ticker, System.currentTimeMillis());
		notification.setLatestEventInfo(AlarmedTimeShowActivity.this, title, text, intent);
				
		// notification id 설정해줌. 이걸로 나중에 notification 관리할수있음.
		nm.notify(1234, notification);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.alarmed_time_show, menu);
		return true;
	}

}
