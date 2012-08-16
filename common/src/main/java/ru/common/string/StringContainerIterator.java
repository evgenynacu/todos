package ru.common.string;

import java.util.Iterator;

/**
 * @author quadro
 * @since 06.08.11 23:27
 */
public final class StringContainerIterator implements Iterator<Character> {
    private final StringContainer container;
    private int currentPart = 0;
    private int currentPartPos = -1;

    public StringContainerIterator(StringContainer container, int startPos) {
        this.container = container;
        moveTo(startPos);
    }

    public void moveTo(int pos) {
        if (pos < -1)
            pos = -1;

        int currentPos = -1;
        currentPart = 0;

        while (currentPos < pos) {
            final int need = pos - currentPos;

            if(currentPart >= container.lengths.length) {
                throw new RuntimeException("Error while moveTo. can't find pos: " + container + "; start=" + pos);
            }

            if (need < container.lengths[currentPart]) {
                currentPartPos = need - 1;
                currentPos = pos;
            } else {
                currentPos += container.lengths[currentPart];
                currentPart++;
            }
        }
    }

    public boolean hasNext() {
        if (currentPart > container.capacity - 1) {
            return false;
        }

        if (currentPartPos < container.lengths[currentPart] - 1) {
            currentPartPos++;
            return true;
        }

        do {
            currentPart++;

            if (currentPart >= container.capacity) {
                return false;
            }
        } while (container.lengths[currentPart] == 0);

        currentPartPos = 0;
        return true;
    }

    public Character next() {
        return container.value[currentPart][currentPartPos];
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
