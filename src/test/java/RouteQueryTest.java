import com.dslplatform.json.DslJson;
import com.dslplatform.json.runtime.Settings;
import hu.tibipi.bumbitrack.core.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

class RouteQueryTest {

    static DslJson<Snapshot> dslJson;
    static List<Snapshot> testSnapshots;

    static QueryManager qm;

    static TestAppUI testAppUiInstance;

    static class TestAppUI implements AppUI {
        @Override
        public void start() {

        }

        @Override
        public void deleteLoadingScreen() {

        }

        @Override
        public void updateLoadingStatus(int progress) {

        }

        @Override
        public void setQueryRunners(Function<QueryManager, Object> stationQuery, Function<QueryManager, Object> bikeQuery) {
        }

        @Override
        public <T> List<Filter<T>> createFiltersFromQueryLines() {
            return null;
        }

        @Override
        public String getBikeNameToFollow() {
            return "860540";
        }

        @Override
        public void setRouteQueryRunner(Function<QueryManager, Void> routeQuery) {
        }

        @Override
        public int getRouteLimit() {
            return 100;
        }

        @Override
        public void setQueryResults(List<Station> results) {
        }

        public Route resultRoute;

        @Override
        public void setRouteResults(Route resultRoute) {
            this.resultRoute = resultRoute;
        }

        @Override
        public void setStatisticsQueryRunner(Function<QueryManager, Void> statisticsQuery) {
        }

        @Override
        public String getChosenStatisticsGetter() {
            return null;
        }

        @Override
        public int getStatisticsLimit() {
            return 0;
        }

        @Override
        public void setStatisticsData(List<Integer> data) {
        }
    }

    @BeforeAll
    static void initTest() throws IOException {
        qm = new QueryManager();

        dslJson = new DslJson<>(Settings.basicSetup());

        testAppUiInstance = new TestAppUI();

        Main.initGetterFunctionMaps();


        testSnapshots = new ArrayList<>();

        for(int i = 0; i < 10; i++) {
            testSnapshots.add(new Snapshot(
                    Objects.requireNonNull(dslJson.deserialize(Snapshot.SnapshotDTO.class,
                            Files.newInputStream(Paths.get("test_data", "country.json"))))));
        }

        SnapshotManager.setSnapshots(testSnapshots);
    }

    @Test
    void routeQueryTest(){
        Assertions.assertEquals(10, testSnapshots.size());

        qm.routeQuery(testAppUiInstance);

        Route route = testAppUiInstance.resultRoute;
        Assertions.assertEquals(1, route.getStops().size());
        Assertions.assertEquals(1, route.getStops().size());
    }
}
