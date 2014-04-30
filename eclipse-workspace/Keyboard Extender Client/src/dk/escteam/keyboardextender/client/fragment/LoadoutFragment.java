package dk.escteam.keyboardextender.client.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

import dk.escteam.keyboardextender.client.view.ButtonView;
import dk.escteam.keyboardextender.client.view.CustomButtonView;
import dk.escteam.keyboardextender.client.view.ImgButtonView;

public class LoadoutFragment extends SherlockFragment
{
	android.support.v7.widget.GridLayout mainView;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	public void unsellect()
	{
		if (mainView != null)
		{
			for (int i = 0; i < mainView.getChildCount(); i++)
			{
				View viewtemp = mainView.getChildAt(i);
				if (viewtemp instanceof ButtonView)
				{
					ButtonView buttontemp = (ButtonView) viewtemp;
					buttontemp.setPressed(false);
				}
				else if (viewtemp instanceof ImgButtonView)
				{
					ImgButtonView buttontemp = (ImgButtonView) viewtemp;
					buttontemp.setPressed(false);
				}
				else if (viewtemp instanceof CustomButtonView)
				{
					CustomButtonView buttontemp = (CustomButtonView) viewtemp;
					buttontemp.setPressed(false);
				}
			}
		}
	}
}
