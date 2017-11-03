package com.mizan.apotiknia;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.TimedMetaData;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.mizan.apotiknia.R.id.alertTitle;
import static com.mizan.apotiknia.R.id.jumlah_barang;
import static com.mizan.apotiknia.R.id.orderpesanan;
import static com.mizan.apotiknia.R.id.stock_barang;
import static com.mizan.apotiknia.R.id.totalbayar;

/**
 * Created by Minami on 08/09/2017.
 */

public class Chart_adapter extends RecyclerView.Adapter<Chart_adapter.Holder>{
    ArrayList<Integer> nopesanan=new ArrayList<>();
    ArrayList<String> id_barang=new ArrayList<>();
    ArrayList<String> nama_barang=new ArrayList<>();
    ArrayList<String> nama_satuan=new ArrayList<>();
    ArrayList<Integer> jumlah_barang=new ArrayList<>();
    ArrayList<Double> harga_barang=new ArrayList<>();
    ArrayList<String> img_barang=new ArrayList<>();
    ArrayList<String> keterangan=new ArrayList<>();
    ArrayList<Integer> stock_barang=new ArrayList<>();
    Context ct;
    android.support.v4.app.Fragment fm;
    NumberFormat nf=NumberFormat.getInstance();
    String result;
    Integer posisiakhir,posisipilih;

    public Chart_adapter(ArrayList<Integer> nopesanan, ArrayList<String> id_barang, ArrayList<String> nama_barang,
                         ArrayList<String> nama_satuan, ArrayList<Integer> jumlah_barang, ArrayList<Double> harga_barang,
                         ArrayList<String> img_barang, ArrayList<String> keterangan, ArrayList<Integer> stock_barang,
                         Context ct) {
        this.nopesanan = nopesanan;
        this.id_barang = id_barang;
        this.nama_barang = nama_barang;
        this.nama_satuan = nama_satuan;
        this.jumlah_barang = jumlah_barang;
        this.harga_barang = harga_barang;
        this.img_barang = img_barang;
        this.keterangan = keterangan;
        this.stock_barang = stock_barang;
        this.ct = ct;
    }

