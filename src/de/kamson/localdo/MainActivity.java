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
import de.kamson.data.DBHandler;
import de.kamson.data.MyConstants;
import de.kamson.data.MyLocation;
import de.kamson.data.TestData;
import de.kamson.data.Task;
import de.kamson.data.MyConstants;
import android.location.Criteria;
import android.location.Location;

import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
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
    // Position is requested in GeoRequester
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
	TaskAdapter active_adapter;
	TaskAdapter finished_adapter;
	List<Task> tasks;	
	List<Task> active_tasks;
	List<Task> finished_tasks;
	List<MyLocation> locations;
	
	static final int TASKS_LIST = 1;
	static final int ACTIVE_TASKS_LIST = 2;
	static final int FINISHED_TASKS_LIST = 3;
	static final int LOCATION_LIST = 4;
	
	// Database variables
	DBHandler dbHandler;
	
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
   
	
	public Task chosenTask;
	public Animation anim_Move;
	public TestData testData;

	
	
		
		
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 		
		setContentView(R.layout.activity_main);		
		
		// To follow lifecycle of the activity
		//Toast.makeText(this, "Main onCreate called", Toast.LENGTH_SHORT).show();
		
		// Loads tasks and locations from database
		loadDataFromDB();
		
		// Build a list of geofences build from locations that are bound to an active task
		createGeofencesList();
		
		// 
		setDeadlineAlarm();
		// Build the listviews for active and finished task lists
		updateListViews();
		
		setListItemClickListener();		
		
		//anim_Move = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move);
		
		updateListTitle();
		
		/*
		// Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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
			addNewTask();
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
		//Toast.makeText(this, "Main onStart called", Toast.LENGTH_SHORT).show();
		//mLocationClient.connect();
		
		/*
         * Record the request as an ADD. If a connection error occurs, the app can automatically restart 
         * the add request if Google Play services can fix the error
         */
		mRequestType = GeofenceUtils.REQUEST_TYPE.ADD;
		
		 /*
         * Check for Google Play services. Do this after setting the request type. If connecting to Google Play services
         * fails, onActivityResult is eventually called, and it needs to know what type of request was in progress.
         */
		if (servicesConnected()) {
			// Start the request. Fail if there's already a request in progress
	        try {
	            // Try to add geofences
	        	mGeofenceRequester.addGeofences(mCurrentGeofences);
	        } catch (UnsupportedOperationException e) {
	            // Notify user that previous request hasn't finished.
	            Toast.makeText(this, R.string.add_geofences_already_requested_error, Toast.LENGTH_SHORT).show();
	        }
		}
	}
	
	public void onResume() {
		super.onResume();
		//locationmanager.requestLocationUpdates(provider, 400, 1, this);
		if (mGeofenceRequester != null) {
			mGeofenceRequester.getLocation();
		}
		//Toast.makeText(this, "Main onResume called", Toast.LENGTH_SHORT).show();
	}
	
	public void onPause() {
		super.onPause();
		//locationmanager.removeUpdates(this);
		// should we do some saving operations here?
		//Toast.makeText(this, "Main onPause called", Toast.LENGTH_SHORT).show();
	}
	
	public void onStop() {		
        /*
         * After disconnect() is called, the client is
         * considered "dead".
         */
        //mLocationClient.disconnect();		
		//Toast.makeText(this, "Main onStop called", Toast.LENGTH_SHORT).show();
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
                
            case MyConstants.REQUESTCODE_SETTASK: {
            	
            	switch(resultCode) {
            	
            		case RESULT_OK:
            			// get Data from AddNewToDo
            			//intent.getExtras().get()
            			loadDataFromDB();
            			updateListViews();
            			updateListTitle();
            		break;
            		case RESULT_FIRST_USER:
            			// Abuse this result code to delete elements
            			loadDataFromDB();
            			updateListViews();
            			updateListTitle();
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
	
	private void loadDataFromDB() {
		
		// Get Global State Singleton
		//gs = (GlobalState) getApplication();
		// Get the handler for the database 
		dbHandler = new DBHandler(this);
		
		// Instantiate database
		dbHandler.open();
		
		// Instantiate testdata
		//testData = new TestData();
		
		
		//  Read all tasks from database 
		tasks = dbHandler.getAllTasks();			
		
		// No task found
		if (tasks == null) {
			tasks = new ArrayList<Task>();
		}
		// Fill the lists for active and passive tasks from all tasks list
		
		// Reset the lists
		active_tasks = new ArrayList<Task>();
		finished_tasks = new ArrayList<Task>();
		for (Task tmpTask: tasks) {			
			if(tmpTask.isActive)				   
			    active_tasks.add(tmpTask);
			else
				finished_tasks.add(tmpTask);
		}
		
		// Get the locations
		locations = dbHandler.getAllLocations();	
		
		// No locations found
		if (locations == null) {
			locations = new ArrayList<MyLocation>();
		}
		
		List<String> taskloc = dbHandler.getAllTasksLocations();
		for(Task t:tasks) {
			Log.d("DB Tasks", ""+t.id+" "+t.name+" "+t.deadline+" "+t.deadline_alert+" "+t.isActive+" "+t.color+" "+t.notes);
		}
		for(MyLocation l:locations) {
			Log.d("DB Locations", ""+l.id+" "+l.name+" "+l.lat+" "+l.lng+" "+l.range+" "+l.isAnonymous);			
		}
		for(String s:taskloc) {
			Log.d("DB TtoL", s);
		}
			
		
	}
	
	private void createGeofencesList() {
		// Instantiate a Geofence requester
        mGeofenceRequester = new GeofenceRequester(this);

        // Instantiate a Geofence remover
        mGeofenceRemover = new GeofenceRemover(this);
        
        // Instantiate the current List of geofences
		mCurrentGeofences = new  ArrayList<Geofence>();
		
		// Build Geofence-objects and add them to current list of geofences
		// for now expiration time is set to endless
		List<MyLocation> task_locations;
		for(Task task: active_tasks) {
			task_locations = dbHandler.getLocationsToTask(task.id);
			for(MyLocation location: task_locations) {
				// Radius of 0.0 not allowed so we have to sort out location without range 0
				if (location.range != 0) {
					mCurrentGeofences.add(new Geofence.Builder()
								.setRequestId(String.valueOf(task.name+" - "+location.name))
								.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
								.setCircularRegion(location.lat, location.lng, location.range)
								.setExpirationDuration(Geofence.NEVER_EXPIRE)
								.build()
								);
				}
			}
		}
		
	}
	
	public void updateListViews() {
		// Get the listviews
		active_tasks_lv = (ListView)findViewById(R.id.active_tasks_list);
		finished_tasks_lv = (ListView)findViewById(R.id.finished_tasks_list);
		
		// Instantiate the adapters for the listviews
		active_adapter = new TaskAdapter(this, R.layout.rowlayout_active, active_tasks, dbHandler, mGeofenceRequester);
		finished_adapter = new TaskAdapter(this, R.layout.rowlayout_finished, finished_tasks, dbHandler, mGeofenceRequester);
		
		// Assign the adapters to the corresponding listviews
		active_tasks_lv.setAdapter(active_adapter);
		finished_tasks_lv.setAdapter(finished_adapter);
	} 
	
	public void setListItemClickListener() {
		// For the active tasks list
		active_tasks_lv.setOnItemClickListener(new OnItemClickListener() {
			// Call activity to edit chosen active task
			@Override			
			public void onItemClick(AdapterView<?> l, View v, int position, long id) {
				chosenTask = (Task)active_tasks_lv.getItemAtPosition(position);
				//v.startAnimation(anim_Move);
				editTask();
			}
		});
		
		// For the finished tasks list
		finished_tasks_lv.setOnItemClickListener(new OnItemClickListener() {
			// Call activity to edit chosen finished task
			@Override			
			public void onItemClick(AdapterView<?> l, View v, int position, long id) {
				chosenTask = (Task)finished_tasks_lv.getItemAtPosition(position);
				editTask();
			}
		});
	}
	
	public void shiftItem(int pos, int list){
		// Remove task from active list and add to finished list
		Task t;
		if(list == ACTIVE_TASKS_LIST){
			// DB update
			t = (Task)active_tasks.get(pos);
			dbHandler.updateTasks(t.id, t.name, t.deadline, t.deadline_alert, 0, t.color, t.notes);
			// Visual update
			finished_tasks.add(active_tasks.remove(pos));
		}
		// Remove task from finished list and add to active list
		else{
			// DB update
			t = (Task)finished_tasks.get(pos);
			dbHandler.updateTasks(t.id, t.name, t.deadline, t.deadline_alert, 1, t.color, t.notes);
			// Visual update
			active_tasks.add(finished_tasks.remove(pos));
		}
		updateListTitle();
		active_adapter.notifyDataSetChanged();
		finished_adapter.notifyDataSetChanged();
	}
	
	public void updateListTitle() {
		active_listTitle = (TextView)findViewById(R.id.active_tasks_title);
		active_listTitle.setText(active_tasks.size() + " tasks to do");
		View v = (View)findViewById(R.id.invisible_space);
		if (active_tasks.size() == 0) {			
			v.setVisibility(View.VISIBLE);
		}
		else
			v.setVisibility(View.GONE);
		finished_listTitle = (TextView)findViewById(R.id.finished_tasks_title);
		finished_listTitle.setText(finished_tasks.size() + " tasks finished");
	}
	
	
	
	// propagate changes to global state
//		private void updateGlobalState() {
//			gs.setToDoList(tasks);
//			gs.setLocationList(locations);
//			gs.setActiveToDoList(active_tasks);
//			gs.setFinishedToDoList(finished_tasks);
//			gs.setChosenToDo(chosenToDo);
//		}
		
	public void addNewTask() {
		// No data to send, to ensure that no other data exist assign NULL here
		chosenTask = null;		
		
		Intent intent = new Intent(getApplicationContext(), SetTaskActivity.class);
		intent.putExtra(MyConstants.OPERATING_MODE, MyConstants.MODE_ADD);
		startActivityForResult(intent, MyConstants.REQUESTCODE_SETTASK);
	}
	
	public void editTask() {
		
		// Create intent for new activity to edit task
		Intent intent = new Intent(getApplicationContext(), SetTaskActivity.class);
		
		// Set operating mode for SetTaskActivity 
		intent.putExtra(MyConstants.OPERATING_MODE, MyConstants.MODE_EDIT);
		
		// Only send the ID of the chosen task and reload the task in the other activity
		intent.putExtra(MyConstants.TASK_ID, chosenTask.id);		
		startActivityForResult(intent, MyConstants.REQUESTCODE_SETTASK);
	}

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
	 
	 public void setDeadlineAlarm() {		 
			for(Task task: active_tasks) {				
					if (task.deadline_alert != 0) {
					
						Intent intentAlarm = new Intent(this, DeadlineAlarmReceiver.class);
						intentAlarm.putExtra(MyConstants.TASK_NAME, task.name);
			            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			            
			            // Time is calculated 
			            long time = task.deadline-task.deadline_alert;
			           
			            // Set the alarm for particular time
			            alarmManager.set(AlarmManager.RTC_WAKEUP,time, PendingIntent.getBroadcast(this,1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
				}
			}
				
	 }
	 
	 public void clearTables(View v) {
		 //dbHandler.clearTasks();
		// dbHandler.clearLocations();
		 dbHandler.clearTasks_Locations();
		 Toast.makeText(getApplicationContext(), "Table cleared", Toast.LENGTH_SHORT).show();
	 }
	 
	 public void sortTasks() {
		 
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
