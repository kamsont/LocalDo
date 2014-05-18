package de.kamson.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyConstants {
	public final static String TASK_ID = "task_id";
	public final static String TASK_NAME = "task_name";
	public final static String TASK_DEADLINE = "deadline";
	public final static String TASK_DEADLINE_ALERT = "deadline_alert";
	public final static String TASK_ISACTIVE = "isActive";
	public final static String TASK_COLOR = "color";
	public final static String TASK_NOTES = "notes";
	
	public final static String LOCATION_ID = "location_id";
	public final static String LOCATION_NAME = "location_name";
	public final static String LOCATION_LATITUDE = "latitude";
	public final static String LOCATIOIN_LONGITUDE = "longitude";
	public final static String LOCATION_RANGE = "range";
	
	public final static int REQUESTCODE_SETTASK = 0;
	public final static int REQUESTCODE_SETLOCATION = 1;
	
	public final static int ACTION_ACCEPT = 0;
	public final static int ACTION_CANCEL = 1;
	public final static int ACTION_DISCARD = 2;	
	
	public final static int RED = 0xffff0000;
	public final static int BLUE = 0xff0000ff;
	public final static int YELLOW = 0xffffff00;
	public final static int GREEN = 0xff00ff00;
	public final static int NO_COLOR = 0x00000000; //alpha set to 0, color does not matter
	
	public final static String OPERATING_MODE = "operating_mode";
	public final static int MODE_ADD = 0;
	public final static int MODE_EDIT = 1;
	
	// Lookup arrays for spinners
	public final static HashMap<String, Integer> STRING_TO_COLOR = new HashMap<String, Integer>();	
	static{ 
		STRING_TO_COLOR.put("Red", RED);
		STRING_TO_COLOR.put("Blue", BLUE);
		STRING_TO_COLOR.put("Yellow", YELLOW);
		STRING_TO_COLOR.put("Green", GREEN);
		STRING_TO_COLOR.put("No color", NO_COLOR);
	}
	
	public final static HashMap<Integer, String> COLOR_TO_STRING = new HashMap<Integer, String>();	
	static{ 
		COLOR_TO_STRING.put(RED, "Red");
		COLOR_TO_STRING.put(BLUE, "Blue");
		COLOR_TO_STRING.put(YELLOW, "Yellow");
		COLOR_TO_STRING.put(GREEN, "Green");
		COLOR_TO_STRING.put(NO_COLOR, "No color");
	}
	
	public final static HashMap<String, Integer> STRING_TO_DISTANCE = new HashMap<String, Integer>();	
	static{ 
		STRING_TO_DISTANCE.put("20 m", 20);
		STRING_TO_DISTANCE.put("50 m", 50);
		STRING_TO_DISTANCE.put("100 m", 100);
		STRING_TO_DISTANCE.put("250 m", 250);
		STRING_TO_DISTANCE.put("500 m", 500);
	}
	
	public final static HashMap<String, Integer> STRING_TO_DEADLINETIME = new HashMap<String, Integer>();	
	static{ 
		STRING_TO_DEADLINETIME.put("5 min", (5*60*1000));
		STRING_TO_DEADLINETIME.put("10 min", (10*60*1000));
		STRING_TO_DEADLINETIME.put("30 min", (30*60*1000));
		STRING_TO_DEADLINETIME.put("1 h", (1*60*60*1000));
		STRING_TO_DEADLINETIME.put("4 h", (4*60*60*1000));
		STRING_TO_DEADLINETIME.put("12 h", (12*60*60*1000));
		STRING_TO_DEADLINETIME.put("1 day", (1*24*60*60*1000));
		STRING_TO_DEADLINETIME.put("2 days", (2*24*60*60*1000));
		STRING_TO_DEADLINETIME.put("7 days", (7*24*60*60*1000)); // still fits in integer
	}
	
	public final static HashMap<Integer, String> DEADLINETIME_TO_STRING = new HashMap<Integer, String>();	
	static{ 
		DEADLINETIME_TO_STRING.put((5*60*1000), "5 min");
		DEADLINETIME_TO_STRING.put((10*60*1000), "10 min");
		DEADLINETIME_TO_STRING.put((30*60*1000), "30 min");
		DEADLINETIME_TO_STRING.put((1*60*60*1000), "1 h");
		DEADLINETIME_TO_STRING.put((4*60*60*1000), "4 h");
		DEADLINETIME_TO_STRING.put((12*60*60*1000), "12 h");
		DEADLINETIME_TO_STRING.put((1*24*60*60*1000), "1 day");
		DEADLINETIME_TO_STRING.put((2*24*60*60*1000), "2 days");
		DEADLINETIME_TO_STRING.put((7*24*60*60*1000), "7 days"); // still fits in integer
	}
	
	public final static List<Long> DEADLINETIMES = new ArrayList<Long>();
	static {
		DEADLINETIMES.add(5*60*1000l);
		DEADLINETIMES.add(10*60*1000l); 
		DEADLINETIMES.add(30*60*1000l);
		DEADLINETIMES.add(1*60*60*1000l);
		DEADLINETIMES.add(4*60*60*1000l);
		DEADLINETIMES.add(12*60*60*1000l);
		DEADLINETIMES.add(1*24*60*60*1000l);
		DEADLINETIMES.add(2*24*60*60*1000l);
		DEADLINETIMES.add(7*24*60*60*1000l);
	}
	
	
}
