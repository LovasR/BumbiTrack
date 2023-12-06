package hu.tibipi.bumbitrack.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a filter that filters stations based on distance from a specified origin.
 * Extends the Filter class for Station objects.
 */
public class DistanceFilter extends Filter<Station> {

    Place origin; // The origin Place for distance calculation
    double distance; // The distance threshold for filtering stations

    /**
     * Constructs a DistanceFilter object with a specified origin and distance.
     *
     * @param origin   The origin Place for distance calculation
     * @param distance The distance threshold for filtering stations
     */
    public DistanceFilter(Place origin, double distance){
        this.origin = origin;
        this.distance = distance;
    }

    /**
     * Applies the distance filter to the input list of stations and populates the output list.
     *
     * @param in  The input list of stations to be filtered
     * @param out The output list to store the filtered stations
     */
    @Override
    public void apply(List<Station> in, List<Station> out) {
        out.clear();

        List<Double> distances = new ArrayList<>(in.size());
        LinkedList<Station> stations = new LinkedList<>(in);

        // Calculate distances from origin to each station and add to distances list (scaled to meters)
        for (Station station : stations) {
            distances.add(station.getPlace().calcDistanceTo(origin) * 1000); // Scale to meters
        }

        // Filter stations based on distance threshold
        for(int i = 0; i < stations.size(); i++){
            if(distances.get(i) <= distance){
                out.add(stations.get(i));
            }
        }
    }
}
