package de.kamson.localdo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationStatusCodes;

import de.kamson.localdo.GeofenceRemover;
import de.kamson.localdo.GeofenceRequester;
import de.kamson.localdo.GeofenceUtils.*;
import de.kamson.data.MyLocation;
import de.kamson.data.TestData;
import de.kamson.data.ToDo;
import de.kamson.data.ToDoUtils;
import android.location.Criteria;
import android.location.Location;

import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	/*
	 * Geofence variables
	 */
	// Store the current request
    private REQUEST_TYPE mRequestType;

    // Store the current type of removal
    private REMOVE_TYPE mRemoveType;
    
    // Store a list of geofences to add
    List<Geofence> mCurrentGeofences;
    
    // Store the list of geofences to remove
    private List<String> mGeofenceIdsToRemove;
    
    // Add geofences handler
    private GeofenceRequester mGeofenceRequester;
    // Remove geofences handler
    private GeofenceRemover mGeofenceRemover;
    
    /*
     * Location variables
     */
	public static Location mLocation;
	LocationClient mLocationClient;
	LocationRequest mLocationRequest;
	LocationManager mLocationManager;
	private PendingIntent mGeofenceRequestIntent;
	private PendingIntent mTransitionPendingIntent;
	String provider;
	
	/* 
	 * UI variables 
	 */
	ListView active_tasks_lv;
	ListView finished_tasks_lv;
	TextView active_listTitle;
	TextView finished_listTitle;	
	
	/*
	 * List variables
	 */
	ToDoAdapter active_adapter;
	ToDoAdapter finished_adapter;
	List<ToDo> toDos = new ArrayList<ToDo>();	
	List<ToDo> active_toDos = new ArrayList<ToDo>();
	List<ToDo> finished_toDos = new ArrayList<ToDo>();
	List<MyLocation> locations = new ArrayList<MyLocation>();
	
	static final int TODO_LIST = 1;
	static final int ACTIVE_TODO_LIST = 2;
	static final int FINISHED_TODO_LIST = 3;
	static final int LOCATION_LIST = 4;
	
	// Global state variable
	GlobalState gs;
	// Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    
    
    // Request Code
    static final int ADDNEWTODO = 200;
	
	public ToDo chosenToDo;
	public Animation anim_Move;
	public TestData testData;

	
	
		
		
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 		
		setContentView(R.layout.activity_main);		
		loadData();
		createGeofences();
		updateLists();
		setListItemClickListener();		
		//anim_Move = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move);
		updateListTitle();
		/*
		// Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

		mLocationClient = new LocationClient(this, this, this);
		*/
