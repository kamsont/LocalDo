package de.kamson.data;



public class MyLocation {	
	public long id;
	public String name;
	public double lat;
	public double lng;
	public int range;
	
	public MyLocation(long id, String name, double lat, double lng, int range) {		
		this.id = id;
		this.name = name;
		this.lat = lat;
		this.lng = lng;
		this.range = range;
	}
	
	public MyLocation() {
		this.id = -1;
	}
	
	public boolean equals(MyLocation location) {
		if (this.id == location.id)
			return true;
		else
			return false;
	}
}
