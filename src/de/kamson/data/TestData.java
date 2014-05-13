package de.kamson.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.android.gms.location.Geofence;

public class TestData {

	public List<MyLocation> locations;
	public List<ToDo> toDos;	
	public List<Geofence> geofences; //abspeichern oder onstart zusammenbauen?
	
	public TestData() {
		locations = new ArrayList<MyLocation>(); 
		locations.add(new MyLocation(0, "BWL Bibliothek", 49.482588, 8.464097, 100));
		locations.add(new MyLocation(1, "Schloss", 49.483606, 8.462477, 150));
		locations.add(new MyLocation(2, "Paradeplatz", 49.487202, 8.466404, 100));
		locations.add(new MyLocation(3, "Studienbüro", 49.484477, 8.463743, 100));
		locations.add(new MyLocation(4, "Café Sammo A3", 49.485787, 8.461233, 100));
		
		List<MyLocation> locationAsParam = new ArrayList<MyLocation>();
		
		toDos = new ArrayList<ToDo>();
		locationAsParam.add(locations.get(0));
		toDos.add(new ToDo(0, "Return book", 1400503759000l, 0, locationAsParam, true, ToDoUtils.RED)); 
		locationAsParam = new ArrayList<MyLocation>();
		locationAsParam.add(locations.get(3));
		toDos.add(new ToDo(1, "Pay college tuitions", 1400162400000l, 0, locationAsParam, true, ToDoUtils.YELLOW));
		locationAsParam = new ArrayList<MyLocation>();
		locationAsParam.add(locations.get(2));
		toDos.add(new ToDo(2, "Buy gift", -1, -1, locationAsParam, false, ToDoUtils.GREEN));
		locationAsParam = new ArrayList<MyLocation>();
		locationAsParam.add(locations.get(4));
		toDos.add(new ToDo(3, "Try new Coffee", 1400162400000l, 0, locationAsParam, false, ToDoUtils.NO_COLOR));
		locationAsParam = new ArrayList<MyLocation>();
		locationAsParam.add(locations.get(0));
		locationAsParam.add(locations.get(1));
		locationAsParam.add(locations.get(4));
		toDos.add(new ToDo(4, "Meet group member",1400857200000l, 0, locationAsParam, true, ToDoUtils.RED));
//		locationAsParam = new ArrayList<MyLocation>();
//		locationAsParam.add(locations.get(4));
//		toDos.add(new ToDo(4, "Meet group member",locationAsParam, true, ToDoColor.RED));
//		locationAsParam = new ArrayList<MyLocation>();
		locationAsParam.add(locations.get(0));
		toDos.add(new ToDo(5, "Get book", -1, -1, locationAsParam, false, ToDoUtils.BLUE));
		locationAsParam = new ArrayList<MyLocation>();		
		toDos.add(new ToDo(6, "Meet Assistant", -1, -1, locationAsParam, true, ToDoUtils.RED));
		locationAsParam = new ArrayList<MyLocation>();
		locationAsParam.add(locations.get(3));
		toDos.add(new ToDo(6, "Meet Assistant", -1, -1, locationAsParam, true, ToDoUtils.RED));
		locationAsParam = new ArrayList<MyLocation>();
		locationAsParam.add(locations.get(2));
		toDos.add(new ToDo(7, "Get milk", -1, -1, locationAsParam, true, ToDoUtils.YELLOW)); 
		locationAsParam = new ArrayList<MyLocation>();
		locationAsParam.add(locations.get(0));
		locationAsParam.add(locations.get(1));
		locationAsParam.add(locations.get(2));
		locationAsParam.add(locations.get(3));
		locationAsParam.add(locations.get(4)); 
		toDos.add(new ToDo(8, "Learn for exams!", -1, -1, locationAsParam, true, ToDoUtils.RED));
//		locationAsParam = new ArrayList<MyLocation>();
//		locationAsParam.add(locations.get(1));
//		toDos.add(new ToDo(8, "Learn for exams!", locationAsParam, true, ToDoColor.RED));
		locationAsParam = new ArrayList<MyLocation>();
		locationAsParam.add(locations.get(3));
		toDos.add(new ToDo(9, "Get new ECUM", -1, -1, locationAsParam, false, ToDoUtils.GREEN));
	}	
	
	
	
}
