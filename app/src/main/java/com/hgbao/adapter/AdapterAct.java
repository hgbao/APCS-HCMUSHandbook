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
import com.hgbao.model.Act;
import com.hgbao.provider.SupportProvider;

import java.util.ArrayList;
import java.util.List;

public class AdapterAct extends ArrayAdapter<Act>{
    Activity context;
    int resource;
    List<Act> objects;
    List<Act> objects_original;

    public AdapterAct(Activity context, int resource, List<Act> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater flater = this.context.getLayoutInflater();
        View custom_row = flater.inflate(this.resource, null);
        TextView txtName = (TextView) custom_row.findViewById(R.id.txtActivityName);
        TextView txtDate = (TextView) custom_row.findViewById(R.id.txtActivityDate);
        ImageView imgType = (ImageView) custom_row.findViewById(R.id.imgActivityType);

        //Set data
        Act activity = this.objects.get(position);
        txtName.setText(activity.getName());
        txtDate.setText("(" + SupportProvider.stringDate(activity.getDateCreated()) + ")");
        switch (activity.getType()){
            case 1:
                imgType.setImageResource(R.drawable.act_exchange);
                break;
            case 2:
                imgType.setImageResource(R.drawable.act_contest);
                break;
            case 3:
                imgType.setImageResource(R.drawable.act_voluntary);
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
    public Act getItem(int position) {
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
                final ArrayList<Act> results = new ArrayList<>();
                if (objects_original == null)
                    objects_original = objects;
                if (constraint != null) {
                    if (objects_original != null && objects_original.size() > 0) {
                        for (final Act cur : objects_original) {
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
                objects = (ArrayList<Act>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
