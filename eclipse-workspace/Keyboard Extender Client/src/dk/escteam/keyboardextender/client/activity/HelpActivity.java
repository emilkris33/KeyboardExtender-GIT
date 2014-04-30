package dk.escteam.keyboardextender.client.activity;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.webkit.WebView;

import com.actionbarsherlock.app.SherlockActivity;

import dk.escteam.keyboardextender.client.R;
import dk.escteam.keyboardextender.client.app.KeyboardExtender;

public class HelpActivity extends SherlockActivity
{
	private KeyboardExtender application;
	private static SharedPreferences preferences;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		this.application = (KeyboardExtender) this.getApplication();
		this.preferences = this.application.getPreferences();
		
		WebView webview = new WebView(this);
		setContentView(webview);
		webview.loadUrl("file:///android_asset/" + getResources().getString(R.string.lang_prefix) + "-" + "help.htm");
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		MainActivity.checkFullscreen(this);
	}
}
