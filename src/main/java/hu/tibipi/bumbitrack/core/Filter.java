package hu.tibipi.bumbitrack.core;

import java.util.List;

/**
 * Represents an abstract class for filtering elements of type T.
 * Provides a method 'apply' that subclasses must implement for filtering.
 *
 * @param <T> The type of elements to be filtered
 */
public abstract class Filter<T> {

    /**
     * Applies the filter logic to the input list 'in' and populates the output list 'out'.
     * Subclasses must implement this method for specific filtering behavior.
     *
     * @param in  The input list of elements to be filtered
     * @param out The output list to store the filtered elements
     */
    public abstract void apply(List<T> in, List<T> out);
}
