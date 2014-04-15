package de.kamson.localdo;

import java.util.ArrayList;
import java.util.List;

import de.kamson.data.Location;
import de.kamson.data.ToDo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
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
	
	private ListView active_tasks_lv;
	private ListView finished_tasks_lv;
	private TextView active_listTitle;
	private TextView finished_listTitle;	
	private ToDoAdapter active_adapter;
	private ToDoAdapter finished_adapter;
	ToDo[] toDos;
	ToDo[] finished_toDos;
	private List<ToDo> active_tasks_values = new ArrayList<ToDo>();
	private List<ToDo> finished_tasks_values = new ArrayList<ToDo>();	
	public final static String TODO_ID = "id";
	public final static String TODO_NAME = "name";
	public final static String TODO_LOCATION = "location";
	public final static String TODO_RANGE = "range";
	public final static String TODO_COLOR = "color";
	public ToDo chosenToDo;
	public Animation anim_Move;

	
	private void createTestValues() {
		/*
		Location[] locations = new Location[] {
				new Location("Home", 0.0, 0.0),
				new Location("University", 10.0, 10.0),
				new Location("Library", 20.0, 20.0)};
		*/
		
		toDos = new ToDo[]{
				new ToDo(0,"Return book", "Library", 5.0, true, 0xffff0000), 
				new ToDo(1,"Submit paper", "University", 1.0, true, 0xffffff00), 
				new ToDo(2,"Call mom", "Home", 1.0, true, 0xff00ff00), 
				new ToDo(3,"Watch Minority Report", "Home", 0.5, false, 0xff0000ff),
				new ToDo(4,"Check Mailbox", "Home", 0.5, false, 0xff0000ff)};
		
		for (int i = 0; i < toDos.length; ++i) {
		      active_tasks_values.add(toDos[i]);
		    }
		
		finished_toDos = new ToDo[]{
				new ToDo(10,"Get book", "Library", 5.0, true, 0xffff0000), 
				new ToDo(11,"Meet Assistant", "University", 1.0, true, 0xffffff00), 
				new ToDo(12,"Feed cat", "Home", 1.0, true, 0xff00ff00), 
				new ToDo(13,"Turn off heater", "Home", 0.5, false, 0xff0000ff),
				new ToDo(14,"Turn on heater", "Home", 0.5, false, 0xff0000ff)};
		
		for (int i = 0; i < toDos.length; ++i) {
		      finished_tasks_values.add(finished_toDos[i]);
		    }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 		
		setContentView(R.layout.activity_main);
		createTestValues();
		updateLists();
		setListItemClickListener();		
		//anim_Move = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move);
		updateListTitle();
		
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
	public void updateLists() {
		active_tasks_lv = (ListView)findViewById(R.id.active_tasks_list);
		finished_tasks_lv = (ListView)findViewById(R.id.finished_tasks_list);
		active_adapter = new ToDoAdapter(this, R.layout.rowlayout_active, active_tasks_values);
		//ArrayAdapter<String> active_adapter = new ArrayAdapter<String>(this, R.layout.rowlayout, R.id.label, test_values1);
		finished_adapter = new ToDoAdapter(this, R.layout.rowlayout_finished, finished_tasks_values);
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
				Toast.makeText(getApplicationContext(), item + " selected", Toast.LENGTH_LONG).show();
				//v.startAnimation(anim_Move);
				editToDo();
			}
		});
		
		finished_tasks_lv.setOnItemClickListener(new OnItemClickListener() {
			@Override			
			public void onItemClick(AdapterView<?> l, View v, int position, long id) {
				chosenToDo = (ToDo)finished_tasks_lv.getItemAtPosition(position);
				String item = chosenToDo.name;
				Toast.makeText(getApplicationContext(), item + " selected", Toast.LENGTH_LONG).show();
				
				editToDo();
			}
		});
	}
	
	public void shiftItem(int pos, String list){
		if(list == "active"){
			finished_tasks_values.add(active_tasks_values.remove(pos));
		}
		else{
			active_tasks_values.add(finished_tasks_values.remove(pos));
		}
		updateListTitle();
		active_adapter.notifyDataSetChanged();
		finished_adapter.notifyDataSetChanged();
	}
	
	public void updateListTitle() {
		active_listTitle = (TextView)findViewById(R.id.active_tasks_title);
		active_listTitle.setText(active_tasks_values.size() + " tasks to do");
		finished_listTitle = (TextView)findViewById(R.id.finished_tasks_title);
		finished_listTitle.setText(finished_tasks_values.size() + " tasks finished");
	}
	
	public void openAddNew() {
		Intent intent = new Intent(getApplicationContext(), AddNewToDoActivity.class);		
		startActivity(intent);
	}
	
	public void editToDo() {
		Intent intent = new Intent(getApplicationContext(), AddNewToDoActivity.class);
		intent.putExtra(TODO_ID, chosenToDo.id);
		intent.putExtra(TODO_NAME, chosenToDo.name);
		intent.putExtra(TODO_LOCATION, chosenToDo.location);
		intent.putExtra(TODO_RANGE, chosenToDo.range);
		intent.putExtra(TODO_COLOR, chosenToDo.color);
		startActivity(intent);
	}

}
