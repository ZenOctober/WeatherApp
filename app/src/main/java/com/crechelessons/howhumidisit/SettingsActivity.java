package com.crechelessons.howhumidisit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity{

    private Switch units;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageView shareButton = findViewById(R.id.share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "How Humid Is It - Android App");
                String link = "htpps://play.google.com/store/apps/details?id=com.crechelessons.howhumidisit";
                intent.putExtra(Intent.EXTRA_TEXT, "Try this new app: "+"\n"+link);
                startActivity(intent);
            }
        });

        units = findViewById(R.id.unit_metrics);
        units.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = getSharedPreferences("Units", MODE_PRIVATE).edit();
                editor.putBoolean("metric", units.isChecked());
                editor.apply();
            }
        });

        if(getUnits()){
            units.setChecked(true);
        } else {
            units.setChecked(false);
        }
    }

    public boolean getUnits() {
        SharedPreferences mSharedPreferences = getSharedPreferences("Units", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("metric", true);
    }
}
