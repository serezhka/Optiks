package com.ifmo.optiks.provider;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Author: Aleksey Vladiev (Avladiev2@gmail.com)
 */

public class OptiksProviderMetaData {

    public static final String AUTHORITY = "com.ifmo.optiks.Provider";
    public static final String DATA_BASE_NAME = "optiks";
    public static final int DATA_BASE_VERSION = 1;

    final static UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static String getUriType(final Uri uri) {
        switch (TypeUri.getType(URI_MATCHER.match(uri))) {
            case COOKIE_COLLECTION_URI_ID:
                return CookieTable.CONTENT_TYPE;
            case COOKIE_SINGLE_URI_ID:
                return CookieTable.CONTENT_ITEM_TYPE;
            case SEASONS_COLLECTION_URI_ID:
                return SeasonsTable.CONTENT_TYPE;
            case SEASONS_SINGLE_URI_ID:
                return SeasonsTable.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown uri : " + uri);
        }
    }

    static {
        URI_MATCHER.addURI(AUTHORITY, CookieTable.TABLE_NAME, TypeUri.COOKIE_COLLECTION_URI_ID.v);
        URI_MATCHER.addURI(AUTHORITY, CookieTable.TABLE_NAME + "/#", TypeUri.COOKIE_SINGLE_URI_ID.v);
        URI_MATCHER.addURI(AUTHORITY, SeasonsTable.TABLE_NAME, TypeUri.SEASONS_COLLECTION_URI_ID.v);
        URI_MATCHER.addURI(AUTHORITY, SeasonsTable.TABLE_NAME + "/#", TypeUri.SEASONS_SINGLE_URI_ID.v);
        URI_MATCHER.addURI(AUTHORITY, LevelsTable.TABLE_NAME, TypeUri.LEVEL_COLLECTION_URI_ID.v);
        URI_MATCHER.addURI(AUTHORITY, LevelsTable.TABLE_NAME + "/#", TypeUri.LEVEL_SINGLE_URI_ID.v);


    }

    TypeUri getType(final Uri uri) {
        return TypeUri.getType(URI_MATCHER.match(uri));
    }

    public static boolean isCollectionUri(final Uri uri) {
        final TypeUri t = TypeUri.getType(uri);
        return t == TypeUri.SEASONS_COLLECTION_URI_ID || t == TypeUri.COOKIE_COLLECTION_URI_ID;
    }

    public final static class CookieTable implements BaseColumns {
        public final static String TABLE_NAME = "cookie_table";
        public final static Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.optiks.cookie";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.optiks.cookie";
        //  type string
        static final String NAME = "name";
        static final String DEFAULT_NAME = "default_name";

        //type string
        static final String VALUE = "value";
        static final String DEFAULT_VALUE = "default_value";
    }
    public final static class LevelsTable implements BaseColumns {
        public final static String TABLE_NAME = "levels_table";
        public final static Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.optiks.levels";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.optiks.levels";

        public static final String LEVEL = "level";
        public static final String LEVEL_ID = "level_id";
        public static final String SEASON_ID = "season_id";



    }




    public final static class SeasonsTable implements BaseColumns {
        public final static String TABLE_NAME = "seasons";
        public final static Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.optiks.level";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.optiks.level";

        //  type string
        public static final String NAME = "name";
        public static final String DEFAULT_NAME = "default_name";

        //type string        in json
        public static final String DESCRIPTION = "description";
        public static final String DEFAULT_DESCRIPTION = "default_value";
    }

    public enum TypeUri {
        COOKIE_COLLECTION_URI_ID(0),
        COOKIE_SINGLE_URI_ID(1),
        SEASONS_COLLECTION_URI_ID(2),
        SEASONS_SINGLE_URI_ID(3),
        LEVEL_SINGLE_URI_ID(4),
        LEVEL_COLLECTION_URI_ID(5);
        final int v;

        private TypeUri(final int t) {
            v = t;
        }

        static TypeUri getType(final Uri uri) {
            return TypeUri.getType(URI_MATCHER.match(uri));
        }


        static TypeUri getType(final int match) {
            return TypeUri.values()[match];
        }
    }
}
