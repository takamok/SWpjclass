package com.example.newapp;
import android.provider.BaseColumns;

public final class DataBases {
    public static final class CreateDB implements BaseColumns{
    public static final String Latitude = "Latitude";
    public static final String longitude = "longitude";
    public static final String time = "time";
    public static final String _TABLENAME0 = "GPStable";
    public static final String _CREATE0 = "create table if not exists "+_TABLENAME0+"("
            +_ID+" integer primary key autoincrement, "
            +Latitude+" text not null , "
            +longitude+" text not null , "
            +time+" text not null);";
    }
}