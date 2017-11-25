package com.mizan.emha;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Minami on 08/09/2017.
 */

public class Header_history_adapter extends RecyclerView.Adapter<Header_history_adapter.Holder>{
    ArrayList<String> idtransaksi=new ArrayList<>();
    ArrayList<String> notransaksi=new ArrayList<>();;
    ArrayList<String> tanggal=new ArrayList<>();
    ArrayList<String> status=new ArrayList<>();
    ArrayList<Double> totalorder=new ArrayList<>();
    Context ct;
    NumberFormat nf=NumberFormat.getInstance();

    public Header_history_adapter(ArrayList<String> idtransaksi, ArrayList<String> notransaksi,
                                  ArrayList<String> tanggal, ArrayList<String> status,
                                  ArrayList<Double> totalorder, Context ct) {
        this.idtransaksi = idtransaksi;
        this.notransaksi = notransaksi;
        this.tanggal = tanggal;
        this.status = status;
        this.totalorder = totalorder;
        this.ct = ct;
    }


    public class Holder extends RecyclerView.ViewHolder{
        public ImageView img_order;
        public TextView notransaksi;
        public TextView status;
        public TextView tanggal;
        public TextView totalorder;
        public LinearLayout linearbt;
        public Holder(View itemView) {
            super(itemView);
            notransaksi=(TextView) itemView.findViewById(R.id.notransaksi);
            status=(TextView) itemView.findViewById(R.id.status);
            tanggal=(TextView) itemView.findViewById(R.id.tanggal);
            totalorder=(TextView) itemView.findViewById(R.id.totalorder);
            img_order=(ImageView) itemView.findViewById(R.id.img_order);
            linearbt=(LinearLayout) itemView.findViewById(R.id.linearbt);
        }
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_header_history,parent,false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {
        holder.notransaksi.setText(notransaksi.get(position));
        Date dt=null;
        try {
            dt=new SimpleDateFormat("yyyy-MM-dd").parse(tanggal.get(position));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String tanggalterformat=new SimpleDateFormat("dd/MM/yyyy").format(dt);
        holder.tanggal.setText(tanggalterformat);
        if(status.get(position).equals("Proses")){
            holder.status.setTextColor(Color.parseColor("#f4425c"));
            holder.status.setText(status.get(position));
        }else{
            holder.status.setTextColor(Color.parseColor("#0e8c12"));
            holder.status.setText(status.get(position));
        }
        holder.totalorder.setText("Rp. "+nf.format(totalorder.get(position)));
        holder.linearbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(ct,History_activity.class);
                i.putExtra("id_transaksi",idtransaksi.get(position));
                ct.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return idtransaksi.size();
    }


}
