package com.krishna.schoolbustracker;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Myservice extends IntentService {
    int admin=0;
    int id=-1;
    SharedPreferences sharedPreferences;
    boolean run=false;
    double[] latitude=null;
    double[] longitude=null;
    String[] busno=null;
    String[] iconurl=null;
    int lenn=0;
    private Timer timer=new Timer();
    RequestQueue queue;


    public Myservice() {
        super("Myservice");
    }



    @Override
    protected void onHandleIntent(final Intent i) {
        sharedPreferences=getSharedPreferences("com.krishna.schoolbustracker", Context.MODE_PRIVATE);
        admin=sharedPreferences.getInt("admin",0);
        id=sharedPreferences.getInt("id",-1);
        queue = Volley.newRequestQueue(this);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //If previous String request has been responded to by the server
                if (run == false) {
                    run=true;
                    String url = "http://www.thantrajna.com/sjec_01/getLocation.php";
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        run=false;
                                        JSONObject jsonObject = new JSONObject(response);
                                        JSONArray jsonArray = jsonObject.getJSONArray("loc_info");
                                        lenn=jsonArray.length();
                                        latitude = new double[lenn];
                                        longitude = new double[lenn];
                                        busno=new String[lenn];
                                        iconurl=new String[lenn];

                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jobj = jsonArray.getJSONObject(i);
                                            latitude[i] = jobj.getDouble("lat");
                                            longitude[i] = jobj.getDouble("lng");
                                            busno[i]=jobj.getString("busno");
                                            iconurl[i]=jobj.getString("url");

                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    ResultReceiver rr = i.getParcelableExtra("reciver");
                                    Bundle b = new Bundle();
                                    b.putDoubleArray("lat", latitude);
                                    b.putDoubleArray("lng", longitude);
                                    b.putInt("leng",lenn);
                                    b.putStringArray("busno",busno);
                                    b.putStringArray("url",iconurl);
                                    rr.send(MapsActivity.RESULT_CODE, b);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    run=false;
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("id",""+id);
                            if(admin==1)
                                params.put("code", "getall");
                            else if(admin==0)
                                params.put("code", "getone");
                            else
                                params.put("code","getDriver");
                            return params;
                        }
                    };
                    queue.add(stringRequest);
                }
            }
        },0,5000);
    }
}
