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
		
		// notificationManager ����. 
		NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
				
		// pendingintent ����. notification �˶� �޼����� Ŭ���ϸ� NotificationMessage�� �����. (�츮 �ۿ����� �̰� ���� activity�� �ٲٸ� �ɵ�)
		PendingIntent intent = PendingIntent.getActivity(AlarmedTimeShowActivity.this, 0, 
				new Intent(AlarmedTimeShowActivity.this, SplashActivity.class), 0);
				
		// notification �޼����� ������ ������ ����.
		String ticker = "D-day! PhotoCapsule���� ������ �������ּ��� :)";
		String title = "����ĸ�� D-day";
		String text = "������ �ϼ��ϴ� ���̿���";
											
		// �̰� ���̷����� �𸣰���... notification ����. (����� �̹���, �޼���, ����� �ð�)
		Notification notification = new Notification(R.drawable.ic_launcher, ticker, System.currentTimeMillis());
		notification.setLatestEventInfo(AlarmedTimeShowActivity.this, title, text, intent);
				
		// notification id ��������. �̰ɷ� ���߿� notification �����Ҽ�����.
		nm.notify(1234, notification);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.alarmed_time_show, menu);
		return true;
	}

}
