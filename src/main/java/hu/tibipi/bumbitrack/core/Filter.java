package hu.tibipi.bumbitrack.core;

import java.util.List;

public abstract class Filter<T> {
    public abstract void apply(List<T> in, List<T> out);
}
