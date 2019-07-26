package com.krishna.schoolbustracker.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.krishna.schoolbustracker.BusLocHistory;
import com.krishna.schoolbustracker.R;
import com.krishna.schoolbustracker.models.busLocInfoModel;

import java.util.List;

public class BusLocInfoAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<busLocInfoModel> info;

    public BusLocInfoAdapter(Activity activity, List<busLocInfoModel> info) {
        this.activity = activity;
        this.info = info;
    }

    @Override
    public int getCount() {
        return info.size();
    }

    @Override
    public Object getItem(int location) {
        return info.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.bus_loc_info_model, null);
        TextView destination=(TextView)convertView.findViewById(R.id.destination);
        TextView time=(TextView)convertView.findViewById(R.id.Bustime);
        ImageView showmap=(ImageView)convertView.findViewById(R.id.locationicon);

        final busLocInfoModel busLocInfo=info.get(i);

        destination.setText(busLocInfo.getPlace());

        time.setText(busLocInfo.getTime());

        showmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng latLng=busLocInfo.getPosition();
                Intent i=new Intent(activity, BusLocHistory.class);
                i.putExtra("latlng",latLng);
                i.putExtra("title",busLocInfo.getPlace()+" ("+busLocInfo.getTime()+")");
                activity.startActivity(i);
            }
        });

        return convertView;
    }
}
