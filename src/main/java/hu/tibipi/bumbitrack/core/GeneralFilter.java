package hu.tibipi.bumbitrack.core;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Represents a generic filter capable of filtering elements based on a comparison predicate.
 * Extends the Filter class for elements of type T.
 *
 * @param <T> The type of elements to be filtered
 * @param <C> The type of the value to be compared with
 */
public class GeneralFilter<T, C> extends Filter<T> {

    C comparedValue; // The value to be compared with
    BiPredicate<C, C> compareFunction; // Comparison predicate function
    Function<T, C> getterFunction; // Function to retrieve the value to be compared

    /**
     * Constructs a GeneralFilter object with getter function, compared value, and comparison function.
     *
     * @param getterFunction  The function to retrieve the value from the elements
     * @param comparedValue   The value to be compared with
     * @param compareFunction The comparison predicate function
     */
    public GeneralFilter(Function<T, C> getterFunction, C comparedValue, BiPredicate<C, C> compareFunction) {
        this.comparedValue = comparedValue;
        this.getterFunction = getterFunction;
        this.compareFunction = (compareFunction != null) ? compareFunction : Object::equals;
    }

    /**
     * Applies the general filter to the input list 'in' and populates the output list 'out'.
     * Compares the value obtained from each element using the getter function with the compared value
     * based on the comparison function and adds matching elements to the output list.
     *
     * @param in  The input list of elements to be filtered
     * @param out The output list to store the filtered elements
     */
    @Override
    public void apply(List<T> in, List<T> out) {
        out.clear();
        for (T item : in) {
            if (compareFunction.test(getterFunction.apply(item), comparedValue)) {
                out.add(item);
            }
        }
    }
}
