package de.kamson.localdo;

import java.io.IOException;
import java.util.ArrayList;
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

import de.kamson.data.DBHandler;
import de.kamson.data.MyLocation;
import de.kamson.data.Task;
import de.kamson.data.MyConstants;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.Toast;

public class SetLocationActivity extends Activity {
	Context mContext;
	Intent intent;
	DBHandler dbHandler;	
	static final LatLng MANNHEIM_SCHLOSS = new LatLng(49.48325, 8.462);
	
	// Map variables
	private GoogleMap mMap;
	private Marker mMarker;
	private Circle mCircle;
	private CircleOptions circleoptions;
	private String mAddress;
	private int mRadius;
	private double mLat;
	private double mLng;
	private CustomInfoWindowAdapter iwAdapter;
	private LatLng mLatLng;
	
	// UI variables
	EditText et_locationName;
	EditText et_locationAddress;
	CheckBox cb_addLocation;
	CheckBox cb_addLocationAlert;
	Spinner spinner_locationRanges;
	
	MyLocation location;
	List<Task> location_tasks;
	
	private int operating_mode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_location);
		//Toast.makeText(this, "SetLocation onCreate called", Toast.LENGTH_SHORT).show();
		mContext = getApplicationContext();
		intent = getIntent();
		
		loadDataFromDB();
		
		// Show the Up button in the action bar.
		setupActionBar();			
		
		// UI
		et_locationName = (EditText)findViewById(R.id.location_name);
		et_locationAddress = (EditText)findViewById(R.id.location_address);
		cb_addLocation = (CheckBox)findViewById(R.id.setLocation_cb_addLocationToList);
		cb_addLocationAlert = (CheckBox)findViewById(R.id.setLocation_cb_setAlert);
		spinner_locationRanges = (Spinner)findViewById(R.id.setLocation_spinner_distances);
		spinner_locationRanges.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		fillInData();
		// Add map
		setUpMap();
		
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
		if (operating_mode == MyConstants.MODE_ADD) {
			getMenuInflater().inflate(R.menu.add_new_location, menu);
		}
		else {
			getMenuInflater().inflate(R.menu.edit_location, menu);
		}
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
			backToSetTask(MyConstants.ACTION_ACCEPT);
			return true;
		case R.id.action_cancel:			
			backToSetTask(MyConstants.ACTION_CANCEL);
			return true;
		case R.id.action_discard:			
			backToSetTask(MyConstants.ACTION_DISCARD);
			return true;		
		}
		return super.onOptionsItemSelected(item);
	}	
	
	
	public void backToSetTask(int action) {
		Intent intent = new Intent();
		switch (action) {
			case MyConstants.ACTION_DISCARD:
				
				// Location can be safely removed when no tasks exist
				if (location_tasks.size() == 0) {
					
					// Sending back ID of deleted location
					intent.putExtra(MyConstants.LOCATION_ID, location.id);
					
					// Delete task from database here
					dbHandler.deleteLocation(location.id);
					Toast.makeText(getApplicationContext(), "Location with ID "+location.id+" deleted", Toast.LENGTH_SHORT).show();					
				}
				// Alert that remove operation cannot be performed
				else {				
					Toast.makeText(getApplicationContext(), "Deletion denied - Location still bound to another task", Toast.LENGTH_SHORT).show();
				}
				setResult(RESULT_CANCELED, intent);				
				break;
			case MyConstants.ACTION_ACCEPT:	
				// For now no correctness check of user input
				readUserInput();
				long id = location.id;
				String name = location.name;
				double latitude = location.lat;
				double longitude = location.lng;
				int range = location.range;
				int anonymous = location.isAnonymous ? 1 : 0;				
				// No location ID existed before means new location
				if (location.id == -1) {
					id = dbHandler.createLocation(name, latitude, longitude, range, anonymous);
					Toast.makeText(getApplicationContext(), "Location created", Toast.LENGTH_SHORT).show();
				}
				
				// Location ID existed so we can update the database
				else {
					id = location.id;
					dbHandler.updateLocations(id, name, latitude, longitude, range, anonymous);
					Toast.makeText(getApplicationContext(), "Location updated", Toast.LENGTH_SHORT).show();
				}
				
				// Sending back ID of new or updated task
				intent.putExtra(MyConstants.LOCATION_ID, id);
				intent.putExtra(MyConstants.LOCATION_NAME, location.name);
				setResult(RESULT_OK, intent);
				break;
			case MyConstants.ACTION_CANCEL:
				// No action needed
				break;
		}
		
		finish();
	}
	
	private void loadDataFromDB() {
		
		intent = getIntent();
		
		// Instantiate database access
		dbHandler = new DBHandler(mContext);
		dbHandler.open();
		
		// Set operating mode
		operating_mode = intent.getIntExtra(MyConstants.OPERATING_MODE, -1); 				
		
		// Check if a location was send from SetTaskActivity
		if (operating_mode == MyConstants.MODE_EDIT) {			
			
			// Get location from database
			location = dbHandler.getLocation(intent.getLongExtra(MyConstants.LOCATION_ID, -1));
			
			// Get tasks to which this location is bound
			location_tasks = dbHandler.getTasksToLocation(location.id);
		}
		
		// No location was given so instantiate a new one; id = -1
		else {
			location = new MyLocation();
			location_tasks = new ArrayList<Task>();
		}		
	}
	
	private void readUserInput() {
		
		// Check if user entered a personal name for the new location
		if(et_locationName.getText().length() > 0) {
			location.name = et_locationName.getText().toString();
		}
		
		// If no personal name exist, take the address from map
		else {				
			location.name = et_locationAddress.getText().toString();
		}
		
		// Fields set in map
		location.lat = mLat;		
		location.lng = mLng;
		
		// Field set in InfoWindow
		location.range = mRadius;
		
		//
		
		location.isAnonymous = !cb_addLocation.isChecked();
	}
	
	private void setUpMap() {
	    // Do a null check to confirm that we have not already instantiated the map.
	    if (mMap == null) {
	        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
	                            .getMap();
	        // Check if we were successful in obtaining the map.
	        if (mMap != null) {
	            iwAdapter = new CustomInfoWindowAdapter(this, R.layout.custom_info_window);
	        	iwAdapter.setTitle(mAddress);
	        	iwAdapter.setRadius(mRadius);	        	
	        	mMap.setInfoWindowAdapter(iwAdapter);
	        	mMarker = mMap.addMarker(new MarkerOptions().position(mLatLng));
	        	
	        	mMarker.showInfoWindow();
	        	
	        	// Set a circle around chosen point on map
	        	circleoptions = new CircleOptions()
	        		.center(mLatLng)
	        		.radius(mRadius)
	        		.fillColor(0x40bb0000)
	        		.strokeColor(Color.TRANSPARENT)
	        		.strokeWidth(2);
	        	mCircle = mMap.addCircle(circleoptions);	        	
	        	
	        	mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
					
					@Override
					public void onMapClick(LatLng point) {
						// TODO Auto-generated method stub
						mAddress = getAddress(point);
						et_locationAddress.setText(mAddress);
						mLat = point.latitude;
						mLng = point.longitude;
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
						et_input.setBackgroundColor(0x00000000);
						new AlertDialog.Builder(SetLocationActivity.this)
					    .setTitle("Change Radius in m")
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
            //return ("IO Exception trying to get address");
            return ("Could not get address");
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
	
	private void fillInData() {
		
		if (location.id != -1) {
			mLatLng = new LatLng(location.lat, location.lng);
			mAddress = getAddress(mLatLng);
			et_locationName.setText(location.name);			
			mLat = location.lat;
			mLng = location.lng;
			mRadius = location.range;
			// If location is anonymous then uncheck checkbox
			cb_addLocation.setChecked(!location.isAnonymous);
		}
		// If New Location was chosen, get user location that was requested in MainActivity  	
		else if(operating_mode == MyConstants.MODE_ADD) {
    		if (MainActivity.mLocation != null) {
    			mLatLng = new LatLng(MainActivity.mLocation.getLatitude(), MainActivity.mLocation.getLongitude());
    		}
    		else {
    			mLatLng = MANNHEIM_SCHLOSS;    			
    		}
	    		// Standard range for new locations
	    		mRadius = 100;
        		// Get the address 
        		mAddress = getAddress(mLatLng);
        		mLat = mLatLng.latitude;
        		mLng = mLatLng.longitude;
        		
    	}	  
    	et_locationAddress.setText(mAddress);
	}
	/*
	public void onStart() {
		super.onStart();
		Toast.makeText(this, "SetLocation onStart called", Toast.LENGTH_SHORT).show();
		
	}
	
	public void onResume() {
		super.onResume();
		Toast.makeText(this, "SetLocation onResume called", Toast.LENGTH_SHORT).show();
	}
	
	public void onPause() {
		super.onPause();
		Toast.makeText(this, "SetLocation onPause called", Toast.LENGTH_SHORT).show();
	}
	
	public void onStop() {		
        Toast.makeText(this, "SetLocation onStop called", Toast.LENGTH_SHORT).show();
		super.onStop();
	}
	*/


}
