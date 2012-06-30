package com.shutter.iNotes;

import java.util.Date;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;
import android.widget.ImageButton;


public class INotesActivity extends Activity {

	private String currentFilename = "";
	private String changeBuffer = "";
	private MediaPlayer mp;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
      //Remove title bar
      this.requestWindowFeature(Window.FEATURE_NO_TITLE);
      
      setContentView(R.layout.main);   
      
      //Load list of files
      refreshNotesList();
      
      
      // Prepare button listeners
      setAddNewButtonEvent();
      setDeleteButtonEvent() ;
      setListViewSelectionEvent();
      setEmailButtonEvent();
      
      // Set focus on the edit text view
      TextView edit = (TextView)findViewById(R.id.editText1);
      edit.requestFocus();
      
    }
    
    @Override
	protected void onStop() {
		super.onStop();
			
		updateNote();
	}
    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		refreshNotesList();
	}

    
	private void setAddNewButtonEvent() {
		
        ImageButton imgBtn = (ImageButton)findViewById(R.id.imageButton1);
        imgBtn.setOnClickListener(new View.OnClickListener() {

  			public void onClick(View v) {
  				
  				mp = MediaPlayer.create(getApplicationContext(), R.raw.click);
  				mp.start();
  				
  				updateNote();
  				
  				currentFilename = "";
  				changeBuffer = "";
  				
  				TextView t = (TextView)findViewById(R.id.editText1);
  				t.setText("");
  				
  				TextView title = (TextView)findViewById(R.id.editTextTitle);
  				title.setText("New Note");
  				
  				//Set last modified date
  				TextView lmd = (TextView)findViewById(R.id.txtDate);
  				lmd.setText("");
  				
  			}

        	}
        );
        
		
	}
	
	private void setDeleteButtonEvent() {
		
        ImageButton imgBtn = (ImageButton)findViewById(R.id.imageButtonDelete);
        imgBtn.setOnClickListener(new View.OnClickListener() {

  			public void onClick(View v) {
  				
  				mp = MediaPlayer.create(getApplicationContext(), R.raw.click);
  				mp.start();
  				
  				if(currentFilename != "")
  				{
  			    	File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes");
  			    	File file = new File(root, currentFilename);
  			   
  			    	file.delete();
  				}
  				
  				refreshNotesList();
  				
  				currentFilename = "";
  				changeBuffer="";
  				
  				TextView t = (TextView)findViewById(R.id.editText1);
  				t.setText("");
  				
  				TextView title = (TextView)findViewById(R.id.editTextTitle);
  				title.setText("New Note");
  				
  				//Set last modified date
  				TextView lmd = (TextView)findViewById(R.id.txtDate);
  				lmd.setText("");
  				
  			}

        	}
        );
        
		
	}


	private void setListViewSelectionEvent() {
		
		ListView filesListView = (ListView)findViewById(R.id.listView1);
		
		filesListView.setSelector(R.drawable.list_selector);
		
		//Set the listener
		filesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			
			public void onItemClick(AdapterView<?> parentView, View childView, int position, long id)
			{
  				mp = MediaPlayer.create(getApplicationContext(), R.raw.click);
  				mp.start();				
  				
  				//Save the current note
				updateNote();
				
				//Get the title of the item
				Object o = parentView.getItemAtPosition(position);
				String filename = o.toString();
				
				currentFilename = filename;
				
				//Load the note detail
				String note = getNoteOnSD(filename);
				String lastModifiedDate =  getLastModifiedDate(filename);
				changeBuffer=note;
				
				// Set the text body
				TextView t = (TextView)findViewById(R.id.editText1);
				t.setText(note);
				
				// Set the title of the note
  				TextView title = (TextView)findViewById(R.id.editTextTitle);
  				title.setText(filename);
  				
  				//Set last modified date
  				TextView lmd = (TextView)findViewById(R.id.txtDate);
  				lmd.setText("Last modified: " + lastModifiedDate);
  				
			}

		});
		
	}
	
	private void setEmailButtonEvent() {
		
        ImageButton imgBtn = (ImageButton)findViewById(R.id.imageButtonMail);
        imgBtn.setOnClickListener(new View.OnClickListener() {

  			public void onClick(View v) {
  				
  				mp = MediaPlayer.create(getApplicationContext(), R.raw.click);
  				mp.start();

  				TextView txtView;
  				txtView = (TextView)findViewById(R.id.editText1);
  				
  				Intent i = new Intent(Intent.ACTION_SEND);
  				i.setType("text/plain");
  				i.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
  				i.putExtra(Intent.EXTRA_SUBJECT, "");
  				i.putExtra(Intent.EXTRA_TEXT, txtView.getText().toString());
  				
  				try {
  					startActivity(Intent.createChooser(i, "Send mail..."));
  				}
  				catch (android.content.ActivityNotFoundException ex)
  				{
  					Toast.makeText(getApplicationContext(), "There are no mail clients installed.", Toast.LENGTH_SHORT).show();
  				}
  				
  			}
        });
	};
    
	
    public void updateNote()
    {
		//Get the body string
		TextView txtView;
		txtView = (TextView)findViewById(R.id.editText1);
		String note = txtView.getText().toString();
		
    	if(currentFilename == "")
    	{
    		if(note.length() == 0)
    			return;
    		else
    		{
	    		//Generate a file name
	    		Integer len = note.length();
	    		Integer eol = note.indexOf('\n');
	    		
	    		if(len > 15)
	    			len = 15;
	    		
	    		if(eol > -1 && eol < len)
	    			len = eol;
	    		
	    		currentFilename = note.substring(0, len).trim();
    		}
    	}
    	
    	writeNoteOnSD(currentFilename, note);
    	
    	refreshNotesList();
    }
    
    public void writeNoteOnSD(String sFilename, String sBody)
    {
    	if(!changeBuffer.equals(sBody))
    	{
	    	try
	    	{
	    		File root = new File(Environment.getExternalStorageDirectory(), "Notes");
	    		if(!root.exists())
	    		{
	    			root.mkdirs();
	    		}
	    		
	    		File file = new File(root, sFilename);
	    		FileWriter writer = new FileWriter(file);
	    		writer.append(sBody);
	    		writer.flush();
	    		writer.close();
	    		
	    		Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
	    	}
	    	catch(IOException e)
	    	{
	    		e.printStackTrace();
	    		Log.d("IOError", e.getMessage());
	    		Toast.makeText(this, "An iNotes Error Occured.  Note NOT Saved", Toast.LENGTH_SHORT).show();
	    	}
    	}
    	else
    	{
    		Toast.makeText(this, "No changes", Toast.LENGTH_SHORT).show();
    	}
    }
 
    public String getNoteOnSD(String sFilename)
    {
    	File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes");
    	File file = new File(root, sFilename);
    	
    	StringBuilder text = new StringBuilder();
    	
    	try {
    		BufferedReader br = new BufferedReader(new FileReader(file));
    		String line;
    		
    		while((line = br.readLine()) != null) {
    			text.append(line);
    			text.append('\n');
    		}
    	}
    	catch (IOException e)
    	{
    		Log.d("Error", e.getMessage());
    	}

    	return text.toString();
    	
    }

    public String getLastModifiedDate(String sFilename)
    {
    	File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes");
    	File file = new File(root, sFilename);
    	
		Date lastModDate = new Date(file.lastModified());
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:MM");
		return sdf.format(lastModDate);
    }
    
    
    public void refreshNotesList()
    {
    	File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes");
    	File[] files = root.listFiles();
    	
    	List<String> fileList = new ArrayList<String>();
    	List<String> dateList = new ArrayList<String>();
    	fileList.clear();
    	dateList.clear();
    	
    	for(File file : files)
    	{
    		
    		fileList.add(file.getName());
    	}
    	
  		ArrayAdapter<String> dirList = new ArrayAdapter<String>(this, R.layout.customlistview, fileList);
  				
  		ListView filesListView = (ListView)findViewById(R.id.listView1);
		filesListView.setAdapter(dirList); 	
		
    }
}