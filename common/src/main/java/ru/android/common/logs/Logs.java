package ru.android.common.logs;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * @author quadro
 */
public final class Logs {
    public static final int MAX_LINES = 2000;

    public static String TAG = "JustReader";
    public static boolean enabled = false;

    public static final List<Logs> logs = new LinkedList<Logs>();

    public static void d(String tag, String msg) {
        d(tag, msg, null);
    }

    public static void d(String tag, String msg, Throwable e) {
        if (enabled) {
            synchronized (Logs.class) {
                logs.add(new Logs(System.currentTimeMillis(), tag, msg, e));
                final String s = tag == null ? TAG : TAG + "/" + tag;

                if (logs.size() > MAX_LINES) {
                    logs.remove(0);
                }

                if (e == null)
                    Log.d(s, msg);
                else
                    Log.d(s, msg, e);
            }
        }
    }

    public final long t;
    public final String tag;
    public final String msg;
    public final Throwable e;

    public Logs(long t, String tag, String msg, Throwable e) {
        this.t = t;
        this.tag = tag;
        this.msg = msg;
        this.e = e;
    }
}
