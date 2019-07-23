package com.krishna.schoolbustracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    RequestQueue queue;
    String uid,pass;
    TextInputLayout username,password;
    Button login;
    TextView parent;
    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //login directly if users have'nt logged out in the previous session.
        SharedPreferences sharedPreferences=getSharedPreferences("com.krishna.schoolbustracker",Context.MODE_PRIVATE);
        String def="#$%^^&";
        String namesaved=sharedPreferences.getString("email",def);
        String passwordsaved=sharedPreferences.getString("password",def);
        if(!(namesaved.equals(def) || passwordsaved.equals(def))){
            Intent i=new Intent(MainActivity.this,MapsActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Just a Moment");

        login=(Button)findViewById(R.id.loginbtn);
        username=(TextInputLayout)findViewById(R.id.username);
        password=(TextInputLayout)findViewById(R.id.password);
        parent=(TextView)findViewById(R.id.parent);
        queue= Volley.newRequestQueue(this);

        //For login. Checks if credentials are entered and calls for validation.
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uid=username.getEditText().getText().toString();
                pass=password.getEditText().getText().toString();
                if(uid.isEmpty()){
                    Toast.makeText(MainActivity.this, "Enter your email id", Toast.LENGTH_SHORT).show();
                }else if(pass.isEmpty()){
                    Toast.makeText(MainActivity.this, "Enter a valid password", Toast.LENGTH_SHORT).show();
                }else{
                    progress.show();
                    validate();
                }
            }
        });

        //For parent registration.
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,ParentRegister.class);
                startActivity(i);

            }
        });

    }

    //Validates the user by querying the database.
    private void validate() {
        String url="http://www.thantrajna.com/sjec_01/loginValidation.php";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        //Email or password is wrong/unregistered.
                        if(response.equalsIgnoreCase("fault")) {
                            Toast.makeText(MainActivity.this,"email or password is wrong." , Toast.LENGTH_SHORT).show();
                        }
                        //Obtaining further details from the database through the response.
                        else {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray=jsonObject.getJSONArray("sub_info");
                                JSONObject jobject=jsonArray.getJSONObject(0);
                                saveCredientials(jobject.getString("id"),jobject.getString("admin"));
                                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.dismiss();
                        Toast.makeText(MainActivity.this, ""+error, Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String,String> getParams()throws AuthFailureError {
                        Map<String,String> parms=new HashMap<String, String>();
                        parms.put("userid",uid);
                    parms.put("password",pass);
                    return parms;
                }
        };
        queue.add(stringRequest);
    }

    //If validated saves the details in "com.krishna.schoolbustracker" file for future use.
    private void saveCredientials(String id,String admin) {
        SharedPreferences prefs = this.getSharedPreferences("com.krishna.schoolbustracker", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=prefs.edit();
        editor.putString("email",uid);
        editor.putString("password",pass);
        try {
            int id1=Integer.parseInt(id);
            editor.putInt("id",id1);
            editor.putInt("admin",Integer.parseInt(admin));
        }catch (Exception e){
            e.printStackTrace();
        }

        editor.commit();
    }


}
