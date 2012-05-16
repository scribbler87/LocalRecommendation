package fi.uni.aalto.activities;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import fi.uni.aalto.activities.R;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

public class SimpleActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple);

		        
		        Button bInformationVisualizer = (Button) findViewById(R.id.buttonInfoVisualizer);
		        bInformationVisualizer.setOnClickListener(new OnClickListener() {
					
					public void onClick(View arg0) {
						startActivity(new Intent(getApplicationContext(), InformationVisualizerActivity.class));
					}
				});
		        
		        
		        try {
						URL url = new URL("https://playground.cs.hut.fi/t-110.5140_2012/hello.txt");
						displayFileAndModifiedDateFromURL(url);
						getImageFromURL();
		    
		        } catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
	}

	private void getImageFromURL() {
		Button confButton = (Button) findViewById(R.id.buttonURLConfirm);
		confButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				ImageView imageView = (ImageView) findViewById(R.id.imageViewURL);

				EditText urlTextView = (EditText) findViewById(R.id.editTextUrlImage);
				String urlString = urlTextView.getText().toString();
				System.err.println(urlString);
				try {
					URL url = new URL(urlString);
					url.toURI();
					imageView.setImageBitmap(BitmapFactory.decodeStream(url.openStream()));

				} catch (IOException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					System.err.println("not valid URL");
					e.printStackTrace();
				}
			}
		});
	}

	private void displayFileAndModifiedDateFromURL(URL url) throws IOException {
		URLConnection ucon = url.openConnection();

		long lastModified = ucon.getLastModified();
		Date dateLastModified = new Date(lastModified);


		InputStream inputStream = ucon.getInputStream();

		BufferedReader bfText = new BufferedReader(new InputStreamReader(inputStream));


		String text = "";
		while(true)
		{
			String tmpText = bfText.readLine();
			if(tmpText != null)
			{
				text += tmpText;
			}
			else
			{
				break;
			}
		}

		TextView findViewById = (TextView) findViewById(R.id.textviewFileCon);
		findViewById.setText(text + "\nLast modified: " + dateLastModified);
		inputStream.close();
	}

}