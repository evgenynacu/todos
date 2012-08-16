package ru.nacu.todos.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import ru.common.StreamTools;

import java.io.InputStream;

/**
 * @author quadro
 * @since 6/21/12 4:57 PM
 */
public final class DataHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "data";
    public static final int DATABASE_VERSION = 19;

    private void executeScript(SQLiteDatabase db, String name, boolean stopOnError) {
        InputStream create = getClass().getClassLoader().getResourceAsStream(name + ".sql");
        try {
            String s = StreamTools.readToString(create);
            String[] execute = s.split(";");

            for (String line : execute) {
                if (line.trim().length() != 0) {
                    try {
                        db.execSQL(line);
                    } catch (Exception e) {
                        if (stopOnError) {
                            throw e;
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            StreamTools.close(create);
        }
    }

    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        executeScript(sqLiteDatabase, "create", true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        for (int i = oldVer + 1; i <= newVer; i++) {
            executeScript(db, "update" + i, true);
        }
    }
}