    @Override
    public int getItemViewType(int position) {
        return (position==id_barang.size())? R.layout.recfooter : R.layout.adapter_chart;
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if(viewType==R.layout.adapter_chart) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_chart, parent, false);
        }else{
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recfooter, parent, false);
        }
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        if(position==id_barang.size()){
            if (position==0){
                holder.orderpesanan.setVisibility(View.INVISIBLE);
            }else {
                holder.orderpesanan.setVisibility(View.VISIBLE);
            }
            double totalbayarraw=0.0;
            for (int i = 0; i < position; i++) {
                totalbayarraw=totalbayarraw+harga_barang.get(i)*jumlah_barang.get(i);
            }
            holder.totalbayars.setText("Total Order : Rp. "+nf.format(totalbayarraw));
            holder.orderpesanan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder adb = new AlertDialog.Builder(ct);
                    adb.setTitle("Konfirmasi");
                    adb.setMessage("Yakin Melanjutkan Proses Pesanan ?");
                    adb.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            prosespesanan();
                        }
                    });
                    adb.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    adb.setCancelable(false);
                    adb.show();

                }
            });
        }else {
            holder.nama_barang.setText(nama_barang.get(position));
            holder.jumlah_barang.setText(String.valueOf(jumlah_barang.get(position)) + " " + nama_satuan.get(position));
            holder.harga_barang.setText("Rp. " + nf.format(harga_barang.get(position) * jumlah_barang.get(position)));
            holder.keterangan.setText(keterangan.get(position));
            Glide.with(ct).
                    load(img_barang.get(position)).
                    diskCacheStrategy(DiskCacheStrategy.ALL).crossFade().placeholder(R.drawable.placeholder).
                    centerCrop().into(holder.img_barang);
            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ct, Detail_barang_Activity.class);
                    i.putExtra("idpesanan", nopesanan.get(position));
                    i.putExtra("id_barang", id_barang.get(position));
                    i.putExtra("nama_barang", nama_barang.get(position));
                    i.putExtra("stock", String.valueOf(stock_barang.get(position)));
                    i.putExtra("img_barang", img_barang.get(position));
                    i.putExtra("jumlahbarang", jumlah_barang.get(position));
                    i.putExtra("keterangan", keterangan.get(position));
                    i.putExtra("statustransaksi", "edit");
                    ct.startActivity(i);
                    //((Activity) ct).finish();
                }
            });

            holder.hapus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder adb = new AlertDialog.Builder(ct);
                    adb.setTitle("Konfirmasi");
                    adb.setMessage("Yakin ingin menghapus barang dari keranjang ?");
                    adb.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deletedatabarang(String.valueOf(nopesanan.get(position)),holder);

                        }
                    });
                    adb.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                        }
                    });
                    adb.setCancelable(false);
                    adb.show();
                }
            });
        }


    }



    @Override
    public int getItemCount() {
        return id_barang.size()+1;

    }

    public class Holder extends RecyclerView.ViewHolder{
        public ImageView img_barang;
        public TextView nama_barang;
        public TextView harga_barang;
        public TextView jumlah_barang;
        public TextView keterangan;
        public Button edit;
        public Button hapus;
        public Button orderpesanan;
        public TextView totalbayars;
        public Holder(View itemView) {
            super(itemView);
            img_barang=(ImageView) itemView.findViewById(R.id.img_barang);
            nama_barang=(TextView) itemView.findViewById(R.id.nama_barang);
            harga_barang=(TextView) itemView.findViewById(R.id.harga_barang);
            jumlah_barang=(TextView) itemView.findViewById(R.id.jumlah_barang);
            keterangan=(TextView) itemView.findViewById(R.id.keterangan);
            totalbayars=(TextView) itemView.findViewById(R.id.totalbayar);
            orderpesanan=(Button) itemView.findViewById(R.id.orderpesanan);
            edit=(Button) itemView.findViewById(R.id.edit);
            hapus=(Button) itemView.findViewById(R.id.hapus);
        }
    }


    private void prosespesanan(){
        final ProgressDialog pd=new ProgressDialog(ct);
        pd.setMessage("Memproses Pesanan...");
        pd.setCancelable(false);
        pd.show();
        RequestQueue rq= Volley.newRequestQueue(ct);
        StringRequest sr=new StringRequest(Request.Method.POST, Config.url+"/prosesorder",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        result=response;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ct, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<String, String>();
                params.put("idcards",Config.idcard);
                return params;
            }
        };
        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                if(result.equals("berhasil")){
                    pd.dismiss();
                    final AlertDialog.Builder adb = new AlertDialog.Builder(ct);
                    adb.setTitle("Informasi");
                    adb.setMessage("Pesananmu Sudah Berhasil Diproses");
                    adb.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            nopesanan.clear();
                            id_barang.clear();
                            nama_barang.clear();
                            nama_satuan.clear();
                            jumlah_barang.clear();
                            harga_barang.clear();
                            img_barang.clear();
                            keterangan.clear();
                            stock_barang.clear();
                            notifyDataSetChanged();

                        }
                    });
                    adb.setCancelable(false);
                    adb.show();

                }else{
                    Toast.makeText(ct,result, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void deletedatabarang(final String nopesanans, final Holder holder){
        final ProgressDialog pd=new ProgressDialog(ct);
        pd.setCancelable(true);
        pd.setMessage("Memproses Data...");
        pd.show();
        RequestQueue rq= Volley.newRequestQueue(ct);
        StringRequest sr=new StringRequest(Request.Method.POST, Config.url+"/deleteorderpenjualan",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        result=response;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ct, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<String, String>();
                params.put("idcards",Config.idcard);
                params.put("idpesanan", nopesanans);
                return params;
            }
        };
        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                String inres="";
                try {
                    inres=result.split("--")[0];
                }catch (Exception e){
                    inres=result;
                }
                if(inres.equals("berhasil")){
                    pd.dismiss();
                    Toast.makeText(ct,  "Barang Berhasil Dihapus", Toast.LENGTH_SHORT).show();
                    nopesanan.remove(holder.getAdapterPosition());
                    id_barang.remove(holder.getAdapterPosition());
                    nama_barang.remove(holder.getAdapterPosition());
                    nama_satuan.remove(holder.getAdapterPosition());
                    jumlah_barang.remove(holder.getAdapterPosition());
                    harga_barang.remove(holder.getAdapterPosition());
                    img_barang.remove(holder.getAdapterPosition());
                    keterangan.remove(holder.getAdapterPosition());
                    stock_barang.remove(holder.getAdapterPosition());
                    notifyDataSetChanged();
                }else{
                    Toast.makeText(ct,inres, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
