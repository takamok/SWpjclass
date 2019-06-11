package com.example.newapp;

import android.content.Intent;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.MapView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static int PICK_IMAGE_REQUEST = 1;
    public TextView mView, metadata,mTimestamp;
    Button Gallary;
    Button Review;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTimestamp=(TextView)findViewById(R.id.Timestamp);
        mView = (TextView) findViewById(R.id.metadata);
        metadata = (TextView) findViewById(R.id.metadatatwo);

        mTimestamp = (TextView) findViewById(R.id.Timestamp);
        mView = (TextView) findViewById(R.id.metadata);
        metadata = (TextView) findViewById(R.id.metadatatwo);

        MapView mapView = new MapView(this);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        Gallary = findViewById(R.id.Gallary);
        Review = findViewById(R.id.Review);
        Review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReviewActivity.class);
                startActivity(intent);
            }
        });

        Gallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                //Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                i.setType("image/*"); //이미지만 보이게
                //Intent 시작 - 갤러리앱을 열어서 원하는 이미지를 선택할 수 있다.
                startActivityForResult(i, 1);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && null != data) {
                Uri uri = data.getData();
                Log.d("Real path is : ", getRealPath(data.getData()));
            } else {
                Toast.makeText(this, "onActivity취소 되었습니다.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Oops! onActivity로딩에 오류가 있습니다.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public String getRealPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor c = getContentResolver().query(uri, proj, null, null, null);
        int index = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        c.moveToFirst();
        String path = c.getString(index);
        try {
            ExifInterface exif = new ExifInterface(path);
            //호출
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show();
        }
        return path;
    }
}
