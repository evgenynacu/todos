package ru.common.string;

import java.io.*;

/**
 * @author quadro
 * @since 06.08.11 22:41
 */
public final class StringContainer implements Serializable {
    public static final int ENLARGE = 3;

    int partCapacity;

    char[][] value;
    int[] lengths;
    int capacity;

    public StringContainer() {
        this(1000);
    }

    public StringContainer(int partCapacity) {
        this.partCapacity = partCapacity;
        value = new char[0][];
        lengths = new int[0];
        capacity = 0;
    }

    private int ensureSomethingAvailable() {
        final int available = capacity > 0 ? value[capacity - 1].length - lengths[capacity - 1] : 0;
        if (available > 0)
            return available;

        int[] newLengths = new int[capacity + ENLARGE];
        System.arraycopy(lengths, 0, newLengths, 0, capacity);

        char[][] newValue = new char[capacity + ENLARGE][];
        System.arraycopy(value, 0, newValue, 0, capacity);

        for (int i = capacity; i < capacity + ENLARGE; i++) {
            newValue[i] = new char[partCapacity];
        }

        capacity++;
        value = newValue;
        lengths = newLengths;

        return partCapacity;
    }

    public void write(char[] value, int offset, int length) {
        int rest = length;
        int currentOffset = offset;

        while (rest != 0) {
            int available = ensureSomethingAvailable();
            int write = Math.min(available, rest);
            System.arraycopy(value, currentOffset, this.value[capacity - 1], lengths[capacity - 1], write);
            lengths[capacity - 1] += write;
            rest = rest - write;
            currentOffset += write;
        }
    }


    @Override
    public String toString() {
        int length = 0;
        for (int i = 0; i < capacity; i++) {
            length += lengths[i];
        }

        StringBuilder b = new StringBuilder(length);
        for (int i = 0; i < capacity; i++) {
            b.append(value[i], 0, lengths[i]);
        }

        return b.toString();
    }

    public int indexOf(char[] target, int start) {
        return indexOf(this, start, target);
    }

    static int indexOf(StringContainer source, int start, char[] target) {
        if (start < 0) {
            start = 0;
        }

        int matchedPos = 0;
        final StringContainerIterator it = new StringContainerIterator(source, start - 1);

        int cnt = -1;
        while (it.hasNext()) {
            /* Look for first character. */
            final char current = it.next();
            cnt++;

            if (target[matchedPos] == current) {
                matchedPos++;
                if (matchedPos == target.length) {
                    return start + cnt - matchedPos + 1;
                }
            } else {
                matchedPos = 0;
            }
        }
        return -1;
    }

    public StringContainer append(char[] a) {
        write(a, 0, a.length);
        return this;
    }

    public StringContainer append(String s) {
        write(s.toCharArray(), 0, s.length());
        return this;
    }

    public StringContainer append(StringContainer source) {
        char[][] newValue = new char[capacity + source.capacity][];
        int[] newLengths = new int[capacity + source.capacity];

        System.arraycopy(value, 0, newValue, 0, capacity);
        System.arraycopy(lengths, 0, newLengths, 0, capacity);

        System.arraycopy(source.value, 0, newValue, capacity, source.capacity);
        System.arraycopy(source.lengths, 0, newLengths, capacity, source.capacity);

        capacity = capacity + source.capacity;
        value = newValue;
        lengths = newLengths;

        return this;
    }

    public int lastIndexOf(char[] target, int start) {
        return lastIndexOf(this, start, target);
    }

    static int lastIndexOf(StringContainer source, int start, char[] target) {
        int length = source.length();
        if (start > length - 1) {
            start = length - 1;
        }

        int matchedPos = target.length - 1;
        final StringContainerInverseIterator it =
                new StringContainerInverseIterator(source, start);

        int cnt = -1;
        while (it.hasNext()) {
            /* Look for first character. */
            final char current = it.next();
            cnt++;

            if (target[matchedPos] == current) {
                matchedPos--;
                if (matchedPos == 0) {
                    return start - cnt - 1;
                }
            } else {
                matchedPos = target.length - 1;
            }
        }

        return -1;
    }

    private static class ContainerPos {
        private final int part;
        private final int partPos;

        private ContainerPos(int part, int partPos) {
            this.part = part;
            this.partPos = partPos;
        }

