package com.syncadapters.czar.exchange.contentproviders;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

public class SyncContentProvider extends ContentProvider {

    private static final String TAG = "MSG";
    public SyncContentProvider(){


    }

    @Override
    public boolean onCreate() {
        
        return false;
    }
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

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
