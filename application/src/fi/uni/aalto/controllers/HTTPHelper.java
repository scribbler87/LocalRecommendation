package fi.uni.aalto.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import fi.uni.aalto.activities.LocalRecommendationsActivity;

public class HTTPHelper {
	public static String executePostRequest( HashMap<String, String> pairData, String url) {
		String res = "";
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		httppost.setHeader("User-Agent", "dalvik");


		try {
			// Add your data

			Set<String> keySet = pairData.keySet();
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(keySet.size());
			for (String key : keySet) 
			{
				nameValuePairs.add(new BasicNameValuePair(key, pairData.get(key)));
			}


			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));


			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);


			res = readResponse(response);

		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}




	public static String readResponse(HttpResponse response)
			throws IOException, UnsupportedEncodingException, Exception {
		String res = "";
		InputStream inputStream  = response.getEntity().getContent();

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));

		String string = bufferedReader.toString();

		if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
		{
			throw new Exception(response.getStatusLine().getReasonPhrase());
			//			System.err.println("Error: " + response.getStatusLine().getReasonPhrase());
			//			return res;
		}

		StringBuilder stringBuilder = new StringBuilder();

		String answer = "";
		for ( int c; ( c = bufferedReader.read() ) != -1; ) 
		{
			answer += ( (char) c ); 
		}

		bufferedReader.close();
		inputStream.close();

		res = answer;
		return res;
	} 

	public static String sendPostPost(String title, String description, String id, String access_token, String picUrl) throws MalformedURLException, IOException, JSONException
	{
		String res = "";
		String destUrl = LocalRecommendationsActivity.BASE_ADDRESS + "/post/create";

		HashMap<String, String> data = new HashMap<String, String>();
		data.put("title", title);
		data.put("description",description);
		data.put("id",id);
		data.put("access_token", access_token);
		data.put("link", picUrl);

		res = executePostRequest( data, destUrl);
		return res;
	}

	public static String getPostData(String firstName, String lastName, long expires, String id,String access_token, String url) throws MalformedURLException, IOException, JSONException
	{
		String res = "";
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("firstName", firstName);
		data.put("lastName",lastName);
		data.put("expires", Long.toString(expires));
		data.put("id",id);
		data.put("access_token", access_token);

		res = executePostRequest( data, url);
		return res;
	}

	public static String getGetData(String firstName, String lastName, long expires, String id,String access_token, String url) throws MalformedURLException, IOException, JSONException
	{
		String res = "";
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("firstName", firstName);
		data.put("lastName",lastName);
		data.put("expires", Long.toString(expires));
		data.put("id",id);
		data.put("access_token", access_token);

		res = executePostRequest( data, url);

		return res;
	}

	public static String executeGetRequest( HashMap<String, String> pairData, String url) {
		String res = "";
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);


		httpGet.setHeader("User-Agent", "dalvik");


		try {
			// Add your data
			if(pairData != null)
			{
				Set<String> keySet = pairData.keySet();
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(keySet.size());
				for (String key : keySet) 
				{
					nameValuePairs.add(new BasicNameValuePair(key, pairData.get(key)));
				}


				((HttpResponse) httpGet).setEntity(new UrlEncodedFormEntity(nameValuePairs));
			}
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httpGet);


			res = readResponse(response);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
}