package com.chmod0.muteplaces;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.widget.Toast;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class PlacesOverlay extends ItemizedOverlay<OverlayItem> {

	private Drawable marker=null;
	private List<OverlayItem> items = new ArrayList<OverlayItem>();
	private Context ctx=null;
	protected final Paint placeCirclePaint = new Paint();
	private Place place;
	private GeoPoint point;
	
	public PlacesOverlay(Drawable defaultMarker) {
		super(defaultMarker);
	}
	
	public PlacesOverlay(Drawable marker, Context ctx, Place place){
		super(marker);
		this.marker = marker;
		this.ctx = ctx;
		point = new GeoPoint((int)(place.getLatitude() * 1E6), (int)(place.getLongitude() * 1E6));
		this.place = place;
		items.add(new OverlayItem(point, place.getName(), "Your place"));
		this.placeCirclePaint.setARGB(0, 255, 100, 100);
		this.placeCirclePaint.setAntiAlias(true);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return items.get(i);
	}

	@Override
	public int size() {
		return items.size();
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, false);
		boundCenter(marker);
		
		final Projection pj = mapView.getProjection();
		Point screenCoords = new Point();
		pj.toPixels(point, screenCoords);
		final float radius = pj.metersToEquatorPixels(place.getRadius());
		
		this.placeCirclePaint.setAlpha(50);
		this.placeCirclePaint.setStyle(Style.FILL);
        canvas.drawCircle(screenCoords.x, screenCoords.y, radius, this.placeCirclePaint);
		
		this.placeCirclePaint.setAlpha(255);
		this.placeCirclePaint.setStyle(Style.STROKE);
		canvas.drawCircle(screenCoords.x, screenCoords.y, radius, this.placeCirclePaint);
	}

	@Override
	protected boolean onTap(int index) {
		Toast.makeText(ctx, items.get(index).getTitle(), Toast.LENGTH_LONG).show();
		return super.onTap(index);
	}
	
	

}
