package com.crechelessons.howhumidisit;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ClipDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentOne extends Fragment {

    public static final int MAX_LEVEL = 10000;
    ClipDrawable clipDrawable;
    TextView txtCity, txtHumidity;
    private View view;

    public FragmentOne() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_one, container, false);
        txtCity = view.findViewById(R.id.txtHumidityCity);
        txtHumidity = view.findViewById(R.id.txtHumidity);
        txtCity.setText(getName());
        txtHumidity.setText(getString(R.string.humidity, String.valueOf(getHumidity())));

        ImageView bottomImage = view.findViewById(R.id.empty_t);
        clipDrawable = (ClipDrawable) bottomImage.getBackground();
        clipDrawable.setLevel(10000);

        ImageView topImage = view.findViewById(R.id.full_t);
        clipDrawable = (ClipDrawable) topImage.getBackground();
        clipDrawable.setLevel(getHumidity() * MAX_LEVEL/100);

        return view;
    }

    private int getHumidity() {
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("HumidityLevel", Context.MODE_PRIVATE);
        return mSharedPreferences.getInt("humidity", 0);

    }

    private String getName() {
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("CityName", Context.MODE_PRIVATE);
        return mSharedPreferences.getString("city", "London");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        view = null;
    }



}
