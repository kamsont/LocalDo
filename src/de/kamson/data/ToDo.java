package de.kamson.data;

public class ToDo {
	public long id;
	public String name;
	public String location;
	public double range;
	public boolean active;
	public int color;
	
	
	public ToDo(long id, String name, String location, double range, boolean active, int color) {
		this.id = id;
		this.name = name;
		this.location = location;
		this.range = range;
		this.active = active;
		this.color = color;
	}
	
	public boolean equals(Object o) {
		ToDo test = (ToDo) o;
		if (test.id == this.id)
			return true;
		else
			return false;
	}
	
}
