package com.baptistecarlier.android.cover4hue.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baptistecarlier.android.cover4hue.hue.AccessPointListAdapter;
import com.baptistecarlier.android.cover4hue.hue.HueSharedPreferences;
import com.baptistecarlier.android.cover4hue.hue.PHPushlinkActivity;
import com.baptistecarlier.android.cover4hue.hue.PHWizardAlertDialog;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;

import com.baptistecarlier.android.cover4hue.R;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;

import java.util.List;


public class MainActivity extends Activity implements OnItemClickListener {

	public static final String PACKAGE = "com.baptistecarlier.android.cover4hue";
	public static final String PREFS = "Cover4HuePrefs";
	public static final String APPNAME = "Cover4Hue";

	public Button redBtn;
	public ImageView imageView;
	public TextView textView;

	// Hue Stuff
	private PHHueSDK phHueSDK;
	public static final String TAG = "Cover4HueTag";
	private HueSharedPreferences prefs;
	private AccessPointListAdapter adapter;
	private boolean lastSearchWasIPScan = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		//initBridge();

		initMusic();
	}

	private void initMusic() {
		setContentView(R.layout.activity_main);

		redBtn = (Button) findViewById(R.id.redBtn);
		redBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeColor(Color.RED);
			}
		});
		imageView = (ImageView) findViewById(R.id.imageView);
		textView = (TextView) findViewById(R.id.textView);


		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.android.music.metachanged");
		intentFilter.addAction("com.android.music.playstatechanged");
		intentFilter.addAction("com.android.music.playbackcomplete");
		intentFilter.addAction("com.android.music.queuechanged");

		registerReceiver(mReceiver, intentFilter);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			String cmd = intent.getStringExtra("command");
			Log.d(TAG+" / mIntentReceiver.onReceive ", action + " / " + cmd);
			String artist = intent.getStringExtra("artist");
			String album = intent.getStringExtra("album");
			String track = intent.getStringExtra("track");
