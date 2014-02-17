package com.socialintegration.test;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

/**
 * displays a webview for twitter login
 * 
 * @author Master Software Solutions
 * 
 */
public class TwitterDialog extends Dialog {
	String					url;
	ProgressDialog			progressDialog;
	OnVarificationListener	listener;

	interface OnVarificationListener {

		/**
		 * for varification of oauth url
		 * 
		 * @param verifier
		 *            url to be varified
		 */
		void onVarify(String verifier);
	}

	/**
	 * constructor for setting up twitter login dialog
	 * 
	 * @param context
	 * @param url
	 *            url to passed to webview
	 * @param progressDialog
	 *            progress dialog to be shown until webview is completely loaded
	 */
	public TwitterDialog(Context context, String url, ProgressDialog progressDialog) {
		super(context);
		this.url = url;
		this.progressDialog = progressDialog;
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ln_dialog);
		WebView mWebView = (WebView) findViewById(R.id.webkitWebView1);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.loadUrl(url);
		mWebView.setWebViewClient(new TwitterWebViewClient());
		ImageButton crossButton = (ImageButton) findViewById(R.id.crossBtn);
		crossButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				cancel();
			}
		});
	}

	/**
	 * webview client for varification of oauth request
	 * 
	 * @author Master Software Solution
	 * 
	 */
	class TwitterWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.contains(MainActivity.TWITTER_CALLBACK_URL)) {
				Uri uri = Uri.parse(url);
				String verifier = uri.getQueryParameter("oauth_verifier");
				listener.onVarify(verifier);
				cancel();
			} else
				view.loadUrl(url);
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

	/**
	 * sets the varification listener
	 * 
	 * @param listener
	 */
	public void setVarification(OnVarificationListener listener) {
		this.listener = listener;
	}
}
