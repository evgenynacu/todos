package ru.common;

import org.apache.http.HttpEntity;
import ru.common.string.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author quadro
 * @since 19.02.11 10:54
 */
public final class StreamTools {
    @SuppressWarnings("UnusedDeclaration")
    public static void close(Closeable closeable) {
        if (closeable == null) return;

        try {
            closeable.close();
        } catch (Exception e) {
            //ok
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public static void close(Socket socket) {
        if (socket == null) return;

        try {
            socket.close();
        } catch (Exception e) {
            //ok
        }
    }

    public static void close(ServerSocket socket) {
        if (socket == null) return;

        try {
            socket.close();
        } catch (Exception e) {
            //ok
        }
    }

    public static void writeFromString(String s, OutputStream out) throws IOException {
        final StringReader reader = new StringReader(s);
        final OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");

        try {
            copy(reader, writer);
            writer.flush();
        } finally {
            close(reader);
        }
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        copy(in, out, null);
    }

    public static void copy(InputStream in, OutputStream out, ProgressPublisher progressPublisher) throws IOException {
        byte[] buf = new byte[5000];

        int read;
        int totalRead = 0;
        while ((read = in.read(buf)) != -1) {
            totalRead += read;
            out.write(buf, 0, read);
            if (progressPublisher != null) {
                boolean b = progressPublisher.publish(totalRead);
                if (!b) {
                    break;
                }
            }
        }
    }

    public static void copy(Reader in, Writer out) throws IOException {
        char[] buf = new char[5000];

        int read;
        while ((read = in.read(buf)) != -1) {
            out.write(buf, 0, read);
        }
    }

    public static void copy(Reader in, Writer... out) throws IOException {
        char[] buf = new char[5000];

        int read;
        while ((read = in.read(buf)) != -1) {
            for (Writer writer : out) {
                writer.write(buf, 0, read);
            }
        }
    }

    public static void write(StringBuilder what, OutputStream to) throws IOException {
        OutputStreamWriter out = new OutputStreamWriter(to, "UTF-8");
        StringBuilderReader in = new StringBuilderReader(what);

        try {
            copy(in, out);
            out.flush();
        } finally {
            close(in);
        }
    }

    public static void write(StringContainer what, OutputStream to) throws IOException {
        write(what, to, true);
    }

    public static void write(StringContainer what, OutputStream to, boolean flush) throws IOException {
        OutputStreamWriter out = new OutputStreamWriter(to, "UTF-8");
        StringContainerIterator i = new StringContainerIterator(what, -1);

        while (i.hasNext()) {
            out.write(i.next().charValue());
        }

        if (flush) {
            out.flush();
        }
    }

    public static String readToString(InputStream in) throws IOException {
        return readToString(in, "UTF-8");
    }

    public static String readToString(InputStream in, String encoding) throws IOException {
        return readToBuilder(in, encoding).toString();
    }

    public static StringContainer readToContainer(InputStream in) throws IOException {
        return readToContainer(in, "UTF-8");
    }

    public static StringContainer readToContainer(InputStream in, String encoding) throws IOException {
        StringContainerWriter writer = new StringContainerWriter();
        Reader reader = new InputStreamReader(in, encoding);

        try {
            copy(reader, writer);
            return writer.getBuilder();
        } finally {
            close(reader);
            close(writer);
        }
    }

    public static StringBuilder readToBuilder(InputStream in, String encoding) throws IOException {
        StringBuilderWriter writer = new StringBuilderWriter();
        Reader reader = new InputStreamReader(in, encoding);

        try {
            copy(reader, writer);
            return writer.getBuilder();
        } finally {
            close(reader);
            close(writer);
        }
    }

    public static void consume(HttpEntity entity) {
        try {
            entity.consumeContent();
        } catch (IOException e) {
            //ignore
        }
    }

    public static interface ProgressPublisher {
        /**
         * Публикует, сколько загружено информации
         *
         * @param copied размер скопированного файла
         * @return true, если все ок. false, если нужно завершить выполнение
         */
        boolean publish(int copied);
    }
}
