package com.nitolmotorsltd.jobscheduler;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class ExampleJobService extends JobService {
    private static final String TAG = "ExampleJobService";
    //private boolean jobCancelled = false;

    private String url = "http://172.16.1.131:8888/ncalllog/save.php";
    private String send_url = "http://172.16.1.131:8888/ncalllog/save.php";

    private Timer mTimer = new Timer();

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
       // doBackgroundWork(params);
        mTimer.scheduleAtFixedRate(new MyTask(), 0,    10000);
        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "doBackgroundWork: run: calling");
                saveLocation();
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        //jobCancelled = true;
        return true;
    }

    private class MyTask extends TimerTask {
        @Override
        public void run() {
            saveLocation();
        }
    }

    public void saveLocation(){
        Log.d(TAG, "saveLocation: calling");
        send_url = url + "?value="+getCurrentDateTime();
        Log.d(TAG, "saveLocation: Send URL: " + send_url);

        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                send_url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: ");

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
            }
        });

        mRequestQueue.add(jsonObjReq);

    }

    public String getCurrentDateTime() {
        String date_time = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(new Date());
        return date_time;
    }

}