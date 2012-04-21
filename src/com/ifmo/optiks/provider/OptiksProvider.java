package com.ifmo.optiks.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

/**
 * Author: Aleksey Vladiev (Avladiev2@gmail.com)
 */

public class OptiksProvider extends ContentProvider {

    private final static String TAG = "OptiksProviderTAG";


    private DatabaseHelper openHelper;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        /**
         * Create a helper object to create, open, and/or manage a database.
         * This method always returns very quickly.  The database is not actually
         * created or opened until one of {@link #getWritableDatabase} or
         * {@link #getReadableDatabase} is called.
         *
         * @param context to use to open or create the database
         * @param name    of the database file, or null for an in-memory database
         * @param factory to use for creating cursor objects, or null for the default
         * @param version number of the database (starting at 1); if the database is older,
         *                {@link #onUpgrade} will be used to upgrade the database
         */
        public DatabaseHelper(final Context context, final String name, final SQLiteDatabase.CursorFactory factory, final int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(final SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + OptiksProviderMetaData.CookieTable.TABLE_NAME + " (" +
                    OptiksProviderMetaData.CookieTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + OptiksProviderMetaData.CookieTable.NAME + " TEXT, " +
                    OptiksProviderMetaData.CookieTable.VALUE + " TEXT);");

            db.execSQL("CREATE TABLE " + OptiksProviderMetaData.SeasonsTable.TABLE_NAME + " (" +
                    OptiksProviderMetaData.SeasonsTable._ID + " INTEGER PRIMARY KEY , "                       //AUTOINCREMENT
                    + OptiksProviderMetaData.SeasonsTable.NAME + " TEXT, " +
                    OptiksProviderMetaData.SeasonsTable.DESCRIPTION + " TEXT);");

        }

