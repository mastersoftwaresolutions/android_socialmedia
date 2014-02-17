package com.socialintegration.test;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

/**
 * A custom class that first checks for any logged-in account, if so then
 * navigates to Result Activity else navigates to Main Activity
 * 
 * @author Master software Solutions
 * 
 */
public class SplashActivity extends Activity {

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_activity);
		try {
			getActionBar().hide();
		} catch (Exception e) {

		}
		// navigates to MainActivity or ResultActivity if the user is already
		// logged in to any account after a time interval of 3 seconds
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
				String account = sharedPreferences.getString(Config.ACCOUNT, "");
				if (account == null || account.equals(""))
					startActivity(new Intent(SplashActivity.this, MainActivity.class));
				else
					startActivity(new Intent(SplashActivity.this, ResultActivity.class));
				finish();
			}
		}, 3000);
	}
}
