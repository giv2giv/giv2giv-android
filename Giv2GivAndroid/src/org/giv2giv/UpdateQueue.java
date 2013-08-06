package org.giv2giv;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public final class UpdateQueue 
{
	public static Hashtable<String, String> updateQueue;
	
	public static String CreateDonor(Map data)
	{
		//Parameters: address, city, country, email, name, password, phone_number, state, zip
		HashMap<String, String> fixedData = new HashMap<String, String>();
		fixedData.put("name", "" + data.get("Name"));
		//Log.i("SIGNUP_INFO", "name: " + data.get("First Name") + data.get("Last Name"));
		fixedData.put("email", "" + data.get("Email Address"));
		///Log.i("SIGNUP_INFO", "email: " + data.get("Email Address"));
		fixedData.put("password", "" + data.get("Password"));
		//Log.i("SIGNUP_INFO", "pw: " + data.get("Password"));
		String address = "" + data.get("Address");
		if (!address.equals(""))
		{
			fixedData.put("address", address);
		}
		String city = "" + data.get("City");
		if (!city.equals(""))
		{
			fixedData.put("city", city);
		}
		String state = "" + data.get("State");
		if (!state.equals(""))
		{
			fixedData.put("state", state);
		}
		String zip = "" + data.get("Zip Code");
		if (!zip.equals(""))
		{
			fixedData.put("zip", zip);
		}
		String url = "http://www.giv2giv.org/api/donors.json";
//		fixedData.put("name", "dodge Tesat");
//		fixedData.put("email", "dodgetesdstsdsd@email.com");
//		fixedData.put("password", "dodgetest");
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		//fixedData.put("email", "dodgetest@email.com");
		try 
		{
			return httpPost(url, fixedData, headers);
		} 
		catch (Exception e) 
		{
			Log.i("UPDATE_QUEUE", "Create donor Failed with " + e.toString());
			e.printStackTrace();
			return "";
		}
	}
	
	public static boolean CheckEmailInUse(String emailAddress)
	{
		return false;
	}
	
	private UpdateQueue(Context context)
	{
		//updateQueue = new Hashtable<String, Double>();
		//netManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	public static void MakeLocalChange(String tag, String changedValue)
	{
		if (updateQueue == null)
		{
			updateQueue = new Hashtable<String, String>();
		}
		updateQueue.put(tag, changedValue);
	}
	
	public static String PushChanges(Context context, String url)
	{
		if (updateQueue == null)
		{
			updateQueue = new Hashtable<String, String>();
		}
		ConnectivityManager netManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiStatus = netManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobileStatus = netManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        String out = "NoInternetConnection";
        SharedPreferences prefs = context.getSharedPreferences("giv2givprefs", Context.MODE_WORLD_READABLE);
		if (wifiStatus.isConnected())
		{
			try {
				out = DoPush(url);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Send updates to server via wifi / internet
		}
		else if (mobileStatus.isConnected() && !mobileStatus.isRoaming())
		{
			try {
				out = DoPush(url);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Send updates to server via phone signal / 3G
		}
		else
		{
			//DoPush(); later in a runnable
			//Don't send updates as we have no net connection
		}
		return out;
	}
	
	public static String Login(Context context)
	{
		
		return "";
	}
	
	private static String DoPush(String url) throws JSONException
	{
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		JSONObject json = new JSONObject();
        String out = "RandomError";
        for (String tag : updateQueue.keySet())
        {
        	json.put(tag, updateQueue.get(tag).toString());
        }
        try
        {
        	StringEntity jsonEntity = new StringEntity(json.toString());
        	jsonEntity.setContentType("application/json;charset=UTF-8");
        	httpPost.setEntity(jsonEntity);
        	System.out.println(jsonEntity.toString());
        	try
        	{
        		HttpResponse response = httpClient.execute(httpPost);
        		out = EntityUtils.toString(response.getEntity());
        		updateQueue.clear();
        		//test.setText(EntityUtils.toString(response.getEntity()));
        	}
        	catch (ClientProtocolException c)
        	{
        		return "ClientProtocolException";
        	}
        	catch (IOException i)
        	{
        		return "IOException";
        	}
        }
        catch (UnsupportedEncodingException e)
        {
        	return "UnsupportedEncodingException";
        }
        return out;
	}

	public static Charity[] GetCharityList(Context context)
	{
		HttpResponse charityResponse = UpdateQueue.httpGet(context, 
				"http://demo.giv2giv.org/api/charities");
		String jsonOut = "";
		Charity[] out = null;
		if (charityResponse == null)
		{
			return null;
		}
		try 
		{
			jsonOut = EntityUtils.toString(charityResponse.getEntity());
			try {
				JSONArray jsonAr = ((JSONArray)new JSONTokener(jsonOut).nextValue());
				JSONObject charity = null;
				out = new Charity[jsonAr.length()];
				for (int i = 0; i < jsonAr.length(); i++)
				{
					charity = (JSONObject)jsonAr.get(i);
					out[i] = new Charity(charity.get("name").toString(),
							charity.getString("mission"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		catch (ParseException e1) 
		{
			return null;
		} 
		catch (IOException e1) 
		{
			return null;
		}
		//try 
		//{
			JSONObject jsonRaw;
			for (int i = 0; i < 50; i++)
			{
				//jsonRaw = (JSONObject)new JSONTokener(jsonOut).nextValue();
				//out[i] = jsonRaw.toString();
				//System.out.println(out[i]);
			}
		//}
			/*
		catch (JSONException e) 
		{
			return null;
		}*/
		return out;
	}

	public static boolean GetUserToken(Context context, String user)
	{
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet get = new HttpGet();
		String out = "RandomError";
		try 
		{
			get.setURI(new URI("http://192.168.1.117/transactions/index.php/requestToken/" + user));
			try
	    	{
	    		HttpResponse response = httpClient.execute(get);
	    		out = EntityUtils.toString(response.getEntity());
	    		JSONObject tokenObject = new JSONObject();
	    		try 
	    		{
	    			String test = out.toString();
	    			tokenObject = (JSONObject)new JSONTokener(out).nextValue();
	    			String token = "";
		    		try 
		    		{
		    			token = tokenObject.getString("donor_id");
		    			if (token == "false")
		    			{
		    				return false;
		    			}
			    		SharedPreferences prefs = context.getSharedPreferences("giv2givprefs", Context.MODE_WORLD_READABLE);
			    		SharedPreferences.Editor editor = prefs.edit();
			    		editor.putString("donor_id", token);
			    		editor.commit();
			    		return true;
		    		} 
		    		catch (JSONException e) 
		    		{
		    			return false;
		    		}
	    		} 
	    		catch (JSONException e) 
	    		{
	    			return false;
	    		}
	    	}
	    	catch (ClientProtocolException c)
	    	{
	    		return false;
	    	}
	    	catch (IOException i)
	    	{
	    		return false;
	    	}
		} 
		catch (URISyntaxException e) 
		{
			return false;
		}
	}
	
	private static HttpResponse httpGet(Context context, String url)
	{
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet get = new HttpGet();
		HttpResponse response = null;
		try 
		{
			get.setURI(new URI(url));
			try
	    	{
	    		response = httpClient.execute(get);
	    	}
	    	catch (ClientProtocolException c)
	    	{
	    		return null;
	    	}
	    	catch (IOException i)
	    	{
	    		return null;
	    	}
		} 
		catch (URISyntaxException e) 
		{
			return null;
		}
		return response;
	}
		
	@SuppressWarnings("unchecked")
	private static String httpPost(String url, Map data, Map headers) 
			throws UnsupportedEncodingException, JSONException
	{

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		Iterator iter = data.entrySet().iterator();

		JSONObject holder = new JSONObject();

		while(iter.hasNext()) 
		{
			Entry pairs = (Entry)iter.next();
			String key = (String)pairs.getKey();
			String values = (String)pairs.getValue();
			   
//			JSONObject innerData = new JSONObject();
//			Iterator iter2 = m.entrySet().iterator();
//			while(iter2.hasNext()) 
//			{
//				Entry pairs2 = (Entry)iter2.next();
//				innerData.put((String)pairs2.getKey(), (String)pairs2.getValue());
//			}
			holder.put(key, values);
		}
//		iter = headers.entrySet().iterator();
//		while (iter.hasNext())
//		{
//			Entry pairs = (Entry)iter.next();
//			String key = (String)pairs.getKey();
//			String value = (String)pairs.getValue();
//			post.setHeader(key, value);
//		}
		post.setHeader("Content-Type", "application/json");
		Log.i("SIGNUP_INFO", holder.toString());
		StringEntity entity = new StringEntity(holder.toString());
		post.setEntity(entity);

		ResponseHandler responseHandler = new BasicResponseHandler();
		try 
		{
			return client.execute(post, responseHandler).toString();
			//return response.toString();
		} 
		catch (ClientProtocolException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}
