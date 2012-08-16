package ru.common.string;

import java.io.Serializable;
import java.io.Writer;

/**
 * {@link java.io.Writer} implementation that outputs to a {@link StringBuilder}.
 * <p/>
 * <strong>NOTE:</strong> This implementation, as an alternative to
 * <code>java.io.StringWriter</code>, provides an <i>un-synchronized</i>
 * (i.e. for use in a single thread) implementation for better performance.
 * For safe usage with multiple {@link Thread}s then
 * <code>java.io.StringWriter</code> should be used.
 *
 * @version $Revision: 1003647 $ $Date: 2010-10-01 21:53:59 +0100 (Fri, 01 Oct 2010) $
 * @since Commons IO 2.0
 */
public final class StringContainerWriter extends Writer implements Serializable {

    private final StringContainer builder;

    /**
     * Construct a new {@link StringBuilder} instance with default capacity.
     */
    public StringContainerWriter() {
        this.builder = new StringContainer(1000);
    }

    /**
     * Append a single character to this Writer.
     *
     * @param value The character to append
     * @return This writer instance
     */
    @Override
    public Writer append(char value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Append a character sequence to this Writer.
     *
     * @param value The character to append
     * @return This writer instance
     */
    @Override
    public Writer append(CharSequence value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Append a portion of a character sequence to the {@link StringBuilder}.
     *
     * @param value The character to append
     * @param start The index of the first character
     * @param end   The index of the last character + 1
     * @return This writer instance
     */
    @Override
    public Writer append(CharSequence value, int start, int end) {
        throw new UnsupportedOperationException();
    }

    /**
     * Closing this writer has no effect.
     */
    @Override
    public void close() {
    }

    /**
     * Flushing this writer has no effect.
     */
    @Override
    public void flush() {
    }


    /**
     * Write a String to the {@link StringBuilder}.
     *
     * @param value The value to write
     */
    @Override
    public void write(String value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Write a portion of a character array to the {@link StringBuilder}.
     *
     * @param value  The value to write
     * @param offset The index of the first character
     * @param length The number of characters to write
     */
    @Override
    public void write(char[] value, int offset, int length) {
        if (value != null) {
            builder.write(value, offset, length);
        }
    }

    /**
     * Return the underlying builder.
     *
     * @return The underlying builder
     */
    public StringContainer getBuilder() {
        return builder;
    }

    /**
     * Returns {@link StringBuilder#toString()}.
     *
     * @return The contents of the String builder.
     */
    @Override
    public String toString() {
        return builder.toString();
    }
}