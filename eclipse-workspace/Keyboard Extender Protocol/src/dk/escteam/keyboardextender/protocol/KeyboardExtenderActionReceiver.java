package dk.escteam.keyboardextender.protocol;

import dk.escteam.keyboardextender.protocol.action.KeyboardExtenderAction;

public interface KeyboardExtenderActionReceiver
{
	public void receiveAction(KeyboardExtenderAction action);
}
