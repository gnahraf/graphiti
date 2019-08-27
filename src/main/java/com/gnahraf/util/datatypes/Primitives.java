/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.datatypes;

/**
 * Utility for numeric primitives.
 */
public class Primitives {

    private Primitives() { }

    public final static int MAX_USHORT = 0xffff;
    public final static int MAX_USHORT_EXC = MAX_USHORT + 1;


    /**
     * Tryte mask.
     */
    public final static int MAX_TRYTE = 0xffffff;

    /**
     * Bit width of a tryte.
     */
    public final static int TRYTE_WIDTH = 24;


    public static class Sizeof {
        private Sizeof() { }
        public final static int BYTE = 1;
        public final static int SHORT = 2;
        public final static int TRYTE = 3;
        public final static int INT = 4;
        public final static int LONG = 8;
    }

    public static byte signedByte(int value) {
        byte out = (byte) value;
        if (out != value)
            throw new ArithmeticException(overflowMessage(value));
        return out;
    }


    public static byte unsignedByte(int value) {
        int out = value & 0xff;
        if (out != value)
            throw new ArithmeticException(overflowMessage(value));
        return (byte) out;
    }


    public static short signedShort(int value) {
        short out = (short) value;
        if (out != value)
            throw new ArithmeticException(overflowMessage(value));
        return out;
    }


    public static short unsignedShort(int value) {
        int out = value & 0xffff;
        if (out != value)
            throw new ArithmeticException(overflowMessage(value));
        return (short) out;
    }




    public static int unsign(byte unsigned) {
        return 0xff & unsigned;
    }

    public static int unsign(short unsigned) {
        return 0xffff & unsigned;
    }


    /**
     * Returns the value encoded in the low nyble of the given byte.
     * A byte contains 2 nybles.
     */
    public static byte loNyble(byte value) {
        value &= 0xf;
        return value;
    }


    /**
     * Returns the value encoded in the high nyble of the given byte.
     * A byte contains 2 nybles.
     */
    public static byte hiNyble(byte value) {
        value &= 0xf0;
        value >>>= 4;
        return value;
    }


    public static byte setLoNyble(byte target, int value) {
        value &= 0xf;   // sanity
        target &= 0xf0;
        target |= value;
        return target;
    }


    public static byte setHiNyble(byte target, int value) {
        value &= 0xf;   // sanity
        target &= 0xf;
        target |= (value << 4);
        return target;
    }




    public static byte stitchNybles(byte left, byte right) {
        // expanded: return (byte) ( (loNyble(left) << 4) | hiNyble(right) );
        return (byte) ( ((0xf & left) << 4) | ((0xf0 & right) >>> 4)  );
    }


//    public static byte stitchOutNybles(byte left, byte right) {
//        // expanded: return (byte) ( (hiNyble(left) << 4) | loNyble(right) );
//        return (byte) ( (0xf0 & left) | (0xf & right) );
//    }


    public static void writeNyble(byte[] b, int nibIndex, int value) {
        int index = nibIndex / 2;
        if (nibIndex % 2 == 0)
            b[index] = setHiNyble(b[index], value);
        else
            b[index] = setLoNyble(b[index], value);
    }


    public static byte readNyble(byte[] b, int nibIndex) {
        byte nybleRegion = b[nibIndex / 2];
        return nibIndex % 2 == 0 ? hiNyble(nybleRegion) : loNyble(nybleRegion);
    }


    public static void writeShort(byte[] b, int index, short value) {
        byte lo = (byte) (value & 0xff);
        byte hi = (byte) (value >>> 8);
        b[index + 1] = lo;
        b[index] = hi;
    }


//    /**
//     * A nybled short is 3 nybles (1.5 bytes) wide and is unsigned.
//     */
//    public static void writeNybledShort(byte[] b, int nibIndex, short value) {
//        if (value > 0xfff || value < 0)
//            throw new ArithmeticException(overflowMessage(value));
//
//        int index = nibIndex / 2;
//        if (nibIndex % 2 == 0) {
//            int nyble = value & 0xf;
//            b[index++] = (byte) (value >>> 4);
//            b[index] = setHiNyble(b[index], nyble);
//        } else {
//            int nyble = (value & 0xf00) >>> 8;
//
//        }
//    }


    public static short readShort(byte[] b, int index) {
        int hi = (b[index] & 0xff) << 8;
        int lo = b[index + 1] & 0xff;
        return (short) (hi | lo);
    }


    public static void writeTryte(byte[] b, int index, int value) {
        int orig = value;
        byte b0 = (byte) (value & 0xff);
        value >>>= 8;
        byte b1 = (byte) (value & 0xff);
        value >>>= 8;
        byte b2 = (byte) (value & 0xff);
        if (value > 0xff)
            throw new ArithmeticException(overflowMessage(orig));
        b[index + 2] = b0;
        b[index + 1] = b1;
        b[index] = b2;
    }





    public static int readTryte(byte[] b, int index) {
        byte b2 = b[index];
        byte b1 = b[index + 1];
        byte b0 = b[index + 2];
        int value = b2 & 0xff;
        value <<= 8;
        value |= (b1 & 0xff);
        return value << 8 | (b0 & 0xff);
    }


    public static void writeInt(byte[] b, int index, int value) {
        byte b0 = (byte) (value & 0xff);
        value >>>= 8;
        byte b1 = (byte) (value & 0xff);
        value >>>= 8;
        byte b2 = (byte) (value & 0xff);
        byte b3 = (byte) (value >>> 8);
        b[index + 3] = b0;
        b[index + 2] = b1;
        b[index + 1] = b2;
        b[index] = b3;
    }


    public static int readInt(byte[] b, int index) {
        byte b3 = b[index];
        byte b2 = b[index + 1];
        byte b1 = b[index + 2];
        byte b0 = b[index + 3];
        int value = b3 & 0xff;
        value <<= 8;
        value |= (b2 & 0xff);
        value <<= 8;
        value |= (b1 & 0xff);
        return value << 8 | (b0 & 0xff);
    }





    public static long toLong(double value) {
        if (value < Long.MIN_VALUE || value > Long.MAX_VALUE)
            throw new ArithmeticException("on conversion to long: " + value);
        return (long) value;
    }


    public static int toInt(long value) {
        int val = (int) value;
        if (val != value)
            throw new ArithmeticException(overflowMessage(value));
        return val;
    }


    public static int toTryte(long value) {
        if ((0xffffff & value) != value)
            throw new ArithmeticException(overflowMessage(value));
        return (int) value;
    }








    private static String overflowMessage(long value) {
        return "overflow: 0x" + Long.toHexString(value) + " (" + value + ")";
    }

}
