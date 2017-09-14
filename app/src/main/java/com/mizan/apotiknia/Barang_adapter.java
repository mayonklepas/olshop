package com.mizan.apotiknia;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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

public class Barang_adapter extends RecyclerView.Adapter<Barang_adapter.Holder>{
    ArrayList<String> id_barang=new ArrayList<>();
    ArrayList<String> nama_barang=new ArrayList<>();
    ArrayList<Double> harga_barang=new ArrayList<>();
    ArrayList<Integer> stock_barang=new ArrayList<>();
    ArrayList<String> img_barang=new ArrayList<>();
    ArrayList<String> satuan=new ArrayList<>();
    Context ct;
    NumberFormat nf=NumberFormat.getInstance();

    public Barang_adapter(ArrayList<String> id_barang, ArrayList<String> nama_barang, ArrayList<Double> harga_barang,
                          ArrayList<Integer> stock_barang, ArrayList<String> img_barang,ArrayList<String> satuan, Context ct) {
        this.id_barang = id_barang;
        this.nama_barang = nama_barang;
        this.harga_barang = harga_barang;
        this.stock_barang = stock_barang;
        this.img_barang = img_barang;
        this.satuan=satuan;
        this.ct = ct;
    }

    public class Holder extends RecyclerView.ViewHolder{
        Typeface tp=Typeface.createFromAsset(ct.getAssets(),Config.fontname);
        public ImageView img_barang;
        public TextView nama_barang;
        public TextView harga_barang;
        public TextView stock_barang;
        public Button detail;
        public Holder(View itemView) {
            super(itemView);
            img_barang=(ImageView) itemView.findViewById(R.id.img_barang);
            nama_barang=(TextView) itemView.findViewById(R.id.nama_barang);
            harga_barang=(TextView) itemView.findViewById(R.id.harga_barang);
            stock_barang=(TextView) itemView.findViewById(R.id.stock_barang);
            detail=(Button) itemView.findViewById(R.id.detail);
        }
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_barang,parent,false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {
        holder.nama_barang.setText(nama_barang.get(position));
        holder.harga_barang.setText("Rp. "+nf.format(harga_barang.get(position)));
        holder.stock_barang.setText("Stock : "+String.valueOf(stock_barang.get(position))+" "+satuan.get(position));
        Glide.with(ct).
                load(img_barang.get(position)).
                diskCacheStrategy(DiskCacheStrategy.ALL).
                crossFade().placeholder(R.drawable.placeholder).
                centerCrop().into(holder.img_barang);
        holder.img_barang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(ct,Detail_barang_Activity.class);
                i.putExtra("id_barang",id_barang.get(position));
                i.putExtra("nama_barang",nama_barang.get(position));
                i.putExtra("stock",String.valueOf(stock_barang.get(position)));
                i.putExtra("img_barang",img_barang.get(position));
                i.putExtra("statustransaksi","insert");
                ct.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return id_barang.size();
    }


}
