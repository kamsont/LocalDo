package de.kamson.data;

public class Task {
	
	public long id;
	public String name;
	public long deadline;
	public long deadline_alert;
	public boolean isActive;
	public int color;
	public String notes;
	
	public Task(long id, String name, long deadline, long deadline_alert, boolean isActive, int color, String notes) {
		this.id = id;
		this.name = name;
		this.deadline = deadline;
		this.deadline_alert = deadline_alert;
		this.isActive = isActive;
		this.color = color;
		this.notes = notes;
	}
}
