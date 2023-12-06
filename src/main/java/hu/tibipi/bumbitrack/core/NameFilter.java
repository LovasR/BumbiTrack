package hu.tibipi.bumbitrack.core;

import java.util.List;
import java.util.function.Function;

/**
 * Represents a filter that filters elements based on containing a specific token in a name.
 * Extends the Filter class for elements of type T.
 *
 * @param <T> The type of elements to be filtered
 */
public class NameFilter<T> extends Filter<T> {

    Function<T, String> getterFunction; // Function to retrieve the name from elements

    String token; // The token to search for in the names

    /**
     * Constructs a NameFilter object with getter function and token.
     *
     * @param getterFunction The function to retrieve the name from elements
     * @param token          The token to search for in the names
     */
    public NameFilter(Function<T, String> getterFunction, String token){
        this.getterFunction = getterFunction;
        this.token = token;
    }

    /**
     * Applies the name filter to the input list 'in' and populates the output list 'out'.
     * Checks if the name obtained from each element using the getter function contains the token,
     * and adds matching elements to the output list.
     *
     * @param in  The input list of elements to be filtered
     * @param out The output list to store the filtered elements
     */
    @Override
    public void apply(List<T> in, List<T> out) {
        out.clear();

        for(T item : in) {
            if(getterFunction.apply(item).contains(token)){
                out.add(item);
            }
        }
    }
}
