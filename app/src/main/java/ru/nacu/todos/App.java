package ru.nacu.todos;

import android.app.Application;
import android.content.Context;

/**
 * @author quadro
 * @since 8/16/12 10:32 PM
 */
public final class App extends Application {
    private static Context ctx;

    public static Context getCtx() {
        return ctx;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.ctx = this.getApplicationContext();
    }
}
