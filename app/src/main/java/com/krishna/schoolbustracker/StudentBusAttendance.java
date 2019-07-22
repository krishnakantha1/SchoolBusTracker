package com.krishna.schoolbustracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.krishna.schoolbustracker.fragments.Evening;
import com.krishna.schoolbustracker.fragments.Morning;
import com.krishna.schoolbustracker.models.StudentModels;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentBusAttendance extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    String busno="";
    RequestQueue queue;
    List<StudentModels> studentList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_bus_attendance);

        queue= Volley.newRequestQueue(this);

        busno=getIntent().getExtras().getString("busno");

        tabLayout=(TabLayout)findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Morning"));
        tabLayout.addTab(tabLayout.newTab().setText("Evening"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager=(ViewPager)findViewById(R.id.pager);

        getStudents();
    }


    private void getStudents() {
        String url="http://www.thantrajna.com/sjec_01/getStudentList.php";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("student_info");
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject object=jsonArray.getJSONObject(i);
                                StudentModels studentModels=new StudentModels();
                                studentModels.setName(object.getString("student_name"));
                                studentModels.setId(object.getString("student_id"));
                                studentList.add(studentModels);
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        FragmentAdapter fragmentAdapter=new FragmentAdapter(getSupportFragmentManager(),tabLayout.getTabCount(),studentList,busno);
                        viewPager.setAdapter(fragmentAdapter);
                        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                            @Override
                            public void onTabSelected(TabLayout.Tab tab) {
                                viewPager.setCurrentItem(tab.getPosition());
                            }

                            @Override
                            public void onTabUnselected(TabLayout.Tab tab) {

                            }

                            @Override
                            public void onTabReselected(TabLayout.Tab tab) {

                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError{
                Map<String,String> params=new HashMap<>();
                params.put("busno",busno);
                return params;
            }
        };
        queue.add(stringRequest);
    }


    class FragmentAdapter extends FragmentStatePagerAdapter implements Serializable {
        int counttab;
        Bundle bundle;
        Morning morning;
        Evening evening;
        List<StudentModels> studentList=new ArrayList<>();
        String busno;
        public FragmentAdapter(FragmentManager fm, int counttab, List<StudentModels> studentList, String busno) {
            super(fm);
            this.counttab=counttab;
            this.studentList=studentList;
            bundle=new Bundle();
            morning=new Morning();
            evening=new Evening();
            this.busno=busno;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    bundle.putSerializable("students",(Serializable)studentList);
                    bundle.putString("busno",busno);
                    morning.setArguments(bundle);
                    return morning;
                case 1:
                    bundle.putSerializable("students",(Serializable)studentList);
                    bundle.putString("busno",busno);
                    evening.setArguments(bundle);
                    return evening;
            }
            return null;
        }

        @Override
        public int getCount() {
            return counttab;
        }
    }
}
