import com.dslplatform.json.DslJson;
import com.dslplatform.json.runtime.Settings;
import hu.tibipi.bumbitrack.core.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

class CityTest {
    static DslJson<Snapshot> dslJson;
    @BeforeAll
    static void initDslJson(){
        dslJson = new DslJson<>(Settings.basicSetup());
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
    void createSnapshot() throws IOException {
        Snapshot snapshot = new Snapshot(
                Objects.requireNonNull(dslJson.deserialize(Snapshot.SnapshotDTO.class,
                        Files.newInputStream(Paths.get("test_data", "country.json")))));

        Assertions.assertNotNull(snapshot);
        System.out.println(snapshot.getDateTime() + " " + snapshot.getID());
        for(Station station : snapshot.getCity().getStations())
            System.out.println(station.getName() + " " + station.getBikesNumber());
    }
}
