package me.awesome.mylocationtracker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends ActionBarActivity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	private static final int GPS_ERRORDIALOG_REQUEST = 9001;
	private static final int ZOOM = 17;
	private GoogleMap mGoogleMap;
	private LocationClient mLocationClient;
	private static double RUET_LAT = 24.371119, RUET_LNG = 88.625813;
	private static Marker mMarker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (servicesOK()) {
			setContentView(R.layout.maps_main_layout);
			initMap();

		} else
			setContentView(R.layout.activity_failed);
	}

	private void initMap() {
		SupportMapFragment mMapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mGoogleMap = mMapFragment.getMap();

		if (mGoogleMap != null) {
			mLocationClient = new LocationClient(this, this, this);
			mLocationClient.connect();

		} else {
			showToast(this, "Maps failed to load");
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id == R.id.action_my_location) {
			showMyLocation();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showMyLocation() {

		LocationRequest mLocationRequest = LocationRequest.create();
		mLocationRequest.setInterval(5000);
		mLocationRequest.setFastestInterval(1000);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		mLocationClient.requestLocationUpdates(mLocationRequest, this);
		showToast(this, "GMap is Searching for my location...");

	}

	public boolean servicesOK() {
		int isAvailable = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);

		if (isAvailable == ConnectionResult.SUCCESS) {
			return true;
		} else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable,
					this, GPS_ERRORDIALOG_REQUEST);
			dialog.show();
		} else {
			Toast.makeText(this, "Can't connect to Google Play services",
					Toast.LENGTH_SHORT).show();
		}
		return false;
	}

	void showToast(Context mContext, CharSequence mText) {
		Toast.makeText(mContext, mText, Toast.LENGTH_SHORT).show();
	}

	@SuppressWarnings("unused")
	private void hideSoftKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {

		// LocationRequest mLocationRequest = LocationRequest.create();
		// mLocationRequest.setInterval(5000);
		// mLocationRequest.setFastestInterval(1000);
		// mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		// mLocationClient.requestLocationUpdates(mLocationRequest, this);
		showToast(this, "GMap connected.");
		Geocoder mGeocoder = new Geocoder(MainActivity.this);
		List<Address> address = null;
		try {
			address = mGeocoder.getFromLocationName("Rajshahi", 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (!address.isEmpty()) {
			LatLng ll = new LatLng(address.get(0).getLatitude(), address.get(0)
					.getLongitude());

			goToThere(ll, ZOOM);
		}

	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location location) {

		LatLng position = new LatLng(location.getLatitude(),
				location.getLongitude());

		showToast(MainActivity.this, position.toString());
		if (mMarker != null)
			mMarker.remove();

		addMarker(position);

		goToThere(position, ZOOM);

	}

	private Marker addMarker(LatLng position) {
		MarkerOptions markerOptions = new MarkerOptions()
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.cling_arrow_up))
				.position(position).anchor(0.5f, 0.5f).rotation(0.0f);
		return mGoogleMap.addMarker(markerOptions);
	}

	private void goToThere(LatLng ll, int zoom) {
		CameraUpdate mCameraUpdate = CameraUpdateFactory
				.newLatLngZoom(ll, zoom);
		mGoogleMap.animateCamera(mCameraUpdate);

	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(mLocationClient!=null)
		mLocationClient.disconnect();
	}

}
