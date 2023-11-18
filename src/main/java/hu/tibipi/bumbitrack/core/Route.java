package hu.tibipi.bumbitrack.core;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Route {
    List<RouteItem> stops;

    Route(List<Station> stationList, List<LocalDateTime> times){
        stops = new ArrayList<>();

        stationsToRoute(stationList, times);
    }

    private void stationsToRoute(List<Station> stationList, List<LocalDateTime> times){
        if(stationList.isEmpty())
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

            //check for gap in data
            Duration duration = Duration.between(
                    times.get(stationList.indexOf(lastStation)),
                    times.get(stationList.indexOf(station)));
            if(duration.toMinutes() > 30){
                stops.add(new RouteGap(duration));
            }

            lastStation = station;
        }
    }

    public List<RouteItem> getStops() {
        return stops;
    }

    public interface RouteItem{}
    public static class RouteStop implements RouteItem {
        private final LocalDateTime start;
        private final LocalDateTime end;
        private final Station station;

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

    public static class RouteGap implements RouteItem {
        private final Duration duration;

        RouteGap(Duration duration){
            this.duration = duration;
        }

        public Duration getDuration() {
            return duration;
        }
    }
}
