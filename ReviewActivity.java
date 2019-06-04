package com.example.newapp;

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

import java.util.ArrayList;
import java.util.List;
import static java.lang.Long.parseLong;

public class ReviewActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Review";

    Button btn_Update;
    Button btn_Insert;
    Button btn_Select;
    EditText edit_Latitude;
    EditText edit_longitude;
    EditText edit_time;
    TextView text_Latitude;
    TextView text_longitude;
    TextView text_time;
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
        setContentView(R.layout.activity_main);
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
        btn_Update.setEnabled(false);
    }

    public void setInsertMode(){
        edit_Latitude.setText("");
        edit_longitude.setText("");
        edit_time.setText("");
        btn_Insert.setEnabled(true);
        btn_Update.setEnabled(false);
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
            edit_Latitude.setText(tempData[0].trim());
            edit_longitude.setText(tempData[1].trim());
            edit_time.setText(tempData[2].trim());
            btn_Insert.setEnabled(false);
            btn_Update.setEnabled(true);
        }
    };

    private AdapterView.OnItemLongClickListener longClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long Latitude) {
            Log.d("Long Click", "position = " + position);
            nowIndex = parseLong(arrayIndex.get(position));
            String[] nowData = arrayData.get(position).split("\\s+");
            String viewData = nowData[0] + ", " + nowData[1] + ", " + nowData[2] + ", " + nowData[3];
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

    public void showDatabase(String sort){
        Cursor iCursor = mDbOpenHelper.sortColumn(sort);
        Log.d("showDatabase", "DB Size: " + iCursor.getCount());
        arrayData.clear();
        arrayIndex.clear();
        while(iCursor.moveToNext()){
            String tempIndex = iCursor.getString(iCursor.getColumnIndex("_Latitude"));
            String tempLatitude = iCursor.getString(iCursor.getColumnIndex("Latitude"));
            tempLatitude = setTextLength(tempLatitude,10);
            String templongitude = iCursor.getString(iCursor.getColumnIndex("longitude"));
            templongitude = setTextLength(templongitude,10);
            String temptime = iCursor.getString(iCursor.getColumnIndex("time"));
            temptime = setTextLength(temptime,10);

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
            case R.id.btn_insert:
                Latitude = edit_Latitude.getText().toString();
                longitude = edit_longitude.getText().toString();
                time = edit_time.getText().toString();
                mDbOpenHelper.open();
                mDbOpenHelper.insertColumn(Latitude, longitude, time);
                showDatabase(sort);
                setInsertMode();
                edit_Latitude.requestFocus();
                edit_Latitude.setCursorVisible(true);
                break;

            case R.id.btn_update:
                Latitude = edit_Latitude.getText().toString();
                longitude = edit_longitude.getText().toString();
                time = edit_time.getText().toString();
                mDbOpenHelper.updateColumn(nowIndex,Latitude, longitude, time);
                showDatabase(sort);
                setInsertMode();
                edit_Latitude.requestFocus();
                edit_Latitude.setCursorVisible(true);
                break;

            case R.id.btn_select:
                showDatabase(sort);
                break;

            case R.id.check_Latitude:
                check_longitude.setChecked(false);
                check_time.setChecked(false);
                sort = "Latitude";
                break;

            case R.id.check_longitude:
                check_Latitude.setChecked(false);
                check_time.setChecked(false);
                sort = "longitude";
                break;

            case R.id.check_time:
                check_Latitude.setChecked(false);
                check_longitude.setChecked(false);
                sort = "time";
                break;
        }
    }
}
