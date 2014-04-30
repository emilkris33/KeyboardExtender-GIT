package dk.escteam.keyboardextender.client.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import dk.escteam.keyboardextender.client.R;
import dk.escteam.keyboardextender.protocol.KeyboardExtenderActionReceiver;
import dk.escteam.keyboardextender.protocol.action.KeyboardExtenderAction;

public class ChemFragment extends LoadoutFragment implements KeyboardExtenderActionReceiver
{
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		final View rootView = inflater.inflate(R.layout.chem, container, false);
		
		// this.application = (KeyboardExtender) this.getApplication();
		
		mainView = (android.support.v7.widget.GridLayout) rootView.findViewById(R.id.chem_grid);
		ViewTreeObserver vto = mainView.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener()
		{
			@Override
			public void onGlobalLayout()
			{
				
				android.support.v7.widget.GridLayout gl = (android.support.v7.widget.GridLayout) rootView.findViewById(R.id.chem_grid);
				
				ViewTreeObserver obs = gl.getViewTreeObserver();
				obs.removeGlobalOnLayoutListener(this);
			}
		});
		
		return rootView;
	}
	
	public void receiveAction(KeyboardExtenderAction action)
	{
		
	}
}
