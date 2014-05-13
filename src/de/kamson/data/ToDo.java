package de.kamson.data;

import java.util.ArrayList;
import java.util.List;

public class ToDo {
	public long id;
	public String name;
	public long deadline;
	public long timeToDeadline;
	public List<MyLocation> locations;
	//double range;
	public boolean isActive;
	public int color;
	
	
	public ToDo(long id, String name, long deadline, long timeToDeadline, List<MyLocation> locations, boolean isActive, int color) {
		this.id = id;
		this.name = name;
		this.deadline = deadline;
		this.timeToDeadline = timeToDeadline;
		this.locations = locations;
		//this.range = range;
		this.isActive = isActive;
		this.color = color;
	}
	
	public boolean equals(Object o) {
		ToDo test = (ToDo) o;
		if (test.id == this.id)
			return true;
		else
			return false;
	}
	
	public String[] getLocationNames() {		
		if (!locations.isEmpty()) {
		List<String> names = new ArrayList<String>();		
		for(MyLocation location: locations) {
			names.add(location.name);
		}
		return names.toArray(new String[names.size()]);
		}
		else {
			return null;
		}
	}
	
	
	
}
