package com.mizan.emha;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class Splashscreen extends AppCompatActivity {
    boolean status;
    SharedPreferences sharep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        sharep=getSharedPreferences("datalogin", Context.MODE_PRIVATE);
        Intent i=new Intent(Splashscreen.this,LoginActivity.class);
        startActivity(i);
        finish();
        //loginfunc();
    }


    private void loginfunc(){
        RequestQueue rq= Volley.newRequestQueue(Splashscreen.this);
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
                        Toast.makeText(Splashscreen.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<String, String>();
                params.put("username", sharep.getString("username","n/a"));
                params.put("password", sharep.getString("password","n/a"));
                return params;
            }
        };
        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                if(status==true){
                    Config.username=sharep.getString("username","n/a");
                    Config.password=sharep.getString("password","n/a");
                    Intent i=new Intent(Splashscreen.this,MainActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    Intent i=new Intent(Splashscreen.this,LoginActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        });
    }
}
