package hu.tibipi.bumbitrack.core;

import java.util.List;
import java.util.function.Function;

public interface AppUI {
    void start();

    void addTestQueryActionListener(Function<QueryManager, Object> queryAction);

    <T> List<Filter<T>> createFiltersFromQueryLines();
}
