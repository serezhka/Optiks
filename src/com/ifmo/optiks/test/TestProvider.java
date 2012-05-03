package com.ifmo.optiks.test;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import com.ifmo.optiks.provider.OptiksProviderMetaData;

/**
 * Author: Aleksey Vladiev (Avladiev2@gmail.com)
 */
public class TestProvider extends Activity {

    private final static String TAG = "TestProviderTAG";

    @Override     //перед запуском теста приложение    удалять!
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ContentValues cv = new ContentValues();
        cv.put(OptiksProviderMetaData.LevelsTable.LEVEL_ID, 12);
        cv.put(OptiksProviderMetaData.LevelsTable.LEVEL, "des");
        cv.put(OptiksProviderMetaData.LevelsTable.SEASON_ID, 1);
        getContentResolver().insert(OptiksProviderMetaData.LevelsTable.CONTENT_URI, cv);
        cv.clear();

        Cursor cursor = managedQuery(OptiksProviderMetaData.LevelsTable.CONTENT_URI, null, null, null, null);
        final int id = cursor.getColumnIndex(OptiksProviderMetaData.LevelsTable._ID);
        final int level = cursor.getColumnIndex(OptiksProviderMetaData.LevelsTable.LEVEL);
        final int levelId = cursor.getColumnIndex(OptiksProviderMetaData.LevelsTable.LEVEL_ID);
        final int seasonId = cursor.getColumnIndex(OptiksProviderMetaData.LevelsTable.SEASON_ID);
        cursor.moveToFirst();
        Log.d(TAG, "cout=" + cursor.getColumnCount());
        for (int i = 0; !cursor.isAfterLast(); cursor.moveToNext(), i++) {

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
