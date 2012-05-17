package fi.uni.aalto.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SettingsActivity extends Activity{


	
	
	
	public static int restriction = 0;
	public static boolean restricted = false;
	private SeekBar seekBar;
	private TextView textFieldAreaInkm;
	private ToggleButton toggle;
	private Button settingsConfirm;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		 toggle = (ToggleButton) findViewById(R.id.toggleRestriction);
		 textFieldAreaInkm = (TextView) findViewById(R.id.areaInKm);
		 seekBar = (SeekBar)findViewById(R.id.seekBar1);
		 settingsConfirm = (Button) findViewById(R.id.settingsConfirm);
		 
		 toggle.setChecked(restricted);
		 seekBar.setProgress(restriction);
		
		if(!toggle.isChecked())
		{
			seekBar.setVisibility(View.INVISIBLE);	
			textFieldAreaInkm.setVisibility(View.INVISIBLE);	
			
		}
		restricted = toggle.isChecked();
		
		restriction = seekBar.getProgress();
		
		settingsConfirm.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),MainActivity.class));
			}
		});
		
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				restriction = progress;
				textFieldAreaInkm.setText("Area in km: " + restriction);
				
			}
		});
		
		toggle.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				restricted = toggle.isChecked();
				if(!toggle.isChecked())
				{
					seekBar.setVisibility(View.INVISIBLE);	
					textFieldAreaInkm.setVisibility(View.INVISIBLE);	
					
				}
				else
				{
					seekBar.setVisibility(View.VISIBLE);	
					textFieldAreaInkm.setVisibility(View.VISIBLE);	
				}
			}
		});
	}

}
