package com.chmod0.muteplaces;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;

public class PlaceIntentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		Bundle extras = arg1.getExtras();
		int ringerMode = extras.getInt("ringerMode");
		boolean muteMultimedia = extras.getBoolean("muteMultimedia");
		AudioManager audioManager = (AudioManager)arg0.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setRingerMode(ringerMode);
		if(muteMultimedia)
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
	}

}
