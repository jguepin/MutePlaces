package com.chmod0.muteplaces;

import java.io.Serializable;
import android.media.AudioManager;

public class Place implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String name;
	private double latitude;
	private double longitude;
	private int ringerMode;
	private int radius;
	private boolean muteMultimedia;
	
	public Place(double latitude, double longitude){
		this.latitude = latitude;
		this.longitude = longitude;
		this.ringerMode = AudioManager.RINGER_MODE_VIBRATE; 
		this.radius = 50;
		this.muteMultimedia = false;
	}
	
	public Place(String name, double latitude, double longitude) {
		super();
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.ringerMode = AudioManager.RINGER_MODE_VIBRATE; 
		this.radius = 50;
		this.muteMultimedia = false;
	}
	
	/**
	 * @return the ringerMode
	 */
	public int getRingerMode() {
		return ringerMode;
	}

	/**
	 * @param ringerMode the ringerMode to set
	 */
	public void setRingerMode(int ringerMode) {
		this.ringerMode = ringerMode;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}
	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}
	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the radius
	 */
	public int getRadius() {
		return radius;
	}

	/**
	 * @param radius the radius to set
	 */
	public void setRadius(int radius) {
		this.radius = radius;
	}

	public String toString(){
		return this.getName();
	}

	/**
	 * @return the muteMultimedia
	 */
	public boolean isMuteMultimedia() {
		return muteMultimedia;
	}

	/**
	 * @param muteMultimedia the muteMultimedia to set
	 */
	public void setMuteMultimedia(boolean muteMultimedia) {
		this.muteMultimedia = muteMultimedia;
	}
}
