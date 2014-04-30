package dk.escteam.keyboardextender.client.activity.connection;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;

import dk.escteam.keyboardextender.client.R;
import dk.escteam.keyboardextender.client.activity.MainActivity;
import dk.escteam.keyboardextender.client.connection.Connection;

public class ConnectionEditActivity extends SherlockActivity implements OnClickListener
{
	public static Connection connectionParam;
	
	private Connection connection;
	
	private Button save;
	
	private EditText name;
	private EditText password;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		this.connection = connectionParam;
		
		this.save = (Button) this.findViewById(R.id.save);
		this.save.setOnClickListener(this);
		
		this.name = (EditText) this.findViewById(R.id.name);
		this.password = (EditText) this.findViewById(R.id.password);
		
		MainActivity.checkFullscreen(this);
	}
	
	protected void onResume()
	{
		super.onResume();
		
		this.name.setText(this.connection.getName());
		this.password.setText(this.connection.getPassword());
	}
	
	protected void onPause()
	{
		super.onPause();
		
		this.connection.setName(this.name.getText().toString());
		this.connection.setPassword(this.password.getText().toString());
	}
	
	public void onClick(View v)
	{
		if (v == this.save)
		{
			this.finish();
		}
	}
}
