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
import java.util.logging.Level;
import java.util.stream.Stream;

public class SnapshotManager {

    SnapshotManager(){}

    private static final List<Snapshot> snapshots = new ArrayList<>();
    public static List<Snapshot> getSnapshots() {
        return snapshots;
    }
    private static Settings settings;

    private static String currentSnapshotDirectoryName;

    private static boolean initialOldFileDeleted = false;

    private static final Object fileDeletionLock = new Object();

    private static final String FILE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static void initSnapshotManager(){
        settings = new Settings();

        int updateFrequency = settings.getSetting(Settings.UPDATE_FREQUENCY);
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(SnapshotManager::createNewSnapshot, 0, updateFrequency, TimeUnit.SECONDS);

        setupEnvironment();

        Thread loadThread = new Thread(SnapshotManager::loadSnapshots);
        loadThread.start();
    }

    public static void createNewSnapshot() {
        String jsonString;
        try {
            jsonString = NetUtils.downloadJsonFromURL(settings.getSetting(Settings.URL));
        } catch (IOException e) {
            Main.log.log(Level.SEVERE, "Couldnt load .json from web");
            Main.currentSnap = null;
            return;
        }

        byte[] jsonBytes = augmentJsonString(jsonString);

        try{
            DslJson<Object> dslJson = new DslJson<>(com.dslplatform.json.runtime.Settings.basicSetup());
            Snapshot snapshot = dslJson.deserialize(Snapshot.class, jsonBytes, jsonBytes.length);

            snapshots.add(snapshot);
            Main.currentSnap = snapshot;
            Main.log.info("New snapshot created");

            storeNewSnapshot(jsonBytes, snapshot);
        } catch (IOException e){
            Main.log.severe("Couldn't store snapshot: " + e.getLocalizedMessage());
        }
    }

    private static byte[] augmentJsonString(String jsonIn){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FILE_DATE_FORMAT);
        int index = jsonIn.lastIndexOf("}");
        StringBuilder extendedJsonObject = new StringBuilder(jsonIn);
        extendedJsonObject.setCharAt(index, ' ');
        extendedJsonObject.append(", \"snapshot_time\": \"").append(LocalDateTime.now().format(formatter)).append("\" }");

        return extendedJsonObject.toString().getBytes();
    }

    private static void storeNewSnapshot(byte[] jsonObject, Snapshot snapshot) throws IOException {
        deleteOldestFileIfOverLimit(Paths.get("data", snapshot.getID()), settings.getSetting(Settings.SNAPSHOT_LIMIT));

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

    private static String createSnapshotFilename(Snapshot snapshot){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FILE_DATE_FORMAT);
        return snapshot.getID() + "@" + snapshot.getDateTime().format(formatter) + ".json";
    }

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
        try(Stream<Path> filesStream = Files.walk(oldDirectoryPath)) {
            filesStream
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            DslJson<Object> dslJson = new DslJson<>(com.dslplatform.json.runtime.Settings.basicSetup());
                            Snapshot snapshot = dslJson.deserialize(Snapshot.class, Files.newInputStream(file));

                            assert snapshot != null;
                            snapshots.add(snapshot);
                            Main.currentSnap = snapshot;
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
        Main.log.info("Snapshots loaded");
    }

    private static void setupEnvironment() {
        currentSnapshotDirectoryName = Snapshot.createID(settings.getSetting(Settings.COUNTRY_NAME), settings.getSetting(Settings.CITY_NAME));

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

    private static void createNewSnapshotDirectory(String directoryName){
        File snapshotDirectory = new File(Paths.get("data", directoryName).toUri());

        if(!snapshotDirectory.mkdir()){
            Main.log.severe("Couldn't create used snapshot directory");
        }
    }

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

    private static class EnvironmentSetupException extends RuntimeException{}
    private static class SnapshotLoadException extends RuntimeException{}
}
