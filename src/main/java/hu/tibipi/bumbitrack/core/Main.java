package hu.tibipi.bumbitrack.core;

import hu.tibipi.bumbitrack.ui.UIManager;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Main class responsible for initializing the application and managing its core functionalities.
 */
public class Main {

    /**
     * Logger instance for logging application events
     */
    public static final Logger log = Logger.getLogger(Main.class.getName());

    /**
     * Snapshot instance representing the current snapshot
     */
    static Snapshot currentSnap = null;

    /**
     * QueryManager instance managing queries
     */
    public static final QueryManager qm = new QueryManager();

    /**
     * Application User Interface instance
     */
    private static AppUI appUI;

    /**
     * Flag indicating whether the application UI is loaded
     */
    private static boolean isAppUiLoaded = false;

    /**
     * Object used for synchronization of app UI loading
     */
    private static final Object appUiLoadedLock = new Object();

    /**
     * Map for station getter functions
     */
    private static Map<String, Function<Station, ?>> stationGetterFunctionMap;

    /**
     * Map for bike getter functions
     */
    private static Map<String, Function<Bike, ?>> bikeGetterFunctionMap;

    /**
     * Main method to start the application.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        // Setting log formatting
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        log.log(Level.INFO, "Initialized");

        // Initialize SnapshotManager
        SnapshotManager.initSnapshotManager();

        // Initialize and start the application UI
        appUI = new UIManager();
        appUI.start();
        synchronized (appUiLoadedLock) {
            isAppUiLoaded = true;
            appUiLoadedLock.notifyAll();
        }

        // Set query runners for UI interactions
        appUI.setQueryRunners(t -> qm.testStationUIGeneratedQuery(appUI), t -> qm.testBikeUIGeneratedQuery(appUI));
        appUI.setRouteQueryRunner(t -> qm.routeQuery(appUI));
        appUI.setStatisticsQueryRunner(t -> qm.statisticsQuery(appUI));

        // Initialize getter function maps
        initGetterFunctionMaps();
    }

    /**
     * Waits until the application UI is loaded.
     * It synchronizes on {@code appUiLoadedLock} and waits until the flag {@code isAppUiLoaded} becomes true.
     * If an {@link InterruptedException} occurs, it logs a warning and interrupts the thread.
     */
    private static void waitForAppUiLoaded() {
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

    /**
     * Signals that snapshots are loaded by waiting for the application UI to be loaded and then deletes the loading screen from the UI.
     * Calls {@link AppUI#deleteLoadingScreen()} after the application UI is loaded.
     */
    public static void snapshotsLoaded() {
        waitForAppUiLoaded();
        appUI.deleteLoadingScreen();
    }

    /**
     * Updates the loading progress of snapshots by waiting for the application UI to be loaded and then updating the loading status in the UI.
     * Calls {@link AppUI#updateLoadingStatus(int)} after the application UI is loaded.
     *
     * @param progress The progress value indicating the loading progress of snapshots.
     */
    public static void updateSnapshotLoadingProgress(long progress) {
        waitForAppUiLoaded();
        appUI.updateLoadingStatus((int) progress);
    }

    /**
     * Initializes getter function maps for stations and bikes, populating them with functions that access specific properties of Station and Bike instances.
     * This method creates getter functions for various Station and Bike properties and adds them to respective maps.
     */
    public static void initGetterFunctionMaps() {
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
    public static <T> Function<Station, T> getStationGetterFunction(String key) {
        return (Function<Station, T>) stationGetterFunctionMap.get(key);
    }

    @SuppressWarnings("unchecked")
    public static <T> Function<Bike, T> getBikeGetterFunction(String key) {
        return (Function<Bike, T>) bikeGetterFunctionMap.get(key);
    }

    /**
     * Generates a log record representing an error occurred within a lambda function.
     * Creates a {@link LogRecord} containing error information for a lambda function and marks the severity as severe.
     *
     * @param e The exception that occurred within the lambda function.
     * @return A log record containing error information.
     */
    private static LogRecord generateLambdaErrorRecord(Exception e) {
        return new LogRecord(Level.SEVERE, e.toString() + "\n\nEXITING");
    }

    /**
     * Creates a getter function dynamically for the given property name of the specified class type.
     * This method uses reflection to create a function that accesses a specific property of an object dynamically.
     *
     * @param classType     The class type for which the getter function is created.
     * @param propertyName  The name of the property for which the getter function is created.
     * @param <T>           The class type.
     * @param <R>           The return type of the property.
     * @return A function to get the specified property value from an object.
     */
    @SuppressWarnings("unchecked")
    public static <T, R> Function<T, R> createGetterFunction(Class<T> classType, String propertyName) {
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
