package com.mizan.emha;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Toolbar tb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        tb=(Toolbar) findViewById(R.id.tb);
        Bundle ex=getIntent().getExtras();
        String ref="";
        try {
            ref=ex.getString("ref");
        }catch (Exception e){
            ref="";
        }
        if(ref.equals("cart")){
            getFragmentManager().beginTransaction().replace(R.id.content,new Chart_fragment()).commit();
            navigation.setSelectedItemId(R.id.navigation_chart);
            tb.setTitle("Daftar Keranjang");
        }else{
            getFragmentManager().beginTransaction().replace(R.id.content,new Home_fragment()).commit();
            navigation.setSelectedItemId(R.id.navigation_home);
            tb.setTitle("PT Rania Jaya Farmarindo");
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
        adb.setTitle("Konfirmasi");
        adb.setMessage("Yakin ingin keluar dari aplikasi ?");
        adb.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
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

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    getFragmentManager().beginTransaction().replace(R.id.content,new Home_fragment()).commit();
                    tb.setTitle("PT Rania Jaya Farmarindo");
                    return true;
                case R.id.navigation_barang:
                    getFragmentManager().beginTransaction().replace(R.id.content,new Barang_fragment()).commit();
                    tb.setTitle("Daftar Barang");
                    return true;
                case R.id.navigation_chart:
                    getFragmentManager().beginTransaction().replace(R.id.content,new Chart_fragment()).commit();
                    tb.setTitle("Daftar Keranjang");
                    return true;
                case R.id.navigation_history:
                    getFragmentManager().beginTransaction().replace(R.id.content,new Header_history_fragment()).commit();
                    tb.setTitle("History Pemesanan");
                    return true;
                case R.id.navigation_profile:
                    getFragmentManager().beginTransaction().replace(R.id.content,new Profile_fragment()).commit();
                    tb.setTitle("Profile User");
                    return true;
            }
            return false;
        }

    };

}
