package com.mizan.emha;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Minami on 12/09/2017.
 */

public class Profile_fragment extends Fragment {

    TextView nama_toko,nama_user;
    EditText passwordlama,passwordbaru,repasswordbaru;
    Button gantipassword;
    String result;
    CircleImageView img_user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragmentprofile,container,false);
        nama_toko=(TextView) v.findViewById(R.id.nama_toko);
        nama_user=(TextView) v.findViewById(R.id.nama_user);
        passwordlama=(EditText) v.findViewById(R.id.passwordlama);
        passwordbaru=(EditText) v.findViewById(R.id.passwordbaru);
        repasswordbaru=(EditText) v.findViewById(R.id.repasswordbaru);
        gantipassword=(Button) v.findViewById(R.id.gantipassword);
        img_user=(CircleImageView) v.findViewById(R.id.img_user);
        nama_toko.setText(Config.namatoko);
        nama_user.setText("User : "+Config.username);
        Glide.with(getActivity()).
                load(Config.url+"/pelanggan/"+Config.idcard+".jpg").
                centerCrop().
                into(img_user);
        gantipassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passwordlama.getText().toString().equals(Config.password)){
                    if(passwordbaru.getText().toString().equals(repasswordbaru.getText().toString())){
                        kirimpassword();
                    }else{
                        AlertDialog.Builder adb=new AlertDialog.Builder(getActivity());
                        adb.setCancelable(false);
                        adb.setTitle("Informasi");
                        adb.setMessage("Konfirmasi password tidak cocok, coba lagi");
                        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        adb.show();
                    }
                }else{
                    AlertDialog.Builder adb=new AlertDialog.Builder(getActivity());
                    adb.setCancelable(false);
                    adb.setTitle("Informasi");
                    adb.setMessage("Password lama tidak cocok");
                    adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    adb.show();
                }

            }
        });
        return v;
    }

    private void kirimpassword(){
        final ProgressDialog pg=new ProgressDialog(getActivity());
        pg.setMessage("Sedang Memuat...");
        pg.show();
        RequestQueue rq= Volley.newRequestQueue(getActivity());
        StringRequest sr=new StringRequest(Request.Method.POST, Config.url+"/updatepassword",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        result=response;
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
                params.put("username",Config.username);
                params.put("newpassword",passwordbaru.getText().toString());
                return params;
            }
        };
        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                pg.dismiss();
                if(result.equals("berhasil")){
                    AlertDialog.Builder adb=new AlertDialog.Builder(getActivity());
                    adb.setCancelable(false);
                    adb.setTitle("Informasi");
                    adb.setMessage("Password Berhasil Diubah");
                    adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i=new Intent(getActivity(),LoginActivity.class);
                            startActivity(i);
                            getActivity().finish();
                        }
                    });
                    adb.show();
                }else{
                    Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
