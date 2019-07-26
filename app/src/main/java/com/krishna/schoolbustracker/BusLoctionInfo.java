package com.krishna.schoolbustracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.krishna.schoolbustracker.adapter.BusLocInfoAdapter;
import com.krishna.schoolbustracker.models.busLocInfoModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusLoctionInfo extends AppCompatActivity {
    ProgressDialog progress;
    TextView date,busname;
    ListView infolist;
    int year,month,day;
    RequestQueue queue;
    String busno,formateddate;
    DatePickerDialog.OnDateSetListener listener;
    Map<Integer,String> letterMonth=new HashMap<>();
    List<busLocInfoModel> datalist=new ArrayList<>();
    BusLocInfoAdapter busLocInfoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_loction_info);
        date=(TextView)findViewById(R.id.date);
        busname=(TextView)findViewById(R.id.businfo) ;
        busno=getIntent().getExtras().getString("busno");
        queue= Volley.newRequestQueue(this);

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Just a Moment");

        //As to maintain uniformity with the database entry for date.
        letterMonth.put(0,"Jan");
        letterMonth.put(1,"Feb");
        letterMonth.put(2,"Mar");
        letterMonth.put(3,"Apr");
        letterMonth.put(4,"May");
        letterMonth.put(5,"Jun");
        letterMonth.put(6,"Jul");
        letterMonth.put(7,"Aug");
        letterMonth.put(8,"Sep");
        letterMonth.put(9,"Oct");
        letterMonth.put(10,"Nov");
        letterMonth.put(11,"Dec");

        infolist=(ListView)findViewById(R.id.busLocInfo);

        //Get current date from phone.
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month++;
        date.setText(day+" / "+month+" / "+year);
        busname.setText(busno.toUpperCase());

        //to pick date. press the text view to do it.
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(BusLoctionInfo.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, listener, year, month-1, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        listener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int pyear, int pmonth, int pday) {
                day=pday;
                month=pmonth+1;
                year=pyear;
                String sdate=day+" / "+month+" / "+year;
                date.setText(sdate);
                infolist.setAdapter(null);
                datalist.clear();
                progress.show();
                getData(letterMonth.get(pmonth));
            }
        };
        progress.show();
        getData(letterMonth.get(month-1));
    }

    //get data from database to populate the listview.
    private void getData(String mon) {
        formateddate=year+"-"+mon+"-"+day;
        String url="http://www.thantrajna.com/sjec_01/getBusLocInfo.php";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("locinfo");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jobj = jsonArray.getJSONObject(i);
                                busLocInfoModel blim=new busLocInfoModel();
                                blim.setplace(jobj.getString("place"));
                                blim.setTime(jobj.getString("time"));
                                blim.setLat(Double.parseDouble(jobj.getString("lat")));
                                blim.setLng(Double.parseDouble(jobj.getString("lng")));
                                datalist.add(blim);
                            }
                            busLocInfoAdapter = new BusLocInfoAdapter(BusLoctionInfo.this, datalist);
                            infolist.setAdapter(busLocInfoAdapter);
                            busLocInfoAdapter.notifyDataSetChanged();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.dismiss();
                    }
                }){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError{
                Map<String,String> params=new HashMap<>();
                params.put("busno",busno);
                params.put("date",formateddate);
                return params;
        }};
        queue.add(stringRequest);
    }
}
