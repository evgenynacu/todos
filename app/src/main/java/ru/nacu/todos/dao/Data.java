package ru.nacu.todos.dao;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import ru.android.common.logs.Logs;
import ru.nacu.todos.App;
import ru.nacu.todos.Constants;

import java.util.Arrays;

/**
 * Основной ContentProvider приложения. все действия должны выполняться из batch (кроме query)
 * (чтобы проходили в транзакциях и выполнялись дополнительные проверки)
 *
 * @author quadro
 * @since 6/21/12 4:59 PM
 */
public final class Data extends ContentProvider {
    public static final boolean DEBUG = false;

    public static final String TAG = "Data";

    public static final String CONTENT_AUTHORITY = Constants.PACKAGE + "db";
    public static final Uri CONTENT_URI_BASE = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final Uri CONTENT_URI_TODO =
            CONTENT_URI_BASE.buildUpon().appendEncodedPath("todo").build();

    private static final int TODO = 10;

    public static final String TODO_CONTENT_TYPE =
            "vnd.android.cursor.dir/vnd.todo";

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;

        matcher.addURI(authority, "todo", TODO);
        matcher.addURI(authority, "todo/*", 2423);
        matcher.addURI(authority, "task", 2342);

        return matcher;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODO:
                return TODO_CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        db = new DataHelper(getContext()).getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (Logs.enabled && DEBUG) {
            Logs.d(TAG, "query() " + uri);
        }

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODO: {
                final Cursor c = db.query(Tables.TODO, projection, selection, selectionArgs, null, null, sortOrder);
                c.setNotificationUri(getContext().getContentResolver(), CONTENT_URI_TODO);
                return c;
            }
        }

        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues v) {
        if (Logs.enabled && DEBUG)
            Logs.d(TAG, "insert " + uri + ": " + v);

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODO: {
                final Uri retVal = ContentUris.withAppendedId(CONTENT_URI_TODO, db.insert(Tables.TODO, null, v));
                n(CONTENT_URI_TODO);
                return retVal;
            }
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String where, String[] params) {
        if (Logs.enabled && DEBUG) {
            Logs.d(TAG, "delete(): " + uri + "; where: " + where + "; params: " + Arrays.toString(params));
        }

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODO: {
                final int delete = db.delete(Tables.TODO, where, params);
                n(CONTENT_URI_TODO);
                return delete;
            }
        }

        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    @Override
    public int update(Uri uri, ContentValues v, String where, String[] params) {
        if (Logs.enabled && DEBUG)
            Logs.d(TAG, "update " + uri + ": " + v + "; " + where + "; params: " + Arrays.toString(params));

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODO: {
                final int update = db.update(Tables.TODO, v, where, params);
                n(CONTENT_URI_TODO);
                return update;
            }

        }

        throw new UnsupportedOperationException();
    }

    public static void n(Uri uri) {
        App.getCtx().getContentResolver().notifyChange(uri, null);
    }
}
