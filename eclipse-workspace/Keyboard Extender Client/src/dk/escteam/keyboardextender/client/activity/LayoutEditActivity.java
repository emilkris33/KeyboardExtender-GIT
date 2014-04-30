package dk.escteam.keyboardextender.client.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;

import dk.escteam.keyboardextender.client.R;
import dk.escteam.keyboardextender.client.app.KeyboardExtender;
import dk.escteam.keyboardextender.client.view.EditorButtonView;

public class LayoutEditActivity extends SherlockActivity
{
	private static final String[] EMPTY_STRING_ARRAY = new String[0];
	
	android.support.v7.widget.GridLayout mainView;
	KeyboardExtender application;
	
	public int col = 4;
	public int row = 5;
	public String name;
	
	String editorFile = "editor_temp";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.application = (KeyboardExtender) this.getApplication();
		
		setContentView(R.layout.activity_layout_edit);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		
		mainView = (android.support.v7.widget.GridLayout) this.findViewById(R.id.editor_main_view);
		
		File file = getBaseContext().getFileStreamPath(editorFile);
		if (file.exists())
		{
			try
			{
				loadLayout(editorFile);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				generateLayout();
			}
		}
		else
		{
			generateLayout();
		}
		
		MainActivity.checkFullscreen(this);
	}
	
	public void generateLayout()
	{
		clearlayout();
		
		for (int i = 0; i < row; i++)
		{
			for (int j = 0; j < col; j++)
			{
				GridLayout.LayoutParams lpBt = new GridLayout.LayoutParams(GridLayout.spec(i), GridLayout.spec(j));
				EditorButtonView bt = new EditorButtonView(this, null);
				bt.setText("");
				bt.setLayoutParams(lpBt);
				mainView.addView(bt);
				bt.row = i;
				bt.col = j;
			}
		}
		
		setName("New Layout");
	}
	
	public void loadLayout(String FILENAME) throws IOException
	{
		clearlayout();
		
		FileInputStream fis = openFileInput(FILENAME);
		
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
				EditorButtonView bt = new EditorButtonView(this, null);
				bt.setText(new String(textBuffer, "UTF-8"));
				bt.setLayoutParams(lpBt);
				mainView.addView(bt);
				bt.row = i;
				bt.col = j;
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
	
	private void resize()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// Get the layout inflater
		LayoutInflater inflater = this.getLayoutInflater();
		
		builder.setCancelable(false);
		builder.setTitle(R.string.action_resize);
		
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(inflater.inflate(R.layout.dialog_resize, null))
		// Add action buttons
		        .setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener()
		        {
			        public void onClick(DialogInterface dialog, int id)
			        {
				        EditText coledit = (EditText) ((AlertDialog) dialog).findViewById(R.id.resize_col_box);
				        EditText rowedit = (EditText) ((AlertDialog) dialog).findViewById(R.id.resize_row_box);
				        
				        try
				        {
					        setRow(Integer.parseInt(rowedit.getText().toString()));
				        }
				        catch (NumberFormatException e)
				        {
					        
				        }
				        try
				        {
					        setCol(Integer.parseInt(coledit.getText().toString()));
				        }
				        catch (NumberFormatException e)
				        {
					        
				        }
				        
				        try
				        {
					        savelayout(editorFile);
				        }
				        catch (IOException e)
				        { // TODO Auto-generated catch block
					        e.printStackTrace();
				        }
				        
			        }
		        }).setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener()
		        {
			        public void onClick(DialogInterface dialog, int id)
			        {
				        // Do nothing on cancel
			        }
		        });
		final AlertDialog dialog = builder.create();
		
		dialogShow(dialog);
		
		EditText rowEdit = (EditText) ((AlertDialog) dialog).findViewById(R.id.resize_row_box);
		EditText colEdit = (EditText) ((AlertDialog) dialog).findViewById(R.id.resize_col_box);
		
		rowEdit.setText("" + row);
		colEdit.setText("" + col);
	}
	
	private void dialogShow(final AlertDialog dialog)
	{
		dialog.setOnKeyListener(new Dialog.OnKeyListener()
		{
			
			@Override
			public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event)
			{
				if (keyCode == KeyEvent.KEYCODE_BACK)
				{
					dialog.dismiss();
					return true;
				}
				return false;
			}
			
		});
		
		dialog.show();
	}
	
	private void setRow(int newRow)
	{
		if (newRow == row)
		{
			return;
		}
		else if (newRow > row)
		{
			for (int i = row; i < newRow; i++)
			{
				for (int j = 0; j < col; j++)
				{
					GridLayout.LayoutParams lpBt = new GridLayout.LayoutParams(GridLayout.spec(i), GridLayout.spec(j));
					EditorButtonView bt = new EditorButtonView(this, null);
					bt.setText("");
					bt.setLayoutParams(lpBt);
					mainView.addView(bt);
					bt.row = i;
					bt.col = j;
				}
			}
		}
		else if (newRow < row)
		{
			for (int i = mainView.getChildCount() - 1; i >= 0; i--)
			{
				EditorButtonView viewtemp = (EditorButtonView) mainView.getChildAt(i);
				if (viewtemp.row >= newRow)
				{
					((android.support.v7.widget.GridLayout) viewtemp.getParent()).removeView(viewtemp);
				}
			}
		}
		row = newRow;
	}
	
	private void setCol(int newCol)
	{
		if (newCol == col)
		{
			return;
		}
		else if (newCol > col)
		{
			for (int i = 0; i < row; i++)
			{
				for (int j = col; j < newCol; j++)
				{
					GridLayout.LayoutParams lpBt = new GridLayout.LayoutParams(GridLayout.spec(i), GridLayout.spec(j));
					EditorButtonView bt = new EditorButtonView(this, null);
					bt.setText("");
					bt.setLayoutParams(lpBt);
					mainView.addView(bt);
					bt.row = i;
					bt.col = j;
				}
			}
		}
		else if (newCol < col)
		{
			for (int i = mainView.getChildCount() - 1; i >= 0; i--)
			{
				EditorButtonView viewtemp = (EditorButtonView) mainView.getChildAt(i);
				if (viewtemp.col >= newCol)
				{
					((android.support.v7.widget.GridLayout) viewtemp.getParent()).removeView(viewtemp);
				}
			}
		}
		col = newCol;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.layout_edit, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.action_resize:
				resize();
				return true;
			case R.id.action_save:
				if (name.matches("^[a-zA-Z0-9 ][a-zA-Z0-9 ]*$"))
				{
					saveToFile(name);
				}
				else
				{
					saveToFile("");
				}
				return true;
			case R.id.action_load:
				loadFromFile();
				return true;
			case R.id.action_delete_file:
				deleteFile();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void editbutton(final EditorButtonView button)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// Get the layout inflater
		LayoutInflater inflater = this.getLayoutInflater();
		
		builder.setCancelable(false);
		builder.setTitle(R.string.text_edit_button);
		
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(inflater.inflate(R.layout.dialog_edit_button, null))
		// Add action buttons
		        .setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener()
		        {
			        public void onClick(DialogInterface dialog, int id)
			        {
				        EditText textEdit = (EditText) ((AlertDialog) dialog).findViewById(R.id.edit_button_text_box);
				        EditText outputEdit = (EditText) ((AlertDialog) dialog).findViewById(R.id.edit_button_output_box);
				        
				        button.setText(textEdit.getText().toString());
				        button.output = outputEdit.getText().toString();
				        try
				        {
					        savelayout(editorFile);
				        }
				        catch (IOException e)
				        {
					        // TODO Auto-generated catch block
					        e.printStackTrace();
				        }
			        }
		        }).setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener()
		        {
			        public void onClick(DialogInterface dialog, int id)
			        {
				        // Do nothing on cancel
			        }
		        });
		final AlertDialog dialog = builder.create();
		
		dialogShow(dialog);
		
		EditText textEdit = (EditText) ((AlertDialog) dialog).findViewById(R.id.edit_button_text_box);
		EditText outputEdit = (EditText) ((AlertDialog) dialog).findViewById(R.id.edit_button_output_box);
		
		textEdit.setText(button.getText());
		outputEdit.setText(button.output);
	}
	
	private void savelayout(String FILENAME) throws IOException
	{
		String[][][] currentLayout = new String[row][col][2];
		
		for (int i = 0; i < mainView.getChildCount(); i++)
		{
			EditorButtonView viewtemp = (EditorButtonView) mainView.getChildAt(i);
			currentLayout[viewtemp.row][viewtemp.col][0] = viewtemp.getText().toString();
			currentLayout[viewtemp.row][viewtemp.col][1] = viewtemp.output;
		}
		
		FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
		
		byte[] nameTemp = name.getBytes("UTF-8");
		int nameLength = nameTemp.length;
		if (nameLength > 250)
		{
			nameLength = 250;
		}
		fos.write(nameLength);
		fos.write(nameTemp, 0, nameLength);
		
		fos.write(row);
		fos.write(col);
		
		for (int i = 0; i < row; i++)
		{
			for (int j = 0; j < col; j++)
			{
				byte[] textString = currentLayout[i][j][0].getBytes("UTF-8");
				byte[] outputString = currentLayout[i][j][1].getBytes("UTF-8");
				int textLength = textString.length;
				int outputLength = outputString.length;
				if (textLength > 250)
				{
					textLength = 250;
				}
				if (outputLength > 250)
				{
					outputLength = 250;
				}
				
				fos.write(textLength);
				fos.write(textString, 0, textLength);
				fos.write(outputLength);
				fos.write(outputString, 0, outputLength);
			}
		}
		fos.close();
	}
	
	public void editName(View view)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// Get the layout inflater
		LayoutInflater inflater = this.getLayoutInflater();
		
		builder.setCancelable(false);
		builder.setTitle(R.string.text_edit_layout_name);
		
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(inflater.inflate(R.layout.dialog_edit_layout_name, null))
		// Add action buttons
		        .setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener()
		        {
			        public void onClick(DialogInterface dialog, int id)
			        {
				        EditText nameEdit = (EditText) ((AlertDialog) dialog).findViewById(R.id.edit_layout_name_box);
				        setName(nameEdit.getText().toString());
			        }
		        }).setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener()
		        {
			        public void onClick(DialogInterface dialog, int id)
			        {
				        // Do nothing on cancel
			        }
		        });
		final AlertDialog dialog = builder.create();
		
		dialogShow(dialog);
		EditText nameEdit = (EditText) ((AlertDialog) dialog).findViewById(R.id.edit_layout_name_box);
		
		nameEdit.setText(name);
	}
	
	private void setName(String newName)
	{
		name = newName;
		Locale l = Locale.getDefault();
		((Button) this.findViewById(R.id.editor_title_strip)).setText(newName.toUpperCase(l));
		try
		{
			savelayout(editorFile);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void saveToFile(String filename)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// Get the layout inflater
		LayoutInflater inflater = this.getLayoutInflater();
		
		builder.setCancelable(false);
		builder.setTitle(R.string.text_save_layout);
		
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(inflater.inflate(R.layout.dialog_save_layout, null))
		// Add action buttons
		        .setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener()
		        {
			        public void onClick(DialogInterface dialog, int id)
			        {
				        EditText saveEdit = (EditText) ((AlertDialog) dialog).findViewById(R.id.save_layout_box);
				        String filename = saveEdit.getText().toString();
				        try
				        {
					        Integer.parseInt(filename);
				        }
				        catch (NumberFormatException e)
				        {
					        try
					        {
						        savelayout(filename);
						        return;
					        }
					        catch (IOException e1)
					        {
						        // TODO Auto-generated catch block
						        e1.printStackTrace();
					        }
				        }
				        saveToFile(filename);
				        warning(R.string.warning_only_int_title, R.string.warning_only_int_message);
			        }
		        }).setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener()
		        {
			        public void onClick(DialogInterface dialog, int id)
			        {
				        // Do nothing on cancel
			        }
		        });
		final AlertDialog dialog = builder.create();
		
		dialogShow(dialog);
		
		EditText saveEdit = (EditText) ((AlertDialog) dialog).findViewById(R.id.save_layout_box);
		saveEdit.setText(filename);
	}
	
	protected void warning(int titleString, int messageString)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle(titleString);
		CharSequence message = getResources().getText(messageString);
		builder.setMessage(message);
		builder.setPositiveButton(R.string.text_ok, new OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				
			}
		});
		final AlertDialog dialog = builder.create();
		
		dialogShow(dialog);
	}
	
	private void loadFromFile()
	{
		String[] filestemp = fileList();
		List<String> list = new ArrayList<String>(Arrays.asList(filestemp));
		list.remove(editorFile);
		final String[] files = list.toArray(EMPTY_STRING_ARRAY);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle(R.string.text_load_layout);
		
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setItems(files, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				try
				{
					loadLayout(files[which]);
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				// Do nothing on cancel
			}
		});
		final AlertDialog dialog = builder.create();
		
		dialogShow(dialog);
	}
	
	private void deleteFile()
	{
		String[] filestemp = fileList();
		
		List<String> list = new ArrayList<String>(Arrays.asList(filestemp));
		list.remove(editorFile);
		
		final String[] files = list.toArray(EMPTY_STRING_ARRAY);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle(R.string.text_delete_layout);
		
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setItems(files, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				deleteSure(files[which]);
			}
		}).setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				// Do nothing on cancel
			}
		});
		final AlertDialog dialog = builder.create();
		
		dialogShow(dialog);
	}
	
	private void deleteSure(final String file)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle(R.string.text_delete_layout_sure);
		CharSequence message = getResources().getText(R.string.text_delete_layout_sure_text);
		builder.setMessage(message + " " + file);
		builder.setPositiveButton(R.string.text_yes, new OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				deleteFile(file);
			}
		});
		builder.setNegativeButton(R.string.text_no, new OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				
			}
		});
		final AlertDialog dialog = builder.create();
		
		dialogShow(dialog);
	}
	
	private void checkFullscreen()
	{
		if (MainActivity.preferences.getBoolean("fullscreen", false))
		{
			this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
