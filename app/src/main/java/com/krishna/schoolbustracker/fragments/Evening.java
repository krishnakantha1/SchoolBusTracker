package com.krishna.schoolbustracker.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.krishna.schoolbustracker.R;
import com.krishna.schoolbustracker.adapter.StudentAdapter;
import com.krishna.schoolbustracker.models.StudentModels;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Evening extends Fragment {
    List<StudentModels> studentList=new ArrayList<>();
    JSONObject idObject=new JSONObject();
    JSONObject statusObject=new JSONObject();
    ListView listView;
    StudentAdapter studentAdapter;
    String busno;
    Button upload;
    RequestQueue queue;
    ProgressDialog progress;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        queue=Volley.newRequestQueue(getContext());

        View root=inflater.inflate(R.layout.evening, container, false);
        listView=(ListView)root.findViewById(R.id.studentlistevening);
        studentList=(List<StudentModels>) getArguments().getSerializable("students");
        busno=getArguments().getString("busno");
        studentAdapter = new StudentAdapter(getActivity(), studentList);
        listView.setAdapter(studentAdapter);
        studentAdapter.notifyDataSetChanged();

        progress = new ProgressDialog(getContext());
        progress.setTitle("Loading");
        progress.setMessage("Just a Moment");

        upload=(Button)root.findViewById(R.id.eveningSubmit);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                while(idObject.length()>0) {
                    idObject.remove(idObject.keys().next());
                    statusObject.remove(statusObject.keys().next());
                }
                getStatus();
            }
        });

        return root;
    }

    //to check which students are present and which are absent.
    private void getStatus() {
        int count=0;
        for(int i=0;i<studentList.size();i++){
            Object obj=listView.getItemAtPosition(i);
            StudentModels sm=(StudentModels)obj;
            if(sm.getCheck()){
                try {
                    idObject.put("count" + count, sm.getId());
                    statusObject.put("status"+count++,"1");
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }else{
                try{
                    idObject.put("count"+count,sm.getId());
                    statusObject.put("status"+count++,"0");
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }
        progress.show();
        sendData();
    }

    //to perform action depending the status of the students.
    private void sendData() {
        String url="http://www.thantrajna.com/sjec_01/evening.php";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.dismiss();
                    }
                }){@Override
            protected Map<String,String> getParams()throws AuthFailureError{
                    Map<String,String> params=new HashMap<>();
                    params.put("length",""+idObject.length());
                    params.put("ids",idObject.toString());
                    params.put("status",statusObject.toString());
                    params.put("busno",busno);
                    return params;
        }

        };
        queue.add(stringRequest);
    }
}
