package com.ifmo.optiks.test;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.ifmo.optiks.R;
import com.ifmo.optiks.provider.OptiksProviderMetaData;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * Author: Aleksey Vladiev (Avladiev2@gmail.com)
 */
public class TestConnection extends ListActivity {

    private TextView mTextView;
    private String TAG = "TestConnection2TAG";
    private final static String url = "http://89.112.11.137:8028/Download";
    private Cursor mCursor;
    final static String IS_CHANGE = "is_CHANGE";
    final static int REQUEST_CODE = 0;


    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection_layuot2);
        mTextView = (TextView) findViewById(R.id.textSelect);
        mCursor = managedQuery(OptiksProviderMetaData.LevelTable.CONTENT_URI, null, null, null, null);
        setListAdapter(new SimpleCursorAdapter(this, R.layout.row, mCursor,
                new String[]{OptiksProviderMetaData.LevelTable.NAME, OptiksProviderMetaData.LevelTable._ID},
                new int[]{R.id.name, R.id._ID}
        ));
        mTextView = (TextView) findViewById(R.id.textSelect);
    }

    public void onListItemClick(
            final ListView parent, final View v, final int position, final long id) {
        mTextView.setText("Select: " + "pos = " + position + " id = " + id);
        final Intent intent = new Intent(this, LevelView.class);
        intent.putExtra(OptiksProviderMetaData.LevelTable._ID, id);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == RESULT_OK) {
            final Bundle ex = data.getExtras();
            if (ex.getBoolean(IS_CHANGE)) {   //todo
                mCursor.requery();
            }
        }
    }

    final static int LOAD_LEVEL_MENU = 1;
    final static int DELETE_ALL_MENU = 2;

    public boolean onCreateOptionsMenu(final Menu menu) {
        menu.add(Menu.NONE, LOAD_LEVEL_MENU, Menu.NONE, "loadLavel");
        menu.add(Menu.NONE, DELETE_ALL_MENU, Menu.NONE, "delete all");
        return (super.onCreateOptionsMenu(menu));
    }

    public boolean onOptionsItemSelected(final MenuItem item) {
        final long id = this.getSelectedItemId();

        switch (item.getItemId()) {
            case LOAD_LEVEL_MENU: {
                for (int i = 1; i < 4; i++) {
                    final String req = connect("level=" + i);
                    if (req != null) {
                        final String name = req.split("name")[1].replace(":", "").replace("\"", "").replace("}", "");
                        final ContentValues val = new ContentValues();
                        val.put(OptiksProviderMetaData.LevelTable.NAME, name);
                        val.put(OptiksProviderMetaData.LevelTable.VALUE, req);
                        getContentResolver().insert(OptiksProviderMetaData.LevelTable.CONTENT_URI, val);
                    }
                }
                mCursor.requery();//todo

            }
            break;
            case DELETE_ALL_MENU:
                getContentResolver().delete(OptiksProviderMetaData.LevelTable.CONTENT_URI, null, null);
                mCursor.requery();//todo
                break;

        }
        return (super.onOptionsItemSelected(item));
    }


    public String connect(final String param) {
        String res = null;
        final HttpClient client = new DefaultHttpClient();
        final HttpGet httpGet = new HttpGet(url + "?" + param);

        final ResponseHandler<String> responseHandler = new BasicResponseHandler();
        try {
            res = client.execute(httpGet, responseHandler);
        } catch (IOException e) {
            Log.e(TAG, "", e);
        }

        return res;
    }
}
