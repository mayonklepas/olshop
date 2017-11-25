package com.mizan.emha;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by Minami on 08/09/2017.
 */

public class History_adapter extends RecyclerView.Adapter<History_adapter.Holder>{
    ArrayList<String> noindex=new ArrayList<>();
    ArrayList<String> kode=new ArrayList<>();
    ArrayList<String> nama=new ArrayList<>();
    ArrayList<Integer> qty=new ArrayList<>();
    ArrayList<String> satuan=new ArrayList<>();
    ArrayList<String> img_barang=new ArrayList<>();
    ArrayList<Double> harga=new ArrayList<>();
    Context ct;
    NumberFormat nf=NumberFormat.getInstance();

    public History_adapter(ArrayList<String> noindex, ArrayList<String> kode, ArrayList<String> nama,
                           ArrayList<Integer> qty, ArrayList<String> satuan, ArrayList<Double> harga,ArrayList<String> img_barang, Context ct) {
        this.noindex = noindex;
        this.kode = kode;
        this.nama = nama;
        this.qty = qty;
        this.satuan = satuan;
        this.harga = harga;
        this.img_barang=img_barang;
        this.ct = ct;
    }

    public class Holder extends RecyclerView.ViewHolder{
        public ImageView img_barang;
        public TextView nama;
        public TextView qty;
        public TextView harga;
        public Button belilagi;
        public TextView totalbayar;
        public Holder(View itemView) {
            super(itemView);
            img_barang=(ImageView) itemView.findViewById(R.id.img_barang);
            nama=(TextView) itemView.findViewById(R.id.nama);
            qty=(TextView) itemView.findViewById(R.id.qty);
            harga=(TextView) itemView.findViewById(R.id.harga);
            totalbayar=(TextView) itemView.findViewById(R.id.totalbayar);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return (position==noindex.size())? R.layout.recfooterdetailhistory : R.layout.adapter_history;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if(viewType==R.layout.adapter_history) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_history, parent, false);
        }else{
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recfooterdetailhistory, parent, false);
        }
        return new History_adapter.Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {
        if(position==noindex.size()){
            double totalbayarraw=0.0;
            for (int i = 0; i < position; i++) {
                totalbayarraw=totalbayarraw+harga.get(i)*qty.get(i);
            }
            holder.totalbayar.setText("Total : Rp. "+nf.format(totalbayarraw));
        }else {
            holder.nama.setText(nama.get(position));
            holder.qty.setText(String.valueOf(qty.get(position)) + " " + satuan.get(position));
            holder.harga.setText(nf.format(harga.get(position) * qty.get(position)));
            Glide.with(ct).
                    load(img_barang.get(position)).placeholder(R.drawable.placeholder).
                    diskCacheStrategy(DiskCacheStrategy.ALL).
                    crossFade().centerCrop().into(holder.img_barang);
        }
    }


    @Override
    public int getItemCount() {
        return noindex.size()+1;
    }


}
