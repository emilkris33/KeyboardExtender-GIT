package dk.escteam.keyboardextender.client.fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;

import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import dk.escteam.keyboardextender.client.R;
import dk.escteam.keyboardextender.client.activity.MainActivity;
import dk.escteam.keyboardextender.client.view.CustomButtonView;
import dk.escteam.keyboardextender.protocol.KeyboardExtenderActionReceiver;
import dk.escteam.keyboardextender.protocol.action.KeyboardExtenderAction;

public class CustomLayoutFragment extends LoadoutFragment implements KeyboardExtenderActionReceiver
{
	MainActivity activity;
	
	public int col = 4;
	public int row = 5;
	public String name;
	
	String layout;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		final View rootView = inflater.inflate(R.layout.custom_layout, container, false);
		activity = (MainActivity) getSherlockActivity();
		
		// this.application = (KeyboardExtender) this.getApplication();
		
		mainView = (android.support.v7.widget.GridLayout) rootView.findViewById(R.id.custom_layout_grid);
		ViewTreeObserver vto = mainView.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener()
		{
			@Override
			public void onGlobalLayout()
			{
				
				android.support.v7.widget.GridLayout gl = (android.support.v7.widget.GridLayout) rootView.findViewById(R.id.custom_layout_grid);
				
				ViewTreeObserver obs = gl.getViewTreeObserver();
				obs.removeGlobalOnLayoutListener(this);
			}
		});
		
		layout = getArguments().getString("layout");
		
		File file = activity.getBaseContext().getFileStreamPath(layout);
		if (file.exists())
		{
			try
			{
				loadLayout(layout);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return rootView;
	}
	
	public void loadLayout(String FILENAME) throws IOException
	{
		clearlayout();
		
		FileInputStream fis = activity.openFileInput(FILENAME);
		
		int nameLength = fis.read();
		byte[] nameBuffer = new byte[nameLength];
		fis.read(nameBuffer, 0, nameLength);
		
		row = fis.read();
		col = fis.read();
		
		for (int i = 0; i < row; i++)
		{
			for (int j = 0; j < col; j++)
			{
				int textLength = fis.read();
				byte[] textBuffer = new byte[textLength];
				fis.read(textBuffer, 0, textLength);
				
				int outputLength = fis.read();
				byte[] outputBuffer = new byte[outputLength];
				fis.read(outputBuffer, 0, outputLength);
				
				GridLayout.LayoutParams lpBt = new GridLayout.LayoutParams(GridLayout.spec(i), GridLayout.spec(j));
				CustomButtonView bt = new CustomButtonView(activity, null);
				bt.setText(new String(textBuffer, "UTF-8"));
				bt.setLayoutParams(lpBt);
				mainView.addView(bt);
				bt.row = i;
				bt.col = j;
				bt.rows = row;
				bt.cols = col;
				bt.output = new String(outputBuffer, "UTF-8");
			}
		}
		fis.close();
		setName(new String(nameBuffer, "UTF-8"));
	}
	
	public void clearlayout()
	{
		mainView.removeAllViews();
	}
	
	private void setName(String newName)
	{
		name = newName;
		Locale l = Locale.getDefault();
		// ((Button)
		// this.findViewById(R.id.editor_title_strip)).setText(newName.toUpperCase(l));
	}
	
	public void receiveAction(KeyboardExtenderAction action)
	{
		
	}
}
