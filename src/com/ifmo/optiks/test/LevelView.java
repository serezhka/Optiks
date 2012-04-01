package com.ifmo.optiks.test;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.ifmo.optiks.R;
import com.ifmo.optiks.provider.OptiksProviderMetaData;

/**
 * Author: Aleksey Vladiev (Avladiev2@gmail.com)
 */

public class LevelView extends Activity {

    static final String TAG = "LevelViewTAG";
    private final int DELETE_LEVEL = 0;
    private TextView mTextView;
    private long id;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lavel);
        mTextView = (TextView) findViewById(R.id.level_text);
        id = getIntent().getExtras().getLong(OptiksProviderMetaData.LevelTable._ID);
        final Cursor c = managedQuery(OptiksProviderMetaData.LevelTable.CONTENT_URI, null, "_id=?",
                new String[]{id + ""}, null);
        c.moveToFirst();
        final int idName = c.getColumnIndex(OptiksProviderMetaData.LevelTable.NAME);
        final int idValue = c.getColumnIndex(OptiksProviderMetaData.LevelTable.VALUE);
        final String s = c.getString(idName) + c.getString(idValue);
        mTextView.setText(s);
    }

    public boolean onCreateOptionsMenu(final Menu menu) {
        menu.add(Menu.NONE, DELETE_LEVEL, Menu.NONE, "deleteLavel");
        return (super.onCreateOptionsMenu(menu));
    }

    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_LEVEL: {
                getContentResolver().delete(OptiksProviderMetaData.LevelTable.CONTENT_URI, "_id=?", new String[]{id + ""});
                final Intent in = new Intent();
                in.putExtra(TestConnection.IS_CHANGE, true);
                setResult(RESULT_OK, in);
                finish();
            }
        }
        return true;
    }
}


