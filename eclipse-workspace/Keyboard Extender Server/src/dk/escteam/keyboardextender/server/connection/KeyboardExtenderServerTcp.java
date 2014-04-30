package dk.escteam.keyboardextender.server.connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import dk.escteam.keyboardextender.protocol.tcp.KeyboardExtenderConnectionTcp;
import dk.escteam.keyboardextender.server.KeyboardExtenderServerApp;

public class KeyboardExtenderServerTcp extends KeyboardExtenderServer implements Runnable
{
	private ServerSocket serverSocket;
	
	public KeyboardExtenderServerTcp(KeyboardExtenderServerApp application) throws IOException
	{
		super(application);
		
		int port = this.application.getPreferences().getInt("port", KeyboardExtenderConnectionTcp.DEFAULT_PORT);
		this.serverSocket = new ServerSocket(port);
		
		(new Thread(this)).start();
	}
	
	public void run()
	{
		try
		{
			while (true)
			{
				Socket socket = this.serverSocket.accept();
				KeyboardExtenderConnectionTcp connection = new KeyboardExtenderConnectionTcp(socket);
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
			this.serverSocket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
}
