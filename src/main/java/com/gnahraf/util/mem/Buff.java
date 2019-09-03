/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.mem;


import java.nio.ByteBuffer;

import com.gnahraf.util.datatypes.Primitives;
import com.gnahraf.util.datatypes.Primitives.Sizeof;

/**
 * An immutable boundary of memory. Used for slicing and dicing byte arrays.
 * <p/>
 * A reminder to myself why I'm not using an nio.ByteBuffer instead of
 * this class: ByteBuffers can be stateful even on <em>read</em> access.
 * I want something simpler and thread-safe on read access. 
 */
public class Buff implements Comparable<Buff> {

    public final static Buff EMPTY = new Buff(new byte[0]) {
        @Override
        String memToString() { return "EMPTY"; }
    };


    private final byte[] mem;
    private final int offset;
    private final int size;


    public Buff(int size) {
        this(new byte[size]);
    }

    public Buff(byte[] mem) {
        this(mem, 0, mem.length, null);
    }

    public Buff(byte[] mem, int offset, int size) {
        this(mem, offset, size, null);

        if (offset < 0 || size < 0 || offset + size > mem.length)
            throw new IllegalArgumentException(
                "offset (" + offset + "); size (" + size + "); bytes (" + mem.length + ")");

    }


    private Buff(byte[] mem, int offset, int size, Object ignored) {
        this.mem = mem;
        this.offset = offset;
        this.size = size;
    }


    private Buff newBuff(byte[] mem, int offset, int size) {
        return new Buff(mem, offset, size, null);
    }




    public boolean sizeMultipleOf(int width) {
//        return size != 0 && size % width == 0;
        return size % width == 0;
    }


    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size() == 0;
    }


    public byte get(int index) {
        checkIndexBounds(index);
        return getUnchecked(index);
    }


    public ByteBuffer asByteBuffer() {
        return ByteBuffer.wrap(mem, offset, size).asReadOnlyBuffer();
    }

    public void put(ByteBuffer in, int index) {
        checkBulkPutParams(in.remaining(), index);
        in.get(mem, index + offset, in.remaining());
    }


    public void put(Buff in, int index) {
        checkBulkPutParams(in.size, index);
        putImpl(in.mem, in.offset, in.size, index);
    }


    public void put(byte[] in, int index) {
        checkBulkPutParams(in.length, index);
        putImpl(in, 0, in.length, index);
    }


    public void put(byte value, int index) {
        checkIndexBounds(index);
        mem[offset + index] = value;
    }




    public void putShort(short value, int index) {
        checkShortIndexBounds(index);
        Primitives.writeShort(mem, offset + index, value);
    }


    public short getShort(int index) {
        checkShortIndexBounds(index);
        return Primitives.readShort(mem, offset + index);
    }



    public void putTryte(int value, int index) {
        checkIndexBounds(index, Sizeof.TRYTE);
        Primitives.writeTryte(mem, offset + index, value);
    }


    public int getTryte(int index) {
        checkIndexBounds(index, Sizeof.TRYTE);
        return Primitives.readTryte(mem, offset + index);
    }




    public int getInt(int index) {
        checkIntIndexBounds(index);
        return Primitives.readInt(mem, offset + index);
    }


    public void putInt(int value, int index) {
        checkIntIndexBounds(index);
        Primitives.writeInt(mem, offset + index, value);
    }




    private void putImpl(byte[] in, int srcOff, int srcSize, int index) {
        int dtnOff = offset + index;
        int srcEnd = srcOff + srcSize;
        while (srcOff < srcEnd)
            mem[dtnOff++] = in[srcOff++];
    }




    public void copyInto(ByteBuffer out) {
        if (out.remaining() < size)
            throw new IllegalArgumentException(
                "out.remaining " + out.remaining() + "; size " + size);
        out.put(mem, offset, size);
    }


    public void copyInto(Buff out, int outIndex) {
        checkBulkOutParams(out.size, outIndex);
        copyIntoImpl(out.mem, out.offset + outIndex);
    }


    public void copyInto(byte[] out, int outIndex) {
        checkBulkOutParams(out.length, outIndex);
        copyIntoImpl(out, outIndex);
    }




    private void copyIntoImpl(byte[] out, int outIndex) {
        int srcOff = offset;
        int srcEnd = offset + size;
        while (srcOff < srcEnd)
            out[outIndex++] = mem[srcOff++];
    }

    private void checkBulkOutParams(int outSize, int outIndex) {
        if (outIndex < 0 || outSize - outIndex < size)
            throw new IllegalArgumentException(
                "outIndex " + outIndex + "; out (size) " + outSize + "; size " + size);
    }

    private void checkBulkPutParams(int inSize, int index) {
        if (index < 0 || index + inSize > size)
            throw new IllegalArgumentException(
                "index " + index + "; in (size) " + inSize + "; size " + size);

    }

    private void checkIndexBounds(int index) {
        checkIndexBounds(index, Sizeof.BYTE);
    }

    private void checkShortIndexBounds(int index) {
        checkIndexBounds(index, Sizeof.SHORT);
    }

    private void checkIntIndexBounds(int index) {
        checkIndexBounds(index, Sizeof.INT);
    }


    private void checkIndexBounds(int index, int count) {
        if (index < 0 || index + count > size)
            throw new IndexOutOfBoundsException(String.valueOf(index));
    }



    private byte getUnchecked(int index) {
        return mem[index + offset];
    }



    public Buff slice(int index, int size) {
        return sub(index, index + size);
    }


    public Buff sub(int startIndex, int endIndex) {
        if (startIndex < 0 || endIndex < startIndex || endIndex > size)
            throw new IllegalArgumentException(
                "startIndex, endIndex, size: " + startIndex + ", " + endIndex + ", " + size);
        int subSize = endIndex - startIndex;
        if (subSize == 0)
            return EMPTY;
        else if (subSize == size)
            return this;
        else
            return newBuff(mem, startIndex + offset, subSize);
    }



    public Buff sub(int startIndex) {
        return sub(startIndex, size);
    }



    @Override
    public int compareTo(Buff another) {
        if (this == another)
            return 0;

        int minSize = Math.min(this.size, another.size);
        for (int i = 0; i < minSize; ++i) {
            int diff = ((int) getUnchecked(i)) - another.getUnchecked(i);
            if (diff != 0)
                return diff;
        }

        if (this.size == another.size)
            return 0;
        else if (this.size == minSize)
            return -1;
        else
            return 1;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (o instanceof Buff) {
            Buff other = (Buff) o;
            return other.size == size && 0 == compareTo(other);
        } else
            return false;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        for (int index = Math.min(128, size); index-- > 0; ) {
            int b = getUnchecked(index);
            b <<= (index % 4);
            hash ^= b;
        }
        return hash;
    }



    public String toString() {
        return "[" + memToString() + "(" + offset + "," + size + ")]";
    }


    String memToString() {
        return mem + ":" + mem.length;
    }


    public Buff[] chunk(int chunkSize) {
        if (chunkSize <= 0 || size % chunkSize != 0)
            throw new IllegalArgumentException(
                    "chunkSize (" + chunkSize + ") % size (" + size + ") != 0");

        int count = size / chunkSize;
        Buff[] chunks = new Buff[count];

        for (int index = 0, off = offset; index < count; ++index, off += chunkSize)
            chunks[index] = newBuff(mem, off, chunkSize);

        return chunks;
    }
}