        @Override
        public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
            Log.w(TAG, "onUpgrade data base on " + oldVersion + "to" + newVersion);
            db.execSQL("DROP TABLE IF EXISTS " + OptiksProviderMetaData.CookieTable.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + OptiksProviderMetaData.SeasonsTable.TABLE_NAME);
            onCreate(db);

        }
    }

    @Override
    public boolean onCreate() {
        openHelper = new DatabaseHelper(getContext(), OptiksProviderMetaData.DATA_BASE_NAME, null, OptiksProviderMetaData.DATA_BASE_VERSION);
        return true;
    }

    @Override
    public Cursor query(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder) {
        final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (OptiksProviderMetaData.TypeUri.getType(uri)) {
            case COOKIE_COLLECTION_URI_ID: {
                qb.setTables(OptiksProviderMetaData.CookieTable.TABLE_NAME);
                break;
            }
            case COOKIE_SINGLE_URI_ID: {
                qb.setTables(OptiksProviderMetaData.CookieTable.TABLE_NAME);
                qb.appendWhere(OptiksProviderMetaData.CookieTable._ID + "=" + uri.getPathSegments().get(1));
                break;
            }
            case SEASONS_COLLECTION_URI_ID: {
                qb.setTables(OptiksProviderMetaData.SeasonsTable.TABLE_NAME);
                break;
            }
            case SEASONS_SINGLE_URI_ID: {
                qb.setTables(OptiksProviderMetaData.SeasonsTable.TABLE_NAME);
                qb.appendWhere(OptiksProviderMetaData.SeasonsTable._ID + "=" + uri.getPathSegments().get(1));
                break;
            }
            case LEVEL_COLLECTION_URI_ID: {
                qb.setTables(uri.getPathSegments().get(0) + uri.getPathSegments().get(1));
                break;
            }
            case LEVEL_SINGLE_URI_ID: {
                qb.setTables(uri.getPathSegments().get(0) + uri.getPathSegments().get(1));
                qb.appendWhere(OptiksProviderMetaData.LevelTables._ID + "=" + uri.getPathSegments().get(2));
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown uri : " + uri);
        }


        final SQLiteDatabase db = openHelper.getReadableDatabase();
        final Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(final Uri uri) {
        return OptiksProviderMetaData.getUriType(uri);
    }

    @Override
    public Uri insert(final Uri uri, final ContentValues values) {
        final List<String> pathSegments = uri.getPathSegments();
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final long row;
        final Uri insertUrl;
        switch (OptiksProviderMetaData.TypeUri.getType(uri)) {
            case COOKIE_COLLECTION_URI_ID: {
                row = db.insert(OptiksProviderMetaData.CookieTable.TABLE_NAME, OptiksProviderMetaData.CookieTable._ID, values);
                if (row > 0) {
                    insertUrl = ContentUris.withAppendedId(OptiksProviderMetaData.CookieTable.CONTENT_URI, row);
                } else {
                    throw new SQLException("Failed to insert uri : " + uri);
                }
                break;
            }

            case SEASONS_COLLECTION_URI_ID: {
                row = db.insert(OptiksProviderMetaData.SeasonsTable.TABLE_NAME, OptiksProviderMetaData.SeasonsTable._ID, values);
                if (row > 0) {
                    insertUrl = ContentUris.withAppendedId(OptiksProviderMetaData.SeasonsTable.CONTENT_URI, row);
                } else {
                    throw new SQLException("Failed to insert uri : " + uri);
                }
                break;
            }
            case CREATE_LEVEL_TABLE: {
                final String tableName = pathSegments.get(0) + pathSegments.get(1);
                db.execSQL("DROP TABLE IF EXISTS " + tableName);
                db.execSQL("CREATE TABLE " + tableName + " (" +
                        OptiksProviderMetaData.LevelTables._ID + " INTEGER PRIMARY KEY , "
                        + OptiksProviderMetaData.LevelTables.LEVEL + " TEXT);");
                insertUrl = Uri.parse(OptiksProviderMetaData.LevelTables.CONTENT_URI + "/" + pathSegments.get(1));


                break;
            }
            case LEVEL_COLLECTION_URI_ID: {
                final String tableName = pathSegments.get(0) + pathSegments.get(1);
                row = db.insert(tableName, OptiksProviderMetaData.LevelTables._ID, values);
                if (row >= 0) {
                    insertUrl = Uri.parse(OptiksProviderMetaData.LevelTables.CONTENT_URI + "/" + pathSegments.get(1) + "/" + row);
                } else {
                    throw new SQLException("Failed to insert uri : " + uri);
                }
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown uri : " + uri);

        }
        getContext().getContentResolver().notifyChange(insertUrl, null);
        return insertUrl;
    }

    @Override
    public int delete(final Uri uri, final String selection, final String[] selectionArgs) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final List<String> pathSegments = uri.getPathSegments();
        final int col;
        switch (OptiksProviderMetaData.TypeUri.getType(uri)) {
            case COOKIE_COLLECTION_URI_ID: {
                col = db.delete(OptiksProviderMetaData.CookieTable.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case COOKIE_SINGLE_URI_ID: {
                final String id = uri.getPathSegments().get(1);
                col = db.delete(OptiksProviderMetaData.CookieTable.TABLE_NAME,
                        OptiksProviderMetaData.CookieTable._ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
                        selectionArgs);
                break;
            }

            case SEASONS_COLLECTION_URI_ID: {
                col = db.delete(OptiksProviderMetaData.SeasonsTable.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case SEASONS_SINGLE_URI_ID: {
                final String id = uri.getPathSegments().get(1);
                col = db.delete(OptiksProviderMetaData.SeasonsTable.TABLE_NAME,
                        OptiksProviderMetaData.SeasonsTable._ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
                        selectionArgs);
                break;
            }
            case LEVEL_COLLECTION_URI_ID: {
                final String tableName = pathSegments.get(0) + pathSegments.get(1);
                col = db.delete(tableName, selection, selectionArgs);
                break;
            }
            case LEVEL_SINGLE_URI_ID: {
                final String tableName = pathSegments.get(0) + pathSegments.get(1);
                final String id = pathSegments.get(2);
                col = db.delete(tableName,
                        OptiksProviderMetaData.LevelTables._ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
                        selectionArgs);
                break;
            }

            default:
                throw new IllegalArgumentException("Unknown uri : " + uri);
        }
        return col;
    }

    @Override
    public int update(final Uri uri, final ContentValues values, final String selection,
                      final String[] selectionArgs) {
        final List<String> pathSegments = uri.getPathSegments();
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int col;
        switch (OptiksProviderMetaData.TypeUri.getType(uri)) {
            case COOKIE_COLLECTION_URI_ID: {
                col = db.update(OptiksProviderMetaData.CookieTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case COOKIE_SINGLE_URI_ID: {
                final String id = uri.getPathSegments().get(1);
                col = db.update(OptiksProviderMetaData.CookieTable.TABLE_NAME, values,
                        OptiksProviderMetaData.CookieTable._ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
                        selectionArgs);
                break;
            }

            case SEASONS_COLLECTION_URI_ID: {
                col = db.update(OptiksProviderMetaData.SeasonsTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case SEASONS_SINGLE_URI_ID: {
                final String id = uri.getPathSegments().get(1);
                col = db.update(OptiksProviderMetaData.SeasonsTable.TABLE_NAME, values,
                        OptiksProviderMetaData.SeasonsTable._ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
                        selectionArgs);
                break;
            }
            case LEVEL_COLLECTION_URI_ID: {
                final String tableName = pathSegments.get(0) + pathSegments.get(1);
                col = db.update(tableName, values, selection, selectionArgs);
                break;
            }
            case LEVEL_SINGLE_URI_ID: {
                final String tableName = pathSegments.get(0) + pathSegments.get(1);
                final String id = pathSegments.get(2);
                col = db.update(tableName, values,
                        OptiksProviderMetaData.LevelTables._ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
                        selectionArgs);
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown uri : " + uri);
        }
        return col;
    }


}
