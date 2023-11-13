package hu.tibipi.bumbitrack.core;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class GeneralFilter<T, C> extends Filter<T> {

    C comparedValue;
    BiPredicate<C, C> compareFunction;
    Function<T, C> getterFunction;

    public GeneralFilter(Function<T, C> getterFunction, C comparedValue, BiPredicate<C, C> compareFunction){
        this.comparedValue = comparedValue;
        this.getterFunction = getterFunction;
        this.compareFunction = (compareFunction != null) ? compareFunction : Object::equals;
    }

    @Override
    public void apply(List<T> in, List<T> out) {
        out.clear();
        for(T item : in){
            if(compareFunction.test(getterFunction.apply(item), comparedValue)){
                out.add(item);
            }
        }
    }
}
