/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.util.list;

import com.gnahraf.util.Comparators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A union view of 2 sorted lists. The underlying lists should not be structurally modified
 * when under this view; results are definitely undefined otherwise. This may be useful when
 * <ul>
 *     <li>The caller will likely not inspect the entire list, in particular, its end.</li>
 *     <li>The lists are large, and a copy/merge is memory inefficient.</li>
 *     <li>When one list is a lot smaller relative to the other.</li>
 * </ul>
 * <p/>
 * Note if the lists contain duplicates, then order of those duplicates will be interleaved.
 * The exact rule for this interleaving is unspecified.
 *
 * <h3>Implementation</h3>
 *
 * The lazy merge is controlled by a pivots array. The elements of this array (list)
 * determine the internal index and list used to retrieve an item at a particular
 * external facing index.
 *
 * <h4>Structure of Pivots</h4>
 *
 * Each element of the pivots array encodes<ol>
 *     <li>the first external index to which the pivot applies (called <tt>count</tt>)</li>
 *     <li>the internal list to which this pivot applies to, and</li>
 *     <li>the first index into the internal list that the pivot applies to.</li>
 * </ol>
 *
 <pre>{@literal

                                (sub)
                count           index
         +----------------+----------------+
         |     size()     |                |  <------ sentinel entry marking that pivot array
         +----------------+----------------+          is complete
         .                .                .
         .                .                .  <------ the pivots are calculated lazily
         .                .                .
         +----------------+----------------+
         |       C3       |       B2       |
         +----------------+----------------+
         |       C2       |       A1       |  <------ list a becomes the pivot list at index == C2
         +----------------+----------------+
         |       C1       |     B0 (0)     |  <------ list b becomes the pivot list at index == C1
         +----------------+----------------+
         |        0       |     A0 (0)     |  <------ sentinel entry for list a
         +----------------+----------------+

         [zeroth (first) location depicted at bottom]
 }</pre>
 * <p/>
 * The 2 fields of a pivot, <tt>count</tt> and <tt>index</tt>, each one
 * <tt>int</tt> wide, are encoded as a <tt>Long</tt>, while the position of
 * the pivot in the pivot-list, determines which internal list the pivot
 * applies to.
 */
public class SortedListUnion<T> extends BaseList<T> {


    /**
     * Convenience, conditional pseudo constructor.
     *
     * @param a a non-null, possibly empty sorted list
     * @param b a non-null, possible empty sorted list
     * @param order the non-null <tt>Comparator</tt> defining how the lists are sorted
     *
     * @return a <tt>SortedListUnion</tt> instance if neither <tt>a</tt> nor <tt>b</tt> is empty;
     *         <tt>b</tt>, if <tt>a</tt> is empty; <tt>a</tt> if <tt>b</tt> is empty.
     */
    public static <T> List<T> asUnion(List<T> a, List<T> b, Comparator<? super T> order) {

        if (a.isEmpty())
            return b;
        else if (b.isEmpty())
            return a;

        return new SortedListUnion<>(a, b, order);
    }

    /**
     * Convenience, conditional pseudo constructor.
     *
     * @param a a non-null, possibly empty, sorted list of mutually <tt>Comparable</tt> objects
     * @param b a non-null, possible empty, sorted list of mutually <tt>Comparable</tt> objects
     *
     * @return a <tt>SortedListUnion</tt> instance if neither <tt>a</tt> nor <tt>b</tt> is empty;
     *         <tt>b</tt>, if <tt>a</tt> is empty; <tt>a</tt> if <tt>b</tt> is empty.
     */
    public static <U extends Comparable<U>> List<U> asUnion(List<U> a, List<U> b)
            throws NullPointerException {

        if (a.isEmpty())
            return b;
        else if (b.isEmpty())
            return a;

        Comparator<U> nat = Comparators.naturalComparator();
        return new SortedListUnion<>(a, b, nat);
    }



    private final ArrayList<Long> pivots = new ArrayList<>();

    private final List<T> a;
    private final List<T> b;

    private final Comparator<? super T> order;


