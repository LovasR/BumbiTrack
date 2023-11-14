package hu.tibipi.bumbitrack.core;

import java.util.*;

public class Query<T> {
    private ArrayList<Filter<T>> filters;

    Query(){
        filters = new ArrayList<>();
    }

    void addFilter(Filter<T> filter){
        filters.add(filter);
    }
    void setFilters(List<Filter<T>> filters){
        this.filters = new ArrayList<>(filters);
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

    ArrayList<Station> executeForBikes(List<Station> stationsInput){
        Set<Station> stations = new HashSet<>(stationsInput);

        List<Bike> bikesIn;
        List<Bike> currentBikes;

        ArrayList<Station> stationsToRemove = new ArrayList<>();
        for (Filter<T> filter : filters) {
            for (Station station : stations) {
                bikesIn = station.getBikes();
                currentBikes = new ArrayList<>();
                filter.apply((List<T>) bikesIn, (List<T>) currentBikes);

                if (currentBikes.isEmpty()) {
                    stationsToRemove.add(station);
                } else {
                    station.setBikes(currentBikes);
                }
            }

            for(Station station : stationsToRemove){
                stations.remove(station);
            }
            stationsToRemove.clear();
        }

        return new ArrayList<>(stations);
    }
}
