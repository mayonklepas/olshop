package com.mizan.emha;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Detail_barang_Activity extends AppCompatActivity {

    TextView nama,harga,stock,keterangan;
    Spinner satuan;
    ImageView app_bar_image;
    Button pesan;
    EditText jumlahpesan,keterangantambahan;
    ArrayList<satuanentity> satuanlist=new ArrayList<>();
    StringBuilder sbmultiharga=new StringBuilder();
    NumberFormat nf=NumberFormat.getInstance();
    String idsatuanpilih,result;
    static String statustransaksitop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_barang_);
        nama=(TextView) findViewById(R.id.nama_barang);
        stock=(TextView) findViewById(R.id.stock_barang);
        keterangan=(TextView) findViewById(R.id.keterangan);
        satuan=(Spinner) findViewById(R.id.satuan);
        pesan=(Button) findViewById(R.id.pesan);
        jumlahpesan=(EditText) findViewById(R.id.jumlahpesan);
        keterangantambahan=(EditText) findViewById(R.id.keterangantambahan);
        app_bar_image=(ImageView) findViewById(R.id.app_bar_image);
        Bundle ex=getIntent().getExtras();
        final String id_barang=ex.getString("id_barang");
        String nama_barang=ex.getString("nama_barang");
        String stock_barang=ex.getString("stock");
        String img_barang=ex.getString("img_barang");
        final String statustransaksi=ex.getString("statustransaksi");
        final String idpesanan=String.valueOf(ex.getInt("idpesanan"));
        nama.setText(nama_barang);
        stock.setText("Stock : "+stock_barang);
        if(ex.getInt("jumlahbarang")==0){
            jumlahpesan.setText("1");
        }else{
            jumlahpesan.setText(String.valueOf(ex.getInt("jumlahbarang")));
        }

        jumlahpesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumlahpesan.setText("");
            }
        });

        keterangantambahan.setText(ex.getString("keterangan"));
        Glide.with(this).
                load(img_barang).
                diskCacheStrategy(DiskCacheStrategy.ALL).
                crossFade().centerCrop().placeholder(R.drawable.placeholder).
        into(app_bar_image);
        loaddata(id_barang);
        pesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(jumlahpesan.getText().toString().isEmpty()){
                    Snackbar.make(findViewById(R.id.detaildatabarang), "Pesanan Tidak Boleh Kosong" ,1000*3).show();
                }else{
                    if(statustransaksi.equals("insert")){
                        insertdatabarang(id_barang);
                    }else{
                        updatedatabarang(id_barang,idpesanan);
                    }

                }

            }
        });
    }


    private void loaddata(final String id_barang){
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setCancelable(true);
        pd.setMessage("Memuat Data...");
        pd.show();
        RequestQueue rq= Volley.newRequestQueue(Detail_barang_Activity.this);
        StringRequest sr=new StringRequest(Request.Method.POST, Config.url+"/getdetailbarang",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        try {
                            JSONObject jo=new JSONObject(response);

                            JSONArray jamultisatuan=jo.getJSONArray("multisatuan");
                            for (int i = 0; i < jamultisatuan.length(); i++) {
                                JSONObject joins=jamultisatuan.getJSONObject(i);
                                satuanlist.add(new satuanentity(joins.getString("IDSATUAN"),joins.getString("KODESATUAN")));
                            }


                            JSONArray jamultiharga=jo.getJSONArray("multihargajual");

                            for (int i = 0; i < jamultiharga.length(); i++) {
                                JSONObject joins=jamultiharga.getJSONObject(i);
                                sbmultiharga.append(joins.getString("QTY")+" = Rp. "+
                                        nf.format(joins.getDouble("HARGAJUAL"))+" / "+
                                                joins.getString("SATUAN")+
                                        "\n");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Detail_barang_Activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<String, String>();
                params.put("idcards",Config.idcard);
                params.put("idbarang",id_barang);
                return params;
            }
        };
        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                ArrayAdapter<satuanentity> satuanadapter=new ArrayAdapter<satuanentity>(Detail_barang_Activity.this,
                        android.R.layout.simple_spinner_item,satuanlist);
                satuanadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                satuan.setAdapter(satuanadapter);
                keterangan.setText(sbmultiharga.toString());
                pd.dismiss();
            }
        });


        satuan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                satuanentity snt=(satuanentity) parent.getSelectedItem();
                idsatuanpilih=snt.getIdsatuan();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                satuanentity snt=(satuanentity) parent.getSelectedItem();
                idsatuanpilih=snt.getIdsatuan();
            }
        });
    }

    private void insertdatabarang(final String idbarang){
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage("Memproses Pesanan...");
        pd.show();
        RequestQueue rq= Volley.newRequestQueue(Detail_barang_Activity.this);
        StringRequest sr=new StringRequest(Request.Method.POST, Config.url+"/insertorderpenjualan",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        result=response;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Detail_barang_Activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<String, String>();
                params.put("idcards",Config.idcard);
                params.put("idbarang", idbarang);
                params.put("idsatuan", idsatuanpilih);
                params.put("jumlah", jumlahpesan.getText().toString());
                params.put("keterangan", keterangantambahan.getText().toString());
                return params;
            }
        };
        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                String inres1="",inres2="";
                try {
                    inres1=result.split("--")[0];
                    inres2=result.split("--")[1];
                }catch (Exception e){
                    //Toast.makeText(Detail_barang_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    inres1=result;
                }
                if(inres1.equals("berhasil")){
                    pd.dismiss();
                    //Snackbar.make(findViewById(R.id.detaildatabarang), "Berhasil" ,1000*2).show();
                    Toast.makeText(Detail_barang_Activity.this, inres2+" Barang Berhasil Ditambahkan ke Keranjang", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                    finish();
                }else{
                    pd.dismiss();
                    Toast.makeText(Detail_barang_Activity.this,result, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void updatedatabarang(final String idbarang, final String idpesanan){
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Memproses Pesanan...");
        pd.setCancelable(false);
        pd.show();
        RequestQueue rq= Volley.newRequestQueue(Detail_barang_Activity.this);
        StringRequest sr=new StringRequest(Request.Method.POST, Config.url+"/editorderpenjualan",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        result=response;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Detail_barang_Activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<String, String>();
                params.put("idpesanan", idpesanan);
                params.put("idcards",Config.idcard);
                params.put("idbarang", idbarang);
                params.put("idsatuan", idsatuanpilih);
                params.put("jumlah", jumlahpesan.getText().toString());
                params.put("keterangan", keterangantambahan.getText().toString());
                return params;
            }
        };
        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                if(result.equals("berhasil")){
                    pd.dismiss();
                    Toast.makeText(Detail_barang_Activity.this, "Barang Berhasil Dikoreksi", Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(Detail_barang_Activity.this,MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtra("ref","cart");
                    startActivity(i);
                    finish();
                }else{
                    pd.dismiss();
                    Toast.makeText(Detail_barang_Activity.this,result, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }



    public class satuanentity{
        String idsatuan,kodesatuan;

        public satuanentity(String idsatuan, String kodesatuan) {
            this.idsatuan = idsatuan;
            this.kodesatuan = kodesatuan;
        }

        public String getIdsatuan() {
            return idsatuan;
        }

        public void setIdsatuan(String idsatuan) {
            this.idsatuan = idsatuan;
        }

        public String getKodesatuan() {
            return kodesatuan;
        }

        public void setKodesatuan(String kodesatuan) {
            this.kodesatuan = kodesatuan;
        }

        @Override
        public String toString() {
            return kodesatuan;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof satuanentity){
                satuanentity c = (satuanentity) obj;
                if(c.getKodesatuan().equals(kodesatuan) && c.getIdsatuan()==idsatuan ) return true;
            }

            return false;
        }
    }
}
