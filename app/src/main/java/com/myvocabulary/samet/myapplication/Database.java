package com.myvocabulary.samet.myapplication;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DATABASE_WORD";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "MYWORDS";

    private static final String COL_NAME = "Name";
    private static final String COL_EQUIVALENT = "Equivalent";
    private static final String COL_TYPE = "Type";
    private static final String COL_EXAMPLE = "Example";
    private static final String COL_ID = "ID";


    public long addWord(Word word){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("NAME",word.getName());
        cv.put("EQUIVALENT",word.getEquivalent());
        cv.put("TYPE",word.getType());
        cv.put("EXAMPLE",word.getExample());

        long id = db.insert(TABLE_NAME,null,cv);

        db.close();
        return id;
    }
    public void deleteWord(String wordName){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COL_NAME + "='" + wordName + "'", null);
        db.close();
    }

    public Word retrieveData (String wordName){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.query(TABLE_NAME, new String[] {
                        COL_NAME, COL_EQUIVALENT, COL_TYPE, COL_EXAMPLE},
                COL_NAME + "='" + wordName + "'", null, null, null,null);


        Word word = null;
        if (c != null && c.moveToFirst()){
            word = new Word(
                    c.getString(c.getColumnIndex(COL_NAME)),
                    c.getString(c.getColumnIndex(COL_EQUIVALENT)),
                    c.getString(c.getColumnIndex(COL_TYPE)),
                    c.getString(c.getColumnIndex(COL_EXAMPLE))
            );
            c.close();
        }
        db.close();

        return word;
    }

    public void update(Word word,String oldName){

        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();

        cv.put("NAME",word.getName());
        cv.put("EQUIVALENT",word.getEquivalent());
        cv.put("TYPE",word.getType());
        cv.put("EXAMPLE",word.getExample());

        db.update(TABLE_NAME,cv,COL_NAME + "='" + oldName + "'",null);
        db.close();

    }

    public List<String> fetchAllWordName(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + COL_NAME + " FROM " + TABLE_NAME,null);
        List<String> wordList = new ArrayList<>();

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
            wordList.add(c.getString(c.getColumnIndex(COL_NAME)));
        }
        db.close();
        return wordList;

    }

    public Database(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " %s TEXT, %s TEXT, %s TEXT, %s TEXT);",
                TABLE_NAME,
                COL_ID,
                COL_NAME,
                COL_EQUIVALENT,
                COL_TYPE,
                COL_EXAMPLE));
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


}
