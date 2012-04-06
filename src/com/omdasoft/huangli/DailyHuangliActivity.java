package com.omdasoft.huangli;

import java.util.Date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import net.yihabits.huangli.R;

public class DailyHuangliActivity extends Activity {
	private String todayUrl = "http://www.99wed.com/tools/tools_selectGooday.php";
	private String searchUrl = "http://www.99wed.com/tools/huangli_search.php";
	private String detailUrl = "http://www.99wed.com/tools/huangli_details.php?ID=";
	private String uid;
	private String LOGTAG = "DailyHuangliActivity";
	
	private DownloadUtil util;
	private String dateId;
	
	private TextView gongliLbl;
	private TextView nongliLbl;
	private TextView yiLbl;
	private TextView jiLbl;
	private TextView chongLbl;
	private DatePicker selectCal;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		
		// ad initialization
		// Create the adView
		AdView adView = new AdView(this, AdSize.BANNER, "a14dedd2cea1a6b");
		// Lookup your LinearLayout assuming it��s been given
		// the attribute android:id="@+id/mainLayout"
		LinearLayout layout = (LinearLayout) findViewById(R.id.ad_layout);
		// Add the adView to it
		layout.addView(adView);
		// Initiate a generic request to load it with an ad
		adView.loadAd(new AdRequest());
		
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
	  // We do nothing here. We're only handling this to keep orientation
	  // or keyboard hiding from causing the WebView activity to restart.
	}

	@Override
	protected void onStart() {
		super.onStart();

		//initialize download folders
		if(util == null){
			util = new DownloadUtil();
		}


	}

	@Override
	protected void onStop() {

		super.onStop();
	}
	
	private class MyListener implements DatePicker.OnDateChangedListener {

		@Override
		public void onDateChanged(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			if (year > 2018 || year < 2010) {
				toastMsg(R.string.yearValidate);
			} else {
					String date = year + "-" + monthOfYear + "-" + dayOfMonth;
					dateId = DownloadUtil.getDayId(date);
			}

		}

	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.menu_today:
			dateId = DownloadUtil.getDayId(new Date());
			refreshDate();
			return true;
		case R.id.menu_search:
//			mainWeb.loadUrl(searchUrl);
			//popup searchActivity
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setClass(DailyHuangliActivity.this, SearchActivity.class);
			startActivity(intent);
			
			return true;
		case R.id.menu_help:
			// popup the about window
//			mainWeb.loadUrl("file:///android_asset/help.html");
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	private void refreshDate() {
		// show waiting dialog
		final ProgressDialog dialog = ProgressDialog.show(this,
				getString(R.string.waitTitle), getString(R.string.wait),
				true);

		Runnable saveUrl = new Runnable() {

			public void run() {

				//get the data from the web
				util

				// refresh the text view
				

				// hide waiting dialog
				dialog.dismiss();
			}
		};
		new Thread(saveUrl).start();
		
	}

	public void toastMsg(int resId, String...args ) {
		final String msg = this.getString(resId, args);
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
						.show();
			}
		});
	}


}