package com.crechelessons.howhumidisit;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTwo extends Fragment {


    private View view;

    public FragmentTwo() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_two, container, false);
        TextView txtCity = view.findViewById(R.id.txtCity);
        TextView txtPressure = view.findViewById(R.id.txtPressure);
        txtCity.setText(getName());
        txtPressure.setText(String.valueOf(getPressure()));
        return view;
    }

    private float getPressure() {
        SharedPreferences mSharedPreferences = this.getActivity().getSharedPreferences("PressureLevel", MODE_PRIVATE);
        return mSharedPreferences.getFloat("pressure", 0);
    }

    private String getName() {
        SharedPreferences mSharedPreferences = this.getActivity().getSharedPreferences("CityName", MODE_PRIVATE);
        return mSharedPreferences.getString("city", "London");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        view = null;
    }

}
