package com.ifmo.optiks.test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.ifmo.optiks.connection.Connector;
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

        } catch (IOException e) {
            Log.e(TAG, "", e);
        } catch (JSONException e) {
            Log.e(TAG, "", e);
        }
    }
}
