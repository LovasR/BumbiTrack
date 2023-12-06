package hu.tibipi.bumbitrack.core;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Represents a Route composed of RouteItems such as RouteStops and RouteGaps.
 */
public class Route {

    private final List<RouteItem> stops; // List of stops in the route

    /**
     * Constructs a Route based on the given list of stations and corresponding times.
     *
     * @param stationList The list of stations on the route
     * @param times       The list of times corresponding to each station
     */
    Route(List<Station> stationList, List<LocalDateTime> times){
        stops = new ArrayList<>();
        stationsToRoute(stationList, times);
    }

    /**
     * Converts the list of stations and times into RouteItems (RouteStops and RouteGaps).
     *
     * @param stationList The list of stations on the route
     * @param times       The list of times corresponding to each station
     */
    private void stationsToRoute(List<Station> stationList, List<LocalDateTime> times) {
        if (stationList.isEmpty())
            return;

        Station lastStation = stationList.get(0);
        Station lastEqualStation = stationList.get(0);
        for (ListIterator<Station> it = stationList.listIterator(); it.hasNext(); ) {
            Station station = it.next();

            if(!station.getName().equals(lastStation.getName()) || !it.hasNext()){
                stops.add(new RouteStop(
                        times.get(stationList.indexOf(lastEqualStation)),
                        times.get(stationList.indexOf(lastStation)),
                        lastStation));

                lastEqualStation = station;
            }

            // Check for gap in data
            Duration duration = Duration.between(
                    times.get(stationList.indexOf(lastStation)),
                    times.get(stationList.indexOf(station)));
            if(duration.toMinutes() > 30){
                stops.add(new RouteGap(duration));
            }

            lastStation = station;
        }
    }

    /**
     * Retrieves the list of RouteItems representing stops and gaps in the route.
     *
     * @return The list of RouteItems in the route
     */
    public List<RouteItem> getStops() {
        return stops;
    }

    /**
     * Represents an item in the Route.
     */
    public interface RouteItem{}

    /**
     * Represents a stop in the Route.
     */
    public static class RouteStop implements RouteItem {
        private final LocalDateTime start;
        private final LocalDateTime end;
        private final Station station;

        /**
         * Constructs a RouteStop with start and end times and the corresponding station.
         *
         * @param start   The start time of the stop
         * @param end     The end time of the stop
         * @param station The station associated with the stop
         */
        RouteStop(LocalDateTime start, LocalDateTime end, Station station){
            this.start = start;
            this.end = end;
            this.station = station;
        }

        public LocalDateTime getEnd() {
            return end;
        }

        public LocalDateTime getStart() {
            return start;
        }

        public Station getStation() {
            return station;
        }
    }

    /**
     * Represents a gap in the Route where data is not available for a specific duration.
     */
    public static class RouteGap implements RouteItem {
        private final Duration duration;

        /**
         * Constructs a RouteGap with a specific duration.
         *
         * @param duration The duration representing the gap
         */
        RouteGap(Duration duration){
            this.duration = duration;
        }

        public Duration getDuration() {
            return duration;
        }
    }
}
