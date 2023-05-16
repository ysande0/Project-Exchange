package com.syncadapters.czar.exchange.contentproviders;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

/*
*  Will primarily be used by Sync Adapters which means the home table will be the main focus.
*
* */

public class SyncContentProvider extends ContentProvider {

    // NOTE: if problems arise, remove syncprovider and just use the package name

    private static final String TAG = "MSG";
    public SyncContentProvider(){


    }

    @Override
    public boolean onCreate() {

        Log.d(TAG, "SyncContentProvider onCreate");
        return false;
    }
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.

       return 0;
    }

    @Override
    public String getType(@NonNull Uri uri) {

        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        return null;
    }



    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {


        return null;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {


        return 0;
    }
}
