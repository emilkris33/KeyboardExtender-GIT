package dk.escteam.keyboardextender.client.activity.connection;

import android.os.Bundle;
import android.widget.EditText;
import dk.escteam.keyboardextender.client.R;
import dk.escteam.keyboardextender.client.connection.ConnectionWifi;

public class ConnectionWifiEditActivity extends ConnectionEditActivity
{
	private ConnectionWifi connection;
	
	private EditText host;
	private EditText port;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		this.setContentView(R.layout.connectionwifiedit);
		
		super.onCreate(savedInstanceState);
		
		this.connection = (ConnectionWifi) connectionParam;
		
		this.host = (EditText) this.findViewById(R.id.host);
		this.port = (EditText) this.findViewById(R.id.port);
	}
	
	protected void onResume()
	{
		super.onResume();
		
		this.host.setText(this.connection.getHost());
		this.port.setText(Integer.toString(this.connection.getPort()));
	}
	
	protected void onPause()
	{
		super.onPause();
		
		this.connection.setHost(this.host.getText().toString());
		this.connection.setPort(Integer.parseInt(this.port.getText().toString()));
	}
}
