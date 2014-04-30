package dk.escteam.keyboardextender.server.connection;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import dk.escteam.keyboardextender.protocol.KeyboardExtenderConnection;
import dk.escteam.keyboardextender.protocol.bluetooth.KeyboardExtenderConnectionBluetooth;
import dk.escteam.keyboardextender.server.KeyboardExtenderServerApp;

public class KeyboardExtenderServerBluetooth extends KeyboardExtenderServer implements Runnable
{
	private StreamConnectionNotifier streamConnectionNotifier;
	
	public KeyboardExtenderServerBluetooth(KeyboardExtenderServerApp application) throws IOException
	{
		super(application);
		
		String uuid = KeyboardExtenderConnection.BLUETOOTH_UUID.replaceAll("-", "");
		this.streamConnectionNotifier = (StreamConnectionNotifier) Connector.open("btspp://localhost:" + uuid + ";name=PRemoteDroid");
		
		if (this.streamConnectionNotifier != null)
		{
			(new Thread(this)).start();
		}
		else
		{
			throw new IOException();
		}
	}
	
	public void run()
	{
		try
		{
			while (true)
			{
				StreamConnection streamConnection = streamConnectionNotifier.acceptAndOpen();
				KeyboardExtenderConnectionBluetooth connection = new KeyboardExtenderConnectionBluetooth(streamConnection);
				new KeyboardExtenderServerConnection(this.application, connection);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void close()
	{
		try
		{
			this.streamConnectionNotifier.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
