package com.droid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class Activity02 extends Activity {
	private OverlayThread overlayThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_2);

	}

	// ����overlay���ɼ�
	private class OverlayThread implements Runnable {
		@Override
		public void run() {
			tin.getCityName("�Ϻ�");
		}

	}

	public void go(View v) {
		Intent intent = new Intent(Activity02.this, Activity01.class);
		startActivity(intent);
		overlayThread = new OverlayThread();
		Handler handler = new Handler();
		handler.postDelayed(overlayThread, 3000);
	}

	static LocateIn tin;

	public static void setLocateIn(LocateIn in) {
		tin = in;
	}

	public interface LocateIn {
		public void getCityName(String name);
	}
}
