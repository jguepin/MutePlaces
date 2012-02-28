package com.chmod0.muteplaces;

import java.util.ArrayList;
import com.chmod0.muteplaces.R;
import android.app.Activity;
import android.media.AudioManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PlacesAdapter extends ArrayAdapter<Place> {
	
	Activity context;
	ArrayList<Place> items;
	
	PlacesAdapter(Activity context, ArrayList<Place> items){
		super(context, R.layout.row, items);
		this.context = context;
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View row = inflater.inflate(R.layout.row, null);
		TextView label = (TextView)row.findViewById(R.id.placeLabel);
		label.setText(items.get(position).toString());
		
		ImageView icon = (ImageView)row.findViewById(R.id.modePlaceIcon);
		switch(items.get(position).getRingerMode()){
		case AudioManager.RINGER_MODE_NORMAL:
			icon.setImageResource(R.drawable.ic_normal_mode);
			break;
			
		case AudioManager.RINGER_MODE_SILENT:
			icon.setImageResource(R.drawable.ic_silent_mode);
			break;
		
		case AudioManager.RINGER_MODE_VIBRATE:
			icon.setImageResource(R.drawable.ic_vibrate_mode);
			break;
		}
		
		if(items.get(position).isMuteMultimedia()){
			ImageView iconMult = (ImageView)row.findViewById(R.id.multimediaPlaceIcon);
			iconMult.setImageResource(R.drawable.ic_multimedia_mute);
		}

		return row;
	}
	

}
