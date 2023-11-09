package hu.tibipi.bumbitrack.core;

import java.util.ArrayList;
import java.util.List;

public class Query<T> {
    private final ArrayList<Filter<T>> filters;

    Query(){
        filters = new ArrayList<>();
    }

    void addFilter(Filter<T> filter){
        filters.add(filter);
    }

    ArrayList<Station> executeForStations(List<Station> stationsInput){
        ArrayList<Station> inputStations = new ArrayList<>(stationsInput);
        ArrayList<Station> outputStations = new ArrayList<>(inputStations);

        for(Filter<T> filter : filters){
            filter.apply((List<T>) inputStations, (List<T>) outputStations);
            inputStations = new ArrayList<>(outputStations);
        }

        return outputStations;
    }
}
