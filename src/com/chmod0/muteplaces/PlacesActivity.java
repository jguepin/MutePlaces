package com.chmod0.muteplaces;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import com.chmod0.muteplaces.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class PlacesActivity extends Activity implements OnClickListener {

	// places list
	private ArrayList<Place> places;
	private LocationManager locationManager;
	// Views builder from xml layouts
	public static LayoutInflater factory;
	private ArrayAdapter<Place> placesAdapter;
	// Context menus ids
	public static final int MODIFY_ID = Menu.FIRST + 1;
	public static final int DELETE_ID = Menu.FIRST + 2;
	public static final int MAPS_ID = Menu.FIRST + 3;

	public final static String saveFileName = "places_backup";
	private static String activityTitle;
	private static Button addBtn = null;
	private static Location userCurrentLocation = null;
	// this activity
	private static Activity activity;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.main);

		// start the Mute Places service if it is not
		if(PlacesService.singleton == null){
			startService(new Intent(this, PlacesService.class));
		}
		// List of all locations;
		places = new ArrayList<Place>();
		placesAdapter = new PlacesAdapter(this, places);

		// Restore the places saved in the file
		restorePlaces();

		// Get the places list view
		ListView placesView = (ListView)findViewById(R.id.places);
		placesView.setAdapter(placesAdapter);
		// Add context menus to the list
		registerForContextMenu(placesView);

		// Add a listener on the "add location" button
		addBtn = (Button)findViewById(R.id.add);
		addBtn.setOnClickListener(this);

		factory = getLayoutInflater(); 

		// Manage location events
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		getUserLocation();

		activity = this;
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.add){
			addMyLocation();
		}
	}

	/**
	 * @return the placesAdapter
	 */
	public ArrayAdapter<Place> getPlacesAdapter() {
		return placesAdapter;
	}

	/**
	 * @param placesAdapter the placesAdapter to set
	 */
	public void setPlacesAdapter(ArrayAdapter<Place> placesAdapter) {
		this.placesAdapter = placesAdapter;
	}

	public void addMyLocation(){
		// If user location is unknown, get it !
		if(userCurrentLocation == null){
			Toast.makeText(this, getResources().getString(R.string.loading_location), Toast.LENGTH_LONG);
			getUserLocation();
		}
		Place place = new Place(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude());
		PlaceConfigPopup.getPlaceConfig(this, this, factory.inflate(R.layout.placeconfig_popup, null, false), place, false);
		places.add(place);
		placesAdapter.notifyDataSetChanged();
	}

	public void getUserLocation(){
		addBtn.setEnabled(false);
		if(! locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			createLocationDisabledAlert();
		}
		this.setProgressBarIndeterminateVisibility(true);
		activityTitle = (String) this.getTitle();
		this.setTitle(getResources().getString(R.string.loading_location));
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener(){

			@Override
			public void onLocationChanged(Location location) {
				userCurrentLocation = location;
				locationManager.removeUpdates(this);
				addBtn.setEnabled(true);
				activity.setProgressBarIndeterminateVisibility(false);
				activity.setTitle(activityTitle);
			}

			@Override
			public void onProviderDisabled(String provider) {
				// Unused
			}

			@Override
			public void onProviderEnabled(String provider) {
				// Unused
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// Unused
			}
		});
	}

	public void createLocationDisabledAlert() {
		AlertDialog builder = new AlertDialog.Builder(this).create();
		builder.setMessage(getResources().getString(R.string.location_disabled_dialog));

		builder.setButton(getResources().getString(R.string.enable_location), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				showLocationOptions();
			}

		});
		builder.setButton2(getResources().getString(R.string.no_thanks), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				return;
			}
		});

		builder.show();
	}

	private void showLocationOptions() {
		Intent locationOptionsIntent = new Intent(
				android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(locationOptionsIntent);

	}

	// Create the context menus for the places list
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add(Menu.NONE, MODIFY_ID, Menu.NONE, R.string.context_modify);
		menu.add(Menu.NONE, DELETE_ID, Menu.NONE, R.string.context_delete);
		menu.add(Menu.NONE, MAPS_ID, Menu.NONE, R.string.context_maps);
	}

	// Handle the selected item in a context menu
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int itemId = (int)info.id;
		switch(item.getItemId()){
		case MODIFY_ID:
			Place p = places.get(itemId);
			PlaceConfigPopup.getPlaceConfig(this, this, factory.inflate(R.layout.placeconfig_popup, null, false), p, true);
			placesAdapter.notifyDataSetChanged();
			return true;

		case DELETE_ID:
			places.remove(itemId);
			placesAdapter.notifyDataSetChanged();
			restartService();
			return true;

		case MAPS_ID:
			savePlaces();
			Intent intent = new Intent(this, PlacesMap.class);
			intent.putExtra("placeId", itemId);
			startActivity(intent);
			return true;

		default:		
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// parse the "options" layout to build the options menu
		new MenuInflater(getApplication()).inflate(R.menu.options, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		// about option
		case R.id.aboutMenu:
			// display about menu : build a new dialog from a layout file
			AlertDialog aboutDialog = new AlertDialog.Builder(this).create();
			aboutDialog.setTitle(getResources().getString(R.string.about));
			aboutDialog.setIcon(R.drawable.icon);
			aboutDialog.setView(factory.inflate(R.layout.about_popup, null, false));
			aboutDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					return;
				}
			});
			aboutDialog.show();
			break;

			// quit option
		case R.id.quitMenu:
			// quit the app
			stopService(new Intent(this, PlacesService.class));
			this.finish();

			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void savePlaces(){
		try {
			ObjectOutputStream oos = new ObjectOutputStream(openFileOutput(saveFileName, Context.MODE_PRIVATE));
			oos.writeObject(places);
			oos.close();
		} catch (FileNotFoundException e) {
			// This should not come here
			e.printStackTrace();
		} catch (IOException e) {
			// This should not come here
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void restorePlaces(){
		try {
			ObjectInputStream ois = new ObjectInputStream(openFileInput(saveFileName));
			ArrayList<Place> ap = (ArrayList<Place>)ois.readObject();
			if(ap.size() > 0){
				for(Place p : ap){
					places.add(p);
				}
				placesAdapter.notifyDataSetChanged();
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
	}

	// Save places and restart the service to take care of new places
	public void restartService(){
		savePlaces();
		stopService(new Intent(this, PlacesService.class));
		startService(new Intent(this, PlacesService.class));
	}

	// Backup the places list before pausing the application
	@Override
	protected void onPause() {
		savePlaces();
		super.onPause();
	}

}