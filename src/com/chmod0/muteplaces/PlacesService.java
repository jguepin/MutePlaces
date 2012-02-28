package com.chmod0.muteplaces;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;

public class PlacesService extends Service {

	private ArrayList<Place> places;
	private static final String saveFileName = "places_backup";
	public static Service singleton;
	private LocationManager locationManager;
	private ArrayList<PendingIntent> pendingIntentList = new ArrayList<PendingIntent>();

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// load places from the internal storage
		places = readPlaces();

		locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

		// add proximity alerts for each saved place
		if(places.size() > 0){
			for(Place p : places){
				addPlaceAlert(p);
			}
		}
		singleton = this;
	}

	public void addPlaceAlert(Place p){
		Intent intent = new Intent(this, PlaceIntentReceiver.class);
		intent.putExtra("ringerMode", p.getRingerMode());
		intent.putExtra("muteMultimedia", p.isMuteMultimedia());
		intent.setAction("foo" + p.getLatitude() + p.getLongitude());
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		pendingIntentList.add(pi);
		locationManager.addProximityAlert(p.getLatitude(), p.getLongitude(), p.getRadius(), -1, pi);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		for(PendingIntent pi : pendingIntentList){
			locationManager.removeProximityAlert(pi);
		}
		singleton = null;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Place> readPlaces(){
		ArrayList<Place> placesInFile = new ArrayList<Place>();
		try {
			ObjectInputStream ois = new ObjectInputStream(openFileInput(saveFileName));
			ArrayList<Place> ap = (ArrayList<Place>)ois.readObject();
			if(ap.size() > 0){
				for(Place p : ap){
					placesInFile.add(p);
				}
			}
			ois.close();

		} catch (FileNotFoundException e) {
			// This should not come here
			e.printStackTrace();
		} catch (IOException e) {
			// This should not come here
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// This should not come here
			e.printStackTrace();
		}
		return placesInFile;
	}
}
