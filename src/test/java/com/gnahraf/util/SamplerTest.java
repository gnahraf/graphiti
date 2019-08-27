/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util;


import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by babak on 6/27/15.
 */
public class SamplerTest {

    @Test
    public void testMinimal() {
        testMinimalImpl(91, 11);
    }


    @Test
    public void testMinimal2() {
        testMinimalImpl(11, 91);
    }


    private void testMinimalImpl(Object a, Object b) {
        assertNotEquals(a, b);

        Sampler<Object> sampler = new Sampler<>();
        sampler.setWeight(a, 3);
        sampler.setWeight(b, 1);

        sampler.prepare(0);

        assertEquals(4, sampler.totalWeight());

        int aCount = 0;
        int bCount = 0;

        for (int i = 1000000; i-- > 0; ) {
            Object o = sampler.next();
            if (o == a)
                ++aCount;
            else if (o == b)
                ++bCount;
            else
                fail();
        }

        double aPercent, bPercent;
        {
            aPercent = aCount;
            bPercent = bCount;

            int total = aCount + bCount;

            aPercent /= total;
            bPercent /= total;
        }

        double aWeight, bWeight;
        {
            aWeight = sampler.getWeight(a);
            bWeight = sampler.getWeight(b);
            double totalWeight = aWeight + bWeight;
            aWeight /= totalWeight;
            bWeight /= totalWeight;
        }

//        System.out.println("a: " + aWeight + ", " + aPercent);
//        System.out.println("b: " + bWeight + ", " + bPercent);

        assertEquals(aWeight, aPercent, 0.01);
        assertEquals(bWeight, bPercent, 0.01);
    }


}
