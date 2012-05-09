package com.ifmo.optiks.test;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import com.ifmo.optiks.R;
import com.ifmo.optiks.base.gson.Fields;
import com.ifmo.optiks.provider.OptiksProviderMetaData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Author: Aleksey Vladiev (Avladiev2@gmail.com)
 */
public class TestProvider extends Activity {

    private final static String TAG = "TestProviderTAG";

    @Override     //перед запуском теста приложение    удалять!
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);














        Cursor cursor = managedQuery(OptiksProviderMetaData.SeasonsTable.CONTENT_URI,null,null,null,null);
        final int idCol = cursor.getColumnIndex(OptiksProviderMetaData.SeasonsTable._ID);
        final int nameCol = cursor.getColumnIndex(OptiksProviderMetaData.SeasonsTable.NAME);
        final int descriptionCol = cursor.getColumnIndex(OptiksProviderMetaData.SeasonsTable.DESCRIPTION);
        final int  max3Col = cursor.getColumnIndex(OptiksProviderMetaData.SeasonsTable.MAX_LEVEL_REACHED);
        Log.d(TAG,"season table");
        for ( cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {
            Log.d(TAG,"id = "+ cursor.getInt(idCol));
            Log.d(TAG,"name = "+ cursor.getString(nameCol));
            Log.d(TAG,"description = "+ cursor.getString(descriptionCol));
            Log.d(TAG,"max = "+ cursor.getInt(max3Col));
            
        }
                 
        
                
                
        cursor = managedQuery(OptiksProviderMetaData.LevelsTable.CONTENT_URI, null, null, null, null);
        final int id = cursor.getColumnIndex(OptiksProviderMetaData.LevelsTable._ID);
        final int level = cursor.getColumnIndex(OptiksProviderMetaData.LevelsTable.LEVEL);
        final int seasonId = cursor.getColumnIndex(OptiksProviderMetaData.LevelsTable.SEASON_ID);
       
        Log.d(TAG,"level table");
        Log.d(TAG, "cout=" + cursor.getColumnCount());
        for ( cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Log.d(TAG,"id = " + cursor.getInt(id));
            Log.d(TAG,"seasonId = " + cursor.getInt(seasonId));
        }
    }

    /* private void valid(final Cursor cursor, final String s) {
        final int size = cursor.getCount();
        if (size != 10) {
            throw new RuntimeException();
        }

        final int id = cursor.getColumnIndex(OptiksProviderMetaData.LevelTables._ID);
        final int level = cursor.getColumnIndex(OptiksProviderMetaData.LevelTables.LEVEL);
        cursor.moveToFirst();
        for (int i = 0; !cursor.isAfterLast(); cursor.moveToNext(), i++) {
            final int idData = cursor.getInt(id);
            if (idData != i) {
                throw new RuntimeException();
            }
            final String levelData = cursor.getString(level);
            if (!levelData.equals(s + i)) {
                throw new RuntimeException();
            }
        }
    }*/
}
