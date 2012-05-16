package fi.uni.aalto.activities;

import java.io.FileNotFoundException;
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

public class CreatePostActivity extends Activity 
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
					Toast.makeText(CreatePostActivity.this,"Please add a title.", Toast.LENGTH_SHORT).show();
				String sDescritption = description.getEditableText().toString();
				if(sDescritption == "")
					Toast.makeText(CreatePostActivity.this,"Please add a description.", Toast.LENGTH_SHORT).show();
				
				URL url = null;
				try{
					url = new URL(urlString);
					if(url != null)
					{
						InputStream openStream = url.openStream();
						urlPic.setImageBitmap(BitmapFactory.decodeStream(openStream));
						openStream.close();
						
						String str =HTTPHelper.sendPostPost(sTitle, sDescritption, SocialGagActivity.USER_ID, SocialGagActivity.access_token, urlString);
						System.err.println(str);
					}
				}
				catch(MalformedURLException exception)
				{
					Toast.makeText(CreatePostActivity.this,"This is not a valid url for this picture", Toast.LENGTH_SHORT).show();
				}
				catch (FileNotFoundException e) {
					Toast.makeText(CreatePostActivity.this,"This is not a valid url for a picture", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

	}
	Button sendPost = null;
	private EditText description;
	private EditText title;
	private EditText edUrl;
	private ImageView urlPic;
	
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
		case R.id.socialGags:   intent = new Intent(CreatePostActivity.this, SocialGagActivity.class);
		startActivity(intent);
		break;
		case R.id.post:     intent = new Intent(CreatePostActivity.this, CreatePostActivity.class);
		startActivity(intent);
		break;
		case R.id.profile:  intent = new Intent(CreatePostActivity.this, ProfileActivity.class);
		startActivity(intent);
		break;
		case R.id.friends:  intent = new Intent(CreatePostActivity.this, LocateFriendsActivity.class);
		startActivity(intent);
		break;
		}
		return true;
	}
}
