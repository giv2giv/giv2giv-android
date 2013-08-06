package org.giv2giv;

import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity
{
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.login_page);
		
		Button loginButton = (Button)findViewById(R.id.login_button);
		loginButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				String[] loginInfo = new String[2];
				loginInfo[0] = ((EditText)findViewById(R.id.login_email)).getText().toString();
				loginInfo[1] = ((EditText)findViewById(R.id.login_password)).getText().toString();
    			new LoginTask(v.getContext()).execute(loginInfo);
			}
		});
//		final EditText usernameEntry = (EditText)findViewById(R.id.usernameField);
//		final EditText connectEntry = (EditText)findViewById(R.id.connectField);
//        loginButton.setOnClickListener(new OnClickListener() 
//        {
//            @Override
//            public void onClick(View v) 
//            {
//        		Intent listScreen = new Intent(v.getContext(), ListActivity.class);
//        		//UpdateQueue.GetUserToken(v.getContext(), connectEntry.getText().toString());
//        		if (UpdateQueue.GetUserToken(v.getContext(), "dodge"))
//        		{
//        			startActivity(listScreen);
//        			finish();
//        		}
//            	return;
//            }
//        });
	}
	
	private class LoginTask extends AsyncTask<String, Void, String>
	{
		private Context mContext;
		private ProgressDialog mLoadingDialog;
		
		public LoginTask(Context context)
		{
			mContext = context;
		}
		
		@Override
		protected void onPreExecute()
		{
			mLoadingDialog = ProgressDialog.show(mContext, "", 
	                "Logging in...", true);
		}
		@Override
		protected String doInBackground(String... loginInfo) 
		{
			String userToken = UpdateQueue.AuthenticateUser(loginInfo[0], loginInfo[1]);
			if (userToken.equals(""))
			{
				Log.i("AUTHENTICATE", "Login failed for whatever reason");
			}
			else
			{
				Log.i("AUTHENTICATE", "Login succeeded with token " + userToken);
				SharedPreferences settings = getPreferences(MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("user_token", userToken);
				editor.commit();
			}
			return "";
		}
		@Override
		protected void onPostExecute(String httpResponse)
		{
	    	//TextView output = (TextView)findViewById(R.id.infoToConfirm);
	    	//output.setText(httpResponse);
			mLoadingDialog.dismiss();
			//Log.i("SIGNUP_INFO", "Sign up Finished");
		}	
	}
}