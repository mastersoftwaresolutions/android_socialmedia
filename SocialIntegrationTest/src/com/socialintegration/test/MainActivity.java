package com.socialintegration.test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.OpenRequest;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.google.code.linkedinapi.schema.Person;
import com.socialintegration.test.LinkedinDialog.OnVerifyListener;
import com.socialintegration.test.TwitterDialog.OnVarificationListener;

/**
 * The following class controls the login events of various social networking
 * applications
 * 
 * @author Master Software Solutions
 * 
 */
public class MainActivity extends Activity implements OnClickListener, ConnectionCallbacks, OnConnectionFailedListener {
	private ImageButton					facebookBtn, googlePlusBtn, twitterBtn, linkedInBtn;

	private static final int			GOOGLE_PLUS_CODE			= 1;
	private PlusClient					plusClient;
	private ConnectionResult			connectionResult;
	private ProgressDialog				progressDialog;

	// variables used for twitter
	public static Twitter				twitter;
	private static RequestToken			requestToken;
	static String						TWITTER_CONSUMER_KEY		= "paste your twitter consumer key";
	static String						TWITTER_CONSUMER_SECRET		= "paste your twitter consumer key";
	static String						PREFERENCE_NAME				= "twitter_oauth";
	static final String					PREF_KEY_OAUTH_TOKEN		= "oauth_token";
	static final String					PREF_KEY_OAUTH_SECRET		= "oauth_token_secret";
	static final String					PREF_KEY_TWITTER_LOGIN		= "isTwitterLogedIn";
	public static final String			TWITTER_CALLBACK_URL		= "oauth://t4jsample";
	static final String					URL_TWITTER_AUTH			= "auth_url";
	static final String					URL_TWITTER_OAUTH_VERIFIER	= "oauth_verifier";
	static final String					URL_TWITTER_OAUTH_TOKEN		= "oauth_token";
	// variables used for twitter

	private static SharedPreferences	sharedPreferences;

	// variables used for linkedin
	public static final String			OAUTH_CALLBACK_HOST			= "litestcalback";
	final LinkedInOAuthService			oAuthService				= LinkedInOAuthServiceFactory.getInstance()
																			.createLinkedInOAuthService(
																					Config.LINKEDIN_CONSUMER_KEY,
																					Config.LINKEDIN_CONSUMER_SECRET,
																					Config.scopeParams);
	final LinkedInApiClientFactory		factory						= LinkedInApiClientFactory.newInstance(
																			Config.LINKEDIN_CONSUMER_KEY,
																			Config.LINKEDIN_CONSUMER_SECRET);
	LinkedInRequestToken				linkedInRequestToken;
	LinkedInApiClient					client;
	LinkedInAccessToken					accessToken					= null;

	// variables used for linkedin

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		try {
			getActionBar().hide();
		} catch (Exception e) {

		}
		facebookBtn = (ImageButton) findViewById(R.id.facebookBtn);
		googlePlusBtn = (ImageButton) findViewById(R.id.googlePlusBtn);
		twitterBtn = (ImageButton) findViewById(R.id.twitterBtn);
		linkedInBtn = (ImageButton) findViewById(R.id.linkedInBtn);

		facebookBtn.setOnClickListener(this);
		googlePlusBtn.setOnClickListener(this);
		twitterBtn.setOnClickListener(this);
		linkedInBtn.setOnClickListener(this);

		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Signing in...");
		getFbHashKey();

		plusClient = new PlusClient.Builder(this, this, this).setActions("http://schemas.google.com/AddActivity",
				"http://schemas.google.com/BuyActivity").build();

		sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.facebookBtn:
			loginToFacebook();
			break;
		case R.id.googlePlusBtn:
			if (!plusClient.isConnected()) {
				if (connectionResult == null) {
					progressDialog.show();
					plusClient.connect();
				} else {
					try {
						connectionResult.startResolutionForResult(this, GOOGLE_PLUS_CODE);
					} catch (SendIntentException e) {
						connectionResult = null;
						plusClient.connect();
					}
				}
			}
			break;
		case R.id.twitterBtn:
			loginToTwitter();
			break;
		case R.id.linkedInBtn:
			loginToLinkedIn();
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (Session.getActiveSession() != null)
			Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		if (requestCode == GOOGLE_PLUS_CODE && resultCode == RESULT_OK) {
			connectionResult = null;
			plusClient.connect();
		}
	}

	@SuppressLint("NewApi")
	private String getFbHashKey() {
		String hashKey = null;
		try {
			PackageInfo info = getPackageManager().getPackageInfo("com.socialintegration.test",
					PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				hashKey = new String(Base64.encodeToString(md.digest(), Base64.DEFAULT));
				Log.d("KeyHash:", hashKey);
			}
		} catch (NameNotFoundException e) {
			return null;
		} catch (NoSuchAlgorithmException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
		return hashKey;
	}

	private void loginToFacebook() {
		openActiveSession(this, true, new StatusCallback() {

			@Override
			public void call(Session session, SessionState state, Exception exception) {
				if (session.isOpened()) {
					Session.setActiveSession(session);
					Request.newMeRequest(session, new GraphUserCallback() {

						@Override
						public void onCompleted(GraphUser user, Response response) {
							if (user != null) {
								Log.d("facebook name", user.getName() + " " + user.asMap().get("email"));
								String userimage = "http://graph.facebook.com/" + user.getId() + "/picture?style=small";

								Editor editor = sharedPreferences.edit();
								editor.putString(Config.ACCOUNT, "Facebook");
								editor.putString(Config.NAME, "Name: " + user.getName());
								editor.putString(Config.IMAGE, userimage);
								String location = null;
								try {
									location = user.getLocation().getCity();
									if (location == null || location.equals(""))
										location = "Not found";
								} catch (Exception e) {
									e.printStackTrace();
									location = "Not found";
								}

								String data = "UserName: " + user.getUsername() + "\n" + "Location: " + location + "\n";
								editor.putString(Config.DATA, data);
								editor.commit();

								/*
								 * HashMap<String, String> map = new
								 * HashMap<String, String>(); map.put("account",
								 * "Google+"); map.put("name",
								 * "Name:\n"+user.getFirstName
								 * ()+" "+user.getLastName()); map.put("image",
								 * userimage); String data =
								 * "UserName: "+user.getUsername()+"\n"+
								 * "Location: "+user.getLocation()+"\n";
								 * map.put("userData", data);
								 */
								// in.putExtra("data", map);
								startActivity(new Intent(MainActivity.this, ResultActivity.class));
								overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
								finish();
							}
						}
					}).executeAsync();
				}
			}
		}, Arrays.asList("email", "publish_actions"));
	}

	private void postPhoto() {
		Bitmap image = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher);
		Request request = Request.newUploadPhotoRequest(Session.getActiveSession(), image, new Request.Callback() {
			@Override
			public void onCompleted(Response response) {
				System.out.println("photo post response: " + response);
			}
		});
		request.executeAsync();
	}

	private void postStatus() {
		Request request = Request.newStatusUpdateRequest(Session.getActiveSession(), "test status",
				new Request.Callback() {
					@Override
					public void onCompleted(Response response) {
						System.out.println("status post response: " + response);
					}
				});
		request.executeAsync();
	}

	private Session openActiveSession(Activity activity, boolean allowLoginUI, StatusCallback callback,
			List<String> permissions) {
		OpenRequest openRequest = new OpenRequest(activity).setPermissions(permissions).setCallback(callback);
		Session session = new Session.Builder(activity).build();
		if (SessionState.CREATED_TOKEN_LOADED.equals(session.getState()) || allowLoginUI) {
			Session.setActiveSession(session);
			session.openForPublish(openRequest);
			return session;
		}
		return null;
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		System.out.println("inside onconnectionfailed");
		if (progressDialog.isShowing()) {
			if (result.hasResolution()) {
				try {
					result.startResolutionForResult(this, GOOGLE_PLUS_CODE);
				} catch (SendIntentException e) {
					plusClient.connect();
				}
			}
		}
		connectionResult = result;
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		System.out.println("inside onconnected");
		progressDialog.dismiss();
		String name = plusClient.getCurrentPerson().getDisplayName();
		String userimage = plusClient.getCurrentPerson().getImage().getUrl();
		String location = plusClient.getCurrentPerson().getCurrentLocation();
		if (location == null || location.equals(""))
			location = "Not found";
		String username = "";

		Editor editor = sharedPreferences.edit();
		editor.putString(Config.ACCOUNT, "Google+");
		editor.putString(Config.NAME, "Name: " + name);
		editor.putString(Config.IMAGE, userimage);
		String data = "UserName: " + username + "\n" + "Location: " + location + "\n";
		editor.putString(Config.DATA, data);
		editor.commit();

		/*
		 * HashMap<String, String> map = new HashMap<String, String>();
		 * map.put("account", "Google+"); map.put("name", "Name:\n"+name);
		 * map.put("image", userimage); String data =
		 * "UserName: "+username+"\n"+ "Location: "+location+"\n";
		 * map.put("userData", data);
		 */
		// in.putExtra("data", map);
		startActivity(new Intent(MainActivity.this, ResultActivity.class));
		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		finish();
	}

	@Override
	public void onDisconnected() {
		System.out.println("inside ondisconnect");
	}

	/**
	 * login the user using twitter client
	 */
	private void loginToTwitter() {
		ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Loading...");
		progressDialog.setCancelable(true);

		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
		builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
		Configuration configuration = builder.build();

		TwitterFactory factory = new TwitterFactory(configuration);
		twitter = factory.getInstance();

		try {
			requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
			TwitterDialog dialog = new TwitterDialog(this, requestToken.getAuthenticationURL() + "&force_login=true",
					progressDialog);
			dialog.show();
			dialog.setVarification(new OnVarificationListener() {

				@Override
				public void onVarify(String verifier) {
					try {
						AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
						Editor e = sharedPreferences.edit();
						e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
						e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
						// e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
						e.commit();
						long userID = accessToken.getUserId();
						User user = twitter.showUser(userID);
						Editor editor = sharedPreferences.edit();
						editor.putString(Config.ACCOUNT, "Twitter");
						editor.putString(Config.NAME, "Name: " + user.getName());
						editor.putString(Config.IMAGE, user.getProfileImageURL());
						String location = user.getLocation();
						if (location == null || location.equals(""))
							location = "Not found";
						String data = "UserName: " + user.getScreenName() + "\n" + "Location: " + location + "\n";
						editor.putString(Config.DATA, data);
						editor.commit();
						startActivity(new Intent(MainActivity.this, ResultActivity.class));
						overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
						finish();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		progressDialog.show();
	}

	/**
	 * posts tweet to twitter
	 */
	private void postToTwitter() {
		String status = "test status for twitter";
		new AsyncTask<String, Void, Void>() {

			protected void onPreExecute() {
				progressDialog.setMessage("Posting status to Twitter...");
				progressDialog.show();
			};

			@Override
			protected Void doInBackground(String... params) {
				try {
					ConfigurationBuilder builder = new ConfigurationBuilder();
					builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
					builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
					String access_token = sharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
					String access_token_secret = sharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");
					AccessToken accessToken = new AccessToken(access_token, access_token_secret);
					Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
					twitter4j.Status response = twitter.updateStatus(params[0]);
					System.out.println("twitter status response: " + response);
				} catch (TwitterException e) {
					e.printStackTrace();
				}
				return null;
			}

			protected void onPostExecute(Void result) {
				progressDialog.dismiss();
				Toast.makeText(getApplicationContext(), "Status tweeted successfully", Toast.LENGTH_SHORT).show();
			};
		}.execute(status);
	}

	/**
	 * login the user using linkedin client
	 */
	private void loginToLinkedIn() {
		ProgressDialog progressDialog = new ProgressDialog(this);
		LinkedinDialog d = new LinkedinDialog(this, progressDialog);
		d.show();
		d.setVerifierListener(new OnVerifyListener() {
			@Override
			public void onVerify(String verifier) {
				try {
					Log.i("LinkedinSample", "verifier: " + verifier);
					accessToken = LinkedinDialog.oAuthService.getOAuthAccessToken(LinkedinDialog.liToken, verifier);
					LinkedinDialog.factory.createLinkedInApiClient(accessToken);
					client = factory.createLinkedInApiClient(accessToken);
					Person p = client.getProfileForCurrentUser();
					Editor editor = sharedPreferences.edit();
					editor.putString(Config.ACCOUNT, "LinkedIn");
					editor.putString(Config.NAME, "Name: " + p.getFirstName() + " " + p.getLastName());
					editor.putString(Config.IMAGE, p.getPictureUrl());
					String location;
					try {
						location = p.getLocation().getDescription();
					} catch (Exception e) {
						location = "Not found";
					}
					if (location == null || location.equals(""))
						location = "Not found";
					String data = "UserName: " + "\n" + "Location: " + location + "\n";
					editor.putString(Config.DATA, data);
					editor.commit();
					startActivity(new Intent(MainActivity.this, ResultActivity.class));
					overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
					finish();
				} catch (Exception e) {
					Log.i("LinkedinSample", "error to get verifier");
					e.printStackTrace();
				}
			}
		});
		progressDialog.setMessage("Loading...");
		progressDialog.setCancelable(true);
		progressDialog.show();
	}

	/**
	 * sharing message on linkedin
	 */
	private void shareToLinkedIn() {
		String share = "message to share";
		if (null != share && !share.equalsIgnoreCase("")) {
			client = factory.createLinkedInApiClient(accessToken);
			client.postNetworkUpdate(share);
			Toast.makeText(this, "Shared sucessfully", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "Please enter the text to share", Toast.LENGTH_SHORT).show();
		}
	}
}
