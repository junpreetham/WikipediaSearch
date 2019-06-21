package com.arjunpreetham.wikipediasearch.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.arjunpreetham.wikipediasearch.models.SavedPageDataModel;

import java.util.ArrayList;
import java.util.Arrays;

public class MainDB extends SQLiteOpenHelper {

    private static final String DB_NAME = "wikipedia.db";
    private static final int VER = 1;

    static Context con;
    public MainDB(Context context) {
        super(context, DB_NAME, null, VER);

        con = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String q = "CREATE TABLE SAVED_PAGES(ID INTEGER PRIMARY KEY AUTOINCREMENT, TITLE TEXT, DESCRIPTION TEXT, IMAGE_URL TEXT, PAGE_ID TEXT);";
        db.execSQL(q);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addPageToDB(SavedPageDataModel page){
        String title = page.getHeading();
        String description = page.getDescription();
        String imageURL = page.getImageURL();
        String pageID = page.getPageID();

        ArrayList<String> col = new ArrayList<>(Arrays.asList("TITLE", "DESCRIPTION", "IMAGE_URL", "PAGE_ID"));
        ArrayList<String> data = new ArrayList<>(Arrays.asList(title, description, imageURL, pageID));

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        for(int i = 0; i < col.size(); i++){
            cv.put(col.get(i), data.get(i));
        }

        db.insert("SAVED_PAGES", null, cv);
    }

    public void deletePage(SavedPageDataModel page){
        String heading = page.getHeading();
        String description = page.getDescription();

        String q = "DELETE FROM SAVED_PAGES WHERE TITLE = \"" + heading + "\" AND DESCRIPTION = \"" + description + "\";";

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(q);
    }

    public ArrayList<SavedPageDataModel> getSavedPages(){

        ArrayList<SavedPageDataModel> data = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT TITLE, DESCRIPTION, IMAGE_URL, PAGE_ID FROM SAVED_PAGES;";

        Cursor c = db.rawQuery(query, null);

        while(c.moveToNext()){
            String title = c.getString(0);
            String description = c.getString(1);
            String imageURL = c.getString(2);
            String pageID = c.getString(3);

            if(description.equals("NONE")){
                description = "No description available";
            }

            SavedPageDataModel model = new SavedPageDataModel(title, description, imageURL, pageID);

            data.add(model);
        }

        c.close();

        return data;
    }
}
