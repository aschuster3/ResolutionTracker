package com.Centaurii.app.resolution;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import com.Centaurii.app.resolution.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ResolutionTracker extends Activity {
    /** Called when the activity is first created. */
    int currentDay;
    Calendar calendar;
	SharedPreferences savedResolutions;
    TextView messageTextView;
    ImageView tensImageView;
    ImageView onesImageView;
    String resolution;
    int daysElapsed;
    TableLayout layout;
    long lastNotified;
    String quote;
    int frequency;
   // private static final int NOTIFY_ME_ID=1337;
    
    public void onCreate(Bundle savedInstanceState) {//on app startup
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        savedResolutions=getSharedPreferences("Resolutions", MODE_PRIVATE);
        calendar= Calendar.getInstance();
        currentDay=calendar.get(Calendar.DAY_OF_YEAR); //current day
        TableLayout layout=(TableLayout) findViewById(R.id.TableLayout);
        messageTextView= (TextView) findViewById(R.id.messageTextView);
        tensImageView= (ImageView) findViewById(R.id.tensImageView);
        onesImageView= (ImageView) findViewById(R.id.onesImageView);
        updateDaysElapsed(false); 
        updateNumbers();
        updateText();
        quote=savedResolutions.getString("quote",""); //saved quote
        frequency=savedResolutions.getInt("frequency",1);
       
       //loads saved background color
       if(savedResolutions.getString("color", "default").equals("default")) 
        	layout.setBackgroundColor(Color.rgb(255,231,163));
       else if(savedResolutions.getString("color", "default").equals("white"))
        	layout.setBackgroundColor(Color.WHITE);
       else if(savedResolutions.getString("color", "default").equals("pink"))
        	layout.setBackgroundColor(Color.rgb(255,182,193));
       //if (isQuoteTime())
    	//   quoteNotify(); 
    }
    
    private void updateDaysElapsed(boolean fromUser) //number of days user has stuck with resolution
    {
    	if(fromUser)
    		daysElapsed= savedResolutions.getInt("start", currentDay);
    	
    	else {
        
    	daysElapsed= currentDay-savedResolutions.getInt("day started", currentDay);
    	if (currentDay-savedResolutions.getInt("day started",currentDay) <0)
    		daysElapsed=daysElapsed+365; //compensate for year rollover
    	}
    }
    
    private String getTens() {//tens digit of number of days stuck with resolution
    	return Integer.toString(daysElapsed/ 10);
    }
    
    private String getOnes() {//ones digit of number of days stuck with resolution
    	System.out.println("mod 10 is "+ (daysElapsed %10));
    	return Integer.toString(daysElapsed % 10);
    }
    
    private void updateNumbers() {//updates number of days on screen
    	AssetManager assets= getAssets();
    	InputStream tenStream; 
    	InputStream oneStream;
    	try {
    		tenStream= assets.open(getTens()+".png");
    		Drawable tens= Drawable.createFromStream(tenStream, getTens());
    		tensImageView.setImageDrawable(tens);
    		
    		oneStream=assets.open(getOnes()+".png");
    		Drawable ones= Drawable.createFromStream(oneStream, getOnes());
    		onesImageView.setImageDrawable(ones);
    		
    		
    	}
    	catch (IOException e)
    	{
        	Log.e("Error loading number", e.getMessage()); 
    	}
    }
    
    private void updateText() {//updates text on screen
    	//get name of resolution from shared preferences
    	resolution=savedResolutions.getString("resolution", "your resolution");
    	messageTextView.setText("Congratulations!\n\nYou have stuck with "+ resolution+
    	" for "+ daysElapsed+" days!");
    }
    
    private final int RESOLUTIONS_MENU_ID = Menu.FIRST;
    private final int CLOCK_MENU_ID=Menu.FIRST+1;
	private final int BACKGROUND_MENU_ID= Menu.FIRST+2;
	//private final int MOTIVATION_MENU_ID= Menu.FIRST+3;
	
	
	public boolean onCreateOptionsMenu(Menu menu) {//sets menus when user presses menu button on phone
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, RESOLUTIONS_MENU_ID, Menu.NONE, R.string.resolution);
		menu.add(Menu.NONE, CLOCK_MENU_ID, Menu.NONE, R.string.set_clock);
		menu.add(Menu.NONE, BACKGROUND_MENU_ID, Menu.NONE, R.string.background);
	//	menu.add(Menu.NONE, MOTIVATION_MENU_ID, Menu.NONE, R.string.motivation);
	//	if (isQuoteTime())
	 //   	   quoteNotify(); 
		return true; 
	}
	
	public boolean onOptionsItemSelected(MenuItem item) { //actions for specific menu items
		
		int selected= item.getItemId();
		if (selected==RESOLUTIONS_MENU_ID) //shows dialog box for changing resolution
		{
			
			AlertDialog.Builder resolutionBuilder= new AlertDialog.Builder(this);
			resolutionBuilder.setTitle(R.string.resolution);
			LayoutInflater inflater= (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View dialogView=inflater.inflate(R.layout.resolution_view, null);
			resolutionBuilder.setView(dialogView);
			final EditText resolutionText= (EditText) dialogView.findViewById(R.id.resolutionEditText);
			
			resolutionBuilder.setCancelable(true);
			resolutionBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() 
			
			{
				public void onClick(DialogInterface dialog, int id)
				{
					
					SharedPreferences.Editor preferencesEditor= savedResolutions.edit();
					preferencesEditor.putString("resolution", resolutionText.getText().toString()); //save resolution
					preferencesEditor.commit();
					updateText();
				}
			}
			);
			AlertDialog resolutionDialog=resolutionBuilder.create();
			resolutionDialog.show();
			
		}
		else if (selected==CLOCK_MENU_ID) //shows dialog box for changing clock
		{
			
			AlertDialog.Builder clockBuilder= new AlertDialog.Builder(this);
			clockBuilder.setTitle(R.string.set_clock);
			LayoutInflater inflater= (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View dialogView=inflater.inflate(R.layout.set_clock_view, null);
			clockBuilder.setView(dialogView);
			final EditText clockText= (EditText) dialogView.findViewById(R.id.clockEditText);
			
			clockBuilder.setCancelable(true);
			clockBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() 
			
			{
				public void onClick(DialogInterface dialog, int id)
				{
					try {
					int clockTextNum=Integer.parseInt(clockText.getText().toString());
					SharedPreferences.Editor preferencesEditor= savedResolutions.edit();
					preferencesEditor.putInt("start", clockTextNum); //save clock value
					preferencesEditor.putInt("day started", currentDay-clockTextNum); //set date of resolution starting
					preferencesEditor.commit();
					updateDaysElapsed(true);
					updateNumbers();
					updateText();
					}
					catch (NumberFormatException e) //user put "" or non digit, gives error message
					{
						Context context = getApplicationContext();
						CharSequence text = "Invalid Number!";
						int duration = Toast.LENGTH_SHORT;

						Toast toast = Toast.makeText(context, text, duration);
						toast.show();
					}
					
					
				}
			}
			);
			AlertDialog  clockDialog=clockBuilder.create();
			clockDialog.show();
			
		}
		
//		else if (selected==MOTIVATION_MENU_ID) //shows dialog box for changing motivational quote
//		{
//			
//			AlertDialog.Builder motivationBuilder= new AlertDialog.Builder(this);
//			motivationBuilder.setTitle(R.string.motivation);
//			LayoutInflater inflater= (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			View dialogView=inflater.inflate(R.layout.set_motivation, null);
//			motivationBuilder.setView(dialogView);
//			motivationBuilder.setMessage("Enter your motivational quote and daily frequency of motivational notifications.");
//			final EditText motivationEditText= (EditText) dialogView.findViewById(R.id.motivationEditText);
//			final EditText frequencyEditText= (EditText) dialogView.findViewById(R.id.frequencyEditText);
//			motivationBuilder.setCancelable(true);
//			motivationBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() 
//			
//			{
//				public void onClick(DialogInterface dialog, int id)
//				{   
//					try {
//						SharedPreferences.Editor preferencesEditor= savedResolutions.edit();
//						preferencesEditor.putString("quote", motivationEditText.getText().toString());
//						preferencesEditor.putInt("frequency", Integer.parseInt(frequencyEditText.getText().toString()));
//						preferencesEditor.commit();
//						//quoteNotify();
//					}
//					catch (NumberFormatException e) //user put "" or non digit. 
//					{
//						//Do nothing, since this could be intentional
//					}
//				}
//			}
//			);
//			AlertDialog  motivationDialog=motivationBuilder.create();
//			motivationDialog.show();
//			
//		}
		
		else if (selected==BACKGROUND_MENU_ID) //shows dialog box for changing background
		{
			
			AlertDialog.Builder backgroundBuilder= new AlertDialog.Builder(this);
			backgroundBuilder.setTitle(R.string.background);
			LayoutInflater inflater= (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View dialogView=inflater.inflate(R.layout.background_view, null);
			backgroundBuilder.setView(dialogView);
			final RadioGroup radiogroup= (RadioGroup) dialogView.findViewById(R.id.radiogroup);
			
			backgroundBuilder.setCancelable(true);
			backgroundBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() 
			
			{
				public void onClick(DialogInterface dialog, int id)
				{   TableLayout layout=(TableLayout) findViewById(R.id.TableLayout);
					int selectedId=radiogroup.getCheckedRadioButtonId();
					RadioButton selectedButton= (RadioButton) findViewById(selectedId);
					SharedPreferences.Editor preferencesEditor= savedResolutions.edit();
					
					
					if (selectedId==R.id.defaultRadioButton) 
						{
						preferencesEditor.putString("color", "default"); //save background preference
						preferencesEditor.commit();
						layout.setBackgroundColor(Color.rgb(255,231,163));
						}
					
					else if (selectedId==R.id.whiteRadioButton)
						{
						preferencesEditor.putString("color", "white"); //save background preference
						preferencesEditor.commit();
						layout.setBackgroundColor(Color.WHITE);}
					
					else if (selectedId==R.id.pinkRadioButton)
						{
						preferencesEditor.putString("color", "pink"); //save background preference
						preferencesEditor.commit();
						layout.setBackgroundColor(Color.rgb(255,182,193));
						}
					
				}
			}
			);
			AlertDialog  backgroundDialog=backgroundBuilder.create();
			backgroundDialog.show();
			
		}
		
		return true;
	}
    
