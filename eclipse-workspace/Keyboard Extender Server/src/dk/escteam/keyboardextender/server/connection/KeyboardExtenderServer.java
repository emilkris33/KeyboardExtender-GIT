package dk.escteam.keyboardextender.server.connection;

import dk.escteam.keyboardextender.server.KeyboardExtenderServerApp;

public abstract class KeyboardExtenderServer
{
	protected KeyboardExtenderServerApp application;
	
	public KeyboardExtenderServer(KeyboardExtenderServerApp application)
	{
		this.application = application;
	}
	
	public abstract void close();
}
