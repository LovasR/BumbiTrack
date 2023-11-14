package hu.tibipi.bumbitrack.core;

import java.util.List;
import java.util.function.Function;

public class NameFilter<T> extends Filter<T> {

    Function<T, String> getterFunction;

    String token;

    NameFilter(Function<T, String> getterFunction, String token){
        this.getterFunction = getterFunction;
        this.token = token;
    }
    @Override
    public void apply(List<T> in, List<T> out) {
        out.clear();

        for(T item : in) {
            if (getterFunction.apply(item).contains(token)) {
                out.add(item);
            }
        }
    }
}
