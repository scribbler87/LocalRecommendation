package fi.uni.aalto.activities;


import java.io.IOException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fi.uni.aalto.activities.R;
import fi.uni.aalto.controllers.XMLHelper;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class WeatherActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather);

		Button buttonConfWOEID = (Button) findViewById(R.id.buttonConfWOEID);
		buttonConfWOEID.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				try {
					getWoeidAndDisplay();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				}
			}


		});
	}

	
	private void getWoeidAndDisplay() throws IOException, ParserConfigurationException, SAXException 
	{
	
		
		// get the intent from the textfield with the woeid in it
		EditText editTextWoeid = (EditText) findViewById(R.id.editTextWOEID);
		String woeid = editTextWoeid.getText().toString();

		// create url for getting information about location from the according woeid
		String urlString = "http://weather.yahooapis.com/forecastrss?w=" + woeid + "&u=c";


		// create a new factory, which defines the API for the DOM-Parser
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// Defines API for getting XML-Document
		DocumentBuilder builder = factory.newDocumentBuilder();

		// Parse the actual XML-Document
		Document doc = builder.parse(urlString);
		doc.getDocumentElement().normalize();
		
		
	
		NodeList listOfWeathers  = doc.getElementsByTagName("channel");
		
		
		if(listOfWeathers.getLength() >= 1)
		{
			Node weather = listOfWeathers.item(0);
			
			// get information about the location
			Node userData = null;
	
		
			/* not necessary at the moment, because we get a hole string with the content */
			// getUserData(weather, "yweather:location");
	//		String city = getAttributeValue(userData,"city");
	//		String country = getAttributeValue(userData,"country");
			
			// get information about the units
			userData = XMLHelper.getUserData(weather, "yweather:units");
			String temperatureU = XMLHelper.getAttributeValue(userData,"temperature");
			String distanceU = XMLHelper.getAttributeValue(userData,"distance");
			String pressureU = XMLHelper.getAttributeValue(userData,"pressure");
			String speedU = XMLHelper.getAttributeValue(userData,"speed");
			
			// get information about the wind
			userData = XMLHelper.getUserData(weather, "yweather:wind");
			String chillW = XMLHelper.getAttributeValue(userData,"chill");
			String directionW = XMLHelper.getAttributeValue(userData,"direction");
			String speedW = XMLHelper.getAttributeValue(userData,"speed");
			
			// get information about the atmosphere
			userData = XMLHelper.getUserData(weather, "yweather:atmosphere");
			String humidity = XMLHelper.getAttributeValue(userData,"humidity");
			String visibility = XMLHelper.getAttributeValue(userData,"visibility");
			String pressure = XMLHelper.getAttributeValue(userData,"pressure");
			String rising = XMLHelper.getAttributeValue(userData,"rising");
			
			// get sunrise and sunset
			userData = XMLHelper.getUserData(weather, "yweather:astronomy");
			String sunrise = XMLHelper.getAttributeValue(userData,"sunrise");
			String sunset = XMLHelper.getAttributeValue(userData,"sunset");
			
			
			String weatherString = "";
			

			NodeList conditions  = doc.getElementsByTagName("item");
	
			// get the actual temperature
			if(conditions.getLength() >= 1)
			{
				Node conNode = conditions.item(0);
				userData = XMLHelper.getUserData(conNode, "title");
				String textContent = userData.getTextContent();
					
				
				userData = XMLHelper.getUserData(conNode, "yweather:condition");
				
				// we don't know what this means at the moment
			//	String code = getAttributeValue(userData,"code");
				String temp = XMLHelper.getAttributeValue(userData,"temp");
				String date = XMLHelper.getAttributeValue(userData,"date");
				
				weatherString+= textContent + " on the " + date + "\n";
				weatherString += "Temperature: " + temp + " " + temperatureU + "\n"; 
				
			}
			
			
			TextView findViewById = (TextView) findViewById(R.id.textviewWeather);
			
			
			
			weatherString += "Sunrise: " + sunrise + " \nSunset: " + sunset  + "\n";
			
			weatherString += "wind chill factor: " + chillW + " \nwind direction: " + directionW + " " + distanceU + "\nwind speed: " + speedW + " " + speedU + "\n";
			
			weatherString += "humidity: " + humidity + " \nvisibility: " + visibility + " \npressure: " + pressure + " " + pressureU + " \nrising: " + rising;
			
			
			findViewById.setText(weatherString);
		}	
			
		


	}



}
