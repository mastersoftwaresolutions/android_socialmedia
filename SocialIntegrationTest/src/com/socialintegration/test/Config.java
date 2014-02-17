package com.socialintegration.test;

/**
 * config file for linkedin
 * 
 * @author Master Software Solutions
 * 
 */
public class Config {
	public static String		LINKEDIN_CONSUMER_KEY		= "paste your linkedin consumer key";
	public static String		LINKEDIN_CONSUMER_SECRET	= "paste your linkedin consumer secret";
	public static String		scopeParams					= "rw_nus+r_basicprofile";

	public static String		OAUTH_CALLBACK_SCHEME		= "x-oauthflow-linkedin";
	public static String		OAUTH_CALLBACK_HOST			= "callback";
	public static String		OAUTH_CALLBACK_URL			= OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;

	public static final String	ACCOUNT						= "account";
	public static final String	NAME						= "name";
	public static final String	IMAGE						= "image";
	public static final String	DATA						= "data";
}
