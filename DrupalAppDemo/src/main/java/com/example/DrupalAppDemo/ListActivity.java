package com.example.DrupalAppDemo;

import android.os.*;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.*;
import android.content.*;
import android.widget.*;

import org.apache.http.util.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;

import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import java.util.*;

public class ListActivity extends Activity {

    public String session_id;
    public String session_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();

        //read the session_id and session_name variables
        if (extras != null) {
            session_id = extras.getString("SESSION_ID");
            session_name = extras.getString("SESSION_NAME");
        }

        //initiate the background process to fetch the latest items on Drupal site
        new FetchItems().execute();
    }

    private class FetchItems extends AsyncTask<String, Void, JSONArray> {

        protected JSONArray doInBackground(String... params) {


            HttpClient httpclient = new DefaultHttpClient();

            HttpGet httpget = new HttpGet("http://ec2-54-244-72-198.us-west-2.compute.amazonaws.com/cmac/rest/node");
            //set header to tell REST endpoint the request and response content types
            httpget.setHeader("Accept", "application/json");
            httpget.setHeader("Content-type", "application/json");

            JSONArray json = new JSONArray();

            try {

                HttpResponse response = httpclient.execute(httpget);

                //read the response and convert it into JSON array
                json = new JSONArray(EntityUtils.toString(response.getEntity()));
                //return the JSON array for post processing to onPostExecute function
                return json;



            }catch (Exception e) {
                Log.v("Error adding article",e.getMessage());
            }



            return json;
        }


        //executed after the background nodes fetching process is complete
        protected void onPostExecute(JSONArray result) {

            //get the ListView UI element
            ListView lst = (ListView)  findViewById(R.id.listView);

            //create the ArrayList to store the titles of nodes
            ArrayList<String> listItems=new ArrayList<String>();

            //iterate through JSON to read the title of nodes
            for(int i=0;i<result.length();i++){
                try {
                    listItems.add(result.getJSONObject(i).getString("title").toString());
                } catch (Exception e) {
                    Log.v("Error adding article", e.getMessage());
                }
            }

            //create array adapter and give it our list of nodes, pass context, layout and list of items
            ArrayAdapter ad= new ArrayAdapter(ListActivity.this, android.R.layout.simple_list_item_1,listItems);

            //give adapter to ListView UI element to render
            lst.setAdapter(ad);
        }
    }

    //click listener for addArticleButton
    public void addArticleButton_click(View view){

        //create intent to start AddArticle activity
        Intent intent = new Intent(this, AddArticle.class);
        //pass the session information
        intent.putExtra("SESSION_ID", session_id);
        intent.putExtra("SESSION_NAME", session_name);
        //start the AddArticle activity
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
