package fi.uni.aalto.controllers;

/*
 * Sourcecode from http://www.helloandroid.com/tutorials/using-facebook-sdk-android-development-part-2
 * Slightly modified
 * 
 * 
 * */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public abstract class FBConnectionActivity extends Activity {
	public static final String TAG = "FACEBOOK";
	private Facebook mFacebook;
	public static final String APP_ID = "209858342455450";
	private AsyncFacebookRunner mAsyncRunner;
	private static final String[] PERMS = new String[] { "read_stream, publish_stream" };
	private SharedPreferences sharedPrefs;
	protected Context mContext;


	public void setConnection() {
		mContext = this;
		mFacebook = new Facebook(APP_ID);
		mAsyncRunner = new AsyncFacebookRunner(mFacebook);
	}
	
	public void getID() {
		if (isSession()) {
			Log.d(TAG, "sessionValid");
			mAsyncRunner.request("me", new IDRequestListener());
		} else {
			// no logged in, so relogin
			Log.d(TAG, "sessionNOTValid, relogin");
			mFacebook.authorize(this, PERMS, new LoginDialogListener());
		}
	}


	public boolean isSession() {
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		String access_token = sharedPrefs.getString("access_token", "x");
		Long expires = sharedPrefs.getLong("access_expires", -1);
		Log.d(TAG, access_token);

		if (access_token != null && expires != -1) {
			mFacebook.setAccessToken(access_token);
			mFacebook.setAccessExpires(expires);
		}
		return mFacebook.isSessionValid();
	}

	private class LoginDialogListener implements DialogListener {

		public void onComplete(Bundle values) {
			Log.d(TAG, "LoginONComplete");
			String token = mFacebook.getAccessToken();
			long token_expires = mFacebook.getAccessExpires();
			Log.d(TAG, "AccessToken: " + token);
			Log.d(TAG, "AccessExpires: " + token_expires);
			sharedPrefs = PreferenceManager
					.getDefaultSharedPreferences(mContext);
			sharedPrefs.edit().putLong("access_expires", token_expires)
			.commit();
			sharedPrefs.edit().putString("access_token", token).commit();
			mAsyncRunner.request("me", new IDRequestListener());
		}

		public void onFacebookError(FacebookError e) {
			Log.d(TAG, "FacebookError: " + e.getMessage());
		}

		public void onError(DialogError e) {
			Log.d(TAG, "Error: " + e.getMessage());
		}

		public void onCancel() {
			Log.d(TAG, "OnCancel");
		}

	}

	private class IDRequestListener implements RequestListener {

		public void onComplete(String response, Object state) {
			try {
				Log.d(TAG, "IDRequestONComplete");
				Log.d(TAG, "Response: " + response.toString());
				JSONObject json = Util.parseJson(response);

				final String id = json.getString("id");
				final String first_name = json.getString("first_name");
				final String last_name = json.getString("last_name");
				

				sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
				sharedPrefs.edit().putString("id", id).commit();
				sharedPrefs.edit().putString("first_name", first_name).commit();
				sharedPrefs.edit().putString("last_name", last_name).commit();


				FBConnectionActivity.this.runOnUiThread(new Runnable() {
					public void run() {
					}
				});
			} catch (JSONException e) {
				Log.d(TAG, "JSONException: " + e.getMessage());
			} catch (FacebookError e) {
				Log.d(TAG, "FacebookError: " + e.getMessage());
			}
		}

		public void onIOException(IOException e, Object state) {
			Log.d(TAG, "IOException: " + e.getMessage());
		}

		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			Log.d(TAG, "FileNotFoundException: " + e.getMessage());
		}

		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			Log.d(TAG, "MalformedURLException: " + e.getMessage());
		}

		public void onFacebookError(FacebookError e, Object state) {
			Log.d(TAG, "FacebookError: " + e.getMessage());
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mFacebook.authorizeCallback(requestCode, resultCode, data);
	}
}
