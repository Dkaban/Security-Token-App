package com.example.securitytokenapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Date;
import android.view.View;

public class VerifyActivity extends AppCompatActivity
{
    //Variables to keep track of the Shared Preferences
    private String PREF_NAME = "TimeStamps";
    private SharedPreferences TimeStamps;
    private SharedPreferences.Editor editor;

    //Variables for editing UI
    private ArrayList<Date> dateList = new ArrayList<Date>();
    private ListView listViewTimeStamps;
    private ArrayAdapter<Date> adapter;
    private long mostRecentTimeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        //Initialize the variables for shared preferences and UI list view
        listViewTimeStamps = findViewById(R.id.listView_timeStamps);
        TimeStamps = getSharedPreferences(PREF_NAME,0);
        editor = TimeStamps.edit();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        //We want to Load the time stamps, save the new one, then display in that order.
        loadTimeStamp();
        saveTimestamp();
        setTimeStampListView();
    }

    public void loadTimeStamp()
    {
        //Get the previously saved date and time
        long timeStamp = TimeStamps.getLong(PREF_NAME,0);
        Date date = new Date(timeStamp);

        //Add the date to the persistent date list
        DataHolder.getInstance().addDate(date);
    }

    private void saveTimestamp()
    {
        //Save the most recent timestamp to the shared preferences
        mostRecentTimeStamp = System.currentTimeMillis();
        editor.putLong(PREF_NAME,mostRecentTimeStamp);
        editor.commit();

        Date currentDate = new Date(mostRecentTimeStamp);

        //Add the date to the persistent date list
        DataHolder.getInstance().addDate(currentDate);
    }

    private void setTimeStampListView()
    {
        //We update the adapter so the listview will be populated with the correct data
        adapter = new ArrayAdapter<Date>(this,android.R.layout.simple_list_item_1,DataHolder.getInstance().persistentDateList);
        listViewTimeStamps.setAdapter(adapter);
    }

    public void onClickClearTimeStamps(View view)
    {
        //We clear all the lists and set the adapter to empty
        DataHolder.getInstance().persistentDateList.clear();
        adapter.clear();
        listViewTimeStamps.setAdapter(adapter);
    }

    @Override
    public void onBackPressed()
    {
        //We don't want to allow the user to go back. There is no need to verify again.
        super.onBackPressed();
    }
}

//This class allows us to have persistent date while the app is active
//instead of parsing through SharedPreferences separated by delimeters
class DataHolder
{
    public ArrayList<Date> persistentDateList = new ArrayList<Date>();

    public void addDate(Date date)
    {
        if(!persistentDateList.contains(date))
        {
            persistentDateList.add(date);
        }
    }

    static DataHolder getInstance()
    {
        if( instance == null )
        {
            instance = new DataHolder();
        }
        return instance;
    }

    private static DataHolder instance;
}