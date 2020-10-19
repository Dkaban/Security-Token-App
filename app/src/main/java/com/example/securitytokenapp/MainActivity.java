package com.example.securitytokenapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
{
    private BroadcastReceiver broadcastReceiver;
    private Calendar calendar;
    private TextView passCode;
    private TextView timeRemaining;
    private EditText userEntryPassCode;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get the text information so we can modify / read it.
        passCode = findViewById(R.id.text_code);
        timeRemaining = findViewById(R.id.text_timeRemaining);
        userEntryPassCode = findViewById(R.id.editText_codeEntry);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        setPassCode();
        updateTimeOnEachSecond();

        //Update the PassCode every minute, on the minute.
        broadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context ctx, Intent intent)
            {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0)
                {
                    setPassCode();
                }
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void onStop()
    {
        //When we've finished with this activity, we want to stop the receiver
        super.onStop();
        if (broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
    }

    public void updateTimeOnEachSecond()
    {
        //Timer to keep track of seconds
        timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                calendar = Calendar.getInstance();
                final int sec = calendar.get(Calendar.SECOND);

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        setTimeRemaining(sec);
                    }
                });
            }
        }, 0, 1000);
    }

    private void onVerify()
    {
        //Changing to the Verified activity (displays the Verified UI)
        Intent verified = new Intent(this,VerifyActivity.class);
        startActivity(verified);
    }

    private void setTimeRemaining(int seconds)
    {
        //Updates the time remaining UI
        seconds = 60 - seconds;
        timeRemaining.setText(String.valueOf(seconds) + " seconds remaining");
    }

    public void setPassCode()
    {
        //Calculation for setting the pass code every minute
        calendar = Calendar.getInstance();
        int newCode = (calendar.get(Calendar.MINUTE) * 1245 + 10000);
        //Update the UI
        passCode.setText(Integer.toString(newCode));
    }

    public void onClickVerify(View view)
    {
        //Checks to see if the user entered the right pass code, if not display a message
        if(userEntryPassCode.getText().toString().equals(passCode.getText().toString()))
        {
            //If the codes are equal, we're verified! If not, toast message.
            onVerify();
        }
        else
        {
            Toast toast = Toast.makeText(getApplicationContext(),"Incorrect PassCode", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}