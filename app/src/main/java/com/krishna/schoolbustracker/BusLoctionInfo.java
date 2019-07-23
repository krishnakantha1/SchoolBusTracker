package com.krishna.schoolbustracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;

public class BusLoctionInfo extends AppCompatActivity {
    TextView date;
    ListView infolist;
    int year,month,day;
    RequestQueue queue;
    DatePickerDialog.OnDateSetListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_loction_info);
        date=(TextView)findViewById(R.id.date);

        queue= Volley.newRequestQueue(this);

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        date.setText(day+" / "+month+" / "+year);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog dialog = new DatePickerDialog(BusLoctionInfo.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, listener, year, month, day);
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
            }
        };
    }
}
