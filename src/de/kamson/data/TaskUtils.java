package de.kamson.data;

import java.util.HashMap;

public class TaskUtils {
	public final static String TASK_ID = "task_id";
	public final static String TASK_NAME = "task_name";
	public final static String TASK_DEADLINE = "deadline";
	public final static String TASK_DEADLINE_ALERT = "timeToDeadline";
	public final static String TASK_ISACTIVE = "isActive";
	public final static String TASK_COLOR = "color";
	public final static String TASK_NOTES = "notes";
	
	public final static int RED = 0xffff0000;
	public final static int BLUE = 0xff0000ff;
	public final static int YELLOW = 0xffffff00;
	public final static int GREEN = 0xff00ff00;
	public final static int NO_COLOR = 0x00000000; //alpha set to 0, color does not matter
	
	// missing binding to values/strings or value/colors 
	public final static HashMap<Integer, String> colorStrings = new HashMap<Integer, String>();
	static{ 
		colorStrings.put(RED, "Red");
		colorStrings.put(BLUE, "Blue");
		colorStrings.put(YELLOW, "Yellow");
		colorStrings.put(GREEN, "Green");
		colorStrings.put(NO_COLOR, "No color");
		}
}
