package com.chmod0.muteplaces;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import com.chmod0.muteplaces.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class PlacesMap extends MapActivity { 

	Drawable marker;
	List<Overlay> mapOverlays;
	MapView map;
	ArrayList<Place> places = new ArrayList<Place>();
	Place place;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.map);
		
		// get the location to display 
		Bundle extras = getIntent().getExtras();
		int itemId = extras.getInt("itemId");
		
		// get places from the backup file
		restorePlaces();
		place = places.get(itemId);
		GeoPoint point = new GeoPoint((int)(place.getLatitude() * 1E6), (int)(place.getLongitude() * 1E6));
		
		map =  (MapView)findViewById(R.id.map);
		MapController mapController = map.getController();
		mapOverlays = map.getOverlays();

		map.setBuiltInZoomControls(true);

		mapController.setZoom(16);
		mapController.setCenter(point);
		
		// display a marker on the place location
		Drawable marker = getResources().getDrawable(R.drawable.marker);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
		PlacesOverlay plOverlay = new PlacesOverlay(marker, (Context)this, place);
		mapOverlays.add(plOverlay);
		
		// display user location
		MyLocationOverlay me = new MyLocationOverlay(this, map);
		me.enableMyLocation();
		mapOverlays.add(me);

	}
	
	@SuppressWarnings("unchecked")
	public void restorePlaces(){
		try {
			ObjectInputStream ois = new ObjectInputStream(openFileInput(PlacesActivity.saveFileName));
			places =  (ArrayList<Place>)ois.readObject();
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
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
