package com.ifmo.optiks.test;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.ifmo.optiks.MainOptiksActivity;
import com.ifmo.optiks.R;
import com.ifmo.optiks.provider.OptiksProviderMetaData;

/**
 * Author: Aleksey Vladiev (Avladiev2@gmail.com)
 */

public class StartActivityEx extends Activity {

    final String TAG = "StartActivityExTAG";

    private final String level = "{\"objects\":[" +
            "{\"type\":\"LASER\",\"pY\":2.0,\"rotation\":0.0,\"width\":60,\"height\":60,\"pX\":2.0}," +
            "{\"type\":\"MIRROR\",\"pY\":4.0,\"rotation\":0.0,\"width\":200,\"height\":45,\"pX\":7.0}," +
            "{\"type\":\"MIRROR\",\"pY\":6.0,\"rotation\":0.0,\"width\":200,\"height\":45,\"pX\":7.0}," +
            "{\"type\":\"AIM\",\"pY\":13.0,\"rotation\":0.0,\"width\":70,\"height\":70,\"pX\":21.0}," +
            "{\"type\":\"BARRIER\",\"pY\":4.0,\"rotation\":0.0,\"width\":50,\"height\":150,\"pX\":12.0}" +
            "]}";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity_ex);
        final Listener l = new Listener();
        findViewById(R.id.button_push_to_base).setOnClickListener(l);
        findViewById(R.id.button_launch).setOnClickListener(l);
    }

    private class Listener implements View.OnClickListener {

        int id = 0;

        @Override
        public void onClick(final View v) {
            if (v.getId() == R.id.button_push_to_base) {
                //сохраняем в базу, много констант надо быть внимательным!!
                final ContentValues cv = new ContentValues();
                cv.put(OptiksProviderMetaData.LevelTable.VALUE, level);
                cv.put(OptiksProviderMetaData.LevelTable.NAME, "level1");
                final Uri url = getContentResolver().insert(OptiksProviderMetaData.LevelTable.CONTENT_URI, cv);
                id = Integer.parseInt(url.getPathSegments().get(1));
                Log.d(TAG, url + "");
            } else if (v.getId() == R.id.button_launch) {
                //запускаем Activity
                final Intent intent = new Intent(StartActivityEx.this, MainOptiksActivity.class);
                intent.putExtra(OptiksProviderMetaData.LevelTable._ID, id);
                startActivity(intent);
            }
        }
    }
}
