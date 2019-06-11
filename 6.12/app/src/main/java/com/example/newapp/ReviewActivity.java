package com.example.newapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import static java.lang.Long.parseLong;

public class ReviewActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Review";
    //버튼액션
    Button btn_Update;
    Button btn_Insert;
    Button btn_Select;
    EditText edit_Latitude;
    EditText edit_longitude;
    EditText edit_time;
    TextView text_Latitude;
    TextView text_longitude;
    TextView text_time;
    //뷰 순서
    CheckBox check_Latitude;
    CheckBox check_longitude;
    CheckBox check_time;
    long nowIndex;
    String Latitude;
    String longitude;
    String time;
    String sort = "Latitude";
    ArrayAdapter<String> arrayAdapter;
    static ArrayList<String> arrayIndex =  new ArrayList<String>();
    static ArrayList<String> arrayData = new ArrayList<String>();
    private DbOpenHelper mDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        btn_Insert = (Button) findViewById(R.id.btn_insert);
        btn_Insert.setOnClickListener(this);
        btn_Update = (Button) findViewById(R.id.btn_update);
        btn_Update.setOnClickListener(this);
        btn_Select = (Button) findViewById(R.id.btn_select);
        btn_Select.setOnClickListener(this);
        edit_Latitude = (EditText) findViewById(R.id.edit_Latitude);
        edit_longitude = (EditText) findViewById(R.id.edit_longitude);
        edit_time = (EditText) findViewById(R.id.edit_time);
        text_Latitude = (TextView) findViewById(R.id.text_Latitude);
        text_longitude = (TextView) findViewById(R.id.text_longitude);
        text_time = (TextView) findViewById(R.id.text_time);
        check_Latitude = (CheckBox) findViewById(R.id.check_Latitude);
        check_Latitude.setOnClickListener(this);
        check_longitude = (CheckBox) findViewById(R.id.check_longitude);
        check_longitude.setOnClickListener(this);
        check_time = (CheckBox) findViewById(R.id.check_time);
        check_time.setOnClickListener(this);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        ListView listView = (ListView) findViewById(R.id.db_list_view);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(onClickListener);
        listView.setOnItemLongClickListener(longClickListener);
        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();
        check_Latitude.setChecked(true);
        showDatabase(sort);
        btn_Insert.setEnabled(true);
        btn_Update.setEnabled(true);
        Intent intent = getIntent();
    }

    public void setInsertMode(){
        edit_Latitude.setText("");
        edit_longitude.setText("");
        edit_time.setText("");
        btn_Insert.setEnabled(true);
        btn_Update.setEnabled(true);
    }

    private AdapterView.OnItemClickListener onClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long Latitude) {
            Log.e("On Click", "position = " + position);
            nowIndex = parseLong(arrayIndex.get(position));
            Log.e("On Click", "nowIndex = " + nowIndex);
            Log.e("On Click", "Data: " + arrayData.get(position));
            String[] tempData = arrayData.get(position).split("\\s+");
            Log.e("On Click", "Split Result = " + tempData);
           /* edit_Latitude.setText(tempData[0].trim());
            edit_longitude.setText(tempData[1].trim());
            edit_time.setText(tempData[2].trim());*/
            btn_Insert.setEnabled(true);
            btn_Update.setEnabled(true);
        }
    };

    //슬롯 길게 클릭하여 데이터 삭제 #잘 안됨
    private AdapterView.OnItemLongClickListener longClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long Latitude) {
            Log.d("Long Click", "position = " + position);
            nowIndex = parseLong(arrayIndex.get(position));
            String[] nowData = arrayData.get(position).split("\\s+");
            String viewData = nowData[0] + ", " + nowData[1] + ", " + nowData[2];
            AlertDialog.Builder dialog = new AlertDialog.Builder(ReviewActivity.this);
            dialog.setTitle("데이터 삭제")
                    .setMessage("해당 데이터를 삭제 하시겠습니까?" + "\n" + viewData)
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(ReviewActivity.this, "데이터를 삭제했습니다.", Toast.LENGTH_SHORT).show();
                            mDbOpenHelper.deleteColumn(nowIndex);
                            showDatabase(sort);
                            setInsertMode();
                        }
                    })
                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(ReviewActivity.this, "삭제를 취소했습니다.", Toast.LENGTH_SHORT).show();
                            setInsertMode();
                        }
                    })
                    .create()
                    .show();
            return false;
        }
    };

    //데이터베이스 출력
    public void showDatabase(String sort){
        Cursor iCursor = mDbOpenHelper.sortColumn(sort);
        Log.d("showDatabase", "DB Size: " + iCursor.getCount());
        arrayData.clear();
        arrayIndex.clear();
        while(iCursor.moveToNext()){
            String tempIndex = iCursor.getString(iCursor.getColumnIndex("_id"));
            String tempLatitude = iCursor.getString(iCursor.getColumnIndex("Latitude"));
            tempLatitude = setTextLength(tempLatitude,9);
            String templongitude = iCursor.getString(iCursor.getColumnIndex("longitude"));
            templongitude = setTextLength(templongitude,9);
            String temptime = iCursor.getString(iCursor.getColumnIndex("time"));
            temptime = setTextLength(temptime,9);

            String Result = tempLatitude + templongitude + temptime ;
            arrayData.add(Result);
            arrayIndex.add(tempIndex);
        }
        arrayAdapter.clear();
        arrayAdapter.addAll(arrayData);
        arrayAdapter.notifyDataSetChanged();
    }

    public String setTextLength(String text, int length){
        if(text.length()<length){
            int gap = length - text.length();
            for (int i=0; i<gap; i++){
                text = text + " ";
            }
        }
        return text;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //데이터 삽입
            case R.id.btn_insert:
                Latitude = edit_Latitude.getText().toString();
                longitude = edit_longitude.getText().toString();
                time = edit_time.getText().toString();
                mDbOpenHelper.open();
                mDbOpenHelper.insertColumn(Latitude, longitude, time);
                String[] sendData = {Latitude, longitude, time};
                //데이터 저장
                new postData().execute(sendData);
                showDatabase(sort);
                setInsertMode();
                edit_Latitude.requestFocus();
                edit_Latitude.setCursorVisible(true);
                break;

            //데이터 업데이트 => 전체삭제말고 안의 데이터 가져오는걸로 변경하자
            case R.id.btn_update:
                /*Latitude = edit_Latitude.getText().toString();
                longitude = edit_longitude.getText().toString();
                time = edit_time.getText().toString();
                //get 데이터 베이스 정보 받아옴
                new getData().execute();
                mDbOpenHelper.updateColumn(nowIndex,Latitude, longitude, time);
                showDatabase(sort);
                setInsertMode();
                edit_Latitude.requestFocus();
                edit_Latitude.setCursorVisible(true);*/
                new getData().execute();
                break;

            //선택
            case R.id.btn_select:
                showDatabase(sort);
                break;

            //경도순
            case R.id.check_Latitude:
                check_longitude.setChecked(false);
                check_time.setChecked(false);
                sort = "Latitude";
                break;

            //위도순
            case R.id.check_longitude:
                check_Latitude.setChecked(false);
                check_time.setChecked(false);
                sort = "longitude";
                break;

            //시간순
            case R.id.check_time:
                check_Latitude.setChecked(false);
                check_longitude.setChecked(false);
                sort = "time";
                break;
        }
    }

    //6.5 파일 내부에 gpspost.php, gpsget.php 파일 삽입
    //데이터 받아아는 부분
    class getData extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... Params) {
            //받은데이터 sql에 저장    서버 연결을 위한 접속ip
            try{                   //localhost, 211.183.34.181 서버 열어둠
                URL server = new URL("http://211.183.34.181/gpsget.php");
                HttpURLConnection urlConnection = (HttpURLConnection) server.openConnection();
                //url 받아옴
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuffer sb = new StringBuffer("");
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                br.close();
                return sb.toString();
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result){
            if(result.startsWith("Exception")){
                Log.e("getPeriod onPost: ", "Error: " + result);
            }else{
                Log.d("getPeriod onPost: ", "Get Result: " + result);
                ArrayList<String> list = new ArrayList<String>();
                String[] list_DB = result.split("<br>");
                for (int i=0; i<list_DB.length; i++){
                    String[] tempResult = list_DB[i].split(",");
                    String listResult = "Latitude: " + tempResult[0] + "\nlongitude: " + tempResult[1] + "\ntime: " + tempResult[2];
                    list.add(listResult);
                }
                arrayAdapter.clear();
                arrayAdapter.addAll(list);
                arrayAdapter.notifyDataSetChanged();
            }
        }
    }
    // mysql에서 삽입
    class postData extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... Params) {
            StringBuffer sb = new StringBuffer();
            sb.append("Latitude").append("=").append(Params[0]).append("&");
            sb.append("longitude").append("=").append(Params[1]).append("&");
            sb.append("time").append("=").append(Params[2]);

            Log.d("sendData", "String Buffer: " + sb.toString());
            try{                                    //localhost
                URL server = new URL("http://211.183.34.181/gpspost.php");
                Log.d("sendData", "URL: " + server.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) server.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                OutputStream os = urlConnection.getOutputStream();
                os.write(sb.toString().getBytes("EUC-KR"));
                os.flush();
                os.close();
                String response = "";

                //http 서버 연결
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Log.d("sendData", "Connect to server");
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                }else{// 서버 연결 메세지
                    Log.d("sendData", "DO NOT connect to server");
                }
                urlConnection.disconnect();
                return response;
            }catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result){
            Log.d("sendData", "Result: " + result);
        }
    }
}
