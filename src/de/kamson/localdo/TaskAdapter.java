package de.kamson.localdo;

import java.util.ArrayList;
import java.util.List;

import de.kamson.data.DBHandler;
import de.kamson.data.MyLocation;
import de.kamson.data.Task;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Helper class for displaying the elements of the task-lists in a listview. 
 * The database handler is received as parameter in the constructor to get access to the locations that are bound to a task.
 *
 */
public class TaskAdapter extends ArrayAdapter<Task> {
	
	Context context;
	int resource;
	List<Task> objects;
	DBHandler dbHandler;
	// We want to request location updates from here
	GeofenceRequester gfr;
	
	
	public TaskAdapter(Context context, int resource, List<Task> objects, DBHandler dbHandler, GeofenceRequester gfr) {
		super(context, resource, objects);
		
		this.context = context;
		this.resource = resource;
		this.objects = (ArrayList<Task>)objects;
		this.dbHandler = dbHandler;	
		this.gfr = gfr;
	}
	
	public View getView(final int position, View convertView, ViewGroup parent) {
		// Try to reuse old list item view before creating a new one
		if(convertView == null) {
			LayoutInflater inflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflator.inflate(resource, parent, false);
		}
		
		// Get the view-elements of an item in a list
		final CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox1);
		TextView title = (TextView) convertView.findViewById(R.id.label);
		TextView infos = (TextView) convertView.findViewById(R.id.label2);
		View colorBar = (View) convertView.findViewById(R.id.colorBar);		
		
				
		// Get the Task from task-list to show in list
		Task task = objects.get(position);
		
		// Get the locations bound to this task
		List<MyLocation> task_locations = dbHandler.getLocationsToTask(task.id);
		
		// Distances from my position
		List<Integer> distances = new ArrayList<Integer>();
		
		// Calculate distances
		Location location;
		gfr.getLocation();
		if (MainActivity.mLocation != null) {
			for(MyLocation mLoc:task_locations) {
				location = new Location("");
				location.setLatitude(mLoc.lat);
				location.setLongitude(mLoc.lng);
				distances.add((int)location.distanceTo(MainActivity.mLocation));
			}
		}
		
		// Sort location list by distance - same method as in main for tasks
		if (distances.size() != 0) {
			List<MyLocation> tmpList = new ArrayList<MyLocation>();
			 int pos = 0;
			 int length = task_locations.size();
			 for (int i = 0; i<length;i++) {
				 
				 // Get nearest location
				 pos = MainActivity.getMinFromList(distances);
				 
				 // Rebuild the list - nearest on top
				 tmpList.add(i, task_locations.remove(pos));
				 distances.remove(pos);
				 
			 }
		 
			 task_locations = tmpList;
		}
		
		// Set Title
		title.setText(task.name);
		
		// Set location name
		// No location exist
		int nameCount = task_locations.size();
		if (nameCount == 0) {
			infos.setText("no location bound to task");
		}
		else {
			int i = 0;
			String text = "";
			for (MyLocation mLoc:task_locations) {
				text += mLoc.name;
				if (distances.size() != 0) {
					text += " ("+distances.get(i)+"m)";
				}
				if (i<task_locations.size()-1) {
					text += "; ";
				}
				i++;
				
			}
			infos.setText(text);
		}
				
		// Set the color of the bar to the right of each list item
		colorBar.setBackgroundColor(task.color); 
		
		// OnClickListener for the Checkbox to shift list items from active list to passive list preferred over
		// onCheckedChangedListener because we are also performing a check operation on the checkbox and do not want the
		// listener to be triggered aggain
		cb.setOnClickListener(new View.OnClickListener(){			
			
			public void onClick(View v){
				
				// Checkbox was checked; list item belonged to finished-task-list
				// Uncheck it when moved to active-task-list
				if((cb.isChecked())){
					((MainActivity)context).shiftItem(position, MainActivity.ACTIVE_TASKS_LIST);
					cb.setChecked(false);
					
				}
				// CheckBox was unchecked; list item belonged to active-task-list
				// Check it when moved to finished-task-list
				else {
					((MainActivity)context).shiftItem(position, MainActivity.FINISHED_TASKS_LIST);
					cb.setChecked(true);	
				}
				
			}
		});
		
		return convertView;
	}

}
