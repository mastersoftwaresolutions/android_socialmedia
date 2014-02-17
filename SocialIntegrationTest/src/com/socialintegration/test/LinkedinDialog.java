package com.socialintegration.test;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;

/**
 * displays a webview for linkedin login dialog
 * 
 * @author Master Software Solutions
 * 
 */
public class LinkedinDialog extends Dialog {
	private ProgressDialog					progressDialog	= null;
	public static LinkedInApiClientFactory	factory;
	public static LinkedInOAuthService		oAuthService;
	public static LinkedInRequestToken		liToken;

	/**
	 * constructor for setting up linkedin login dialog
	 * 
	 * @param context
	 * @param progressDialog
	 *            progress dialog to be shown until webview is completely loaded
	 */
	public LinkedinDialog(Context context, ProgressDialog progressDialog) {
		super(context);
		this.progressDialog = progressDialog;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ln_dialog);

		setWebView();
	}

	/**
	 * setting up the webview
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void setWebView() {
		LinkedinDialog.oAuthService = LinkedInOAuthServiceFactory.getInstance().createLinkedInOAuthService(
				Config.LINKEDIN_CONSUMER_KEY, Config.LINKEDIN_CONSUMER_SECRET);
		LinkedinDialog.factory = LinkedInApiClientFactory.newInstance(Config.LINKEDIN_CONSUMER_KEY,
				Config.LINKEDIN_CONSUMER_SECRET);

		LinkedinDialog.liToken = LinkedinDialog.oAuthService.getOAuthRequestToken(Config.OAUTH_CALLBACK_URL);

		WebView mWebView = (WebView) findViewById(R.id.webkitWebView1);
		mWebView.getSettings().setJavaScriptEnabled(true);

		Log.i("LinkedinSample", LinkedinDialog.liToken.getAuthorizationUrl());
		mWebView.loadUrl(LinkedinDialog.liToken.getAuthorizationUrl());
		mWebView.setWebViewClient(new LinkedInWebViewClient());
		ImageButton crossButton = (ImageButton) findViewById(R.id.crossBtn);
		crossButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				cancel();
			}
		});
	}

	/**
	 * web view client for verification of oauth request
	 * 
	 * @author Master Software Solutions
	 * 
	 */
	class LinkedInWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.contains(Config.OAUTH_CALLBACK_URL)) {
				Uri uri = Uri.parse(url);
				String verifier = uri.getQueryParameter("oauth_verifier");

				cancel();

				for (OnVerifyListener d : listeners) {
					d.onVerify(verifier);
				}
			} else {
				view.loadUrl(url);
			}
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		}
	}

	private List<OnVerifyListener>	listeners	= new ArrayList<OnVerifyListener>();

	/**
	 * sets the varification listener
	 * 
	 * @param data
	 */
	public void setVerifierListener(OnVerifyListener data) {
		listeners.add(data);
	}

	interface OnVerifyListener {

		/**
		 * for varification of oauth url
		 * 
		 * @param verifier
		 *            url to be verified
		 */
		void onVerify(String verifier);
	}
}
