package com.krishna.schoolbustracker;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import java.util.HashMap;
import java.util.Map;

public class ParentRegister extends AppCompatActivity {
    TextInputLayout fname,lname,email,phnum,address,brn,password,rpassword;
    String firstname,lastname,emailaddr,phone,add,pass,rpass;
    Button regbtn;
    RequestQueue queue;
    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_register);

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Just a Moment");

        fname=(TextInputLayout) findViewById(R.id.fname);
        lname=(TextInputLayout)findViewById(R.id.lname);
        email=(TextInputLayout)findViewById(R.id.email);
        phnum=(TextInputLayout)findViewById(R.id.phone);
        address=(TextInputLayout)findViewById(R.id.address);
        password=(TextInputLayout)findViewById(R.id.password1);
        rpassword=(TextInputLayout)findViewById(R.id.rpassword1);

        regbtn=(Button)findViewById(R.id.register);

        queue= Volley.newRequestQueue(this);

        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });


    }

    //Basic information checks
    private void validate() {
        firstname=fname.getEditText().getText().toString();
        lastname=lname.getEditText().getText().toString();
        emailaddr=email.getEditText().getText().toString();
        phone=phnum.getEditText().getText().toString();
        add=address.getEditText().getText().toString();
        pass=password.getEditText().getText().toString();
        rpass=rpassword.getEditText().getText().toString();

        if(firstname.isEmpty()){
            Toast.makeText(this, "enter your first name.", Toast.LENGTH_SHORT).show();
        }else if(lastname.isEmpty()){
            Toast.makeText(this, "enter your last name.", Toast.LENGTH_SHORT).show();
        }else if(emailaddr.isEmpty() || emailaddr.indexOf("@")==-1){
            Toast.makeText(this, "enter your email address", Toast.LENGTH_SHORT).show();
        }else if(phone.length()<10){
            Toast.makeText(this, "enter your phone number.", Toast.LENGTH_SHORT).show();
        }else if(add.isEmpty()){
            Toast.makeText(this, "enter your address", Toast.LENGTH_SHORT).show();

        }else if(pass.isEmpty()){
            Toast.makeText(this, "enter a password", Toast.LENGTH_SHORT).show();
        }else if(rpass.isEmpty()){
            Toast.makeText(this, "re-enter your password", Toast.LENGTH_SHORT).show();
        }else{
            if(!pass.equals(rpass)){
                Toast.makeText(this, "Your password doesn't match the re-entered one.", Toast.LENGTH_SHORT).show();
                rpassword.getEditText().getText().clear();
            }else{
                progress.show();
                register();
            }
        }

    }

    //send the data to the database
    private void register() {
        String url="http://www.thantrajna.com/sjec_01/parentRegistration.php";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        if(response.equals("exists")) {
                            Toast.makeText(ParentRegister.this, "You already registered through this email id.", Toast.LENGTH_SHORT).show();
                        }else if(response.equals("fault")){
                            Toast.makeText(ParentRegister.this, "Try again later", Toast.LENGTH_SHORT).show();
                        }else{
                            saveCredientials(response);
                            Toast.makeText(ParentRegister.this, response, Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(ParentRegister.this,MapsActivity.class);
                            startActivity(i);
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
                params.put("firstname",firstname);
                params.put("lastname",lastname);
                params.put("email",emailaddr);
                params.put("phone",phone);
                params.put("address",add);
                params.put("password",pass);

                return params;
            }

        };
        queue.add(stringRequest);
    }

    //save credentials
    private void saveCredientials(String id) {
        SharedPreferences prefs = this.getSharedPreferences("com.krishna.schoolbustracker", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=prefs.edit();
        editor.putString("email",emailaddr);
        editor.putString("password",pass);
        editor.putInt("admin",0);
        try {
            int id1=Integer.parseInt(id);
            editor.putInt("id",id1);
        }catch (Exception e){
            e.printStackTrace();
        }
        editor.commit();
    }
}
