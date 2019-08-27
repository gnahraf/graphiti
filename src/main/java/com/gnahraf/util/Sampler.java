/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util;

import com.gnahraf.util.datatypes.WeightedItem;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 * Discrete, weighted distribution sampler.
 */
public class Sampler<T> implements Iterator<T> {


    private final Map<T, WeightedItem<T>> universe = new HashMap<>();


    private int[] cumulativeWeights;
    private Object[] items;
    private int distributionSize;

    private Random random;

    /**
     * Increments the weight of the given item by one and returns the new weight.
     */
    public int incrWeight(T item) {
        return incrWeight(item, 1);
    }

    /**
     * Increments the weight of the given <tt>item</tt> by <tt>delta</tt> and returns
     * the new weight. If an item's weight drops to zero, it counts for naught and is
     * silently dropped from the distribution.
     */
    public int incrWeight(T item, int delta) {
        WeightedItem<T> w = universe.get(item);
        if (w == null) {
            if (delta == 0)
                return 0;
            w = new WeightedItem<T>(item, delta);
            universe.put(item, w);
        } else if (w.incrWeight(delta) == 0) {
            universe.remove(item);
        }
        return w.getWeight();
    }


    public int decrWeight(T item, int delta) {
        return incrWeight(item, -delta);
    }


    public int getWeight(T item) {
        WeightedItem<T> w = universe.get(item);
        return w == null ? 0 : w.getWeight();
    }


    public void setWeight(T item, int weight) {
        if (weight == 0)
            universe.remove(item);
        WeightedItem<T> w = universe.get(item);
        if (w == null) {
            w = new WeightedItem<T>(item, weight);
            universe.put(item, w);
        } else {
            w.setWeight(weight);
        }
    }


    public Collection<WeightedItem<T>> weightedItems() {
        return universe.values();
    }


    public WeightedItem<T> getWeightedItem(T item) {
        return universe.get(item);
    }



    public WeightedItem<T> newWeightedItem(T item, int weight) {
        WeightedItem<T> w = universe.get(item);
        if (w != null)
            throw new IllegalStateException("already exists: " + w);
        w = new WeightedItem<T>(item, weight);
        universe.put(item, w);
        return w;
    }


    public int getDistributionSize() {
        return distributionSize;
    }





    /**
     * Prepares the weighted distribution. Invoke this in order to sync the
     * distribution with the actual weights.
     */
    public void prepareDistribution() {
        int size = universe.size();
        if (size < 2)
            throw new IllegalStateException("minimum 2 items required; actual " + size);

        // mask the member fields until the very end..
        Object[] items = new Object[size];
        int[] cumulativeWeights = new int[size];

        int index = 0;
        {
            int cw = 0;
            for (WeightedItem<?> weightedItem : universe.values()) {
                int weight = weightedItem.getWeight();
                if (weight == 0)
                    continue;
                items[index] = weightedItem.getItem();
                cw += weight;
                cumulativeWeights[index] = cw;
                ++index;
            }
            if (index < 2)
                throw new IllegalStateException(
                        "at least 2 items with weight required; found " + index);
        }

        int distributionSize = index;

        if (size - index >= size / 2) {
            Object[] itemCopy = new Object[index];
            int[] cumCopy = new int[index];
            while (index-- > 0) {
                itemCopy[index] = items[index];
                cumCopy[index] = cumulativeWeights[index];
            }
            items = itemCopy;
            cumulativeWeights = cumCopy;
        }

        this.items = items;
        this.cumulativeWeights = cumulativeWeights;
        this.distributionSize = distributionSize;
    }


    public void prepare() {
        prepare(System.currentTimeMillis());
    }


    public void prepare(long randomSeed) {
        prepareDistribution();
        setSeed(randomSeed);
    }


    public void setSeed(long seed) {
        if (random == null)
            random = new Random(seed);
        else
            random.setSeed(seed);
    }


    public boolean isPrepared() {
        return random != null && distributionSize != 0;
    }


    public int totalWeight() {
        if (!isPrepared())
            throw new IllegalStateException("not prepared");
        return cumulativeWeights[distributionSize - 1];
    }


    /**
     * There's always a next.
     */
    public boolean hasNext() {
        return true;
    }

    /**
     * Returns the next item, randomly distributed by [relative] weight.
     */
	@SuppressWarnings("unchecked")
	@Override
    public T next() {
        int rand = random.nextInt(totalWeight()) + 1;
        int randIndex = Arrays.binarySearch(cumulativeWeights, 0, distributionSize, rand);
        if (randIndex < 0) {
            randIndex = -randIndex - 1;
            assert randIndex < distributionSize;
        }
        return (T) items[randIndex];
    }

    /**
     * Not supported.
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