    /**
     * Constructs a lazily loaded instance. Arguments are not validated against the given
     * <tt>order</tt>ing. However, other rudimentary checks (non-null, non-empty, etc.) are
     * performed.
     *
     * @param a non-empty, sorted list
     * @param b non-empty, sorted list
     * @param order non-null comparator
     *
     * @see #asUnion(List, List, Comparator)
     * @see #asUnion(List, List)
     */
    public SortedListUnion(List<T> a, List<T> b, Comparator<? super T> order)
            throws IllegalArgumentException {

        checkArgs(a, b, order);


        int comp = order.compare(a.get(0), b.get(0));
        if (comp > 0) {
            List<T> swp = a;
            a = b;
            b = swp;
        }

        this.a = a;
        this.b = b;
        this.order = order;

        // place the starting (sentinel) pivot
        pivots.add(toPivot(0, 0));
    }


    private void checkArgs(List<T> a, List<T> b, Object order) {

        if (a == null || a.isEmpty())
            throw new IllegalArgumentException("null or empty list a: " + a);
        if (b == null || b.isEmpty())
            throw new IllegalArgumentException("null or empty list b: " + b);
        if (order == null)
            throw new IllegalArgumentException("null order");
    }

    @Override
    protected T getImpl(int location) {

        ensurePivotsToIndex(location);

        // determine which internal list and the index into that list,
        // .. which amounts to finding..
        int indexInPivots;
        {
            long pivotKey = ((long) location) << 32;
            indexInPivots = Collections.binarySearch(pivots, pivotKey);

            if (indexInPivots < 0)
                indexInPivots = -indexInPivots - 1;

            if (location < countAtPivot(pivots.get(indexInPivots)))
                --indexInPivots;
        }

        long pivot = pivots.get(indexInPivots);
        int listIndex = location - countAtPivot(pivot) + indexAtPivot(pivot);

        return pivotList(indexInPivots).get(listIndex);
    }

    @Override
    public int size() {
        return a.size() + b.size();
    }


    private void ensurePivotsToIndex(int index) {
        long lastPivot = pivots.get(pivots.size() - 1);
        int lastCount = countAtPivot(lastPivot);
        while (lastCount < index)
            lastCount = nextPivot();
    }


    /**
     * Calculates and stores the next pivot. Returns the count at the next pivot.
     */
    private int nextPivot() {

        // Monikers:
        // + <count> refers to combined external facing index into this list interface
        // + <index> refers to the sub index into one of the 2 internal pivot lists a, b
        // + <last> refers to the current value, e.g. lastCount
        // + <prev> refers to the value just before <last>, e.g. prevIndex
        // + <next> refers to the value (to be stored in pivots) after <last>, e.g. nextIndex

        int pivotsLastIndex = pivots.size() - 1;
        int lastCount = countAtPivot(pivots.get(pivotsLastIndex));
        int lastIndex = indexAtPivot(pivots.get(pivotsLastIndex));

        int nextIndex;
        {
            int prevCount, prevIndex;
            if (pivotsLastIndex == 0)
                prevCount = prevIndex = 0;
            else {
                long prevPivot = pivots.get(pivotsLastIndex - 1);
                prevCount = countAtPivot(prevPivot);
                prevIndex = indexAtPivot(prevPivot);
            }
            nextIndex = prevIndex + (lastCount - prevCount);
        }

        List<T> prevPivotList = pivotList(pivotsLastIndex + 1);

        int nextCount;
        if (nextIndex == prevPivotList.size())
            nextCount = size();
        else {
            List<T> lastPivotList = pivotList(pivotsLastIndex);
            List<T> searchSubList = lastPivotList.subList(lastIndex + 1, lastPivotList.size());
            T searchItem = prevPivotList.get(nextIndex);
            int insertionIndex = Collections.binarySearch(searchSubList, searchItem, order);

            if (insertionIndex < 0)
                insertionIndex = -insertionIndex - 1;
            nextCount = lastCount + insertionIndex + 1;
        }

        long pivot = toPivot(nextCount, nextIndex);
        pivots.add(pivot);

        return nextCount;
    }


    private int countAtPivot(long pivot) {
        return (int) (pivot >>> 32);
    }

    private int indexAtPivot(long pivot) {
        return (int) (pivot & 0xffffffffL);
    }

    private long toPivot(int count, int index) {
        long cl = count;
        long il = index & 0xffffffffL;
        return (cl << 32) | il;
    }

    private List<T> pivotList(int indexInPivots) {
        // pivots alternate from one list to the other.
        // since the first pivot is always into list a,
        // even indices denote pivots into a; odd indices, pivots into b.
        return indexInPivots % 2 == 0 ? a : b;
    }
}
