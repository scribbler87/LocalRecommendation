package fi.uni.aalto.activities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;
import com.google.android.maps.GeoPoint;

import fi.uni.aalto.activities.R;
import fi.uni.aalto.controllers.FBConnectionActivity;
import fi.uni.aalto.controllers.HTTPHelper;
import fi.uni.aalto.controllers.MathHelper;
import fi.uni.aalto.controllers.Post;
import fi.uni.aalto.controllers.XMLHelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


public class LocalRecommendationsActivity extends FBConnectionActivity {


	public static ArrayList<Post> posts; 
	private Post actPost = null;
	private int nextPost;
	private Facebook mFacebook;
	private AsyncFacebookRunner mAsyncRunner;
	private SharedPreferences sharedPrefs;
	public static String USER_ID;
	public static String last_name;
	public static String first_name;
	public static String access_token;
	public static long expires;
	public static final String APP_ID = "209858342455450";
	private ImageView actPic;
	public static int[] lastKnowPosition;
	public static final String BASE_ADDRESS = "http://xx.xx.xx.xx:PORT";//the same you are using for the server
	public static final String urlLogin = BASE_ADDRESS + "/mobilelogin";
	public static final String urlLocation = BASE_ADDRESS  + "/location";
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_recommendation);

		lastKnowPosition = getGPS();
		
		ImageButton shareButton= (ImageButton) findViewById(R.id.buttonShare);
		shareButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				postPicOnWall();
			}
		});

		setConnection();

		mFacebook = new Facebook(APP_ID);
		mAsyncRunner = new AsyncFacebookRunner(mFacebook);


		if(!this.isSession())
		{
			getID();
		}
		else
		{
			if(this.isSession())
			{

				extractUserData();
				postPicOnWall();

			}
			try {

				// send login data, to check if the user is in the db,
				// and if not, a new user is created with the fb data
				HTTPHelper.getPostData(first_name, last_name, expires,
						USER_ID, access_token,urlLogin);

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		nextPost = 0;


		actPic = (ImageView) findViewById(R.id.imageViewPost);



		Button buttonNextComment = (Button) findViewById(R.id.buttonNextComment);
		buttonNextComment.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					putNextImageToImageView();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		try {
			getPosts();
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


	private void postPicOnWall()
	{
		if(this.isSession())
		{
			Bundle parameters = new Bundle();

			if(actPost == null)
				return;
			parameters.putString("message", actPost.getTitle());
			parameters.putString("name", actPost.getTitle());
			parameters.putString("picture", actPost.getUrl());
			parameters.putString("description", actPost.getDescription());

			mFacebook.dialog(this, "stream.publish", parameters, new DialogListener() {

				public void onFacebookError(FacebookError e) {
				}

				public void onError(DialogError e) {
				}

				public void onComplete(Bundle values) {
				}

				public void onCancel() {
				}
			});
		}
	}


	private void getPosts() throws ParserConfigurationException, SAXException,
	IOException, URISyntaxException {
		//			 create url for getting information about location from the according woeid
		//		String urlString = "http://group14.naf.cs.hut.fi/";
		String urlString = BASE_ADDRESS;

		//		if(this.myPost)
		//		{
		//			urlString += "post/" + USER_ID;
		//			this.posts = new ArrayList<Post>();
		//		}


		// create a new factory, which defines the API for the DOM-Parser
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// Defines API for getting XML-Document
		DocumentBuilder builder = factory.newDocumentBuilder();

		// Parse the actual XML-Document
		Document doc = null;
		try{
			doc = builder.parse(urlString);
			doc.getDocumentElement().normalize();
		}
		catch(FileNotFoundException e){
			System.err.println(e.getStackTrace());
		}


		posts = new ArrayList<Post>();
		if(doc != null)
		{
			NodeList wall  = doc.getElementsByTagName("post");

			for(int i = 0; i < wall.getLength(); i++)
			{
				Node wallEl = wall.item(i);

				// get information about the location
				Node userData = null;

				// check if we have get an error file
				/*
				 * <socialgags>
				 * 		<!-- Extra things in head block -->
				 * 		<error>
				 * 			The user bla is not in our system
				        		</error>
				        		<!-- End-->
				   </socialgags>
				 */

				userData = XMLHelper.getUserData(wallEl, "error");
				if(userData != null)
				{
					Toast.makeText(LocalRecommendationsActivity.this,"There is no post at the moment. Maybe the server is not available at the moment" +
							"Please try later again.", Toast.LENGTH_SHORT).show();
					return;
				}


				userData = XMLHelper.getUserData(wallEl, "title");
				String title = userData.getTextContent();
				userData = XMLHelper.getUserData(wallEl, "description");
				String description = userData.getTextContent();
				userData = XMLHelper.getUserData(wallEl, "link");
				String link = userData.getTextContent();
				userData = XMLHelper.getUserData(wallEl, "date");
				String date = userData.getTextContent();

				Post post = new Post();
				post.setDate(date);
				post.setTitle(title);
				post.setDescription(description);

				int sepInd = link.indexOf(";");
				int sepInd2 = link.indexOf(";", sepInd+1);


				String extractedString = link.substring(sepInd+1, sepInd2);
				int latitude  = 	Integer.valueOf(extractedString);

				extractedString = link.substring(sepInd2+1, link.length());
				int  longitude  =   Integer.valueOf(extractedString);


				link = link.substring(0, sepInd);
				post.setUrl(link);
				
				post.setLatitude(latitude);
				post.setLongtitude(longitude);

				
				// check if it in the range
				if(!SettingsActivity.restricted)
					posts.add(post);
				else
				{
					int restriction = SettingsActivity.restriction;
					//calc distance between last know position and actual recommendation
					double distance = MathHelper.distance(((double)post.getLatitude()) /  1E6 ,
							((double)post.getLongitude()) /  1E6,
							((double)this.lastKnowPosition[0]) /  1E6,
							((double)this.lastKnowPosition[1]) /  1E6,
							"K");
					
					System.err.println("distance: " + distance);
					System.err.println("restiction: " + restriction);
					
					if((distance < restriction))
						posts.add(post);
				}
				
			}
			if(posts.size() >= 1)
				putNextImageToImageView();
			else
				Toast.makeText(LocalRecommendationsActivity.this,"There are no posts in the server.", Toast.LENGTH_SHORT).show();
		}


	}

	public void putNextImageToImageView() throws URISyntaxException, IOException
	{
		if(posts.size() == 0)
		{
			Toast.makeText(LocalRecommendationsActivity.this,"There is no post at the moment. Please login.", Toast.LENGTH_SHORT).show();
			return;
		}

		actPost = posts.get(nextPost);

		TextView titleTV = (TextView) findViewById(R.id.textviewSGCommentTitle);
		TextView descriptionTV = (TextView) findViewById(R.id.textviewSGCommentDescription);

		titleTV.setText("Title: " + actPost.getTitle());
		descriptionTV.setText("Description: " + actPost.getDescription());


		String urlString = actPost.getUrl();
		System.err.println("urlstring: " + urlString);
		URL url = null;
		try{
			url = new URL(urlString);
			
			if(url != null)
			{
				InputStream openStream = url.openStream();
				actPic.setImageBitmap(BitmapFactory.decodeStream(openStream));
				openStream.close();
			}
		}
		catch(MalformedURLException exception)
		{
			Toast.makeText(LocalRecommendationsActivity.this,
					"This is not a valid url for this picture", Toast.LENGTH_SHORT).show();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if(nextPost + 1 < posts.size())
		{
			nextPost++;
		}
		else
		{
			nextPost = 0;
		}

	}


	/**
	 * Extract the user data from FB
	 */
	public void extractUserData() {
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		access_token = sharedPrefs.getString("access_token", "x");
		expires = ((SharedPreferences) sharedPrefs).getLong("access_expires", -1);
		USER_ID = sharedPrefs.getString("id", "x");
		first_name = sharedPrefs.getString("first_name", "x");
		last_name = sharedPrefs.getString("last_name", "x");
	}

	public AsyncFacebookRunner getmAsyncRunner() {
		return mAsyncRunner;
	}


	public void setmAsyncRunner(AsyncFacebookRunner mAsyncRunner) {
		this.mAsyncRunner = mAsyncRunner;
	}
	
	private int[] getGPS() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  
        List<String> providers = lm.getProviders(true);

        /* Loop over the array backwards, and if you get an accurate location, then break out the loop*/
        Location l = null;
        
        for (int i=providers.size()-1; i>=0; i--) {
                l = lm.getLastKnownLocation(providers.get(i));
                if (l != null) break;
        }
        
        int[] gps = new int[2];
        if (l != null) {
                gps[0] =  (int) (l.getLatitude()*1E6);
                gps[1] = (int) (l.getLongitude()*1E6);
        }
        return gps;
}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu01, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.settingsA:
			intent = new Intent(LocalRecommendationsActivity.this, SettingsActivity.class);
			startActivity(intent);
			break;
		
		}
		return true;
	}

}
