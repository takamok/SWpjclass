package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import net.daum.mf.map.api.MapView;

public class MapActivity extends AppCompatActivity {

    private static final String DAUM_API_KEY = "19635da0dfed5e004c4f24e284c8c0f1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        MapView mapView = new MapView(this);
        mapView.setDaumMapApiKey(DAUM_API_KEY);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.MapView);
        viewGroup.addView(mapView);

    }

}
