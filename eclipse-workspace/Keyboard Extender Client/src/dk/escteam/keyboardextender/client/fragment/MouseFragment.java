package dk.escteam.keyboardextender.client.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

import dk.escteam.keyboardextender.client.R;
import dk.escteam.keyboardextender.client.view.ControlView;
import dk.escteam.keyboardextender.protocol.KeyboardExtenderActionReceiver;
import dk.escteam.keyboardextender.protocol.action.KeyboardExtenderAction;
import dk.escteam.keyboardextender.protocol.action.ScreenCaptureResponseAction;

public class MouseFragment extends SherlockFragment implements KeyboardExtenderActionReceiver
{
	private ControlView controlView;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// this.application = (KeyboardExtender) this.getApplication();
		
		View rootView = inflater.inflate(R.layout.control, container, false);
		
		return rootView;
	}
	
	public void receiveAction(KeyboardExtenderAction action)
	{
		if (action instanceof ScreenCaptureResponseAction)
		{
			this.controlView.receiveAction((ScreenCaptureResponseAction) action);
		}
	}
}
