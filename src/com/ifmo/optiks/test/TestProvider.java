package com.ifmo.optiks.test;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
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
        cv.put(OptiksProviderMetaData.SeasonsTable._ID, 1);
        cv.put(OptiksProviderMetaData.SeasonsTable.NAME, "name");
        cv.put(OptiksProviderMetaData.SeasonsTable.DESCRIPTION, "des");
        getContentResolver().insert(OptiksProviderMetaData.SeasonsTable.CONTENT_URI, cv);
        cv.clear();
        final Uri uri = getContentResolver().insert(Uri.parse(OptiksProviderMetaData.LevelTables.CONTENT_URI + "/1/create_table"), null);
        Log.d(TAG, uri + "");
        for (int i = 0; i < 10; i++) {
            cv.put(OptiksProviderMetaData.LevelTables._ID, i);
            cv.put(OptiksProviderMetaData.LevelTables.LEVEL, "aaa" + i);

            getContentResolver().insert(uri, cv);
            cv.clear();
        }

        Cursor cursor = managedQuery(uri, null, null, null, null);
        valid(cursor, "aaa");

        for (int i = 0; i < 10; i++) {
            cv.put(OptiksProviderMetaData.LevelTables._ID, i);
            cv.put(OptiksProviderMetaData.LevelTables.LEVEL, "bbb" + i);
            getContentResolver().update(uri, cv, OptiksProviderMetaData.LevelTables._ID + "=" + i, null);
            cv.clear();
        }
        cursor.requery();
        valid(cursor, "bbb");
        Log.d(TAG, "Success");
    }

    private void valid(final Cursor cursor, final String s) {
        final int size = cursor.getCount();
        if (size != 10) {
            throw new RuntimeException();
        }

        final int id = cursor.getColumnIndex(OptiksProviderMetaData.LevelTables._ID);
        final int level = cursor.getColumnIndex(OptiksProviderMetaData.LevelTables.LEVEL);
        cursor.moveToFirst();
        for (int i = 0; !cursor.isAfterLast(); cursor.moveToNext(), i++) {
            int idData = cursor.getInt(id);
            if (idData != i) {
                throw new RuntimeException();
            }
            String levelData = cursor.getString(level);
            if (!levelData.equals(s + i)) {
                throw new RuntimeException();
            }
        }
    }
}