//	private boolean isQuoteTime(){ //checks to see if it's time to notify user with motivational quote
//		long millisInDay=86400000; 
//		try {
//		long interval=millisInDay/savedResolutions.getInt("frequency", 1);
//		if ((System.currentTimeMillis()-lastNotified)>=interval)
//			return true;
//		return false;}
//		catch (Exception e) //divide by 0; user does not want to be notified
//		{
//			return false;
//		}
//	}
//	
//	private void quoteNotify() {//notify user with motivational quote
//		NotificationManager notificationManager = (NotificationManager) 
//				  getSystemService(NOTIFICATION_SERVICE); 
//		
//		Context context= getApplicationContext(); 
//		final Notification notifyDetails =
//				new Notification(R.drawable.ic_launcher,"Motivational Quote",System.currentTimeMillis());
//		String notificationTitle="Motivational Quote";
//		String quote=savedResolutions.getString("quote","Don't ever give up!");
//		Intent notifyIntent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse("http://www.motivateus.com/motivational-quote-of-the-day.htm")); //sends user to motivational quote of the day site if they click notification
//	     
//		PendingIntent intent =
//		      PendingIntent.getActivity(ResolutionTracker.this, 0,
//		      notifyIntent, android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
//		notifyDetails.setLatestEventInfo(context, notificationTitle, quote, intent);
//		notificationManager.notify(NOTIFY_ME_ID, notifyDetails);
//	}

}