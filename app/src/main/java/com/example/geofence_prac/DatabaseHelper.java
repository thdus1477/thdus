package com.example.geofence_prac;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Currency;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "memo";
    private static final String TABLE_extra = "extra_todolist";

    DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "create table " + TABLE_extra + "(id INTEGER PRIMARY KEY AUTOINCREMENT, place TEXT, content TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_extra);
        onCreate(db);
    }


    SQLiteDatabase db;

    public void addMemo(GeofencingMemo memo){
        String sql = "INSERT INTO "+TABLE_extra+" VALUES(NULL, '"+memo.placeText+"', '"+memo.contentText+"');";
        db.execSQL(sql);
    }

//    public ArrayList getAllText(){
//        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
//        ArrayList<String> arrayList = new ArrayList<String>();
//
//        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TABLE_extra, null);
//        cursor.moveToFirst();
//
//        while(!cursor.isAfterLast()){
//            arrayList.add(cursor.getString(cursor.getColumnIndex("memo")));
//            cursor.moveToNext();
//        }
//
//        return arrayList;
//    }

    public ArrayList<GeofencingMemo> getAllText(){
        db = this.getReadableDatabase();

        String sql = "SELECT * FROM "+TABLE_extra;

        ArrayList<GeofencingMemo> list = new ArrayList<>();

        Cursor results = db.rawQuery(sql,null);
        results.moveToFirst();

        while(!results.isAfterLast()){                  //DB 마지막까지 반복문 돌기
            GeofencingMemo memo = new GeofencingMemo(results.getInt(0),results.getString(1),results.getString(2));
            list.add(memo);
            results.moveToNext();
        }

        return list;
    }
}

