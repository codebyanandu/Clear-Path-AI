package com.nextgen.clearpathai.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nextgen.clearpathai.AnimalAlertActivity;
import com.nextgen.clearpathai.HistoryActivity;
import com.nextgen.clearpathai.R;
import com.nextgen.clearpathai.ReportActivity;
import com.nextgen.clearpathai.RouteActivity;

/**
    DashboardFragment
 */
public class DashboardFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    View view;
    CardView reportCV;
    CardView historyCV;
    CardView routeCV;
    CardView alertCV;


    public DashboardFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        reportCV = view.findViewById(R.id.card_report);
        historyCV = view.findViewById(R.id.card_history);
        routeCV = view.findViewById(R.id.card_route);
        alertCV = view.findViewById(R.id.card_animalAlerts);

        reportCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ReportActivity.class);
                startActivity(intent);
            }
        });

        historyCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), HistoryActivity.class);
                startActivity(intent);
            }
        });

        routeCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), RouteActivity.class);
                startActivity(intent);
            }
        });

        alertCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AnimalAlertActivity.class);
                startActivity(intent);
            }
        });



        return view;
    }
}