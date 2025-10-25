package com.nextgen.clearpathai.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nextgen.clearpathai.GlobalPreference;
import com.nextgen.clearpathai.ModelClass.HistoryModelClass;
import com.nextgen.clearpathai.ModelClass.RouteModelClass;
import com.nextgen.clearpathai.R;

import java.util.ArrayList;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.MyViewHolder>{

    ArrayList<RouteModelClass> list;
    Context context;
    String ip;

    public RouteAdapter(ArrayList<RouteModelClass> list, Context context) {
        this.list = list;
        this.context = context;

        GlobalPreference globalPreference = new GlobalPreference(context);
        ip = globalPreference.getIP();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_routes,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        RouteModelClass routeList = list.get(position);
        holder.descriptionTV.setText(routeList.getDescription());
        holder.locationTV.setText(routeList.getLocation());
        holder.dateTV.setText(routeList.getDate());

        Glide.with(context).load("http://" + IMAGE_URL + routeList.getImage()).into(holder.reportIV);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView descriptionTV;
        TextView locationTV;
        TextView dateTV;
        ImageView reportIV;
        LinearLayout viewInMapLL;


        public MyViewHolder(@NonNull View itemview) {
            super(itemview);

            descriptionTV = itemview.findViewById(R.id.rDescriptionTextView);
            reportIV = itemview.findViewById(R.id.rImageView);
            locationTV = itemview.findViewById(R.id.rLocationTextView);
            dateTV = itemview.findViewById(R.id.rDateTextView);
           // viewInMapLL = itemview.findViewById(R.id.rViewMapLL);


        }
    }
}
