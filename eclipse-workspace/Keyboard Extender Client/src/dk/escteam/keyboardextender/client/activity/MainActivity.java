package dk.escteam.keyboardextender.client.activity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import dk.escteam.keyboardextender.client.R;
import dk.escteam.keyboardextender.client.activity.connection.ConnectionListActivity;
import dk.escteam.keyboardextender.client.app.KeyboardExtender;
import dk.escteam.keyboardextender.client.fragment.ChemFragment;
import dk.escteam.keyboardextender.client.fragment.CustomLayoutFragment;
import dk.escteam.keyboardextender.client.fragment.LoadoutFragment;
import dk.escteam.keyboardextender.client.fragment.MathFragment;
import dk.escteam.keyboardextender.client.fragment.MathWithNumFragment;
import dk.escteam.keyboardextender.client.fragment.NumpadFragment;
import dk.escteam.keyboardextender.client.view.ControlView;
import dk.escteam.keyboardextender.protocol.KeyboardExtenderActionReceiver;
import dk.escteam.keyboardextender.protocol.action.KeyboardAction;
import dk.escteam.keyboardextender.protocol.action.KeyboardExtenderAction;
import dk.escteam.keyboardextender.protocol.action.MouseClickAction;
import dk.escteam.keyboardextender.protocol.action.MouseMoveAction;
import dk.escteam.keyboardextender.protocol.action.MouseWheelAction;

public class MainActivity<fragList> extends SherlockFragmentActivity implements KeyboardExtenderActionReceiver
{
	private static final String[] EMPTY_STRING_ARRAY = new String[0];
	private ControlView controlView;
	
	private KeyboardExtender application;
	public static SharedPreferences preferences;
	
	private MediaPlayer mpClickOn;
	private MediaPlayer mpClickOff;
	
	private boolean feedbackSound;
	protected PowerManager.WakeLock mWakeLock;
	
	static int count;
	static String[] active_screens;
	static Resources res;
	
	static List<WeakReference<Fragment>> fragList = new ArrayList<WeakReference<Fragment>>();
	
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;
	
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		this.application = (KeyboardExtender) this.getApplication();
		
		MainActivity.preferences = this.application.getPreferences();
		
		String lang = MainActivity.preferences.getString("app_language", "default");
		if (!lang.equals("default"))
		{
			Resources res = getApplicationContext().getResources();
			
			Locale locale = new Locale(lang);
			Locale.setDefault(locale);
			
			Configuration config = new Configuration();
			config.locale = locale;
			
			res.updateConfiguration(config, res.getDisplayMetrics());
		}
		
		setContentView(R.layout.activity_main);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		
		res = getResources();
		
		String[] active_screens_temp = MainActivity.preferences.getString("active_screens", null).split(",");
		
		List<String> result = new LinkedList();
		List<String> filestemp = Arrays.asList(fileList());
		
		for (String item : active_screens_temp)
		{
			boolean isInt = true;
			try
			{
				Integer.parseInt(item);
			}
			catch (NumberFormatException e)
			{
				isInt = false;
				if (filestemp.contains(item))
				{
					result.add(item);
				}
			}
			if (isInt)
			{
				result.add(item);
			}
		}
		
		active_screens = (String[]) result.toArray(EMPTY_STRING_ARRAY);
		
