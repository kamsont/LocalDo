package de.kamson.localdo;


import android.content.Context;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements InfoWindowAdapter{

	Context context;
	int resource;
	View view;
	String title = "no Title";
	int radius = 0;
	String info2 = "no Info";
	TextView tv_title;
	TextView tv_info1;
	TextView tv_info2;
	
	public CustomInfoWindowAdapter(Context context, int resource) {
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(resource, null);
	}
	
	@Override
	public View getInfoContents(Marker arg0) {
		// TODO Auto-generated method stub
		tv_title = (TextView)view.findViewById(R.id.infowindow_title);
		tv_info1 = (TextView)view.findViewById(R.id.infowindow_info1);
		tv_info2 = (TextView)view.findViewById(R.id.infowindow_info2);
		tv_title.setText(title);
		tv_info1.setText("Radius: "+radius+"m");		
		return view;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setTitle(String newTitle) {
		title = newTitle;
		if (tv_title == null) {
			tv_title = (TextView)view.findViewById(R.id.infowindow_title);
		}
		tv_title.setText(title);
	}
	
	public void setRadius(int newRadius) {
		radius = newRadius;
		if (tv_info1 == null) {
			tv_info1 = (TextView)view.findViewById(R.id.infowindow_info1);
		}
		tv_info1.setText("Radius: "+radius+"m");
	}
	
	public void setInfo2(String newInfo) {
		info2 = newInfo;
		if (tv_info2 == null) {
			tv_info2 = (TextView)view.findViewById(R.id.infowindow_info2);
		}
		tv_info2.setText(info2);
	}

}
