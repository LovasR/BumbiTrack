package hu.tibipi.bumbitrack.core;

import com.dslplatform.json.DslJson;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * Manages the creation and storage of snapshots based on configuration settings.
 */
public class SnapshotManager {
    private static List<Snapshot> snapshots = new ArrayList<>();
    private static Settings settings;
    private static String currentSnapshotDirectoryName;
    private static boolean initialOldFileDeleted = false;
    private static final AtomicLong snapshotsProcessed = new AtomicLong(0);
    private static final Object fileDeletionLock = new Object();
    private static final String FILE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DslJson<Object> dslJson = new DslJson<>(com.dslplatform.json.runtime.Settings.basicSetup());

    /**
     * Initializes the SnapshotManager by setting up settings, scheduling snapshot creation,
     * and loading snapshots from files.
     */
    public static void initSnapshotManager(){
        settings = Settings.initSettingsDirectory(dslJson);

        int updateFrequency = settings.getUpdateCycle();
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(SnapshotManager::createNewSnapshot, 1, updateFrequency, TimeUnit.SECONDS);

        setupEnvironment();

        Thread loadThread = new Thread(SnapshotManager::loadSnapshots);
        loadThread.start();
    }


    /**
     * Creates a new snapshot based on the data retrieved from a URL and stores it.
     */
    public static void createNewSnapshot() {
        String jsonString;
        try {
            jsonString = NetUtils.downloadJsonFromURL(settings.getUsedUrl());
        } catch (IOException e) {
            Main.log.severe("Couldn't load .json from web " + e.getLocalizedMessage());
            Main.currentSnap = null;
            return;
        }

        byte[] jsonBytes = augmentJsonString(jsonString);

        try{
            Snapshot snapshot = new Snapshot(
                    Objects.requireNonNull(dslJson.deserialize(Snapshot.SnapshotDTO.class, jsonBytes, jsonBytes.length)));

            snapshots.add(snapshot);
            Main.currentSnap = snapshot;
            Main.log.info("New snapshot created");

            storeNewSnapshot(jsonBytes, snapshot);
        } catch (IOException e){
            Main.log.severe("Couldn't store snapshot: " + e.getLocalizedMessage());
        }
    }

    /**
     * Augments the JSON string by appending a snapshot_time field with the current date and time.
     *
     * @param jsonIn The JSON string to be extended.
     * @return A byte array representing the extended JSON string.
     */
    private static byte[] augmentJsonString(String jsonIn){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FILE_DATE_FORMAT);
        int index = jsonIn.lastIndexOf("}");
        StringBuilder extendedJsonObject = new StringBuilder(jsonIn);
        extendedJsonObject.setCharAt(index, ' ');
        extendedJsonObject.append(", \"snapshot_time\": \"").append(LocalDateTime.now().format(formatter)).append("\" }");

