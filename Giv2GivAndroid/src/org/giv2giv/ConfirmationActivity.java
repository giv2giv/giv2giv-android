package org.giv2giv;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
    	HashMap<String, String> headers = new HashMap<String, String>();
    	headers.put("Content-Type", "application/json");
    	final HashMap<String, String> info = (HashMap<String, String>)this.getIntent()
    			.getBundleExtra("info").getSerializable("info");
    	
    	TextView output = (TextView)findViewById(R.id.infoToConfirm);
    	String userInfo = "";
    	for (String key : info.keySet())
    	{
    		userInfo += key + ": " + info.get(key) + "\n";
    	}
    	output.setText(userInfo);
		Button completeButton = (Button)findViewById(R.id.confirmButton);
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
                	String httpResult = "Request crashed";
            		try {
            			new DonorSignupTask(v.getContext()).execute(info);
            			Log.i("SIGNUP_INFO", httpResult);
            		} catch (Exception e) {
            			// TODO Auto-generated catch block
            			httpResult = e.toString();
            			Log.i("SIGNUP_INFO", httpResult);
            			e.printStackTrace();
            		}/*
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
	            	});*/
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
            	//AlertDialog alert = builder.create();
            	//alert.show();
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

	private class DonorSignupTask extends AsyncTask<Map, Void, String>
	{
		private Context mContext;
		private ProgressDialog mLoadingDialog;
		
		public DonorSignupTask(Context context)
		{
			mContext = context;
		}
		
		@Override
		protected void onPreExecute()
		{
			mLoadingDialog = ProgressDialog.show(mContext, "", 
	                "Registration in progress...", true);
		}
		@Override
		protected String doInBackground(Map... donorInfoSet) 
		{
			String httpResponse = "";
			for (Map donorInfo : donorInfoSet)
			{
				//THIS SHOULD NEVER BE CREATING MORE THAN ON DONOR PER CALL
				//SO THE BREAK IS TO PREVENT STUPIDITY OR MADNESS FROM HAPPENING
				httpResponse = UpdateQueue.CreateDonor(donorInfo);
				break;
			}	
			return httpResponse;
		}
		@Override
		protected void onPostExecute(String httpResponse)
		{
	    	TextView output = (TextView)findViewById(R.id.infoToConfirm);
	    	//output.setText(httpResponse);
			mLoadingDialog.dismiss();
			Log.i("SIGNUP_INFO", "Sign up Finished");
		}
	}
}