//		locationmanager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//		Criteria criteria = new Criteria();
//		provider = locationmanager.getBestProvider(criteria, false);
//		Location myLocation = locationmanager.getLastKnownLocation(provider);
//		Location mLocation = mLocationClient.getLastLocation();		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_new:
			Toast.makeText(getApplicationContext(), "Add selected", Toast.LENGTH_LONG).show();
			openAddNew();
			return true;
		/*
		case R.id.action_settings:
			return true;
		*/
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	public void onStart() {
		super.onStart();
		
		//mLocationClient.connect();
		
		/*
         * Record the request as an ADD. If a connection error occurs,
         * the app can automatically restart the add request if Google Play services
         * can fix the error
         */
		mRequestType = GeofenceUtils.REQUEST_TYPE.ADD;
		
		 /*
         * Check for Google Play services. Do this after
         * setting the request type. If connecting to Google Play services
         * fails, onActivityResult is eventually called, and it needs to
         * know what type of request was in progress.
         */
		if (servicesConnected()) {
			// Start the request. Fail if there's already a request in progress
	        try {
	            // Try to add geofences		        	
	        	mGeofenceRequester.addGeofences(mCurrentGeofences);
	        } catch (UnsupportedOperationException e) {
	            // Notify user that previous request hasn't finished.
	            Toast.makeText(this, R.string.add_geofences_already_requested_error,
	                        Toast.LENGTH_LONG).show();
	        }
		}
	}
	
	public void onResume() {
		super.onResume();
		//locationmanager.requestLocationUpdates(provider, 400, 1, this);
	}
	
	public void onPause() {
		super.onPause();
		//locationmanager.removeUpdates(this);
		// should we do some saving operations here?
	}
	
	public void onStop() {		
        /*
         * After disconnect() is called, the client is
         * considered "dead".
         */
        //mLocationClient.disconnect();		
        
		super.onStop();
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //super.onActivityResult(requestCode, resultCode, intent);
		// Choose what to do based on the request code		
		switch (requestCode) {

            // If the request code matches the code sent in onConnectionFailed
            case GeofenceUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :

                switch (resultCode) {
                    // If Google Play services resolved the problem
                    case Activity.RESULT_OK:

                        // If the request was to add geofences
                        if (GeofenceUtils.REQUEST_TYPE.ADD == mRequestType) {

                            // Toggle the request flag and send a new request
                            mGeofenceRequester.setInProgressFlag(false);

                            // Restart the process of adding the current geofences
                            mGeofenceRequester.addGeofences(mCurrentGeofences);

                        // If the request was to remove geofences
                        } else if (GeofenceUtils.REQUEST_TYPE.REMOVE == mRequestType ){

                            // Toggle the removal flag and send a new removal request
                            mGeofenceRemover.setInProgressFlag(false);

                            // If the removal was by Intent
                            if (GeofenceUtils.REMOVE_TYPE.INTENT == mRemoveType) {

                                // Restart the removal of all geofences for the PendingIntent
                                mGeofenceRemover.removeGeofencesByIntent(
                                    mGeofenceRequester.getRequestPendingIntent());

                            // If the removal was by a List of geofence IDs
                            } else {

                                // Restart the removal of the geofence list
                                mGeofenceRemover.removeGeofencesById(mGeofenceIdsToRemove);
                            }
                        }
                    break;

                    // If any other result was returned by Google Play services
                    default:

                        // Report that Google Play services was unable to resolve the problem.
                        Log.d(GeofenceUtils.APPTAG, getString(R.string.no_resolution));
                }
                
            case ADDNEWTODO: {
            	
            	switch(resultCode) {
            	
            		case RESULT_OK:
            			// get Data from AddNewToDo
            			//intent.getExtras().get()
            		break;
            		case RESULT_FIRST_USER:
            			// Abuse this result code to delete elements
            			// Delete in complete task list
            			long id = intent.getLongExtra(ToDoUtils.TODO_ID, -1);
            			deleteItemFromList(id, TODO_LIST);            			
            			// Determine if active or finished task and delete from that list
            			if (intent.getBooleanExtra(ToDoUtils.TODO_ISACTIVE, false)) {
            				deleteItemFromList(id, ACTIVE_TODO_LIST);            				
            			}
            			else {
            				deleteItemFromList(id, FINISHED_TODO_LIST);	            				
            			}
            			updateLists();            			
            		break;
            	}
            }

            // If any other request code was received
            default:
               // Report that this Activity received an unknown requestCode
               Log.d(GeofenceUtils.APPTAG,
                       getString(R.string.unknown_activity_request_code, requestCode));

               break;
        }
    }
	
	private void loadData() {
		
		// get Global State Singleton
		gs = (GlobalState) getApplication();
		// Instantiate the database (for now no real database - only test data)
		testData = new TestData();
		
		//  get the todos
		toDos = testData.toDos;			
		
		// fill the lists for active and passive tasks
		for (ToDo tmpToDo: toDos) {			
			if(tmpToDo.isActive)				   
			    active_toDos.add(tmpToDo);
			else
				finished_toDos.add(tmpToDo);
		}
		
		// get the locations
		locations = testData.locations;	
		
		// update the global state after all lists have been filled
		//updateGlobalState();
		
	}
	
	// propagate changes to global state
	private void updateGlobalState() {
		gs.setToDoList(toDos);
		gs.setLocationList(locations);
		gs.setActiveToDoList(active_toDos);
		gs.setFinishedToDoList(finished_toDos);
		gs.setChosenToDo(chosenToDo);
	}
	
	private void createGeofences() {
		// Instantiate a Geofence requester
        mGeofenceRequester = new GeofenceRequester(this);

        // Instantiate a Geofence remover
        mGeofenceRemover = new GeofenceRemover(this);
        
        // Instantiate the current List of geofences
		mCurrentGeofences = new  ArrayList<Geofence>();
		
		// Build Geofence-objects and add them to current list of geofences
		// for now expiration time is set to endless
		for(ToDo toDo: active_toDos) {
			for(MyLocation location: toDo.locations) {
				mCurrentGeofences.add(new Geofence.Builder()
							.setRequestId(String.valueOf(toDo.name+" - "+location.name))
							.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
							.setCircularRegion(location.lat, location.lng, location.range)
							.setExpirationDuration(Geofence.NEVER_EXPIRE)
							.build()
							);
			}
		}
		
	}
	
	public void updateLists() {
		active_tasks_lv = (ListView)findViewById(R.id.active_tasks_list);
		finished_tasks_lv = (ListView)findViewById(R.id.finished_tasks_list);
		active_adapter = new ToDoAdapter(this, R.layout.rowlayout_active, active_toDos);
		//ArrayAdapter<String> active_adapter = new ArrayAdapter<String>(this, R.layout.rowlayout, R.id.label, test_values1);
		finished_adapter = new ToDoAdapter(this, R.layout.rowlayout_finished, finished_toDos);
		//ArrayAdapter<String> finished_adapter = new ArrayAdapter<String>(this, R.layout.rowlayout, R.id.label, test_values2);
		active_tasks_lv.setAdapter(active_adapter);
		finished_tasks_lv.setAdapter(finished_adapter);
	} 
	
	public void setListItemClickListener() {
		active_tasks_lv.setOnItemClickListener(new OnItemClickListener() {
			@Override			
			public void onItemClick(AdapterView<?> l, View v, int position, long id) {
				chosenToDo = (ToDo)active_tasks_lv.getItemAtPosition(position);
				String item = chosenToDo.name;
				Toast.makeText(getApplicationContext(), item + " selected", Toast.LENGTH_SHORT).show();
				//v.startAnimation(anim_Move);
				editToDo();
			}
		});
		
		finished_tasks_lv.setOnItemClickListener(new OnItemClickListener() {
			@Override			
			public void onItemClick(AdapterView<?> l, View v, int position, long id) {
				chosenToDo = (ToDo)finished_tasks_lv.getItemAtPosition(position);
				String item = chosenToDo.name;
				Toast.makeText(getApplicationContext(), item + " selected", Toast.LENGTH_SHORT).show();
				
				editToDo();
			}
		});
	}
	
	public void shiftItem(int pos, String list){
		if(list == "active"){
			finished_toDos.add(active_toDos.remove(pos));
		}
		else{
			active_toDos.add(finished_toDos.remove(pos));
		}
		updateListTitle();
		active_adapter.notifyDataSetChanged();
		finished_adapter.notifyDataSetChanged();
	}
	
	public void updateListTitle() {
		active_listTitle = (TextView)findViewById(R.id.active_tasks_title);
		active_listTitle.setText(active_toDos.size() + " tasks to do");
		finished_listTitle = (TextView)findViewById(R.id.finished_tasks_title);
		finished_listTitle.setText(finished_toDos.size() + " tasks finished");
	}
	
	public void deleteItemFromList(long id, int list) {
		Iterator it;
		ToDo toDo;
		MyLocation location;
		switch (list) {
			case TODO_LIST:
				it = toDos.iterator();			
				while(it.hasNext()) {
					toDo = (ToDo)it.next();
					if (toDo.id == id) {
						it.remove();
					}
				}
				break;
			case ACTIVE_TODO_LIST:
				it = active_toDos.iterator();
				while(it.hasNext()) {
					toDo = (ToDo)it.next();
					if (toDo.id == id) {
						it.remove();
					}
				}
				active_adapter.notifyDataSetChanged();
				break;
			case FINISHED_TODO_LIST:
				it = finished_toDos.iterator();
				while(it.hasNext()) {
					toDo = (ToDo)it.next();
					if (toDo.id == id) {
						it.remove();
					}
				}
				finished_adapter.notifyDataSetChanged();
				break;
			case LOCATION_LIST:
				it = locations.iterator();
				while(it.hasNext()) {
					location = (MyLocation)it.next();
					if (location.id == id) {
						it.remove();
					}
				}
				break;
		}
		updateGlobalState();		
	}
	
	
	public void addItemToList(long id, int list) {
		Iterator it;
		ToDo toDo;
		MyLocation location;
		switch (list) {
			case TODO_LIST:
				toDos.add(chosenToDo);
				break;
			case ACTIVE_TODO_LIST:
				active_toDos.add(chosenToDo);
				active_adapter.notifyDataSetChanged();
				break;
			// This case is never used 
			case FINISHED_TODO_LIST:
				finished_toDos.add(chosenToDo);
				finished_adapter.notifyDataSetChanged();
				break;
			case LOCATION_LIST:
				it = chosenToDo.locations.iterator();
				while(it.hasNext()) {
					location = (MyLocation)it.next();
					locations.add(location);
				}
				break;
		}
		updateGlobalState();		
	}
	
	public void openAddNew() {
		// No data to send, to ensure that no other data exist assign NULL here
		chosenToDo = null;
		
		updateGlobalState();
		Intent intent = new Intent(getApplicationContext(), AddNewToDoActivity.class);		
		startActivity(intent);
	}
	
	public void editToDo() {
		updateGlobalState();
		mLocation = mGeofenceRequester.mLocation;
		Intent intent = new Intent(getApplicationContext(), AddNewToDoActivity.class);
		intent.putExtra(ToDoUtils.TODO_ID, chosenToDo.id);
		intent.putExtra(ToDoUtils.TODO_NAME, chosenToDo.name);
		intent.putExtra(ToDoUtils.TODO_DEADLINE, chosenToDo.deadline);
		intent.putExtra(ToDoUtils.TODO_TIMETODEADLINE, chosenToDo.timeToDeadline);
		intent.putExtra(ToDoUtils.TODO_LOCATIONS, chosenToDo.getLocationNames());
		//intent.putExtra(ToDoUtils.TODO_RANGE, chosenToDo.range);
		intent.putExtra(ToDoUtils.TODO_ISACTIVE, chosenToDo.isActive);
		intent.putExtra(ToDoUtils.TODO_COLOR, chosenToDo.color);
		startActivityForResult(intent, ADDNEWTODO);
	}
