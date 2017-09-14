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
import android.text.InputType;
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

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Minami on 08/09/2017.
 */

public class Barang_fragment extends Fragment {

    SearchView sv;
    RecyclerView rv;
    RecyclerView.LayoutManager layman;
    ProgressBar pb;
    Barang_adapter badapter;
    Typeface tp;

    ArrayList<String> id_barang=new ArrayList<>();
    ArrayList<String> nama_barang=new ArrayList<>();
    ArrayList<Double> harga_barang=new ArrayList<>();
    ArrayList<Integer> stock_barang=new ArrayList<>();
    ArrayList<String> img_barang=new ArrayList<>();
    ArrayList<String> satuan=new ArrayList<>();
    int cpage=0;
    String keyword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_barang,container,false);
        sv=(SearchView) v.findViewById(R.id.sv);
        pb=(ProgressBar) v.findViewById(R.id.pb);
        rv=(RecyclerView) v.findViewById(R.id.rv);
        layman=new GridLayoutManager(getActivity(),2);
        rv.setLayoutManager(layman);
        rv.setHasFixedSize(true);
        rv.setItemAnimator(new DefaultItemAnimator());
        badapter=new Barang_adapter(id_barang,nama_barang,harga_barang,stock_barang,img_barang,satuan,getActivity());
        rv.setAdapter(badapter);
        loaddata();
        rv.addOnScrollListener(new Endlessscroll() {
            @Override
            public void onLoadMore() {
                if (keyword==null || keyword.isEmpty()){
                    loaddata();
                    cpage++;
                }else{
                    caridata(keyword);
                    cpage++;
                }

            }
        });

        sv.setQueryHint("Cari Barang");
        sv.setIconifiedByDefault(true);
        sv.setSubmitButtonEnabled(false);
        sv.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        SearchView.OnQueryTextListener quelist=new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                keyword=query;
                cpage=0;
                cleardata();
                rv.setAdapter(badapter);
                caridata(keyword);
                rv.addOnScrollListener(new Endlessscroll() {
                    @Override
                    public void onLoadMore() {
                        if (keyword==null || keyword.isEmpty()){
                            loaddata();
                            cpage++;
                        }else{
                            caridata(keyword);
                            cpage++;
                        }
                    }
                });
                sv.setIconified(true);
                sv.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        };

        sv.setOnQueryTextListener(quelist);

        return v;
    }


    private void cleardata(){
        id_barang.clear();
        nama_barang.clear();
        stock_barang.clear();
        harga_barang.clear();
        img_barang.clear();
        badapter.notifyDataSetChanged();
    }

    private void loaddata(){
        pb.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RequestQueue rq= Volley.newRequestQueue(getActivity());
                StringRequest sr=new StringRequest(Request.Method.POST, Config.url+"/getdaftarbarang",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    //Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
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
                        params.put("pageinv", String.valueOf(cpage));
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
                        badapter.notifyDataSetChanged();
                    }
                });
            }
        },100);

    }

    private void caridata(final String keyword){
        pb.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RequestQueue rq= Volley.newRequestQueue(getActivity());
                StringRequest sr=new StringRequest(Request.Method.POST, Config.url+"/getdaftarbarangcari",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONArray ja=new JSONArray(response);
                                    System.out.println(ja.length());
                                    for (int i = 0; i < ja.length() ; i++) {
                                        JSONObject jo=ja.getJSONObject(i);
                                        id_barang.add(jo.getString("NOINDEX"));
                                        nama_barang.add(jo.getString("NAMA"));
                                        satuan.add(jo.getString("SATUAN"));
                                        stock_barang.add(jo.getInt("STOK"));
                                        harga_barang.add(jo.getDouble("HARGAJUAL"));
                                        img_barang.add(Config.url+"/"+jo.getString("NOINDEX")+".jpg");

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
                        params.put("cari", keyword);
                        params.put("pageinv", String.valueOf(cpage));
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
                        badapter.notifyDataSetChanged();
                    }
                });
            }
        },100);
    }

}
