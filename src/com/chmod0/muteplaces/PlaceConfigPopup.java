package com.chmod0.muteplaces;

import com.chmod0.muteplaces.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class PlaceConfigPopup {
	private static EditText nameInput;
	private static EditText radiusInput;
	private static RadioGroup rg;
	private static CheckBox checkBox;

	public static AlertDialog getPlaceConfig(final PlacesActivity placesActivity, final Context ctx, final View view, final Place p, boolean modify){
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ctx);
		dialogBuilder.setTitle(ctx.getString(R.string.place_name_popup_title));

		dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				// Apply the place name entered to the place
				String name = nameInput.getText().toString();
				if(name.compareTo("") != 0){
					p.setName(name);
				} else {
					p.setName(ctx.getString(R.string.unnamed_place));
				}
				
				// Check which ringer mode is selected and apply it to the place
				switch(rg.getCheckedRadioButtonId()){
	
				case R.id.radio0:
					p.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
					break;
					
				case R.id.radio1:
					p.setRingerMode(AudioManager.RINGER_MODE_SILENT);
					break;
					
				case R.id.radio2:
					p.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
					break;
				}
				
				// Apply the radius entered to the place
				String radius = radiusInput.getText().toString();
				if(radius.compareTo("") != 0){
					p.setRadius(Integer.parseInt(radius));
				}
				
				// Check if checkbox is checked to mute multimedia
				p.setMuteMultimedia(checkBox.isChecked());
				
				placesActivity.getPlacesAdapter().notifyDataSetChanged();
				placesActivity.restartService();
				dialog.dismiss();
			}
		});

		// add the customized view to the dialog
		dialogBuilder.setView(view);

		// set the latitude and the location of the place
		TextView latView = (TextView)view.findViewById(R.id.lat);
		latView.setText(latView.getText() + " : " + p.getLatitude());
		TextView longView = (TextView)view.findViewById(R.id.longi);
		longView.setText(longView.getText() + " : " + p.getLongitude());
		
		// get the input fields
		nameInput = (EditText)view.findViewById(R.id.placename);
		radiusInput = (EditText)view.findViewById(R.id.radius);
		rg = (RadioGroup)view.findViewById(R.id.radioGroup1);
		checkBox = (CheckBox)view.findViewById(R.id.mute_multimedia_checkbox);
		
		if(modify){
			radiusInput.setText("" + p.getRadius());
			nameInput.setText(p.getName());
			switch(p.getRingerMode()){
			case AudioManager.RINGER_MODE_VIBRATE:
				rg.check(R.id.radio0);
				break;
			case AudioManager.RINGER_MODE_SILENT:
				rg.check(R.id.radio1);
				break;
			case AudioManager.RINGER_MODE_NORMAL:
				rg.check(R.id.radio2);
				break;
			}
			if(p.isMuteMultimedia()){
				checkBox.setChecked(true);
			}
		}
		
		AlertDialog dialog = dialogBuilder.create();
		// display the dialog
		dialog.show();
		
		return dialog;
	}
}
