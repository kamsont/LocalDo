package de.kamson.localdo;

import java.util.ArrayList;
import java.util.List;

import de.kamson.data.ToDo;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



public class ToDoAdapter extends ArrayAdapter<ToDo> {
	
	Context context;
	int resource;
	List<ToDo> objects;
	
	public ToDoAdapter(Context context, int resource, List<ToDo> objects) {
		super(context, resource, objects);
		
		this.context = context;
		this.resource = resource;
		this.objects = (ArrayList<ToDo>)objects;
	}
	
	public View getView(final int position, View convertView, ViewGroup parent) {
		// try to reuse old list entries before creating new ones
		if(convertView == null) {
			LayoutInflater inflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflator.inflate(resource, parent, false);
		}
		
		// List entry 
		final CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox1);
		TextView title = (TextView) convertView.findViewById(R.id.label);
		TextView infos = (TextView) convertView.findViewById(R.id.label2);
		View colorBar = (View) convertView.findViewById(R.id.colorBar);		
		
				
		// get the ToDo from ToDo-List to show in list
		ToDo toDo = objects.get(position);		
				
		// set Title
		title.setText(toDo.name);
		
		// set Location-Name, for now we are just working with one location so take the first element from locationID-array
		// TODO change from location-ID to Location Name
		String[] locationNames = toDo.getLocationNames();
		if (locationNames == null) {
			infos.setText("no location bound to task");
		}
		else if (locationNames.length == 1) {
			infos.setText(locationNames[0]);
		}
		else {
			infos.setText(locationNames[0]+" and other locations");
		}
				
		// set the color of the bar to the right of each list entry
		colorBar.setBackgroundColor(toDo.color); 
		
		// OnClickListener for the Checkbox to shift list items from active list to passive list
		cb.setOnClickListener(new View.OnClickListener(){			
			
			public void onClick(View v){
				
				// Toast to be deleted for final version?
				Toast.makeText(getContext(), position + " selected", Toast.LENGTH_SHORT).show();
				
				// checked-status determines in which list to move
				if((cb.isChecked())){
					((MainActivity)context).shiftItem(position, "active");
					cb.setChecked(false);
					
				}
				else {
					((MainActivity)context).shiftItem(position, "finished");
					cb.setChecked(true);	
				}
				
			}
		});
		
		return convertView;
	}

}