        @Override
        public String toString() {
            return part + ":" + partPos;
        }
    }

    private ContainerPos findPos(int pos) {
        if (pos < 0)
            pos = 0;

        int currentPos = 0;
        int currentPart = 0;
        int currentPartPos = 0;

        while (currentPos < pos) {
            final int need = pos - currentPos;

            if (need < lengths[currentPart]) {
                currentPartPos = need;
                currentPos = pos;
            } else {
                currentPos += lengths[currentPart];
                currentPart++;
            }
        }

        return new ContainerPos(currentPart, currentPartPos);
    }

    public int length() {
        int length = 0;
        for (int i = 0; i < capacity; i++) {
            length += lengths[i];
        }

        return length;
    }

    public String substring(int start) {
        return substring(start, length());
    }

    public String substring(int start, int end) {
        StringBuilder b = new StringBuilder(end - start);
        StringContainerIterator i = new StringContainerIterator(this, start - 1);

        int cnt = 0;
        while (i.hasNext() && cnt < (end - start)) {
            b.append(i.next().charValue());
            cnt++;
        }

        return b.toString();
    }

    public void insert(int where, String what) {
        replace(this, where, where, what);
    }

    public void delete(int start, int end) {
        replace(this, start, end, "");
    }

    public void replace(int start, int end, String string) {
        replace(this, start, end, string);
    }

    public static void replace(StringContainer c, int start, int end, String s) {
        int newCapacity;
        char[][] newValue;
        int[] newLengths;

        try {
            char[] string = s.toCharArray();
            final ContainerPos startPos = c.findPos(start);
            final ContainerPos endPos = c.findPos(end - 1);

            boolean startSkipped = startPos.partPos == 0;
            boolean stringSkipped = string.length == 0;
            boolean endSkipped = (c.lengths[endPos.part] - endPos.partPos - 1) == 0;

            newCapacity = c.capacity - (endPos.part - startPos.part) + 2;
            if(startSkipped) {
                newCapacity--;
            }
            if(stringSkipped) {
                newCapacity--;
            }
            if(endSkipped) {
                newCapacity--;
            }

            newValue = new char[newCapacity][];
            newLengths = new int[newCapacity];

            //копирование информации до startPos
            System.arraycopy(c.lengths, 0, newLengths, 0, startPos.part);
            System.arraycopy(c.value, 0, newValue, 0, startPos.part);

            int nextPart = startPos.part;
            if (!startSkipped) {
                //копирование информации в той части, где находится начало
                newValue[startPos.part] = new char[startPos.partPos];
                newLengths[startPos.part] = startPos.partPos;
                System.arraycopy(c.value[startPos.part], 0, newValue[startPos.part], 0, startPos.partPos);
                nextPart++;
            }

            if (!stringSkipped) {
                //вставка нужной строки
                newValue[nextPart] = string;
                newLengths[nextPart] = string.length;
                nextPart++;
            }

            if (!endSkipped) {
                //остатки после вставки
                newValue[nextPart] = new char[c.lengths[endPos.part] - endPos.partPos - 1];
                System.arraycopy(c.value[endPos.part], endPos.partPos + 1, newValue[nextPart], 0, c.lengths[endPos.part] - endPos.partPos - 1);
                newLengths[nextPart] = c.lengths[endPos.part] - endPos.partPos - 1;
                nextPart++;
            }

            //остатки без изменения
            System.arraycopy(c.value, endPos.part + 1, newValue, nextPart, c.capacity - endPos.part - 1);
            System.arraycopy(c.lengths, endPos.part + 1, newLengths, nextPart, c.capacity - endPos.part - 1);
        } catch (RuntimeException e) {
            throw new RuntimeException(Base64.byteArrayToBase64(c.serialize()) + "\n" + start + "\n" + end + "\n" + s, e);
        }

        c.value = newValue;
        c.lengths = newLengths;
        c.capacity = newCapacity;
    }

    @SuppressWarnings("UnusedDeclaration")
    public byte[] serialize() {
        try {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(buf);
            out.writeObject(this);
            return buf.toByteArray();
        } catch (Exception e) {
            //ignore
            return new byte[0];
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public static StringContainer deserialize(byte[] buf) {
        try {
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buf));
            return (StringContainer) in.readObject();
        } catch (Exception e) {
            return null;
        }
    }
}
