package com.nextgen.clearpathai.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.nextgen.clearpathai.R;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder>{

    ArrayList<HistoryModelClass> list;
    Context context;
    String ip;

    public HistoryAdapter(ArrayList<HistoryModelClass> list, Context context) {
        this.list = list;
        this.context = context;

        GlobalPreference globalPreference = new GlobalPreference(context);
        ip = globalPreference.getIP();

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_history,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        HistoryModelClass historyList = list.get(position);
        holder.descriptionTV.setText(historyList.getDescription());
        holder.locationTV.setText(historyList.getLocation());
        holder.dateTV.setText(historyList.getDate());

        Glide.with(context).load("http://" + IMAGE_URL + historyList.getImage()).into(holder.reportIV);

        holder.viewInMapLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String latitude = historyList.getLatitude();
                String longitude = historyList.getLongitude();

                Intent mintent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr="+latitude+","+longitude));
                context.startActivity(mintent);
            }
        });

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

            descriptionTV = itemview.findViewById(R.id.hDescriptionTextView);
            reportIV = itemview.findViewById(R.id.hImageView);
            locationTV = itemview.findViewById(R.id.hLocationTextView);
            dateTV = itemview.findViewById(R.id.hDateTextView);
            viewInMapLL = itemview.findViewById(R.id.viewMapLL);


        }
    }
}