/*
	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		String msg = "Updated Location: " +
                Double.toString(mLocation.getLatitude()) + "," +
                Double.toString(mLocation.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		mLocation = mLocationClient.getLastLocation();
		// Get the PendingIntent for the request
        mTransitionPendingIntent = getTransitionPendingIntent();
        // Send a request to add the current geofences
        mLocationClient.addGeofences(myGeofences, pendingIntent, this);

		if(mLocation == null) {
			mLocationClient.requestLocationUpdates(mLocationRequest, this);
		} else {
			Toast.makeText(this, "Connected! Location: " + mLocation.getLatitude() + ", " + mLocation.getLongitude(), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();

	}
*/

	 private boolean servicesConnected() {

	        // Check that Google Play services is available
	        int resultCode =
	                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

	        // If Google Play services is available
	        if (ConnectionResult.SUCCESS == resultCode) {

	            // In debug mode, log the status
	            Log.d(GeofenceUtils.APPTAG, getString(R.string.play_services_available));

	            // Continue
	            return true;

	        // Google Play services was not available for some reason
	        } else {

	            // Display an error dialog
//	            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
//	            if (dialog != null) {
//	                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
//	                errorFragment.setDialog(dialog);
//	                errorFragment.show(getSupportFragmentManager(), GeofenceUtils.APPTAG);
//	            }
	        	Toast.makeText(getApplicationContext(), "Play services was not available", Toast.LENGTH_SHORT).show();
	            return false;
	        }
	    }
	 
//	 public static class ErrorDialogFragment extends DialogFragment {
//
//	        // Global field to contain the error dialog
//	        private Dialog mDialog;
//
//	        /**
//	         * Default constructor. Sets the dialog field to null
//	         */
//	        public ErrorDialogFragment() {
//	            super();
//	            mDialog = null;
//	        }
//
//	        /**
//	         * Set the dialog to display
//	         *
//	         * @param dialog An error dialog
//	         */
//	        public void setDialog(Dialog dialog) {
//	            mDialog = dialog;
//	        }
//
//	        /*
//	         * This method must return a Dialog to the DialogFragment.
//	         */
//	        @Override
//	        public Dialog onCreateDialog(Bundle savedInstanceState) {
//	            return mDialog;
//	        }
//	    }

}
