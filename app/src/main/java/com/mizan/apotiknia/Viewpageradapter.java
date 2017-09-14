package com.mizan.apotiknia;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

/**
 * Created by Minami on 13/09/2017.
 */

public class Viewpageradapter extends PagerAdapter {

    ArrayList<String> img=new ArrayList<>();
    LayoutInflater layin;
    Context ct;

    public Viewpageradapter(ArrayList<String> img, Context ct) {
        this.img = img;
        this.ct = ct;
        layin=LayoutInflater.from(ct);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return img.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View imagelayout=layin.inflate(R.layout.slideradapter,container,false);
        ImageView imgview=(ImageView) imagelayout.findViewById(R.id.imgslide);
        Glide.with(ct).
                load(img.get(position)).
                centerCrop().placeholder(R.drawable.placeholder).
                diskCacheStrategy(DiskCacheStrategy.ALL).into(imgview);
        container.addView(imagelayout,0);
        return imagelayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
