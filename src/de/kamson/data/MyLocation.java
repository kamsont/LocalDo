package de.kamson.data;



public class MyLocation {	
	public long id;
	public String name;
	public double lat;
	public double lng;
	public int range;
	public boolean isAnonymous;
	
	public MyLocation(long id, String name, double lat, double lng, int range, boolean isAnonymous) {		
		this.id = id;
		this.name = name;
		this.lat = lat;
		this.lng = lng;
		this.range = range;
		this.isAnonymous = isAnonymous;
	}
	
	public MyLocation() {
		this.id = -1;
	}
	
	public boolean equals(Object location) {
		if (this.id == ((MyLocation)location).id)
			return true;
		else
			return false;
	}
	
	public String toString() {
		return this.name;
	}
}
