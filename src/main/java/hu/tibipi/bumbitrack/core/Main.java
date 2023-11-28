package hu.tibipi.bumbitrack.core;

import hu.tibipi.bumbitrack.ui.UIManager;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Main {

    public static final Logger log = Logger.getLogger(Main.class.getName());
    static Snapshot currentSnap = null;
    public static final QueryManager qm = new QueryManager();
    private static AppUI appUI;
    private static boolean isAppUiLoaded = false;
    private static final Object appUiLoadedLock = new Object();
    private static Map<String, Function<Station, ?>> stationGetterFunctionMap;
    private static Map<String, Function<Bike, ?>> bikeGetterFunctionMap;
    public static void main(String[] args){
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        log.log(Level.INFO, "Initialized");

        SnapshotManager.initSnapshotManager();

        appUI = new UIManager();
        appUI.start();
        synchronized (appUiLoadedLock){
            isAppUiLoaded = true;
            appUiLoadedLock.notifyAll();
        }
        appUI.setQueryRunners(t -> qm.testStationUIGeneratedQuery(appUI), t -> qm.testBikeUIGeneratedQuery(appUI));
        appUI.setRouteQueryRunner(t -> qm.routeQuery(appUI));
        appUI.setStatisticsQueryRunner(t -> qm.statisticsQuery(appUI));

        initGetterFunctionMaps();
    }

    private static void waitForAppUiLoaded(){
        synchronized (appUiLoadedLock) {
            while (!isAppUiLoaded) {
                try {
                    appUiLoadedLock.wait();
                } catch (InterruptedException e) {
                    Main.log.warning("Waiting interrupted" + e.getLocalizedMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public static void snapshotsLoaded(){
        waitForAppUiLoaded();
        appUI.deleteLoadingScreen();
    }

    public static void updateSnapshotLoadingProgress(long progress){
        waitForAppUiLoaded();
        appUI.updateLoadingStatus((int) progress);
    }

    private static void initGetterFunctionMaps(){
        stationGetterFunctionMap = new HashMap<>();
        stationGetterFunctionMap.put(
                "isBike",
                createGetterFunction(Station.class, "isBike")
        );
        stationGetterFunctionMap.put(
                "getBikeCapacity",
                createGetterFunction(Station.class, "getBikeCapacity")
        );
        stationGetterFunctionMap.put(
                "getBikesAvailable",
                createGetterFunction(Station.class, "getBikesAvailable")
        );
        stationGetterFunctionMap.put(
                "getName",
                createGetterFunction(Station.class, "getName")
        );
        stationGetterFunctionMap.put(
                "getPlace",
                createGetterFunction(Station.class, "getPlace")
        );
        stationGetterFunctionMap.put(
                "getBikesNumber",
                createGetterFunction(Station.class, "getBikesNumber")
        );

        bikeGetterFunctionMap = new HashMap<>();
        bikeGetterFunctionMap.put(
                "getName",
                createGetterFunction(Bike.class, "getName")
        );
    }

    @SuppressWarnings("unchecked")
    public static <T> Function<Station, T> getStationGetterFunction(String key){
        return (Function<Station, T>) stationGetterFunctionMap.get(key);
    }
    @SuppressWarnings("unchecked")
    public static <T> Function<Bike, T> getBikeGetterFunction(String key){
        return (Function<Bike, T>) bikeGetterFunctionMap.get(key);
    }

    private static LogRecord generateLambdaErrorRecord(Exception e){
        return new LogRecord(Level.SEVERE, e.toString() + "\n\nEXITING");
    }

    @SuppressWarnings("unchecked")
    private static <T, R> Function<T, R> createGetterFunction(Class<T> classType, String propertyName) {
        return (T input) -> {
            try {
                Method method = classType.getMethod(propertyName);
                Object result = method.invoke(input);
                return (R) result;
            } catch (Exception e) {
                Main.log.log(generateLambdaErrorRecord(e));
                System.exit(1);
                return null;
            }
        };
    }
}
