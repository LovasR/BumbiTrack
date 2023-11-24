import com.dslplatform.json.DslJson;
import com.dslplatform.json.runtime.Settings;
import hu.tibipi.bumbitrack.core.Bike;
import hu.tibipi.bumbitrack.core.Snapshot;
import hu.tibipi.bumbitrack.core.Station;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class CityTest {
    static DslJson<Snapshot> dslJson;
    @BeforeAll
    static void initDslJson(){
        dslJson = new DslJson<>(Settings.basicSetup());
    }

    @Test
    void createBike() throws IOException {

        dslJson = new DslJson<>(Settings.basicSetup());
        Bike jsonBike = dslJson.deserialize(Bike.class, Files.newInputStream(Paths.get("test_data", "bike.json")));

        Assertions.assertNotNull(jsonBike);
        System.out.println(jsonBike.getName() + " " + jsonBike.getIsActive());
    }

    @Test
    void createStation() throws IOException {

        dslJson = new DslJson<>(Settings.basicSetup());
        Station jsonStation = dslJson.deserialize(Station.class, Files.newInputStream(Paths.get("test_data", "station.json")));

        Assertions.assertNotNull(jsonStation);
        System.out.println(jsonStation.getName() + " " + jsonStation.getBikesNumber());
        for(Bike bike : jsonStation.bikes)
            System.out.println(bike.getName() + " " + bike.getIsActive());
    }

    @Test
    void createSnapshot() throws IOException {
        dslJson = new DslJson<>(Settings.basicSetup());
        Snapshot snapshot = dslJson.deserialize(Snapshot.class, Files.newInputStream(Paths.get("test_data", "country.json")));

        Assertions.assertNotNull(snapshot);
        System.out.println(snapshot.getDateTime() + " " + snapshot.getID());
        for(Station station : snapshot.getCity().getStations())
            System.out.println(station.getName() + " " + station.getBikesNumber());
    }
}
