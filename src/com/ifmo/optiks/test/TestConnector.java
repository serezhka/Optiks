package com.ifmo.optiks.test;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import com.ifmo.optiks.connection.Connector;
import com.ifmo.optiks.provider.OptiksProviderMetaData;
import org.json.JSONException;

import java.io.IOException;

/**
 * Author: Aleksey Vladiev (Avladiev2@gmail.com)
 */
public class TestConnector extends Activity {
    private final static String TAG = "TestConnectorTAG";


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Connector connector = new Connector();
        try {
            final int id = 1;
            Log.d(TAG, "getSeasonsCount = " + connector.getSeasonsCount());
            Log.d(TAG, "getLevelCount = " + connector.getLevelCount(id));
            Log.d(TAG, "getNameDescription = " + connector.getNameDescription(id));

            Log.d(TAG,"call saveSeason") ;
            connector.saveSeason(id, getContentResolver());
            log();
            Log.d(TAG,"call updateLavels") ;
            connector.updateSeason(id, getContentResolver());
            log();


        } catch (IOException e) {
            Log.e(TAG, "", e);
        } catch (JSONException e) {
            Log.e(TAG, "", e);

        }

        
    }
    
    private void log() {
        final Cursor cursorSeason = managedQuery(OptiksProviderMetaData.SeasonsTable.CONTENT_URI, null, null, null, null);
        final int idNumSeason = cursorSeason.getColumnIndex(OptiksProviderMetaData.SeasonsTable._ID);
        final int nameNumSeason = cursorSeason.getColumnIndex(OptiksProviderMetaData.SeasonsTable.NAME);
        final int descriptionNumSeason = cursorSeason.getColumnIndex(OptiksProviderMetaData.SeasonsTable.DESCRIPTION);


        for (cursorSeason.moveToFirst(); !cursorSeason.isAfterLast(); cursorSeason.moveToNext()) {
            Log.d(TAG, "id = " + cursorSeason.getInt(idNumSeason) + " name =" + cursorSeason.getInt(nameNumSeason) + " description " + cursorSeason.getString(descriptionNumSeason));
        }

        final Cursor cursorLevel = managedQuery(OptiksProviderMetaData.LevelsTable.CONTENT_URI, null, null, null, null);
        final int levelIdNum = cursorLevel.getColumnIndex(OptiksProviderMetaData.LevelsTable.LEVEL_ID);
        final int seasonIdNum = cursorLevel.getColumnIndex(OptiksProviderMetaData.LevelsTable.SEASON_ID);
        final int levelData = cursorLevel.getColumnIndex(OptiksProviderMetaData.LevelsTable.LEVEL);


        for (cursorLevel.moveToFirst(); !cursorLevel.isAfterLast(); cursorLevel.moveToNext()) {
            Log.d(TAG, "LEVEL_ID" + cursorLevel.getInt(levelIdNum) + " SEASON_ID " + cursorLevel.getInt(seasonIdNum));
            Log.d(TAG, "LEVEL" + cursorLevel.getString(levelData));

        }
    }
}
