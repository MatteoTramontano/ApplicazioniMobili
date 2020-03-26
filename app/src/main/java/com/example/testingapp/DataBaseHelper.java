package com.example.testingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "myDb";
    public static final String TABLE_TASK = "myTable";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "TASK";
    public static final String COL_3 = "CATEGORY";
    public static final String COL_4 = "DATA";
    public static final String COL_5 = "ORA";
    public static final String COL_6 = "PRIORITY";
    public static final String COL_7 = "STATE";


    public static final String TABLE_CAT = "categoryTable";
    public static final String COL_ONE = "ID";
    public static final String COL_TWO = "CAT";

    private String createTable = "CREATE TABLE " + TABLE_TASK + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " + "TASK TEXT, "+" CATEGORY TEXT, "+" DATA TEXT ,"+" ORA TEXT, "+" PRIORITY TEXT, "+" STATE TEXT DEFAULT 'OPEN')";
    private String create = "CREATE TABLE " + TABLE_CAT + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " + "CAT TEXT )";

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null,1 );
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAT);
        db.execSQL(createTable);
        db.execSQL(create);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAT);
        onCreate(db);

    }

    public boolean addData(String task, String category, String data, String ora, String priority) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, task);
        contentValues.put(COL_3,category);
        contentValues.put(COL_4,data);
        contentValues.put(COL_5,ora);
        contentValues.put(COL_6,priority);
        //contentValues.put(COL_7,"OPEN");


        long result = db.insert(TABLE_TASK, null, contentValues);

        if (result == -1) {

            return false;
        }
        else {return true;}
    }

    public boolean addCategory (String category){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TWO, category);

        long result = db.insert(TABLE_CAT, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;

    }

    public Cursor getListContent() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_TASK,  null);
        return data;
    }

    public Cursor getFilterListContent (String filtro){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data =  db.rawQuery("SELECT * FROM " + TABLE_TASK + " WHERE " + COL_3 + " = '" +  filtro + "'",null);
        return data;
    }

    public Cursor getListCategory(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_CAT, null);
        return data;
    }
    //riempo in arraylist con le categoire da datebase, serve per inserirle nello spinner
    public ArrayList<String> getAllCat() {
        ArrayList<String> categoryArrayList = new ArrayList<String>();

        //String selectQuery = "SELECT " + COL_TWO + " FROM " + TABLE_CAT;
        String selectQuery = "SELECT * FROM " + TABLE_CAT;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
            while (c.moveToNext()){
                categoryArrayList.add(c.getString(1));
            }

    return categoryArrayList;
    }

    public void deleteCat (String name){
        SQLiteDatabase db = this.getWritableDatabase();
        //String selectQuery = "DELETE FROM " + TABLE_CAT + " WHERE " + COL_TWO + " LIKE " + name;
        //db.execSQL(selectQuery);
        db.delete(TABLE_CAT,COL_TWO+" = '" + name + "'" ,null);
    }

    public ArrayList <String>  filterCat (String filtro){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList <String> filterCategory = new ArrayList<>();
        String filterQuery = "SELECT * FROM " + TABLE_TASK + " WHERE " + COL_TWO + " = '" +  filtro +"'";
        Cursor c = db.rawQuery(filterQuery,null);
        while(c.moveToNext()){
            filterCategory.add(c.getString(1));
        }
        return filterCategory;

    }

    public Cursor getEditContent(String title, String cat) {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_TASK + " WHERE " + COL_2 + " = '"+ title +"' AND "+COL_3+ " = '"+ cat +"'",  null);
        return data;
    }
    public void deleteTask(String title, String cat) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASK, COL_2 + " = '"+ title +"'AND "+COL_3+ " = '"+ cat +"'", null);

    }
    public boolean setDone (String title, String category){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_7,"CLOSE");
        long result = db.update(TABLE_TASK, contentValues, COL_2 + " = '"+ title +"'AND "+COL_3+ " = '"+ category +"'", null);


        if (result == -1) {

            return false;
        }
        else {
            return true;
        }

    }

    public boolean setOnDoing (String title, String category){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_7,"ONDOING");
        long result = db.update(TABLE_TASK, contentValues, COL_2 + " = '"+ title +"'AND "+COL_3+ " = '"+ category +"'", null);


        if (result == -1) {

            return false;
        }
        else {
            return true;
        }

    }
    public boolean editData(String title, String category, String data, String ora, String priority, String t, String c) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, title);
        contentValues.put(COL_3, category);
        contentValues.put(COL_4, data);
        contentValues.put(COL_5, ora);
        contentValues.put(COL_6, priority);

        long result = db.update(TABLE_TASK, contentValues, COL_2 + " = '"+ t +"'AND "+COL_3+ " = '"+ c +"'", null);

        if (result == -1) {

            return false;
        }
        else {return true;}

    }

    public Cursor getIdTask(String task, String  category, String data, String ora) {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT "+COL_1+ " FROM "+TABLE_TASK+ " WHERE " + COL_2 + " = '"+ task +"' AND "+COL_3+ " = '"+ category +"' AND "+COL_4+ " = '"+ data +"' AND "+COL_5+ " = '"+ ora +"'";
        Cursor id = db.rawQuery(query,null);
        return id;
    }
    public Cursor getOpenTask (){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+ TABLE_TASK+ " WHERE " + COL_7 + "= 'OPEN' ";
        Cursor cur = db.rawQuery(query,null);
        return cur;
    }

    public Cursor getCloseTask (){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+ TABLE_TASK+ " WHERE " + COL_7 + "= 'CLOSE'  ";
        Cursor cur = db.rawQuery(query,null);
        return cur;
    }

    public Cursor getOnDoingTask (){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+ TABLE_TASK+ " WHERE " + COL_7 + "= 'ONDOING'  ";
        Cursor cur = db.rawQuery(query,null);
        return cur;
    }

    public  Cursor getState (String state){
        SQLiteDatabase db = getWritableDatabase();
        Cursor s = db.rawQuery("SELECT * FROM " + TABLE_TASK + " WHERE " +COL_7+ " = '"+ state +"'",  null);
        return s;



    }



}