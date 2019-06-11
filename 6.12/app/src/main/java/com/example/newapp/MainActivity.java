package com.example.newapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
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
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button Gallary;
    Button Review;
    public TextView mView, metadata,mTimestamp;
    private static int PICK_IMAGE_REQUEST = 1;
    private final int PERMISSIONS_REQUEST_RESULT = 1;
    private boolean valid = false;
    public static Float latitude, longitude;
    public static String time;

    int length = 0;
    private String count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTimestamp=(TextView)findViewById(R.id.Timestamp);
        mView = (TextView) findViewById(R.id.metadata);
        metadata = (TextView) findViewById(R.id.metadatatwo);

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
            GeoDegree(exif);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show();
        }
        return path;
    }

    public void GeoDegree(ExifInterface exif) {

        String attrLATITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        String attrLATITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
        String attrLONGITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        String attrLONGITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
        String Timeset = exif.getAttribute(ExifInterface.TAG_DATETIME);

        if ((attrLATITUDE != null) && (attrLATITUDE_REF != null) && (attrLONGITUDE != null)
                && (attrLONGITUDE_REF != null)) {
            valid = true;
            if (attrLATITUDE_REF.equals("N")) {
                latitude = convertToDegree(attrLATITUDE);
            } else {
                latitude = 0 - convertToDegree(attrLATITUDE);
            }
            if (attrLONGITUDE_REF.equals("E")) {
                longitude = convertToDegree(attrLONGITUDE);
            } else {
                longitude = 0 - convertToDegree(attrLONGITUDE);
            }
            time =  converToTime(Timeset);
        }

        mView.setText(longitude.toString());
        metadata.setText(latitude.toString());
        mTimestamp.setText(time);




    }

    private String converToTime(String Time)
    {
        String[] time = Time.split(":",5);
        String D0 = new String(time[0]);
        String D1 = new String(time[1]);
        String D2 = new String(time[2]);
        String D3 = new String(time[3]);
        String D4 = new String(time[4]);
        String FloatA = D0+D1+D2+D3+D4;
        String[] FloatB =  FloatA.split("\\s+",3);
        String T0 = new String(FloatB[0]);
        String T1 = new String(FloatB[1]);
        String FloatC = T0+T1;
        return FloatC;
    }

    private Float convertToDegree(String stringDMS) {
        Float result = null;
        String[] DMS = stringDMS.split(",", 3);
        String[] stringD = DMS[0].split("/", 2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double FloatD = D0 / D1;
        String[] stringM = DMS[1].split("/", 2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double FloatM = M0 / M1;
        String[] stringS = DMS[2].split("/", 2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1]);
        Double FloatS = S0 / S1;
        result = new Float(FloatD + (FloatM / 60) + (FloatS / 3600));
        return result;
    }

    @Override
    public String toString() {
        return (String.valueOf(latitude) + ", " + String.valueOf(longitude));
    }
}