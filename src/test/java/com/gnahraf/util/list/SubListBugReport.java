/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.list;


import org.junit.Test;


import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by babak on 4/29/15.
 */
public class SubListBugReport {

    DecimalFormat formatter = new DecimalFormat("#,###");


    @Test
    public void worksForSmall() {
        assertSubListTraversal(500);
    }


    /**
     * As of java version 10 this is fixed. Used to fail. Yay
     */
    @Test
    public void showIt() {
        assertSubListTraversal(500000);
    }


    void assertSubListTraversal(int count) {
        Integer[] array = new Integer[count];
        for (int i = 0; i < count; ++i)
            array[i] = i;
        assertSubListTraversal(Arrays.asList(array));
    }


    void assertSubListTraversal(List<?> list) {
        int i = 1;
        StackOverflowError error = null;
        long tick = System.currentTimeMillis();
        try {
            List<?> sub = list;
            for (; i < list.size(); ) {
                sub = sub.subList(1, sub.size());
                assertEquals(list.get(i++), sub.get(0));
            }
            assertEquals(1, sub.size());
        } catch (StackOverflowError soe) {
            error = soe;
        }

        long lap = System.currentTimeMillis() - tick;
        System.out.println(label(list) + " " + formatter.format(lap) + " ms");
        if (error != null) {
            fail(label(list) + ": error on " + formatter.format(i) + "th sub-list - " + error);
        }
    }

    private String label(List<?> list) {
        return "[" + list.getClass().getName() + ", size:" + formatter.format(list.size()) + "]";
    }
}
