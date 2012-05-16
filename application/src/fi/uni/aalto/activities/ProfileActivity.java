package fi.uni.aalto.activities;


import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends Activity{


	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);




		try {
			getProfile();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

	}




	/**
	 * Example of an XML-File which is coming from the server!
	 * 
			<socialgags> <!-- Extra things in head block -->
			    <userInfo>
			        <name>
						Rulo
			        </name>
			        <surname>
						García
			        </surname>
			        <username>
						YoYuUm
			        </username>
			        <gender>
						male
			        </gender>
			        <dateOfBirth>
						07/10/1989
			        </dateOfBirth>
			        <location>
						Espoo, Finland
			        </location>
			    </userInfo> <!-- End -->
			</socialgags>
	 * */
	private void getProfile() throws ParserConfigurationException, SAXException,
	IOException, URISyntaxException {
		// create url for getting information about location from the according woeid
//		String urlString = "http://group14.naf.cs.hut.fi/users/" + SocialGagActivity.USER_ID;
		String urlString = "http://127.0.0.1:8080/users/"+ SocialGagActivity.USER_ID;

		ImageView profilePic = (ImageView) findViewById(R.id.imageViewProfilePic);
		URL img_value =  new URL("http://graph.facebook.com/"+SocialGagActivity.USER_ID+"/picture?type=large");
		Bitmap userPicBitMap = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
		profilePic.setImageBitmap(userPicBitMap);



		// create a new factory, which defines the API for the DOM-Parser
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// Defines API for getting XML-Document
		DocumentBuilder builder = factory.newDocumentBuilder();

		// Parse the actual XML-Document
		Document doc = builder.parse(urlString);
		doc.getDocumentElement().normalize();



		NodeList wall  = doc.getElementsByTagName("userInfo");

		if(wall.getLength() >= 1)
		{
			Node wallEl = wall.item(0);

			// get information about the location
			Node userData = null;

			// check if we have get an error file
			/*
			 * <socialgags>
			 * 		<!-- Extra things in head block -->
			 * 		<error>
			 * 			The user bla is not on out system
	        		</error>
	        		<!-- End-->
	        	</socialgags>
			 */

			userData = XMLHelper.getUserData(wallEl, "error");
			if(userData != null)
			{
				Toast.makeText(ProfileActivity.this,"There is no profile at the moment. Please login.", Toast.LENGTH_SHORT).show();
				return;
			}


			userData = XMLHelper.getUserData(wallEl, "name");
			String name = userData.getTextContent();
			userData = XMLHelper.getUserData(wallEl, "surname");
			String secondName = userData.getTextContent();
			userData = XMLHelper.getUserData(wallEl, "username");
			String username = userData.getTextContent();
			userData = XMLHelper.getUserData(wallEl, "gender");
			String gender = userData.getTextContent();
			userData = XMLHelper.getUserData(wallEl, "dateOfBirth");
			String dateOfBirth = userData.getTextContent();
			userData = XMLHelper.getUserData(wallEl, "location");
			String location = userData.getTextContent();

			TextView textView = (TextView) findViewById(R.id.textViewFirstName);
			textView.setText(name);
			textView = (TextView) findViewById(R.id.textViewSecondName);
			textView.setText(secondName);
			textView = (TextView) findViewById(R.id.textViewGender);
			textView.setText(gender);
			textView = (TextView) findViewById(R.id.textViewBirthday);
			textView.setText(dateOfBirth);
			textView = (TextView) findViewById(R.id.textViewLocation);
			textView.setText(location);
			textView = (TextView) findViewById(R.id.textViewFirstName);
			textView.setText(name);
			textView = (TextView) findViewById(R.id.textViewID);
			textView.setText(username);
		}
		else
		{
			Toast.makeText(ProfileActivity.this,"There is no post at the moment. Please login.", Toast.LENGTH_SHORT).show();
		}

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
		case R.id.socialGags:   intent = new Intent(ProfileActivity.this, SocialGagActivity.class);
		startActivity(intent);
		break;
		case R.id.post:     intent = new Intent(ProfileActivity.this, CreatePostActivity.class);
		startActivity(intent);
		break;
		case R.id.profile:  intent = new Intent(ProfileActivity.this, ProfileActivity.class);
		startActivity(intent);
		break;
		case R.id.friends:  intent = new Intent(ProfileActivity.this, LocateFriendsActivity.class);
		startActivity(intent);
		break;
		}
		return true;
	}


}
