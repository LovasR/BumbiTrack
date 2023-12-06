import com.dslplatform.json.DslJson;
import com.dslplatform.json.runtime.Settings;
import hu.tibipi.bumbitrack.core.*;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class MainTest {
    static DslJson<Snapshot> dslJson;
    static Snapshot testSnapshot;

    @BeforeAll
    static void initDslJson() throws IOException {
        testSnapshot = null; //if the test fails later on

        dslJson = new DslJson<>(Settings.basicSetup());

        testSnapshot = new Snapshot(
                Objects.requireNonNull(dslJson.deserialize(Snapshot.SnapshotDTO.class,
                        Files.newInputStream(Paths.get("test_data", "country.json")))));

        System.out.println(testSnapshot.getDateTime() + " " + testSnapshot.getID());
        for(Station station : testSnapshot.getCity().getStations())
            System.out.println(station.getName() + " " + station.getBikesNumber());
    }

    @Test
    void createBike() throws IOException {
        Bike jsonBike = new Bike(
                Objects.requireNonNull(dslJson.deserialize(Bike.BikeDTO.class,
                        Files.newInputStream(Paths.get("test_data", "bike.json")))));

        Assertions.assertNotNull(jsonBike);
        System.out.println(jsonBike.getName() + " " + jsonBike.getIsActive());
    }

    @Test
    void createStation() throws IOException {
        Station jsonStation = new Station(
                Objects.requireNonNull(dslJson.deserialize(Station.StationDTO.class,
                        Files.newInputStream(Paths.get("test_data", "station.json")))));

        Assertions.assertNotNull(jsonStation);
        System.out.println(jsonStation.getName() + " " + jsonStation.getBikesNumber());
        for(Bike bike : jsonStation.getBikes())
            System.out.println(bike.getName() + " " + bike.getIsActive());
    }

    @Test
    void createSnapshot() {
        Assertions.assertNotNull(testSnapshot);
    }

    @Test
    void querySingleStation(){
        Assumptions.assumeFalse(testSnapshot == null);

        Query<Station> query = new Query<>();
        List<Filter<Station>> filters = new ArrayList<>();
        filters.add(
                new NameFilter<>(
                        Main.createGetterFunction(Station.class, "getName"),
                        "1101-Szent Gellért tér"
                )
        );
        query.setFilters(filters);

        // Executes the query for stations and updates the UI with the query results
        List<Station> stations = query.executeForStations(testSnapshot.getCity().getStations());

        Assertions.assertEquals(1, stations.size());
        Station station = stations.get(0);
        Assertions.assertEquals("1101-Szent Gellért tér", station.getName());
        Assertions.assertEquals(12, station.getBikes().size());
    }

    @Test
    void queryBikeStation(){
        Assumptions.assumeFalse(testSnapshot == null);

        Query<Station> query = new Query<>();
        List<Filter<Station>> filters = new ArrayList<>();
        filters.add(
                new GeneralFilter<>(
                        Main.createGetterFunction(Station.class, "isBike"),
                        true,
                        Object::equals
                )
        );
        filters.add(
                new NameFilter<>(
                        Main.createGetterFunction(Station.class, "getName"),
                        "BIKE 860903"
                )
        );
        query.setFilters(filters);

        // Executes the query for stations and updates the UI with the query results
        List<Station> stations = query.executeForStations(testSnapshot.getCity().getStations());

        Assertions.assertEquals(1, stations.size());
        Station station = stations.get(0);
        Assertions.assertEquals("BIKE 860903", station.getName());
        Assertions.assertTrue(station.isBike());
    }
}
