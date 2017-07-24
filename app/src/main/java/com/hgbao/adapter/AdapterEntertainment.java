package com.hgbao.adapter;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hgbao.hcmushandbook.R;
import com.hgbao.model.Entertainment;
import com.hgbao.model.Scholarship;
import com.hgbao.provider.SupportProvider;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class AdapterEntertainment extends ArrayAdapter<Entertainment> {
    Activity context;
    int resource;
    List<Entertainment> objects;
    List<Entertainment> objects_original;

    public AdapterEntertainment(Activity context, int resource, List<Entertainment> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater flater = this.context.getLayoutInflater();
        View custom_row = flater.inflate(this.resource, null);
        ImageView imgAvatar = (ImageView) custom_row.findViewById(R.id.imgEntertainmentAvatar);
        TextView txtName = (TextView) custom_row.findViewById(R.id.txtEntertainmentName);
        TextView txtAddress = (TextView) custom_row.findViewById(R.id.txtEntertainmentAddress);
        RatingBar ratingBar = (RatingBar) custom_row.findViewById(R.id.ratingEntertainment);

        //Set data
        Entertainment entertainment = this.objects.get(position);
        imgAvatar.setImageBitmap(BitmapFactory.decodeStream(new ByteArrayInputStream(entertainment.getAvatar())));
        txtName.setText(entertainment.getName());
        txtAddress.setText(entertainment.getAddress());
        ratingBar.setRating(entertainment.getRating());
        ratingBar.setClickable(false);

        return custom_row;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Entertainment getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<Entertainment> results = new ArrayList<>();
                if (objects_original == null)
                    objects_original = objects;
                if (constraint != null) {
                    if (objects_original != null && objects_original.size() > 0) {
                        for (final Entertainment cur : objects_original) {
                            if (cur.getName().toLowerCase().contains(constraint.toString().toLowerCase()))
                                results.add(cur);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                objects = (ArrayList<Entertainment>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
