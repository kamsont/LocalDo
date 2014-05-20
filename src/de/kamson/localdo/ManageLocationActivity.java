package de.kamson.localdo;

import java.util.ArrayList;
import java.util.List;


import de.kamson.data.DBHandler;
import de.kamson.data.MyConstants;
import de.kamson.data.MyLocation;
import de.kamson.data.Task;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


/**
 * To manage the locations undependent from tasks
 * @author all
 *
 */
public class ManageLocationActivity extends ListActivity {

	private ArrayAdapter<MyLocation> adapter;
	private DBHandler dbHandler;
	List<MyLocation> locations;
	MyLocation location;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_locations);

		// Get the handler for the database 
		dbHandler = new DBHandler(this);
		
		// Instantiate database
		dbHandler.open();		
	
		updateList();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		location = adapter.getItem(position);
		openSetLocation(MyConstants.MODE_EDIT);	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_locations, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_new:
			openSetLocation(MyConstants.MODE_ADD);
			return true;
		/*
		case R.id.action_settings:
			return true;
		*/
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == MyConstants.REQUESTCODE_SETLOCATION) {
			switch (resultCode) {
			case RESULT_OK:	
				updateList();
				break;
			case RESULT_CANCELED:
				
				break;
			}
		}
	}
	
	public void updateList() {
		locations = dbHandler.getAllLocations();		
		adapter = new ArrayAdapter<MyLocation>(this, android.R.layout.simple_list_item_1, locations);
		setListAdapter(adapter);
	}	
	
	private void openSetLocation(int mode) {
								
		Intent intent = new Intent(getApplicationContext(), SetLocationActivity.class);
		intent.putExtra(MyConstants.OPERATING_MODE, mode);
		if (mode == MyConstants.MODE_EDIT)
			intent.putExtra(MyConstants.LOCATION_ID, location.id);
		else
			intent.putExtra(MyConstants.LOCATION_ID, -1);
		startActivityForResult(intent, MyConstants.REQUESTCODE_SETLOCATION);
	}
}
