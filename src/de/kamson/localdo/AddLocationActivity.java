package de.kamson.localdo;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class AddLocationActivity extends Activity {
	boolean hasResult = false;
	static final LatLng MANNHEIM_SCHLOSS = new LatLng(49.48325, 8.462);
	private GoogleMap mMap;
	private Marker mMarker;
	private Circle mCircle;
	private CircleOptions circleoptions;
	private String mAddress;
	private int mRadius;
	private CustomInfoWindowAdapter iwAdapter;
	EditText et_locationName;
	EditText et_locationAddress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_location);
		// Show the Up button in the action bar.
		setupActionBar();
		et_locationName = (EditText)findViewById(R.id.location_name);
		et_locationAddress = (EditText)findViewById(R.id.location_address);
		setUpMapIfNeeded();
		
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_location, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_accept:			
			hasResult = true;
			backToAddNewToDo(hasResult);
			return true;
		case R.id.action_cancel:			
			backToAddNewToDo(hasResult);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}	
	
	
	public void backToAddNewToDo(boolean hasResult) {
		if(hasResult) {
			Intent intent = new Intent();		
			if(et_locationName.getText().length() > 0) {
				intent.putExtra("loc_name", et_locationName.getText());
				setResult(RESULT_OK, intent);
			}
			else if(et_locationAddress.getText().length() > 0) {				
				intent.putExtra("loc_name", et_locationAddress.getText());
				setResult(RESULT_OK, intent);
			}
			else {
				setResult(RESULT_CANCELED, intent);
			}
		}
		finish();
		
	}
	
	private void setUpMapIfNeeded() {
	    // Do a null check to confirm that we have not already instantiated the map.
	    if (mMap == null) {
	        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
	                            .getMap();
	        // Check if we were successful in obtaining the map.
	        if (mMap != null) {
	            // The Map is verified. It is now safe to manipulate the map.
	        	LatLng mLatLng;
	        	if(MainActivity.mLocation != null) {
	        		mLatLng = new LatLng(MainActivity.mLocation.getLatitude(), MainActivity.mLocation.getLongitude());
	        	}
	        	else {
	        		mLatLng = MANNHEIM_SCHLOSS;
	        	}
	        	mAddress = getAddress(mLatLng);
	        	mRadius = 100;
	        	et_locationAddress.setText(mAddress);
	        	iwAdapter = new CustomInfoWindowAdapter(this, R.layout.custom_info_window);
	        	iwAdapter.setTitle(mAddress);
	        	iwAdapter.setRadius(mRadius);	        	
	        	mMap.setInfoWindowAdapter(iwAdapter);
	        	mMarker = mMap.addMarker(new MarkerOptions().position(mLatLng));
	        	
	        	mMarker.showInfoWindow();
	        	
	        	circleoptions = new CircleOptions()
	        		.center(mLatLng)
	        		.radius(mRadius)
	        		.fillColor(0x40ff0000)
	        		.strokeColor(Color.TRANSPARENT)
	        		.strokeWidth(2);
	        	mCircle = mMap.addCircle(circleoptions);	        	
	        	
	        	mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
					
					@Override
					public void onMapClick(LatLng point) {
						// TODO Auto-generated method stub
						mAddress = getAddress(point);
						et_locationAddress.setText(mAddress);
						mMarker.remove();
						iwAdapter.setTitle(mAddress);
						iwAdapter.setRadius(mRadius);						
						mMarker = mMap.addMarker(new MarkerOptions().position(point));
						mMarker.showInfoWindow();
						mCircle.setCenter(point);
						mMap.animateCamera(CameraUpdateFactory.newLatLng(point));
					}
				});
	        	
	        	mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
					
					@Override
					public void onInfoWindowClick(Marker marker) {
						// TODO Auto-generated method stub
						final EditText et_input = new EditText(getApplicationContext());
						et_input.setInputType(InputType.TYPE_CLASS_NUMBER);
						et_input.setText(String.valueOf(mRadius));
						et_input.setTextColor(0xff000000);
						new AlertDialog.Builder(AddLocationActivity.this)
					    .setTitle("Change Radius")
					    //.setMessage("You can change the radius")
					    .setView(et_input)
					    .setPositiveButton(R.string.infowindow_dialog, new DialogInterface.OnClickListener() {
					        public void onClick(DialogInterface dialog, int whichButton) {
					            mRadius = Integer.parseInt(et_input.getText().toString());
					            iwAdapter.setRadius(mRadius);
					            mMarker.showInfoWindow();
								mCircle.setRadius(mRadius);
					        }
					    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					        public void onClick(DialogInterface dialog, int whichButton) {
					            // Do nothing.
					        }
					    }).show();
						
					}
				});
	        	mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 15));
	        }
	    }
	}
	
	private String getAddress(LatLng point) {
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());		
		List <Address> addresses = null;
		try {
			addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);
		} catch (IOException e1) {
            Log.e("LocationSampleActivity",
                    "IO Exception in getFromLocation()");
            e1.printStackTrace();
            return ("IO Exception trying to get address");
            } catch (IllegalArgumentException e2) {
            // Error message to post in the log
            String errorString = "Illegal arguments " +
                    Double.toString(point.latitude) +
                    " , " +
                    Double.toString(point.longitude) +
                    " passed to address service";
            Log.e("LocationSampleActivity", errorString);
            e2.printStackTrace();
            return errorString;
            }
            // If the reverse geocode returned an address
            if (addresses != null && addresses.size() > 0) {
                // Get the first address
                Address address = addresses.get(0);
                /*
                 * Format the first line of address (if available),
                 * city, and country name.
                 */
                String addressText = String.format(
                		"%s, %s",
                		//"%s, %s, %s",
                        // If there's a street address, add it
                        address.getMaxAddressLineIndex() > 0 ?
                                address.getAddressLine(0) : "",
                        // Locality is usually a city
                        address.getLocality());
                        // The country of the address not needed
                        //address.getCountryName());
                // Return the text
                return addressText;
            } else {
                return "No address found";
            }
		
	}


}