//			String albumId = intent.getStringExtra(MediaMetadataRetriever.METADATA_KEY_ALBUM);

			textView.setText(artist + " - " + track);

			Log.d(TAG, "id : "+(intent.getStringExtra("id") == null ? "null!" : intent.getStringExtra("id")));
			Log.d(TAG, "id : "+(intent.getStringExtra("_id") == null ? "null!" : intent.getStringExtra("_id")));

			/*
				Et comme j'ai pas mon cable USB-C, je vais arrêter là pour ce weekend.
				Quelques sources :
				http://stackoverflow.com/questions/1954434/cover-art-on-android
				http://stackoverflow.com/questions/15740359/how-to-get-thumbnails-of-audio-files-by-using-mediastore
				http://stackoverflow.com/questions/14136899/how-to-display-thumbnail-of-song-in-android
				http://www.programcreek.com/java-api-examples/index.php?api=android.media.MediaMetadataRetriever
				http://stackoverflow.com/questions/30876838/android-how-to-retrieve-album-art-for-each-album-using-album-id
				http://stackoverflow.com/questions/21996228/mediastore-audio-albums-album-id-invalid-column

			 */

		}
	};


	private void initBridge() {
		setContentView(R.layout.bridgelistlinear);
		// Gets an instance of the Hue SDK.
		phHueSDK = PHHueSDK.create();

		// Set the Device Name (name of your app). This will be stored in your bridge whitelist entry.
		phHueSDK.setAppName( APPNAME );
		phHueSDK.setDeviceName(android.os.Build.MODEL);

		// Register the PHSDKListener to receive callbacks from the bridge.
		phHueSDK.getNotificationManager().registerSDKListener(listener);

		adapter = new AccessPointListAdapter(getApplicationContext(), phHueSDK.getAccessPointsFound());

		ListView accessPointList = (ListView) findViewById(R.id.bridge_list);
		accessPointList.setOnItemClickListener(this);
		accessPointList.setAdapter(adapter);

		// Try to automatically connect to the last known bridge.  For first time use this will be empty so a bridge search is automatically started.
		prefs = HueSharedPreferences.getInstance(getApplicationContext());
		String lastIpAddress   = prefs.getLastConnectedIPAddress();
		String lastUsername    = prefs.getUsername();

		// Automatically try to connect to the last connected IP Address.  For multiple bridge support a different implementation is required.
		if (lastIpAddress !=null && !lastIpAddress.equals("")) {
			PHAccessPoint lastAccessPoint = new PHAccessPoint();
			lastAccessPoint.setIpAddress(lastIpAddress);
			lastAccessPoint.setUsername(lastUsername);

			if (!phHueSDK.isAccessPointConnected(lastAccessPoint)) {
				PHWizardAlertDialog.getInstance().showProgressDialog(R.string.connecting, MainActivity.this);
				phHueSDK.connect(lastAccessPoint);
			}
		}
		else {  // First time use, so perform a bridge search.
			doBridgeSearch();
		}

	}

	private void changeColor(int colorId) {
		Log.d(TAG, "Change color to : " + colorId);
	}




	// Local SDK Listener
	private PHSDKListener listener = new PHSDKListener() {

		@Override
		public void onAccessPointsFound(List<PHAccessPoint> accessPoint) {
			Log.w(TAG, "Access Points Found. " + accessPoint.size());

			PHWizardAlertDialog.getInstance().closeProgressDialog();
			if (accessPoint != null && accessPoint.size() > 0) {
				phHueSDK.getAccessPointsFound().clear();
				phHueSDK.getAccessPointsFound().addAll(accessPoint);

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						adapter.updateData(phHueSDK.getAccessPointsFound());
					}
				});

			}

		}

		@Override
		public void onCacheUpdated(List<Integer> arg0, PHBridge bridge) {
			Log.w(TAG, "On CacheUpdated");

		}

		@Override
		public void onBridgeConnected(PHBridge b, String username) {
			phHueSDK.setSelectedBridge(b);
			phHueSDK.enableHeartbeat(b, PHHueSDK.HB_INTERVAL);
			phHueSDK.getLastHeartbeat().put(b.getResourceCache().getBridgeConfiguration() .getIpAddress(), System.currentTimeMillis());
			prefs.setLastConnectedIPAddress(b.getResourceCache().getBridgeConfiguration().getIpAddress());
			prefs.setUsername(username);
			PHWizardAlertDialog.getInstance().closeProgressDialog();
			startMainActivity();
		}

		@Override
		public void onAuthenticationRequired(PHAccessPoint accessPoint) {
			Log.w(TAG, "Authentication Required.");
			phHueSDK.startPushlinkAuthentication(accessPoint);
			startActivity(new Intent(MainActivity.this, PHPushlinkActivity.class));

		}

		@Override
		public void onConnectionResumed(PHBridge bridge) {
			if (MainActivity.this.isFinishing())
				return;

			Log.v(TAG, "onConnectionResumed" + bridge.getResourceCache().getBridgeConfiguration().getIpAddress());
			phHueSDK.getLastHeartbeat().put(bridge.getResourceCache().getBridgeConfiguration().getIpAddress(),  System.currentTimeMillis());
			for (int i = 0; i < phHueSDK.getDisconnectedAccessPoint().size(); i++) {

				if (phHueSDK.getDisconnectedAccessPoint().get(i).getIpAddress().equals(bridge.getResourceCache().getBridgeConfiguration().getIpAddress())) {
					phHueSDK.getDisconnectedAccessPoint().remove(i);
				}
			}

		}

		@Override
		public void onConnectionLost(PHAccessPoint accessPoint) {
			Log.v(TAG, "onConnectionLost : " + accessPoint.getIpAddress());
			if (!phHueSDK.getDisconnectedAccessPoint().contains(accessPoint)) {
				phHueSDK.getDisconnectedAccessPoint().add(accessPoint);
			}
		}

		@Override
		public void onError(int code, final String message) {
			Log.e(TAG, "on Error Called : " + code + ":" + message);

			if (code == PHHueError.NO_CONNECTION) {
				Log.w(TAG, "On No Connection");
			}
			else if (code == PHHueError.AUTHENTICATION_FAILED || code==PHMessageType.PUSHLINK_AUTHENTICATION_FAILED) {
				PHWizardAlertDialog.getInstance().closeProgressDialog();
			}
			else if (code == PHHueError.BRIDGE_NOT_RESPONDING) {
				Log.w(TAG, "Bridge Not Responding . . . ");
				PHWizardAlertDialog.getInstance().closeProgressDialog();
				MainActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						PHWizardAlertDialog.showErrorDialog(MainActivity.this, message, R.string.btn_ok);
					}
				});

			}
			else if (code == PHMessageType.BRIDGE_NOT_FOUND) {

				if (!lastSearchWasIPScan) {  // Perform an IP Scan (backup mechanism) if UPNP and Portal Search fails.
					phHueSDK = PHHueSDK.getInstance();
					PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
					sm.search(false, false, true);
					lastSearchWasIPScan=true;
				}
				else {
					PHWizardAlertDialog.getInstance().closeProgressDialog();
					MainActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							PHWizardAlertDialog.showErrorDialog(MainActivity.this, message, R.string.btn_ok);
						}
					});
				}


			}
		}

		@Override
		public void onParsingErrors(List<PHHueParsingError> parsingErrorsList) {
			for (PHHueParsingError parsingError: parsingErrorsList) {
				Log.e(TAG, "ParsingError : " + parsingError.getMessage());
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {

			doBridgeSearch();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		PHAccessPoint accessPoint = (PHAccessPoint) adapter.getItem(position);

		PHBridge connectedBridge = phHueSDK.getSelectedBridge();

		if (connectedBridge != null) {
			String connectedIP = connectedBridge.getResourceCache().getBridgeConfiguration().getIpAddress();
			if (connectedIP != null) {   // We are already connected here:-
				phHueSDK.disableHeartbeat(connectedBridge);
				phHueSDK.disconnect(connectedBridge);
			}
		}
		PHWizardAlertDialog.getInstance().showProgressDialog(R.string.connecting, MainActivity.this);
		phHueSDK.connect(accessPoint);
	}

	public void doBridgeSearch() {
		PHWizardAlertDialog.getInstance().showProgressDialog(R.string.search_progress, MainActivity.this);
		PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
		// Start the UPNP Searching of local bridges.
		sm.search(true, true);
	}

	// Starting the main activity this way, prevents the PushLink Activity being shown when pressing the back button.
	public void startMainActivity() {
		Intent intent = new Intent(getApplicationContext(), MyApplicationActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			intent.addFlags(0x8000); // equal to Intent.FLAG_ACTIVITY_CLEAR_TASK which is only available from API level 11
		startActivity(intent);
	}
}
