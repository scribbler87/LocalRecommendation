package fi.uni.aalto.activities;



import java.util.ArrayList;


import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

import fi.uni.aalto.activities.R;
import fi.uni.aalto.controllers.MyOverlays;
import fi.uni.aalto.controllers.Post;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Inspired by http://www.vogella.com/articles/AndroidLocationAPI/article.html
 * @author jens
 *
 */
public class RecommendationMapActivity extends MapActivity implements LocationListener{



	private MapController mapController;
	private MapView mapView;
	private LocationManager locationManager;
	private MyOverlays itemizedoverlay;
	public static GeoPoint actPosition;
	public static MyLocationOverlay myLocationOverlay;
	private static Drawable drawable;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.recommendations_map); // bind the layout to the activity
		
		int[] lastKnowPosition = LocalRecommendationsActivity.lastKnowPosition;
		actPosition = new GeoPoint(lastKnowPosition[0], lastKnowPosition[1]);
		
		// Configure the Map
		mapView = (MapView) findViewById(R.id.mapView);

		mapView.setBuiltInZoomControls(true);
		
		mapController = mapView.getController();
		// show the hole world
		mapController.setZoom(8); 

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, this);

		myLocationOverlay = new MyLocationOverlay(this, mapView);
		mapView.getOverlays().add(myLocationOverlay);

		myLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				mapController.setCenter(myLocationOverlay.getMyLocation());
			}
		});

		drawable = this.getResources().getDrawable(R.drawable.point);
		itemizedoverlay = new MyOverlays(this, drawable);

		getFriendsAndAddMarker();
		
		mapController.setCenter(actPosition);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public void getFriendsAndAddMarker()
	{
		ArrayList<Post> posts = LocalRecommendationsActivity.posts;
		for (Post post : posts) {
			int latitude = post.getLatitude();
			int longitude = post.getLongitude();
			
			String info = ("Restaurant: " + post.getTitle() + "\n");
			info += "Description: " + post.getDescription();
			
			addMarker(longitude , latitude, info);
		}
		
		
	}

	private void addMarker(int longtitude, int  latitude, String info) 
	{
		GeoPoint geoPoint = new GeoPoint(latitude, longtitude);
		
		if(geoPoint != null)
		{

			System.err.println(geoPoint.getLatitudeE6() + "   " + geoPoint.getLongitudeE6());
			OverlayItem overlayitem = new OverlayItem(geoPoint, "Friends:", info);
			itemizedoverlay.addOverlay(overlayitem,info);
			if (itemizedoverlay.size() > 0) {
				mapView.getOverlays().add(itemizedoverlay);
			}
		}


	}






	@Override
	protected void onResume() {
		super.onResume();
		myLocationOverlay.enableMyLocation();
		myLocationOverlay.enableCompass();
	}

	@Override
	protected void onPause() {
		super.onResume();
		myLocationOverlay.disableMyLocation();
		myLocationOverlay.disableCompass();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.post:     intent = new Intent(RecommendationMapActivity.this, CreateRecommendationActivity.class);
		startActivity(intent);
		break;
		case R.id.settingsA:
			intent = new Intent(RecommendationMapActivity.this, SettingsActivity.class);
			startActivity(intent);
			break;
		
		}
		return true;
	}

	public void onLocationChanged(Location arg0) 
	{
		// get newest location coordinates
		int latitude = (int) (arg0.getLatitude() * 1E6);
		int longtitude = (int) (arg0.getLongitude() * 1E6);
		actPosition = new GeoPoint(latitude, longtitude);
		// take our own position as the point of focus
		mapController.setCenter(actPosition); 
	}

	public void onProviderDisabled(String arg0) {

	}

	public void onProviderEnabled(String arg0) {

	}

	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

	}





}
