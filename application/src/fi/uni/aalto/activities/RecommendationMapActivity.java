package fi.uni.aalto.activities;



import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

import fi.uni.aalto.activities.R;
import fi.uni.aalto.controllers.HTTPHelper;
import fi.uni.aalto.controllers.MyOverlays;
import fi.uni.aalto.controllers.XMLHelper;

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
import android.widget.Toast;

/**
 * Inspired by http://www.vogella.com/articles/AndroidLocationAPI/article.html
 * @author jens
 *
 */
public class RecommendationMapActivity extends MapActivity implements LocationListener{


	/* socialgags>
place> 0..*
   location>name</location>  1..1
   user> username</user>1..*
/place
/socialgags*/

	private String provider = "";
	private Location location = null;
	private int latitude = 0;
	private int longitude = 0;


	private MapController mapController;
	private MapView mapView;
	private LocationManager locationManager;
	private MyOverlays itemizedoverlay;
	private MyLocationOverlay myLocationOverlay;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.recommendations_map); // bind the layout to the activity

		// Configure the Map
		mapView = (MapView) findViewById(R.id.mapView);

		mapView.setBuiltInZoomControls(true);

		mapController = mapView.getController();
		// show the hole world
		mapController.setZoom(2); 

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

		Drawable drawable = this.getResources().getDrawable(R.drawable.point);
		int x = 5;
		int y = 5;
		int width = 5;
		int height = 5;
		drawable. setBounds(x, y, x + width, y + height);
		itemizedoverlay = new MyOverlays(this, drawable);

		getFriendsAndAddMarker();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public void getFriendsAndAddMarker()
	{
		try {
			String xmlFile = HTTPHelper.getGetData(LocalRecommendationsActivity.first_name, LocalRecommendationsActivity.last_name, 
					LocalRecommendationsActivity.expires, LocalRecommendationsActivity.USER_ID,
					LocalRecommendationsActivity.access_token, LocalRecommendationsActivity.BASE_ADDRESS + "/location");

			if("".equals(xmlFile))
			{
				System.err.println("Some server Error");
				return;
			}
			

			// create a new factory, which defines the API for the DOM-Parser
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			// Defines API for getting XML-Document
			DocumentBuilder builder = factory.newDocumentBuilder();

			// Parse the actual XML-Document
			Document doc = null;
			try{
				doc = builder.parse(new InputSource(new StringReader(xmlFile)));
				doc.getDocumentElement().normalize();
			}
			catch(FileNotFoundException e){
				System.err.println(e.getStackTrace());
			} catch (SAXException e) {
				e.printStackTrace();
			}


			if(doc != null)
			{
				NodeList wall  = doc.getElementsByTagName("place");

				for(int i = 0; i < wall.getLength(); i++)
				{
					Node wallEl = wall.item(i);

					// get information about the location
					Node userData = null;

					// check if we have get an error file
					/*
					 * <socialgags>
						<place> 0..*
   								<location>name</location>  1..1
   								<user> username</user>1..*
						<place>
					   </socialgags>
					 */

					userData = XMLHelper.getUserData(wallEl, "error");
					if(userData != null)
					{
						Toast.makeText(RecommendationMapActivity.this,"There are no friends which also use this app.", Toast.LENGTH_SHORT).show();
						return;
					}


					userData = XMLHelper.getUserData(wallEl, "location");
					
					String location = userData.getTextContent();
					
					ArrayList<String> friends = new ArrayList<String>();
					ArrayList<Node> users = XMLHelper.getUserDatas(wallEl,"friend");
					for (Node node : users) 
					{
						friends.add(node.getTextContent());
						System.err.println("F: " + node.getTextContent());
					}

					addMarker(location, friends);
				}
			}


		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}



	private void addMarker(String city, ArrayList<String> friends) 
	{
		/* find the addresses  by using getFromLocationName() method with the given address*/

		GeoPoint geoPoint = null;
		JSONObject locationInfo = XMLHelper.getLocationInfo(city);
		if(locationInfo != null)
		{
			geoPoint = XMLHelper.getLatLong(locationInfo);
		}
		if(geoPoint != null)
		{
			//System.err.println(geoPoint.getLatitudeE6() + "   " + geoPoint.getLongitudeE6());
			OverlayItem overlayitem = new OverlayItem(geoPoint, "Friends:", friends.toString());
			itemizedoverlay.addOverlay(overlayitem,friends);
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
		
		}
		return true;
	}

	public void onLocationChanged(Location arg0) 
	{
		// get newest location coordinates
		int latitude = (int) (arg0.getLatitude() * 1E6);
		int longtitude = (int) (arg0.getLongitude() * 1E6);
		GeoPoint point = new GeoPoint(latitude, longtitude);
		// take our own position as the point of focus
		mapController.setCenter(point); 
	}

	public void onProviderDisabled(String arg0) {

	}

	public void onProviderEnabled(String arg0) {

	}

	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

	}





}
