/*
 * Copyright (C) 2010 Eric Harlow
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dk.escteam.keyboardextender.client.dragndrop;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListActivity;

import dk.escteam.keyboardextender.client.R;
import dk.escteam.keyboardextender.client.activity.MainActivity;
import dk.escteam.keyboardextender.client.app.KeyboardExtender;

public class DragNDropListActivity extends SherlockListActivity
{
	private static final String[] EMPTY_STRING_ARRAY = new String[0];
	
	private KeyboardExtender application;
	private SharedPreferences preferences;
	Object[] mListContent;
	ArrayList<String> content;
	ArrayList<String> orgpos;
	Resources res;
	
	String[] Values;
	String[] Names;
	
	/**
	 * Called when the activity is first created.
	 * 
	 * @param <position>
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		this.application = (KeyboardExtender) this.getApplication();
		this.preferences = this.application.getPreferences();
		
		res = getResources();
		getNames();
		
		mListContent = MainActivity.preferences.getString("active_screens", null).split(",");
		
		setContentView(R.layout.dragndroplistview);
		
		content = new ArrayList<String>(mListContent.length);
		for (int i = 0; i < mListContent.length; i++)
		{
			int position = Arrays.asList(Values).indexOf((String) mListContent[i]);
			content.add(Names[position]);
		}
		orgpos = new ArrayList<String>(content);
		
		setListAdapter(new DragNDropAdapter(this, new int[] {
			R.layout.dragitem
		}, new int[] {
			R.id.TextView01
		}, content));// new DragNDropAdapter(this,content)
		ListView listView = getListView();
		
		if (listView instanceof DragNDropListView)
		{
			((DragNDropListView) listView).setDropListener(mDropListener);
			((DragNDropListView) listView).setRemoveListener(mRemoveListener);
			((DragNDropListView) listView).setDragListener(mDragListener);
		}
		
		MainActivity.checkFullscreen(this);
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void getNames()
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
		
		Values = (String[]) ArrayUtils.addAll(fixedValues, customFiles);
		Names = (String[]) ArrayUtils.addAll(fixedNames, customNames);
	}
	
	protected void onStop()
	{
		super.onStop();
		// We need an Editor object to make preference changes.
		// All objects are from android.context.Context
		SharedPreferences.Editor editor = MainActivity.preferences.edit();
		
		Object[] finalary = content.toArray();
		
		String finalpos = "";
		
		for (int i = 0; i < finalary.length; i++)
		{
			finalpos = finalpos + orgpos.indexOf(finalary[i]);
			finalpos = finalpos + ",";
		}
		
		Debug.out(finalpos);
		editor.putString("screen_order", finalpos);
		
		// Commit the edits!
		editor.commit();
	}
	
	private DropListener mDropListener = new DropListener()
	{
		public void onDrop(int from, int to)
		{
			ListAdapter adapter = getListAdapter();
			if (adapter instanceof DragNDropAdapter)
			{
				((DragNDropAdapter) adapter).onDrop(from, to);
				getListView().invalidateViews();
			}
		}
	};
	
	private RemoveListener mRemoveListener = new RemoveListener()
	{
		public void onRemove(int which)
		{
			ListAdapter adapter = getListAdapter();
			if (adapter instanceof DragNDropAdapter)
			{
				((DragNDropAdapter) adapter).onRemove(which);
				getListView().invalidateViews();
			}
		}
	};
	
	private DragListener mDragListener = new DragListener()
	{
		
		int backgroundColor = 0xe0103010;
		int defaultBackgroundColor;
		
		public void onDrag(int x, int y, ListView listView)
		{
			// TODO Auto-generated method stub
		}
		
		public void onStartDrag(View itemView)
		{
			itemView.setVisibility(View.INVISIBLE);
			defaultBackgroundColor = itemView.getDrawingCacheBackgroundColor();
			itemView.setBackgroundColor(backgroundColor);
			ImageView iv = (ImageView) itemView.findViewById(R.id.ImageView01);
			if (iv != null)
				iv.setVisibility(View.INVISIBLE);
		}
		
		public void onStopDrag(View itemView)
		{
			itemView.setVisibility(View.VISIBLE);
			itemView.setBackgroundColor(defaultBackgroundColor);
			ImageView iv = (ImageView) itemView.findViewById(R.id.ImageView01);
			if (iv != null)
				iv.setVisibility(View.VISIBLE);
		}
		
	};
	
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