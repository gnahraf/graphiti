/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.mem;

import com.gnahraf.util.datatypes.Pinterval;

/**
 * A fixed-width table.
 */
public abstract class Table {





    protected Buff data;
    private int size;

    protected Table(int initCapacity) {
        if (initCapacity < 1)
            throw new IllegalArgumentException("initCapacity " + initCapacity);
        this.data = alloc(initCapacity * itemSize());
    }

    protected Table(Buff data, int size) {
        this.data = data;
        this.size = size;
        checkArgs();
    }


    private void checkArgs() {
        if (data.sizeMultipleOf(itemSize()) || size * itemSize() > data.size())
            throw new IllegalArgumentException(
                    "data " + data + "; size " + size + " [" + getClass().getSimpleName() + "]");

    }


    protected final int offset(int index) {
        return index * itemSize();
    }


    /**
     * Returns the number of items (rows) in this table.
     */
    public final int size() {
        return size;
    }


    public final int lastIndex() {
        return size - 1;
    }


    public final boolean isEmpty() {
        return size == 0;
    }



    protected final void copyFrom(Table table, Pinterval indices, int toIndex) {
        // sanity check args
        if (    table.size < indices.hi() || table.itemSize() != itemSize() ||
                toIndex < 0 || toIndex > size)
            throw new IllegalArgumentException(table + "," + indices + "," + toIndex);


        Buff src = table.data.sub(
                        table.offset(indices.lo()),
                        table.offset(indices.hi()));

        int minSize = toIndex + indices.span();
        ensureCapacity(minSize);

        data.put(src, offset(toIndex));

        if (minSize > size)
            setSize(minSize);
    }


    protected void setSize(int size) {
        if (size < 0)
            throw new IllegalArgumentException(String.valueOf(size));

        ensureCapacity(size);
        this.size = size;
    }

    protected final void incrSize() {
        setSize(size + 1);
    }

    protected final void incrSize(int amount) {
        setSize(size + amount);
    }


    public void trimToSize() {
        if (size == capacity())
            return;
        Buff copy = alloc(size * itemSize());
        data.sub(0, size * itemSize()).copyInto(copy, 0);
        data = copy;
    }


    public int overhead() {
        return remaining() * itemSize();
    }


    public int byteSize() {
        return offset(size);
    }


    protected void ensureAvailable() {
        ensureAvailable(1);
    }

    protected void ensureAvailable(int remaining) {
        ensureCapacity(size + remaining);
    }

    protected void ensureCapacity(int capacity) {
        if (capacity <= capacity())
            return;

        int newCapacity = Math.max(capacity, capacity() * 3 / 2);
        Buff buff = alloc(newCapacity * itemSize());
        data.copyInto(buff, 0);
        data = buff;
    }


    protected final int remaining() {
        return capacity() - size;
    }


    protected final int capacity() {
        return data.size() / itemSize();
    }


    protected Buff alloc(int bytes) {
        return new Buff(bytes);
    }


    /**
     * Returns the number of bytes used to represent each item.
     */
    protected abstract int itemSize();


}
