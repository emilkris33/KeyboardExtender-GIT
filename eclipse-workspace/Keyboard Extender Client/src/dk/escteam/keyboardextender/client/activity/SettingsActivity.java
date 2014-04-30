package dk.escteam.keyboardextender.client.activity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

import dk.escteam.keyboardextender.client.R;
import dk.escteam.keyboardextender.client.app.KeyboardExtender;
import dk.escteam.keyboardextender.client.view.MultiSelectListPreference;

public class SettingsActivity extends SherlockPreferenceActivity
{
	private static final String[] tabFloatPreferences = {
	        "control_sensitivity", "control_acceleration", "control_immobile_distance", "screenCapture_cursor_size", "buttons_size", "wheel_bar_width"
	};
	private static final String[] tabIntPreferences = {
	        "control_click_delay", "control_hold_delay"
	};
	
	private static final int resetPreferencesMenuItemId = 0;
	private static final String[] EMPTY_STRING_ARRAY = new String[0];
	
	private KeyboardExtender application;
	private SharedPreferences preferences;
	Resources res;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		this.addPreferencesFromResource(R.xml.settings);
		
		this.application = (KeyboardExtender) this.getApplication();
		this.preferences = this.application.getPreferences();
		
		res = getResources();
		loadCustomFiles();
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		MainActivity.checkFullscreen(this);
	}
	
	protected void onDestroy()
	{
		super.onDestroy();
	}
	
	protected void onPause()
	{
		super.onPause();
		
		// this.checkPreferences();
	}
	
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(Menu.NONE, resetPreferencesMenuItemId, Menu.NONE, this.getResources().getString(R.string.text_reset_preferences));
		
		return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case resetPreferencesMenuItemId:
				this.resetPreferences();
				break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void checkPreferences()
	{
		Editor editor = this.preferences.edit();
		
		for (String s : tabFloatPreferences)
		{
			try
			{
				Float.parseFloat(this.preferences.getString(s, null));
			}
			catch (NumberFormatException e)
			{
				this.application.debug(e);
				editor.remove(s);
			}
		}
		
		for (String s : tabIntPreferences)
		{
			try
			{
				Integer.parseInt(this.preferences.getString(s, null));
			}
			catch (NumberFormatException e)
			{
				this.application.debug(e);
				editor.remove(s);
			}
		}
		
		editor.commit();
		
		PreferenceManager.setDefaultValues(this, R.xml.settings, true);
	}
	
	private void resetPreferences()
	{
		this.setPreferenceScreen(null);
		
		Editor editor = this.preferences.edit();
		editor.clear();
		editor.commit();
		
		PreferenceManager.setDefaultValues(this, R.xml.settings, true);
		
		this.addPreferencesFromResource(R.xml.settings);
	}
	
	public void loadCustomFiles()
	{
		String[] filestemp = fileList();
		List<String> list = new ArrayList<String>(Arrays.asList(filestemp));
		list.remove("editor_temp");
		String[] customFiles = list.toArray(EMPTY_STRING_ARRAY);
		
		String[] customNames = new String[customFiles.length];
		for (int i = 0; i < customFiles.length; i++)
		{
			FileInputStream fis;
			try
			{
				fis = openFileInput(customFiles[i]);
				int nameLength = fis.read();
				byte[] nameBuffer = new byte[nameLength];
				fis.read(nameBuffer, 0, nameLength);
				fis.close();
				customNames[i] = new String(nameBuffer, "UTF-8");
			}
			catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String[] fixedValues = res.getStringArray(R.array.array_keyboard_names);
		String[] fixedNames = res.getStringArray(R.array.array_keyboard_names_localized);
		
		String[] Values = (String[]) ArrayUtils.addAll(fixedValues, customFiles);
		String[] Names = (String[]) ArrayUtils.addAll(fixedNames, customNames);
		
		MultiSelectListPreference alp = (MultiSelectListPreference) findPreference("active_screens");
		alp.setEntries(Names);
		alp.setEntryValues(Values);
		
	}
}
