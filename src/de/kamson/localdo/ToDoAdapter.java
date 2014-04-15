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
import android.widget.TextView;
import android.widget.Toast;



public class ToDoAdapter extends ArrayAdapter<ToDo> {
	
	Context context;
	int resource;
	List<ToDo> objects;
	
	public ToDoAdapter(Context context, int resource, List<ToDo> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.resource = resource;
		this.objects = (ArrayList<ToDo>)objects;
	}
	
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater inflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflator.inflate(resource, parent, false);
		}
		final CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox1);
		TextView title = (TextView) convertView.findViewById(R.id.label);
		TextView infos = (TextView) convertView.findViewById(R.id.label2);
		View colorBar = (View) convertView.findViewById(R.id.colorBar);
		
		ToDo toDo = objects.get(position);
		cb.setOnClickListener(new View.OnClickListener(){
			
			
			public void onClick(View v){
				Toast.makeText(getContext(), position + " selected", Toast.LENGTH_LONG).show();
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
		title.setText(toDo.name);
		infos.setText(toDo.location);
		colorBar.setBackgroundColor(toDo.color); 
		return convertView;
	}

}
