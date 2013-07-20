package org.giv2giv;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class ConfirmationActivity extends Activity
{
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.confirmation_screen);
		HashMap<String, String> test = new HashMap();
    	test.put("name", "David Dodge");
    	test.put("email", "davids@email.com");
    	test.put("password", "mypassword");
    	HashMap<String, String> headers = new HashMap();
    	headers.put("Content-Type", "application/json");
    	String httpResult = "Request crashed";
    	try {
			httpResult = UpdateQueue.makeRequest("http://www.giv2giv.org/api/donors.json", test, headers);
			Log.i("SIGNUP_INFO", "LOGIN_SUCCESSFUL");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			httpResult = e.toString();
			Log.i("SIGNUP_INFO", "LOGIN_FAILED");
			e.printStackTrace();
		}
    	TextView output = (TextView)findViewById(R.id.infoToConfirm);
    	output.setText(httpResult);
		//String charity = UpdateQueue.GetCharityList(this)[0];
		Button completeButton = (Button)findViewById(R.id.confirmButton);
//        Bundle infoToConfirm = this.getIntent().getBundleExtra("info");
//        ((TextView)findViewById(R.id.infoToConfirm)).setText(
//        		(CharSequence)infoToConfirm.getCharSequence("personalInfo"));
		
		/*Button loginButton = (Button)findViewById(R.id.connectButton);
		final EditText usernameEntry = (EditText)findViewById(R.id.usernameField);
		final EditText connectEntry = (EditText)findViewById(R.id.connectField);
		ImageButton setButton = (ImageButton)findViewById(R.id.dashSetButton);*/
        completeButton.setOnClickListener(new OnClickListener() 
        {
            @Override
            public void onClick(View v) 
            {
            	final Context intentContext = v.getContext();
            	CheckBox termsAgreed = (CheckBox)findViewById(R.id.termsAgreeCheck);
        		AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            	if (termsAgreed.isChecked())
            	{
	            	builder.setTitle("Add Charities").setCancelable(false)
	            	.setMessage("Would you like to add charities to your watch list?")
	            	.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
	            	{
	            		public void onClick(DialogInterface dialog, int id) 
	            		{
	            			Intent nextScreen = new Intent(intentContext, ListActivity.class);
	            			startActivity(nextScreen);
	            			finish();
	            		}
	            	})
	            	.setNegativeButton("No", new DialogInterface.OnClickListener() 
	            	{
	            		public void onClick(DialogInterface dialog, int id) 
	            		{
	            			Intent nextScreen = new Intent(intentContext, DashboardActivity.class);
	            			startActivity(nextScreen);
	            			finish();
	            		}
	            	});
            	}
            	else
            	{
            		builder.setTitle("Terms of Service").setCancelable(false)
	            	.setMessage("You must accept the Terms and Conditions of service before continuing.")
	            	.setNegativeButton("Back", new DialogInterface.OnClickListener() 
	            	{
	            		public void onClick(DialogInterface dialog, int id) 
	            		{
	            			return;
	            		}
	            	});
            	}
            	AlertDialog alert = builder.create();
            	alert.show();
        		//UpdateQueue.GetUserToken(v.getContext(), connectEntry.getText().toString());
        		/*if (UpdateQueue.GetUserToken(v.getContext(), "dodge"))
        		{
        			startActivity(listScreen);
        			finish();
        		}*/
            	return;
            }
        });
	}
}
