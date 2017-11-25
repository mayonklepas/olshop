package com.mizan.emha;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
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

public class Chart_fragment extends Fragment {

    RecyclerView rv;
    Chart_adapter cadapter;
    RecyclerView.LayoutManager layman;
    ProgressBar pb;

    ArrayList<Integer> nopesanan=new ArrayList<>();
    ArrayList<String> id_barang=new ArrayList<>();
    ArrayList<String> nama_barang=new ArrayList<>();
    ArrayList<String> nama_satuan=new ArrayList<>();
    ArrayList<Integer> jumlah_barang=new ArrayList<>();
    ArrayList<Double> harga_barang=new ArrayList<>();
    ArrayList<String> img_barang=new ArrayList<>();
    ArrayList<String> keterangan=new ArrayList<>();
    ArrayList<Integer> stock_barang=new ArrayList<>();
    View v;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_chart,container,false);
        pb=(ProgressBar) v.findViewById(R.id.pb);
        rv=(RecyclerView) v.findViewById(R.id.rv);
        layman=new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layman);
        rv.setHasFixedSize(true);
        rv.setItemAnimator(new DefaultItemAnimator());
        cadapter=new Chart_adapter(nopesanan,id_barang,nama_barang,nama_satuan,jumlah_barang,harga_barang,img_barang,
                keterangan,stock_barang,getActivity());
        rv.setAdapter(cadapter);
        loaddata();
        return v;
    }

    private void loaddata(){
        pb.setVisibility(View.VISIBLE);
        RequestQueue rq= Volley.newRequestQueue(getActivity());
        StringRequest sr=new StringRequest(Request.Method.POST, Config.url+"/daftarcart",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                            JSONArray ja=new JSONArray(response);
                            System.out.println(response);
                            for (int i = 0; i < ja.length() ; i++) {
                                JSONObject jo=ja.getJSONObject(i);
                                id_barang.add(jo.getString("IDBARANG"));
                                nama_barang.add(jo.getString("NAMA"));
                                jumlah_barang.add(jo.getInt("JUMLAH"));
                                stock_barang.add(jo.getInt("STOK"));
                                harga_barang.add(jo.getDouble("HARGAJUAL"));
                                nama_satuan.add(jo.getString("SATUAN"));
                                keterangan.add(jo.getString("KETERANGAN"));
                                nopesanan.add(jo.getInt("NOINDEX"));
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
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("idcards",Config.idcard);
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
                cadapter.notifyDataSetChanged();
                if(id_barang.size()==0){
                    Snackbar.make(v,"Pesanan Kosong",3*1000).show();
                }
            }
        });
    }

}
