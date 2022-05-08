package com.project.caseManager.classes;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
// Datamanager Class used to handle SQLiteDatabase
public class DataManager {

    // This is the actual database
    private SQLiteDatabase db;
    /*
        Next we have a public static final string for
        each row of the table that we need to refer to both
        inside and outside this class
    */
    public static final String TABLE_ROW_ID = "id";
    public static final String TABLE_ROW_FULLNAME = "fullname";
    public static final String TABLE_ROW_ADDRESS = "address";
    public static final String TABLE_ROW_DATE = "dateofexposure";

    /*
        Next we have a private static final strings for
        each row/table that we need to refer to just
        inside this class
    */

    private static final String DB_NAME = "testandroid";
    private static final int DB_VERSION = 1;
    private static final String TABLE_N_AND_A = "patients";

    public DataManager(Context context) {
        // Create an instance of our internal CustomSQLiteOpenHelper
        CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
        // Get a writable database
        db = helper.getWritableDatabase();
    }

    // Here are all our helper methods

    // Insert a record
    public void insert(String name, String address, String date){

        // Add all the details to the table
        String query = String.format("INSERT INTO %s (%s, %s, %s) VALUES ('%s', '%s', '%s')", TABLE_N_AND_A, TABLE_ROW_FULLNAME, TABLE_ROW_ADDRESS, TABLE_ROW_DATE, name, address, date);
        Log.i("insert() = ", query);
        db.execSQL(query);

    }

    //Update a record
    public void update(int id ,String name, String address, String date){
        String query = String.format("UPDATE %s SET %s = '%s', %s = '%s', %s = '%s' WHERE %s = %d",
                TABLE_N_AND_A, TABLE_ROW_FULLNAME, name, TABLE_ROW_ADDRESS, address, TABLE_ROW_DATE, date, TABLE_ROW_ID, id);
        Log.i("update() = ", query);
        db.execSQL(query);
    }

    // Delete a record
    public void delete(int id){
        String query = String.format("DELETE FROM %s WHERE %s = '%d';", TABLE_N_AND_A, TABLE_ROW_ID, id);
        Log.i("delete() = ", query);
        db.execSQL(query);

    }
    // Get all the records
    public Cursor selectAll() {
        Cursor c = db.rawQuery("SELECT *" +" from " +
                TABLE_N_AND_A, null);
        return c;
    }

    // Find a specific record
    public Cursor searchName(String name) {
        String query = String.format("SELECT %s, %s, %d FROM %s WHERE %s = '%s'",
                TABLE_ROW_ID, TABLE_ROW_ADDRESS, TABLE_ROW_DATE, TABLE_N_AND_A, TABLE_ROW_FULLNAME, name);
        Log.i("searchName() = ", query);
        Cursor c = db.rawQuery(query, null);
        return c;
    }

    // This class is created when our DataManager is initialized
    private class CustomSQLiteOpenHelper extends SQLiteOpenHelper {
        public CustomSQLiteOpenHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        // This runs the first time the database is created
        @Override
        public void onCreate(SQLiteDatabase ddb) {
            // Create a table for photos and all their details
            String newTableQueryString = String.format("create table %s " +
                            "(%s integer primary key autoincrement not null, %s text not null, %s text not null, %s string not null);",
                    TABLE_N_AND_A, TABLE_ROW_ID, TABLE_ROW_FULLNAME, TABLE_ROW_ADDRESS, TABLE_ROW_DATE);
            ddb.execSQL(newTableQueryString);
        }

        // This method only runs when we increment DB_VERSION
        @Override
        public void onUpgrade(SQLiteDatabase ddb, int oldVersion, int newVersion) {}

    }

    //Prints the location's path. For debug purposes.
    public void printLocation(){
        System.out.println(db.getPath());
    }

    //getting the AUTO_INCREMENT value of table patients in order to insert the proper Patient entity id.
    public int getAutoIncrementValue (){
        String sql = String.format("select seq from sqlite_sequence where name=\"%s\";", TABLE_N_AND_A);
        Cursor c = db.rawQuery(sql, null);
        c.moveToNext();
        return c.getInt(0);
    }

    //Deletes the database by dynamically finding it's filepath then deleting it.
    public void deleteDb(){
        File file = new File(db.getPath());
        db.deleteDatabase(file);
    }

}
