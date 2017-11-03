package com.mizan.apotiknia;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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

public class Header_history_fragment extends Fragment {

    RecyclerView rv;
    RecyclerView.LayoutManager layman;
    ProgressBar pb;
    Header_history_adapter hadapter;
    Typeface tp;

    ArrayList<String> idtransaksi=new ArrayList<>();
    ArrayList<String> notransaksi=new ArrayList<>();;
    ArrayList<String> tanggal=new ArrayList<>();
    ArrayList<String> status=new ArrayList<>();
    ArrayList<Double> totalorder=new ArrayList<>();
    int cpage=0;
    String keyword;
    View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_header_history,container,false);
        pb=(ProgressBar) v.findViewById(R.id.pb);
        rv=(RecyclerView) v.findViewById(R.id.rv);
        layman=new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layman);
        rv.setHasFixedSize(true);
        rv.setItemAnimator(new DefaultItemAnimator());
        hadapter=new Header_history_adapter(idtransaksi,notransaksi,tanggal,status,totalorder,getActivity());
        rv.setAdapter(hadapter);
        loaddata();
        rv.addOnScrollListener(new Endlessscrolllinear() {
            @Override
            public void onLoadMore() {
                loaddata();
                cpage++;
            }
        });

        return v;
    }


    private void cleardata(){
        idtransaksi.clear();
        notransaksi.clear();
        tanggal.clear();
        status.clear();
        totalorder.clear();
        hadapter.notifyDataSetChanged();
    }

    private void loaddata(){
        pb.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RequestQueue rq= Volley.newRequestQueue(getActivity());
                StringRequest sr=new StringRequest(Request.Method.POST, Config.url+"/historyorder",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONArray ja=new JSONArray(response);
                                    for (int i = 0; i < ja.length() ; i++) {
                                        JSONObject jo=ja.getJSONObject(i);
                                        idtransaksi.add(jo.getString("IDTRANSAKSI"));
                                        notransaksi.add(jo.getString("NOTRANSAKSI"));
                                        tanggal.add(jo.getString("TANGGAL"));
                                        status.add(jo.getString("STATUS"));
                                        totalorder.add(jo.getDouble("TOTALORDER"));

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
                        params.put("halaman", String.valueOf(cpage));
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
                        hadapter.notifyDataSetChanged();
                        if(idtransaksi.size()==0){
                            Snackbar.make(v,"History Kosong",3*1000).show();
                        }
                    }
                });
            }
        },100);

    }


}
