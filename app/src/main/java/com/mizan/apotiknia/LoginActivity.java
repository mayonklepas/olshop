package com.mizan.apotiknia;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText username,password;
    Button login;
    String idcard;
    int pageinv;
    SharedPreferences sharep;
    boolean status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username=(EditText) findViewById(R.id.username);
        password=(EditText) findViewById(R.id.password);
        login=(Button) findViewById(R.id.login);
        sharep=getSharedPreferences("datalogin", Context.MODE_PRIVATE);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginfunc();
            }
        });
    }

    private boolean jsoncheck(String json){
        boolean isvalidjson;
        try {
            new JSONArray(json);
            isvalidjson=true;
        } catch (JSONException e) {
            try {
                new JSONObject(json);
                isvalidjson=true;
            } catch (JSONException e1) {
                isvalidjson=false;
            }
        }
        return isvalidjson;
    }


    private void loginfunc(){
        final ProgressDialog pd=new ProgressDialog(LoginActivity.this);
        pd.setMessage("Sedang Memuat");
        pd.setCancelable(false);
        pd.show();
                RequestQueue rq= Volley.newRequestQueue(LoginActivity.this);
                StringRequest sr=new StringRequest(Request.Method.POST, Config.url+"/login",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONArray ja=new JSONArray(response);
                                    if(ja.length()==0){
                                        status=false;
                                    }else{

                                        for (int i = 0; i < ja.length() ; i++) {
                                            JSONObject jo=ja.getJSONObject(i);
                                            Config.idcard=jo.getString("IDCARDS");
                                            Config.totalpage=jo.getInt("PAGEINV");
                                            Config.namatoko=jo.getString("NAMAPELANGGAN");
                                        }
                                        status=true;
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params=new HashMap<String, String>();
                        params.put("username", username.getText().toString());
                        params.put("password", password.getText().toString());
                        return params;
                    }
                };
                rq.add(sr);
                rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                    @Override
                    public void onRequestFinished(Request<Object> request) {
                        pd.dismiss();
                        if(status==true){
                            SharedPreferences.Editor edit=sharep.edit();
                            edit.putString("username",username.getText().toString());
                            edit.putString("password",password.getText().toString());
                            edit.putString("nama_pelanggan",Config.namatoko);
                            edit.commit();
                            Config.username=username.getText().toString();
                            Config.password=password.getText().toString();
                            Intent i=new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(i);
                            finish();
                        }else{
                            Toast.makeText(LoginActivity.this, "Username atau Password salah", Toast.LENGTH_SHORT).show();
                            //Snackbar.make(findViewById(R.id.loginform),"Username atau Password salah",1000*2).show();

                        }

                    }
                });
            }




}
