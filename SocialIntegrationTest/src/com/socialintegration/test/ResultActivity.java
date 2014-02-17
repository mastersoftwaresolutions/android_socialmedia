package com.socialintegration.test;

import java.net.HttpURLConnection;
import java.net.URL;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Session;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.lazyloading.ImageLoader;

public class ResultActivity extends Activity implements OnClickListener, ConnectionCallbacks,
		OnConnectionFailedListener {
	private TextView			userName, userData;
	private ImageView			userImageView;
	private Button				logoutBtn;
	private ImageLoader			imageLoader;
	private SharedPreferences	sharedPreferences;

	private PlusClient			plusClient;
	private String				account;

	// variables used for linkedin
	final LinkedInOAuthService	oAuthService	= LinkedInOAuthServiceFactory.getInstance().createLinkedInOAuthService(
														Config.LINKEDIN_CONSUMER_KEY, Config.LINKEDIN_CONSUMER_SECRET,
														Config.scopeParams);
	LinkedInRequestToken		linkedInRequestToken;
	// variables used for linkedin

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result_activity);
		try {
			getActionBar().hide();
		} catch (Exception e) {

		}
		userName = (TextView) findViewById(R.id.userTextView);
		userData = (TextView) findViewById(R.id.userData);
		userImageView = (ImageView) findViewById(R.id.userImageView);
		logoutBtn = (Button) findViewById(R.id.logoutBtn);

		logoutBtn.setOnClickListener(this);

		imageLoader = new ImageLoader(this);

		sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);

		try {
			account = sharedPreferences.getString(Config.ACCOUNT, "");
			String name = sharedPreferences.getString(Config.NAME, "");
			String image = sharedPreferences.getString(Config.IMAGE, "");
			String data = sharedPreferences.getString(Config.DATA, "");
			userName.setText(name);
			userData.setText(data);
			imageLoader.displayImage(image, userImageView, R.drawable.ic_launcher);
		} catch (Exception e) {
			e.printStackTrace();
		}

		plusClient = new PlusClient.Builder(this, this, this).setActions("http://schemas.google.com/AddActivity",
				"http://schemas.google.com/BuyActivity").build();

		if (account.equalsIgnoreCase("Facebook")) {
			logoutBtn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.fb_icon), null,
					null, null);
		} else if (account.equalsIgnoreCase("Google+")) {
			logoutBtn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.googleplus), null,
					null, null);
		} else if (account.equalsIgnoreCase("Twitter")) {
			logoutBtn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.twitter), null,
					null, null);
		} else if (account.equalsIgnoreCase("LinkedIn")) {
			logoutBtn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.linkdein), null,
					null, null);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.logoutBtn:
			logout(account);
			break;

		default:
			break;
		}
	}

	/**
	 * logout user from specific account
	 */
	private void logout(String account) {
		if (account.equalsIgnoreCase("Google+")) {
			try {
				plusClient.clearDefaultAccount();
				plusClient.disconnect();
			} catch (Exception e) {
				Editor editor = sharedPreferences.edit();
				editor.putString(Config.ACCOUNT, "");
				editor.putString(Config.NAME, "");
				editor.putString(Config.IMAGE, "");
				editor.putString(Config.DATA, "");
				editor.commit();
				startActivity(new Intent(ResultActivity.this, MainActivity.class));
				finish();
			}
		} else if (account.equalsIgnoreCase("Facebook")) {
			try {
				Session.getActiveSession().closeAndClearTokenInformation();
				Editor editor = sharedPreferences.edit();
				editor.putString(Config.ACCOUNT, "");
				editor.putString(Config.NAME, "");
				editor.putString(Config.IMAGE, "");
				editor.putString(Config.DATA, "");
				editor.commit();
				startActivity(new Intent(ResultActivity.this, MainActivity.class));
				finish();
			} catch (Exception e) {
				Editor editor = sharedPreferences.edit();
				editor.putString(Config.ACCOUNT, "");
				editor.putString(Config.NAME, "");
				editor.putString(Config.IMAGE, "");
				editor.putString(Config.DATA, "");
				editor.commit();
				startActivity(new Intent(ResultActivity.this, MainActivity.class));
				finish();
			}
		} else if (account.equalsIgnoreCase("Twitter")) {
			try {
				Twitter twitter = new TwitterFactory().getInstance();
				twitter.setOAuthAccessToken(null);
				twitter.shutdown();
				Editor editor = sharedPreferences.edit();
				editor.putString(Config.ACCOUNT, "");
				editor.putString(Config.NAME, "");
				editor.putString(Config.IMAGE, "");
				editor.putString(Config.DATA, "");
				editor.putString(MainActivity.PREF_KEY_OAUTH_TOKEN, "");
				editor.putString(MainActivity.PREF_KEY_OAUTH_SECRET, "");
				editor.commit();
				startActivity(new Intent(ResultActivity.this, MainActivity.class));
				finish();
			} catch (Exception e) {
				Editor editor = sharedPreferences.edit();
				editor.putString(Config.ACCOUNT, "");
				editor.putString(Config.NAME, "");
				editor.putString(Config.IMAGE, "");
				editor.putString(Config.DATA, "");
				editor.putString(MainActivity.PREF_KEY_OAUTH_TOKEN, "");
				editor.putString(MainActivity.PREF_KEY_OAUTH_SECRET, "");
				editor.commit();
				startActivity(new Intent(ResultActivity.this, MainActivity.class));
				finish();
			}
		} else if (account.equalsIgnoreCase("LinkedIn")) {
			try {
				linkedInRequestToken = oAuthService
						.getOAuthRequestToken("https://api.linkedin.com/uas/oauth/invalidateToken");
				URL url = new URL(linkedInRequestToken.getAuthorizationUrl());
				HttpURLConnection request = (HttpURLConnection) url.openConnection();
				request.connect();
				Editor editor = sharedPreferences.edit();
				editor.putString(Config.ACCOUNT, "");
				editor.putString(Config.NAME, "");
				editor.putString(Config.IMAGE, "");
				editor.putString(Config.DATA, "");
				editor.commit();
				startActivity(new Intent(ResultActivity.this, MainActivity.class));
				finish();
			} catch (Exception e) {
				Editor editor = sharedPreferences.edit();
				editor.putString(Config.ACCOUNT, "");
				editor.putString(Config.NAME, "");
				editor.putString(Config.IMAGE, "");
				editor.putString(Config.DATA, "");
				editor.commit();
				startActivity(new Intent(ResultActivity.this, MainActivity.class));
				finish();
			}
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {

	}

	@Override
	public void onConnected(Bundle connectionHint) {

	}

	@Override
	public void onDisconnected() {
		Editor editor = sharedPreferences.edit();
		editor.putString(Config.ACCOUNT, "");
		editor.putString(Config.NAME, "");
		editor.putString(Config.IMAGE, "");
		editor.putString(Config.DATA, "");
		editor.commit();
		startActivity(new Intent(ResultActivity.this, MainActivity.class));
		finish();
	}
}
