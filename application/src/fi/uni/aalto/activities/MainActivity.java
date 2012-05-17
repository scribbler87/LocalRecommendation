package fi.uni.aalto.activities;



import fi.uni.aalto.activities.R;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class MainActivity extends TabActivity {
	private TabHost mTabHost;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mTabHost = getTabHost();

		Intent intent = new Intent(MainActivity.this, LocalRecommendationsActivity.class);
		mTabHost.addTab(mTabHost.newTabSpec("tab_test2").setIndicator("BasicInfos").setContent(intent)); 
		
		intent = new Intent(MainActivity.this,RecommendationMapActivity.class);
		mTabHost.addTab(mTabHost.newTabSpec("tab_test2").setIndicator("Restaurants").setContent(intent));
		
		intent = new Intent(MainActivity.this,ProfileActivity.class);
		mTabHost.addTab(mTabHost.newTabSpec("tab_test2").setIndicator("Profile").setContent(intent));
		
//		intent = new Intent(MainActivity.this,SettingsActivity.class);
//		mTabHost.addTab(mTabHost.newTabSpec("tab_test2").setIndicator("Settings").setContent(intent));
		
		mTabHost.setCurrentTab(0);

		
	}


}