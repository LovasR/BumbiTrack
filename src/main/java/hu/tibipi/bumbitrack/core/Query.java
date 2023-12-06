package hu.tibipi.bumbitrack.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The Query class represents a query operation to filter stations or bikes based on specified filters.
 *
 * @param <T> The type of elements that the Query operates on.
 */
public class Query<T> {
    private ArrayList<Filter<T>> filters;

    /**
     * Constructs a Query object with an empty list of filters.
     */
    public Query() {
        filters = new ArrayList<>();
    }

    /**
     * Adds a filter to the list of filters.
     *
     * @param filter The filter to add.
     */
    void addFilter(Filter<T> filter) {
        filters.add(filter);
    }

    /**
     * Sets the list of filters to the provided list.
     *
     * @param filters The list of filters to set.
     */
    public void setFilters(List<Filter<T>> filters) {
        this.filters = new ArrayList<>(filters);
    }

    /**
     * Executes the query for stations based on the specified filters.
     *
     * @param stationsInput The list of stations to apply the query on.
     * @return The list of stations after applying the filters.
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Station> executeForStations(List<Station> stationsInput) {
        ArrayList<Station> inputStations = new ArrayList<>(stationsInput);
        ArrayList<Station> outputStations = new ArrayList<>(inputStations);

        for (Filter<T> filter : filters) {
            filter.apply((List<T>) inputStations, (List<T>) outputStations);
            inputStations = new ArrayList<>(outputStations);
        }

        return outputStations;
    }

    /**
     * Executes the query for bikes based on the specified filters.
     *
     * @param stationsInput The list of stations to apply the query on.
     * @return The list of stations containing filtered bikes.
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Station> executeForBikes(List<Station> stationsInput) {
        Set<Station> stations = new HashSet<>();
        for (Station station : stationsInput) {
            stations.add(new Station(station));
        }

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

            for (Station station : stationsToRemove) {
                stations.remove(station);
            }
            stationsToRemove.clear();
        }

        return new ArrayList<>(stations);
    }
}
