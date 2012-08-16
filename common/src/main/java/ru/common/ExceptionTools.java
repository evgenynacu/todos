package ru.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * @author quadro
 * @since 15.12.2010 22:13:54
 */
public final class ExceptionTools {
    public static boolean isNetworkError(Throwable t) {
        return t instanceof IOException || t.toString().contains("ExpatParser$ParseException");
    }

    public static String getStacktrace(Throwable e) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(out));
        return out.toString();
    }
}
