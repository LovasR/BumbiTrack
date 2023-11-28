package hu.tibipi.bumbitrack.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DistanceFilter extends Filter<Station> {

    Place origin;
    double distance;

    public DistanceFilter(Place origin, double distance){
        this.origin = origin;
        this.distance = distance;
    }

    @Override
    public void apply(List<Station> in, List<Station> out) {
        out.clear();

        List<Double> distances = new ArrayList<>(in.size());
        LinkedList<Station> stations = new LinkedList<>(in);

        for(Station station : stations){
            distances.add(station.getPlace().calcDistanceTo(origin) * 1000); //scale to meters
        }

        for(int i = 0; i < stations.size(); i++){
            if(distances.get(i) <= distance){
                out.add(stations.get(i));
            }
        }
    }
}
