package com.crechelessons.howhumidisit;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentThree extends Fragment {



    private TextView txtWind, metrics;
    private float wind;
    private String metric_system;
    private View view;

    public FragmentThree() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_three, container, false);

        ImageView settings = view.findViewById(R.id.settings_btn);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SettingsActivity.class);
                startActivity(i);
            }
        });

        metrics = view.findViewById(R.id.metrics_units);

        TextView txtCity = view.findViewById(R.id.txtWindCity);
        txtCity.setText(getName());

        txtWind = view.findViewById(R.id.txtWind);
        txtWind.setText(String.valueOf(wind));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();


        if(getUnits()){
            wind = getWind();
            setMetrics();
        }else{
            setMetrics();
            wind = getWind() * 2.2369f;
        }

        if(txtWind !=null){
            txtWind.setText(String.format(Locale.getDefault(), "%.2f", wind));
        }
    }

    public void setMetrics(){
        if(getUnits()){
            metric_system = "meters/sec";
        }else{
            metric_system = "miles/hour";
        }
        if(metrics!=null){
            metrics.setText(metric_system);
        }
    }


    private float getWind() {
        SharedPreferences mSharedPreferences = this.getActivity().getSharedPreferences("WindLevel", Context.MODE_PRIVATE);
        return mSharedPreferences.getFloat("wind", 0);
    }

    private String getName() {
        SharedPreferences mSharedPreferences = this.getActivity().getSharedPreferences("CityName", Context.MODE_PRIVATE);
        return mSharedPreferences.getString("city", "London");
    }

    private boolean getUnits() {
        SharedPreferences mSharedPreferences = this.getActivity().getSharedPreferences("Units", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("metric", true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        view = null;
    }

}
