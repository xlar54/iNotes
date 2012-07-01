package com.shutter.iNotes;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter
{
	private List<String> objects = new ArrayList<String>();

	private final Context   context;
	 
	public MyAdapter(Context context, List<String> objects) {
	    this.context = context;
	    this.objects = objects;
	}
	 
	public int getCount() {
	    return objects.size();
	}
	 
	public Object getItem(int position) {
	    return objects.get(position);
	}
	 
	public long getItemId(int position) {
	    return position;
	}
	 
	public View getView(int position, View convertView, ViewGroup parent) {
	 
	    Object obj = objects.get(position);
	 
	    TextView tv = new TextView(context);
	    tv.setText(obj.toString());
	    tv.setTextSize(24);
	    tv.setPadding(0, 8, 0, 8);
	    tv.setSelected(true);
	    
		Typeface type = Typeface.createFromAsset(context.getAssets(), "fonts/marker-felt-thin.ttf");
	    tv.setTypeface(type);
	    
	    return tv;
	}

}
