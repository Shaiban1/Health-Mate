package com.example.healthmate.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.healthmate.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private Integer[] imageResourceIds;

    public ImageAdapter(Context c, Integer[] imageResourceIds) {
        mContext = c;
        this.imageResourceIds = imageResourceIds;
    }

    @Override
    public int getCount() {
        return imageResourceIds.length;
    }

    @Override
    public Object getItem(int position) {
        return imageResourceIds[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CircleImageView imageView;
        if (convertView == null) {
            imageView = new CircleImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(120, 120));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (CircleImageView) convertView;
        }

        imageView.setImageResource(imageResourceIds[position]);
        return imageView;
    }
}
