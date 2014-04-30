package dk.escteam.keyboardextender.client.connection;

import java.io.IOException;


import dk.escteam.keyboardextender.client.activity.connection.ConnectionWifiEditActivity;
import dk.escteam.keyboardextender.client.app.KeyboardExtender;
import dk.escteam.keyboardextender.protocol.KeyboardExtenderConnection;
import dk.escteam.keyboardextender.protocol.tcp.KeyboardExtenderConnectionTcp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ConnectionWifi extends Connection
{
	private String host;
	private int port;
	
	public ConnectionWifi()
	{
		super();
		
		this.host = "";
		this.port = KeyboardExtenderConnectionTcp.DEFAULT_PORT;
	}
	
	public static ConnectionWifi load(SharedPreferences preferences, int position)
	{
		ConnectionWifi connection = new ConnectionWifi();
		
		connection.host = preferences.getString("connection_" + position + "_host", null);
		
		connection.port = preferences.getInt("connection_" + position + "_port", 0);
		
		return connection;
	}
	
	public void save(Editor editor, int position)
	{
		super.save(editor, position);
		
		editor.putInt("connection_" + position + "_type", WIFI);
		
		editor.putString("connection_" + position + "_host", this.host);
		
		editor.putInt("connection_" + position + "_port", this.port);
	}
	
	public void edit(Context context)
	{
		Intent intent = new Intent(context, ConnectionWifiEditActivity.class);
		this.edit(context, intent);
	}
	
	public KeyboardExtenderConnection connect(KeyboardExtender application) throws IOException
	{
		return KeyboardExtenderConnectionTcp.create(this.host, this.port);
	}
	
	public String getHost()
	{
		return host;
	}
	
	public void setHost(String host)
	{
		this.host = host;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public void setPort(int port)
	{
		this.port = port;
	}
}
