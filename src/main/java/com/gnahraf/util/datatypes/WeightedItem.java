/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.datatypes;

/**
 * Created by babak on 6/26/15.
 */
public class WeightedItem<T> extends EquiDelegate {

    private final static int HASH_BASE = WeightedItem.class.hashCode();




    private final T item;
    private int weight;



    public WeightedItem(T item) {
        this.item = item;
        if (item == null)
            throw new IllegalArgumentException("null item");
    }

    public WeightedItem(T item, int weight) {
        this(item);
        setWeight(weight);
    }

    public final T getItem() {
        return item;
    }

    public final int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        if (weight < 0)
            throw new IllegalArgumentException("negative weight: " + weight);
        this.weight = weight;
    }

    public int incrWeight(int amount) {
        setWeight(weight + amount);
        return weight;
    }


    @Override
    protected final Object equalityDelegate() {
        return item;
    }

    @Override
    public final int hashCode() {
        return HASH_BASE ^ super.hashCode();
    }
}
