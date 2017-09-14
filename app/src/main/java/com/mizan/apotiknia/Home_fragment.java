package com.mizan.apotiknia;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by Minami on 08/09/2017.
 */

public class Home_fragment extends Fragment {

    RecyclerView rv;
    RecyclerView.LayoutManager layman;
    ProgressBar pb;
    Home_adapter adapter;

    ArrayList<String> id_barang=new ArrayList<>();
    ArrayList<String> nama_barang=new ArrayList<>();
    ArrayList<Double> harga_barang=new ArrayList<>();
    ArrayList<Integer> stock_barang=new ArrayList<>();
    ArrayList<String> img_barang=new ArrayList<>();
    ArrayList<String> satuan=new ArrayList<>();

    ArrayList<String> imgslide=new ArrayList<>();
    ViewPager pager;
    CircleIndicator ci;
    Handler handler;
    int currentpage=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_home,container,false);
        imgslide.add(Config.url+"/slide/img1.jpg");
        imgslide.add(Config.url+"/slide/img2.jpg");
        imgslide.add(Config.url+"/slide/img3.jpg");
        pb=(ProgressBar) v.findViewById(R.id.pb);
        rv=(RecyclerView) v.findViewById(R.id.rv);
        layman=new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layman);
        rv.setHasFixedSize(true);
        rv.setItemAnimator(new DefaultItemAnimator());
        adapter=new Home_adapter(id_barang,nama_barang,harga_barang,stock_barang,img_barang,satuan,getActivity());
        rv.setAdapter(adapter);
        loaddata();
        initslider(imgslide,v);
        return v;
    }

    private void initslider(final ArrayList<String> imgslide, View v) {
        pager = (ViewPager) v.findViewById(R.id.pager);
        pager.setAdapter(new Viewpageradapter(imgslide, getActivity()));
        ci = (CircleIndicator) v.findViewById(R.id.indicator);
        ci.setViewPager(pager);
        handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                pager.setCurrentItem(currentpage,true);
                handler.postDelayed(this,1000*3);
                currentpage=currentpage+1;
                if(currentpage==3){
                    currentpage=0;
                }
            }
        };
        handler.postDelayed(r, 1000*3);

    }

    private void loaddata(){
        pb.setVisibility(View.VISIBLE);
                RequestQueue rq= Volley.newRequestQueue(getActivity());
                StringRequest sr=new StringRequest(Request.Method.POST, Config.url+"/getproductbestseller",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONArray ja=new JSONArray(response);

                                    for (int i = 0; i < ja.length() ; i++) {
                                        JSONObject jo=ja.getJSONObject(i);
                                        id_barang.add(jo.getString("NOINDEX"));
                                        nama_barang.add(jo.getString("NAMA"));
                                        satuan.add(jo.getString("SATUAN"));
                                        stock_barang.add(jo.getInt("STOK"));
                                        harga_barang.add(jo.getDouble("HARGAJUAL"));
                                        img_barang.add(Config.url+"/barang/"+jo.getString("NOINDEX")+".jpg");

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
                        Map<String,String> params=new HashMap<String, String>();
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
                        adapter.notifyDataSetChanged();
                    }
                });

    }
}
