package ru.common.string;

import java.util.Iterator;

/**
 * @author quadro
 * @since 06.08.11 23:27
 */
public final class StringContainerInverseIterator implements Iterator<Character> {
    private final StringContainer container;
    private int currentPart = 0;
    private int currentPartPos = -1;

    public StringContainerInverseIterator(StringContainer container, int startPos) {
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

            if (need < container.lengths[currentPart]) {
                currentPartPos = currentPos < 0 ? need - 1 : need;
                currentPos = pos;
            } else {
                currentPos += container.lengths[currentPart];
                currentPart++;
            }
        }
    }

    public boolean hasNext() {
        if (currentPartPos > 0) {
            currentPartPos--;
            return true;
        }

        do {
            currentPart--;

            if (currentPart == -1) {
                return false;
            }
        } while (container.lengths[currentPart] == 0);

        currentPartPos = container.lengths[currentPart] - 1;
        return true;
    }

    public Character next() {
        return container.value[currentPart][currentPartPos];
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
