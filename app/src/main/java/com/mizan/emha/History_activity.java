package com.mizan.emha;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Minami on 08/09/2017.
 */

public class History_activity extends AppCompatActivity {

    SearchView sv;
    RecyclerView rv;
    History_adapter adapter;
    RecyclerView.LayoutManager layman;
    ProgressBar pb;

    ArrayList<String> noindex=new ArrayList<>();
    ArrayList<String> kode=new ArrayList<>();
    ArrayList<String> nama=new ArrayList<>();
    ArrayList<Integer> qty=new ArrayList<>();
    ArrayList<String> satuan=new ArrayList<>();
    ArrayList<String> img_barang=new ArrayList<>();
    ArrayList<Double> harga=new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        sv=(SearchView) findViewById(R.id.sv);
        pb=(ProgressBar) findViewById(R.id.pb);
        rv=(RecyclerView) findViewById(R.id.rv);
        layman=new LinearLayoutManager(this);
        rv.setLayoutManager(layman);
        rv.setHasFixedSize(true);
        rv.setItemAnimator(new DefaultItemAnimator());
        adapter=new History_adapter(noindex,kode,nama,qty,satuan,harga,img_barang,this);
        rv.setAdapter(adapter);
        Bundle ex=getIntent().getExtras();
        loaddata(ex.getString("id_transaksi"));

    }


    private void loaddata(final String id_transaksi){
        pb.setVisibility(View.VISIBLE);
        RequestQueue rq= Volley.newRequestQueue(this);
        StringRequest sr=new StringRequest(Request.Method.POST, Config.url+"/historyorderdetail",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray ja=new JSONArray(response);
                            for (int i = 0; i < ja.length() ; i++) {
                                JSONObject jo=ja.getJSONObject(i);
                                noindex.add(jo.getString("NOINDEX"));
                                kode.add(jo.getString("KODE"));
                                nama.add(jo.getString("NAMA"));
                                qty.add(jo.getInt("QTY"));
                                satuan.add(jo.getString("SATUAN"));
                                harga.add(jo.getDouble("HARGA"));
                                img_barang.add(Config.url+"/barang/"+jo.getString("ID_IMAGE")+".jpg");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(History_activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("idtransaksi",id_transaksi);
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(1000*15,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                pb.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }
        });
    }

}
