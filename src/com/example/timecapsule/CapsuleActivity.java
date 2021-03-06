/*
 * Time Capsule Application
 * Created By Yang (Michael) Liu 
 * Copyright (c) 2013
 */

package com.example.timecapsule;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class CapsuleActivity extends Activity {
	
	private static final String TAG = "CapsuleActivity";
	private static final String LIMIT = "limit_reached";
	private static final String PICS_TAKEN = "pictures_taken_today";
	private static final String CURRENT_PICTURE = "current_pic";
	private static final String PICTURE_GALLERY = "picture_gallery";
	
	private static boolean limitReached = false;
	private static int numOfPicturesTakenToday;
	
	private ImageButton mCameraButton;
	private ImageButton mGalleryButton;
	private EditText mJournalButton;
	private Button mDateButton;
	private ImageButton mScrapbookButton;

	private SimpleDateFormat mDate;
	private SimpleDateFormat mDateWithoutYear;
	private Date mDateValue;
	
	private String pictureTargetDate = "";
	
	private ArrayList<HashMap<String, String>> mPictureNames; //stores all the picture names in gallery
	private String mCurrentPicture;    //stores current picture name
	
	@TargetApi(11)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capsule);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8B008B"))); //sets the action bar to purple
		mDateButton = (Button)findViewById(R.id.current_date);
		mDate = new SimpleDateFormat("MMM d, yyyy");
		mDateWithoutYear = new SimpleDateFormat("MMM d");
		mDateValue = new Date();
		String date = mDateWithoutYear.format(mDateValue);
		mDateButton.setText(mDate.format(mDateValue));
		
		mPictureNames = new ArrayList<HashMap<String,String>>();
		
		mCameraButton = (ImageButton)findViewById(R.id.camera_button);
		mCameraButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (!limitReached) {
					Intent intent = new Intent(CapsuleActivity.this, CameraActivity.class);
					startActivityForResult(intent, 0);
				}
				else {
					
					Toast.makeText(getApplicationContext(), R.string.limit_reached, Toast.LENGTH_SHORT).show();
				}
				
			}
			
		});
		
		mGalleryButton = (ImageButton)findViewById(R.id.gallery_button);
		mGalleryButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CapsuleActivity.this, GalleryActivity.class);
				intent.putExtra(GalleryActivity.EXTRA_PICTURE_NAMES, mPictureNames);
				startActivity(intent);
				
			}
		});
		
		if (savedInstanceState != null) {
			
			numOfPicturesTakenToday = savedInstanceState.getInt(PICS_TAKEN);
	    	limitReached = savedInstanceState.getBoolean(LIMIT);
	    	mCurrentPicture = savedInstanceState.getString(CURRENT_PICTURE);
	    	mPictureNames = (ArrayList<HashMap<String,String>>) savedInstanceState.getSerializable(PICTURE_GALLERY);
		}
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	Log.d(TAG, "In activity result");
    	
    	if (data == null){
    		Log.d(TAG, "DATA IS NULL!");
    		return;
    	}
    	
    	String pictureName = data.getStringExtra(CameraActivity.EXTRA_PICTURE_NAME);
    	String pictureDate = data.getStringExtra(CameraActivity.EXTRA_PICTURE_DATE);
    	String pictureTargetDate = data.getStringExtra(CameraActivity.EXTRA_PICTURE_TARGET_DATE);
    	
    	Log.d(TAG, "GOT INTENT DATA: " + pictureName);
    	
    	if (!pictureName.isEmpty()) {
    		
    		numOfPicturesTakenToday++;
    		mCurrentPicture = pictureName;
    		HashMap<String,String> picture = new HashMap<String,String>();
    		picture.put(mCurrentPicture, pictureTargetDate);
    		mPictureNames.add(picture);
    		Log.d(TAG, "Picture added!");
    		Log.d(TAG, mPictureNames.size() + " PICTURES TOTAL!");
    		
    		if (numOfPicturesTakenToday >= 10) {
    			limitReached = true;
    		}
    	} 
    }

    @Override
	public void onResume(){
    	super.onResume();
    	
    	Calendar c = Calendar.getInstance();
		mDate = new SimpleDateFormat("MMM d, yyyy");
		mDateValue = new Date();
		mDateButton.setText(mDate.format(mDateValue));
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
    	
    	String previousDate = mDate.format(mDateValue);
    	//recognize that the day has passed and reset the numOfPicturesTakenToday (DO LATER)
    	
    	savedInstanceState.putInt(PICS_TAKEN, numOfPicturesTakenToday);
    	savedInstanceState.putBoolean(LIMIT, limitReached);
    	savedInstanceState.putString(CURRENT_PICTURE, mCurrentPicture);
    	savedInstanceState.putSerializable(PICTURE_GALLERY, mPictureNames);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.capsule, menu);
		return true;
	}
}