        return extendedJsonObject.toString().getBytes();
    }

    /**
     * Stores a new snapshot in the data directory and creates a zip file for the snapshot.
     *
     * @param jsonObject The JSON data to be stored.
     * @param snapshot   The snapshot object containing information about the snapshot.
     * @throws IOException if an I/O error occurs while storing the snapshot.
     */
    private static void storeNewSnapshot(byte[] jsonObject, Snapshot snapshot) throws IOException {
        deleteOldestFileIfOverLimit(Paths.get("data", snapshot.getID()), settings.getSnapshotLimit());

        Files.write(Paths.get("data", snapshot.getID(), createSnapshotFilename(snapshot)), jsonObject);

        String zipFilePath = snapshot.getID() + ".zip";

        Map<String, String> env = new HashMap<>();
        env.put("create", "true");

        Path path = Paths.get("data", zipFilePath);
        try (FileSystem fs = FileSystems.newFileSystem(path, env)) {
            Path nf = fs.getPath(createSnapshotFilename(snapshot));
            Files.write(nf, jsonObject);
        }
    }

    /**
     * Deletes the oldest file in a directory if the file count exceeds the specified limit.
     *
     * @param directory The directory where files are stored.
     * @param limit     The maximum number of files allowed in the directory.
     * @throws IOException if an I/O error occurs while deleting the oldest file.
     */
    private static void deleteOldestFileIfOverLimit(Path directory, int limit) throws IOException {
        Path oldestFile = null;

        Stream<Path> stream = Files.list(directory);
        if(stream.count() < limit){
            stream.close();
            initialOldFileDeleted = true;
            synchronized(fileDeletionLock) {
                fileDeletionLock.notifyAll();
            }
            return;
        }
        stream.close();

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
            for (Path file : directoryStream) {
                if (oldestFile == null || Files.getLastModifiedTime(file).compareTo(Files.getLastModifiedTime(oldestFile)) < 0) {
                    oldestFile = file;
                }
            }
        }

        assert oldestFile != null;
        Files.delete(oldestFile);
        initialOldFileDeleted = true;
        synchronized(fileDeletionLock) {
            fileDeletionLock.notifyAll();
        }
    }

    /**
     * Creates a filename for a snapshot based on the snapshot's ID and date/time.
     *
     * @param snapshot The snapshot object for which the filename is created.
     * @return The filename for the snapshot.
     */
    private static String createSnapshotFilename(Snapshot snapshot){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FILE_DATE_FORMAT);
        return snapshot.getID() + "@" + snapshot.getDateTime().format(formatter) + ".json";
    }

    /**
     * Loads snapshots from the old directory path, deserializes them, and adds them to the snapshot list.
     * Starts the thread that monitors the progress of loading snapshots and updates the loading progress in the application.
     */
    private static void loadSnapshots() {
        Path oldDirectoryPath = Paths.get("data", currentSnapshotDirectoryName);
        synchronized(fileDeletionLock) {
            while (!initialOldFileDeleted) {
                try {
                    fileDeletionLock.wait();
                } catch (InterruptedException e) {
                    Main.log.warning("Waiting @loadSnapshots interrupted" + e.getLocalizedMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(SnapshotManager::snapshotLoadProgressMonitor, 0, 100, TimeUnit.MILLISECONDS);

        long startTime = System.nanoTime();
        try(Stream<Path> filesStream = Files.walk(oldDirectoryPath)) {
            List<Path> regularFiles = filesStream
                    .filter(Files::isRegularFile)
                    .toList();

            regularFiles.parallelStream().forEach(file -> {
                try {
                    DslJson<Object> dslJson = new DslJson<>(com.dslplatform.json.runtime.Settings.basicSetup());
                    Snapshot snapshot = new Snapshot(
                            Objects.requireNonNull(
                                    dslJson.deserialize(Snapshot.SnapshotDTO.class, Files.newInputStream(file))));

                    snapshots.add(snapshot);

                    snapshotsProcessed.incrementAndGet();
                } catch (IOException e) {
                    Main.log.severe("Couldn't load snapshot (" + file.getFileName() + "): " + e.getLocalizedMessage());
                    throw new SnapshotLoadException();
                }
            });
        } catch (IOException e){
            Main.log.severe("Couldn't load snapshots: " + e.getLocalizedMessage());
            throw new SnapshotLoadException();
        }
        snapshots.sort(Comparator.comparing(Snapshot::getDateTime));
        Main.currentSnap = snapshots.get(snapshots.size() - 1);

        long endTime = System.nanoTime();
        long executionTimeInNano = endTime - startTime;
        double executionTimeInMilliseconds = (double) executionTimeInNano / 1_000_000;

        Main.log.info(() -> "Snapshots loaded in: " + Math.round(executionTimeInMilliseconds) + " ms");

        Main.snapshotsLoaded();     //show UI
    }

    /**
     * Monitors the progress of loading snapshots and updates the loading progress in the application.
     */
    private static void snapshotLoadProgressMonitor(){
        Main.updateSnapshotLoadingProgress(snapshotsProcessed.get());
    }

    /**
     * Sets up the environment for managing snapshots.
     * Checks the existence of a snapshot directory, deletes the old directory contents if needed,
     * creates a new snapshot directory, and unzips snapshots to the directory.
     */
    private static void setupEnvironment() {
        currentSnapshotDirectoryName = Snapshot.createID(settings.getCountryName(), settings.getCityName());

        File dataDirectory = new File(Paths.get("data").toUri());
        File oldDirectory = null;
        for(File file : Objects.requireNonNull(dataDirectory.listFiles())){
            if(file.isDirectory()){
                if(file.getName().equals(currentSnapshotDirectoryName)){
                    //if there is already a directory with the name, the environment is considered set up
                    return;
                }
                oldDirectory = file;
            }
        }

        if(oldDirectory == null){
            createNewSnapshotDirectory(currentSnapshotDirectoryName);
            return;
        }

        Path oldDirectoryPath = Paths.get(oldDirectory.getAbsolutePath());
        try(Stream<Path> filesStream = Files.walk(oldDirectoryPath)){
            filesStream
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            Files.delete(file);
                        } catch (IOException e) {
                            Main.log.severe("Couldn't delete file (" + file.getFileName() + "): " + e.getLocalizedMessage());
                        }
                    });
            Files.delete(oldDirectoryPath);
        } catch (IOException e) {
            throw new EnvironmentSetupException();
        }

        createNewSnapshotDirectory(currentSnapshotDirectoryName);

        unzipSnapshotsToDirectory(currentSnapshotDirectoryName,
                Paths.get("data", currentSnapshotDirectoryName + ".zip").toString());
    }

    /**
     * Creates a new snapshot directory with the specified directory name.
     *
     * @param directoryName Name of the directory to be created
     */
    private static void createNewSnapshotDirectory(String directoryName){
        File snapshotDirectory = new File(Paths.get("data", directoryName).toUri());

        if(!snapshotDirectory.mkdir()){
            Main.log.severe("Couldn't create used snapshot directory");
        }
    }

    /**
     * Unzips snapshots from the specified zip file path to the given directory.
     *
     * @param directoryName Name of the directory where snapshots are to be unzipped
     * @param zipFilePath   Path to the ZIP file containing snapshots
     */
    private static void unzipSnapshotsToDirectory(String directoryName, String zipFilePath){
        int numThreads = 4;
        try (FileSystem fs = FileSystems.newFileSystem(Paths.get(directoryName, zipFilePath), (Map<String, ?>) null)) {

            ExecutorService executor = Executors.newFixedThreadPool(numThreads);

            try (Stream<Path> entries = Files.walk(fs.getPath("/"))) {
                entries.filter(Files::isRegularFile)
                        .forEach(path -> executor.execute(new FileExtractor(path, fs)));
            }

            executor.shutdown();
        } catch (IOException e) {
            Main.log.severe("Couldn't unzip (" + zipFilePath + "): " + e.getLocalizedMessage());
        }
    }

    /**
     * A worker class that implements the Runnable interface to extract files from a ZIP archive.
     */
    private record FileExtractor(Path zipEntryPath, FileSystem fileSystem) implements Runnable {
        @Override
        public void run() {
            try {
                Path outputFilePath = Paths.get(zipEntryPath.toString().substring(1)); // Remove leading "/"
                Files.copy(fileSystem.getPath(zipEntryPath.toString()), outputFilePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                Main.log.severe("Couldn't unzip snapshot file (" + zipEntryPath + "): " + e.getLocalizedMessage());
                throw new EnvironmentSetupException();
            }
        }
    }

    /**
     * Sets the snapshots list, only if SnapshotManager is called from a JUnit test.
     */
    public static void setSnapshots(List<Snapshot> newSnapshots){
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getClassName().startsWith("org.junit")) {
                snapshots = new ArrayList<>(newSnapshots);
                break;
            }
        }
        // Not called from a JUnit context
    }

    public static List<Snapshot> getSnapshots() {
        return snapshots;
    }

    /**
     * Exception thrown when encountering issues during the setup of the environment for snapshots.
     */
    private static class EnvironmentSetupException extends RuntimeException { }

    /**
     * Exception thrown when encountering issues while loading snapshots.
     */
    private static class SnapshotLoadException extends RuntimeException { }
}
