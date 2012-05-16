package fi.uni.aalto.controllers;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.android.maps.GeoPoint;


public class XMLHelper {

	// returns the userdata from a node to the given nodeName--name of the tag
	public static Node getUserData(Node node, String nodeName)
	{
		Node result = null;
		NodeList nodeList = node.getChildNodes();
		Node actNode = null;
		// parse all childrens
		for(int i = 0; i < nodeList.getLength();i++)
		{
			// until we find the element we are looking for
			actNode = nodeList.item(i);
			if(nodeName.equals(actNode.getNodeName()))
			{
				result = actNode;
				break;
			}
		}

		return result;
	}

	public static ArrayList<Node> getUserDatas(Node node, String nodeName)
	{
		ArrayList<Node> result = new ArrayList<Node>();
		NodeList nodeList = node.getChildNodes();
		Node actNode = null;
		// parse all childrens
		for(int i = 0; i < nodeList.getLength();i++)
		{
			// until we find the element we are looking for
			actNode = nodeList.item(i);
			if(nodeName.equals(actNode.getNodeName()))
			{
				result.add(actNode);
				break;
			}
		}

		return result;
	}



	// returns the ID from a node
	// return -1 if there is node ID
	public static String getAttributeValue(Node node, String attName) {

		NamedNodeMap attributes = node.getAttributes();
		Node namedItem = attributes.getNamedItem(attName);


		if(namedItem != null)
			return namedItem.getNodeValue();
		else
		{
			System.err.println("There was no attribute with this " + node.getNodeName());
			return null;
		}
	}



	// used because of a know bug in the emulator see: http://code.google.com/p/android/issues/detail?id=8816
	// normally the following code is enough not nice  but it works:
	/*
	 * 
		 Geocoder geocoder = new Geocoder(getApplicationContext());
//					String cityA = "Finnland, Helsinki";
//					List<Address> fromLocationName = geocoder.getFromLocationName(cityA, 1);
//					
//					if(fromLocationName.size() > 0)
//					{
//						GeoPoint p = new GeoPoint((int)(fromLocationName.get(0).getLatitude()* 1E6), 
//								(int)(fromLocationName.get(0).getLongitude()* 1E6));//mapView.getMapCenter();
//
//						OverlayItem overlayitem = new OverlayItem(p, "Friends", "Gerd\nToboi");
//						itemizedoverlay.addOverlay(overlayitem);
//						if (itemizedoverlay.size() > 0) 
//						{
//							mapView.getOverlays().add(itemizedoverlay);
//						}
//					}
	 * @param jsonObject
	 * @return
	 */

	public static JSONObject getLocationInfo(String address) {
		StringBuilder stringBuilder = new StringBuilder();
		try {

			address = address.replaceAll(" ","%20");    

			HttpPost httppost = new HttpPost("http://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false");
			HttpClient client = new DefaultHttpClient();
			HttpResponse response;
			stringBuilder = new StringBuilder();


			response = client.execute(httppost);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1) {
				stringBuilder.append((char) b);
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject(stringBuilder.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

	public static GeoPoint  getLatLong(JSONObject jsonObject) {

		Double lon = new Double(0);
		Double lat = new Double(0);

		try {

			lon = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lng");

			lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lat");

		} catch (Exception e) {
			e.printStackTrace();

		}

		return new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
	}
}
