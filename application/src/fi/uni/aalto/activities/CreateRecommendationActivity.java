package fi.uni.aalto.activities;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;

import fi.uni.aalto.activities.R;
import fi.uni.aalto.controllers.HTTPHelper;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class CreateRecommendationActivity extends Activity 
{
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_new_posts);
		
		sendPost = (Button) findViewById(R.id.buttonSendPost);
		description = (EditText) findViewById(R.id.edittextTitle);
		title = (EditText) findViewById(R.id.edittextTitle);
		edUrl = (EditText) findViewById(R.id.edittextURL);
		urlPic = (ImageView) findViewById(R.id.imgViewPost);

		sendPost.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String urlString = edUrl.getEditableText().toString();
				String sTitle = title.getEditableText().toString();
				if(sTitle == "")
					Toast.makeText(CreateRecommendationActivity.this,"Please add a title.", Toast.LENGTH_SHORT).show();
				String sDescritption = description.getEditableText().toString();
				if(sDescritption == "")
					Toast.makeText(CreateRecommendationActivity.this,"Please add a description.", Toast.LENGTH_SHORT).show();
				
				String str = "";
				try {
					str = HTTPHelper.sendPostPost(sTitle, sDescritption, LocalRecommendationsActivity.USER_ID, LocalRecommendationsActivity.access_token, urlString);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				System.err.println("bla: " + str);

			}
		});

	}
	Button sendPost = null;
	private EditText description;
	private EditText title;
	private EditText edUrl;
	private ImageView urlPic;
	
}
