package dk.escteam.keyboardextender.protocol.bluetooth;

import java.io.IOException;

import javax.microedition.io.StreamConnection;

import dk.escteam.keyboardextender.protocol.KeyboardExtenderConnection;

public class KeyboardExtenderConnectionBluetooth extends KeyboardExtenderConnection
{
	private StreamConnection streamConnection;
	
	public KeyboardExtenderConnectionBluetooth(StreamConnection streamConnection) throws IOException
	{
		super(streamConnection.openInputStream(), streamConnection.openOutputStream());
		
		this.streamConnection = streamConnection;
	}
	
	public void close() throws IOException
	{
		this.streamConnection.close();
		super.close();
	}
}
