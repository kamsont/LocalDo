package de.kamson.localdo;

import java.util.List;

import de.kamson.data.MyLocation;
import de.kamson.data.ToDo;

import android.app.Application;
/*
 * This class keeps the global state as singleton
 */
public class GlobalState extends Application{
	
	private List<ToDo> toDos;
	private List<MyLocation> locations;
	private List<ToDo> active_toDos;
	private List<ToDo> finished_toDos;
	private ToDo chosenToDo;
	private MyLocation newLocation;	
	
	
	public ToDo getToDoFromList(long id) {
		if (!(toDos == null)) {
			for(ToDo toDo: toDos) {
				if (toDo.id == id) {
					return toDo;
				}
			}
		}
		return null;
	}
	
	public MyLocation getLocationFromList(long id) {
		if (!(locations == null)) {
			for(MyLocation location: locations) {
				if (location.id == id) {
					return location;
				}
			}
		}
		return null;
	}
	
	
	public List<ToDo> getToDos() {
		return toDos;
	}
	
	public List<MyLocation> getLocations() {
		return locations;
	}
	
	public List<ToDo> getActive_toDos() {
		return active_toDos;
	}
	
	public List<ToDo> getFinished_toDos() {
		return finished_toDos;
	}
	
	public ToDo getChosenToDo() {
		return chosenToDo;
	}
	
	public MyLocation getNewLocation() {
		return newLocation;
	}
	
	public void setToDoList(List<ToDo> toDos) {
		this.toDos = toDos;
	}
	
	public void setLocationList(List<MyLocation> locations) {
		this.locations = locations;
	}
	
	public void setActiveToDoList(List<ToDo> active_toDos) {
		this.active_toDos = active_toDos;
	}
	
	public void setFinishedToDoList(List<ToDo> finished_toDos) {
		this.finished_toDos = finished_toDos;
	}
	
	public void setChosenToDo(ToDo toDo) {
		chosenToDo = toDo;
	}
	
	public void setNewLocation(MyLocation location) {
		newLocation = location;
	}
	
}
