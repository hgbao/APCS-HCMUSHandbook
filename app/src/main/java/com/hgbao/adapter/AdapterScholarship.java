package com.hgbao.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hgbao.hcmushandbook.R;
import com.hgbao.model.Scholarship;
import com.hgbao.provider.SupportProvider;

import java.util.ArrayList;
import java.util.List;

public class AdapterScholarship extends ArrayAdapter<Scholarship>{
    Activity context;
    int resource;
    List<Scholarship> objects;
    List<Scholarship> objects_original;

    public AdapterScholarship(Activity context, int resource, List<Scholarship> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater flater = this.context.getLayoutInflater();
        View custom_row = flater.inflate(this.resource, null);
        TextView txtName = (TextView) custom_row.findViewById(R.id.txtScholarshipName);
        TextView txtDate = (TextView) custom_row.findViewById(R.id.txtScholarshipDate);
        ImageView imgType = (ImageView) custom_row.findViewById(R.id.imgScholarshipType);

        //Set data
        Scholarship scholarship = this.objects.get(position);
        txtName.setText(scholarship.getName());
        txtDate.setText("(" + SupportProvider.stringDate(scholarship.getDateCreated()) + ")");
        switch (scholarship.getType()){
            case 1:
                imgType.setImageResource(R.drawable.sch_support);
                break;
            case 2:
                imgType.setImageResource(R.drawable.sch_longterm);
                break;
            case 3:
                imgType.setImageResource(R.drawable.sch_abroad);
                break;
            case 4:
                imgType.setImageResource(R.drawable.sch_act_result);
        }

        return custom_row;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Scholarship getItem(int position) {
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
                final ArrayList<Scholarship> results = new ArrayList<>();
                if (objects_original == null)
                    objects_original = objects;
                if (constraint != null) {
                    if (objects_original != null && objects_original.size() > 0) {
                        for (final Scholarship cur : objects_original) {
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
                objects = (ArrayList<Scholarship>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