		count = active_screens.length;
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
		{
			@Override
			public void onPageScrollStateChanged(int position)
			{
				MainActivity.unsellectAll();
			}
		});
		
		MainActivity.checkFullscreen(this);
		
		MediaPlayer.create(this, R.raw.clickon);
		MediaPlayer.create(this, R.raw.clickoff);
		
		this.controlView = (ControlView) this.findViewById(R.id.controlView);
		this.checkOnCreate();
		
	}
	
	public void onStart()
	{
		if (MainActivity.preferences.getBoolean("keep_screen_on", false))
		{
			final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
			this.mWakeLock.acquire();
		}
		super.onStart();
	}
	
	@SuppressLint("Wakelock")
	public void onStop()
	{
		if (this.mWakeLock != null)
		{
			this.mWakeLock.release();
		}
		super.onStop();
	}
	
	public static void unsellectAll()
	{
		for (WeakReference<Fragment> ref : fragList)
		{
			LoadoutFragment f = (LoadoutFragment) ref.get();
			if (f != null)
			{
				f.unsellect();
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.CONNECTIONS_MENU_ITEM_ID:
				this.startActivity(new Intent(this, ConnectionListActivity.class));
				break;
			case R.id.KEYBOARD_MENU_ITEM_ID:
				this.toggleKeyboard();
				break;
			case R.id.EDIT_LAYOUT_ITEM_ID:
				this.startActivity(new Intent(this, LayoutEditActivity.class));
				break;
			case R.id.SETTINGS_MENU_ITEM_ID:
				this.startActivity(new Intent(this, SettingsActivity.class));
				break;
			case R.id.HELP_MENU_ITEM_ID:
				this.startActivity(new Intent(this, HelpActivity.class));
				break;
		}
		
		return true;
	}
	
	// Start OF NEW
	public void onResume()
	{
		super.onResume();
		
		this.application.registerActionReceiver(this);
		
		this.feedbackSound = MainActivity.preferences.getBoolean("feedback_sound", false);
	}
	
	public void onPause()
	{
		super.onPause();
		
		this.application.unregisterActionReceiver(this);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		int unicode = event.getUnicodeChar();
		
		if (unicode == 0 && event.getKeyCode() == KeyEvent.KEYCODE_DEL)
		{
			unicode = KeyboardAction.UNICODE_BACKSPACE;
		}
		
		if (unicode != 0)
		{
			this.application.sendAction(new KeyboardAction(unicode));
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	public void receiveAction(KeyboardExtenderAction action)
	{
		/*
		 * if (action instanceof ScreenCaptureResponseAction) {
		 * this.controlView.receiveAction((ScreenCaptureResponseAction) action);
		 * }
		 */
	}
	
	public void mouseClick(byte button, boolean state)
	{
		this.application.sendAction(new MouseClickAction(button, state));
		
		if (this.feedbackSound)
		{
			if (state)
			{
				this.playSound(this.mpClickOn);
			}
			else
			{
				this.playSound(this.mpClickOff);
			}
		}
	}
	
	public void mouseMove(int moveX, int moveY)
	{
		this.application.sendAction(new MouseMoveAction((short) moveX, (short) moveY));
	}
	
	public void mouseWheel(int amount)
	{
		this.application.sendAction(new MouseWheelAction((byte) amount));
	}
	
	private void playSound(MediaPlayer mp)
	{
		if (mp != null)
		{
			mp.seekTo(0);
			mp.start();
		}
	}
	
	// END OF NEW
	
	private void toggleKeyboard()
	{
		InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, 0);
	}
	
	public static void checkFullscreen(Activity act)
	{
		if (((KeyboardExtender) act.getApplication()).getPreferences().getBoolean("fullscreen", false))
		{
			try
			{
				act.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			}
			catch (NullPointerException e)
			{
				
			}
		}
	}
	
	private void checkOnCreate()
	{
		if (this.checkFirstRun())
		{
			this.firstRunDialog();
		}
		else if (this.checkNewVersion())
		{
			this.newVersionDialog();
		}
	}
	
	private boolean checkFirstRun()
	{
		return MainActivity.preferences.getBoolean("debug_firstRun", true);
	}
	
	private void firstRunDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle(R.string.text_first_run_dialog_title);
		builder.setMessage(R.string.text_first_run_dialog);
		builder.setPositiveButton(R.string.text_yes, new OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				MainActivity.this.startActivity(new Intent(MainActivity.this, HelpActivity.class));
				MainActivity.this.disableFirstRun();
			}
		});
		builder.setNegativeButton(R.string.text_no, new OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
				MainActivity.this.disableFirstRun();
			}
		});
		builder.create().show();
	}
	
	private void disableFirstRun()
	{
		Editor editor = MainActivity.preferences.edit();
		editor.putBoolean("debug_firstRun", false);
		editor.commit();
		
		this.updateVersionCode();
	}
	
	private boolean checkNewVersion()
	{
		try
		{
			if (this.getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_META_DATA).versionCode != MainActivity.preferences.getInt("app_versionCode", 0))
			{
				return true;
			}
		}
		catch (NameNotFoundException e)
		{
			this.application.debug(e);
		}
		
		return false;
	}
	
	private void newVersionDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setMessage(R.string.text_new_version_dialog);
		builder.setTitle(R.string.text_new_version_dialog_title);
		builder.setNeutralButton(R.string.text_ok, new OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
				MainActivity.this.updateVersionCode();
			}
		});
		builder.create().show();
	}
	
	private void updateVersionCode()
	{
		try
		{
			Editor editor = MainActivity.preferences.edit();
			editor.putInt("app_versionCode", this.getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_META_DATA).versionCode);
			editor.commit();
		}
		catch (NameNotFoundException e)
		{
			this.application.debug(e);
		}
	}
	
	public void onAttachFragment(Fragment fragment)
	{
		fragList.add(new WeakReference<Fragment>(fragment));
	}
	
	public static String getPageId(int position)
	{
		int actPos = Integer.parseInt(String.valueOf(getOrder()[position]));
		// Debug.out(actPos);
		String id = active_screens[actPos];
		return id;
	}
	
	public static String getPageTitle(int position)
	{
		String PageId = getPageId(position);
		int PageInt;
		try
		{
			PageInt = Integer.parseInt(PageId);
		}
		catch (NumberFormatException e)
		{
			return "";
		}
		return res.getStringArray(R.array.array_keyboard_names_localized)[PageInt];
	}
	
	public static int getCount()
	{
		return count;
	}
	
	public static int[] getOrder()
	{
		int[] STorder = new int[getCount()];
		for (int i = 0; i < getCount(); i++)
		{
			STorder[i] = i;
		}
		
		String[] order = preferences.getString("screen_order", "").split(",");
		if (order.length == getCount())
		{
			int[] results = new int[order.length];
			
			for (int i = 0; i < order.length; i++)
			{
				try
				{
					results[i] = Integer.parseInt(order[i]);
				}
				catch (NumberFormatException nfe)
				{
				}
				;
			}
			return results;
		}
		return STorder;
	}
	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter
	{
		
		public SectionsPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}
		
		@Override
		public Fragment getItem(int position)
		{
			Fragment fragment = null;
			Bundle args = new Bundle();
			
			String PageId = getPageId(position);
			int PageInt;
			try
			{
				PageInt = Integer.parseInt(PageId);
			}
			catch (NumberFormatException e)
			{
				fragment = new CustomLayoutFragment();
				args.putString("layout", PageId);
				fragment.setArguments(args);
				return fragment;
			}
			
			switch (PageInt)
			{
				case 0:
					fragment = new NumpadFragment();
					break;
				case 1:
					fragment = new MathWithNumFragment();
					break;
				case 2:
					fragment = new MathFragment();
					break;
				case 3:
					fragment = new ChemFragment();
					break;
				case 4:
					fragment = new CustomLayoutFragment();
					args.putString("layout", "editor_temp");
					break;
			
			}
			
			fragment.setArguments(args);
			return fragment;
		}
		
		@Override
		public int getCount()
		{
			return MainActivity.getCount();
		}
		
		public CharSequence getPageTitle(int position)
		{
			Locale l = Locale.getDefault();
			Fragment fragtemp = this.getItem(position);
			if (fragtemp instanceof CustomLayoutFragment)
			{
				CustomLayoutFragment customtemp = (CustomLayoutFragment) fragtemp;
				String layout = customtemp.getArguments().getString("layout");
				
				FileInputStream fis;
				try
				{
					fis = openFileInput(layout);
					int nameLength = fis.read();
					byte[] nameBuffer = new byte[nameLength];
					fis.read(nameBuffer, 0, nameLength);
					fis.close();
					return new String(nameBuffer, "UTF-8").toUpperCase(l);
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
			return ((String) MainActivity.getPageTitle(position)).toUpperCase(l);
		}
	}
	
	public final static class Debug
	{
		private Debug()
		{
		}
		
		public static void out(Object msg)
		{
			Log.i("info", msg.toString());
		}
	}
}
