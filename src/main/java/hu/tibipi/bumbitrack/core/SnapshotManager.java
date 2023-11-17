package hu.tibipi.bumbitrack.core;

import org.json.JSONException;
import org.json.JSONObject;

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

        JSONObject rootJSON = new JSONObject(jsonString);

        try{
            City bp = new City(rootJSON,
                    settings.getSetting(Settings.COUNTRY_NAME),
                    settings.getSetting(Settings.CITY_NAME));
            Snapshot snapshot = new Snapshot(bp, settings.getSetting(Settings.COUNTRY_NAME));
            snapshots.add(snapshot);
            Main.currentSnap = snapshot;
            Main.log.info("New snapshot created");

            storeNewSnapshot(jsonString, snapshot);
        } catch (CountryNotFoundException e){
            Main.log.log(Level.SEVERE, "Specified country not found in file");
        } catch (CityNotFoundException e){
            Main.log.log(Level.SEVERE, "Specified city not found in file");
        } catch (IOException e){
            Main.log.severe("Couldn't store snapshot: " + e.getLocalizedMessage());
        }
    }

    private static void storeNewSnapshot(String jsonObject, Snapshot snapshot) throws IOException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FILEDATEFORMAT);
        int index = jsonObject.lastIndexOf("}");
        StringBuilder extendedJsonObject = new StringBuilder(jsonObject);
        extendedJsonObject.setCharAt(index, ' ');
        extendedJsonObject.append(", \"snapshot_time\": \"").append(snapshot.getDateTime().format(formatter)).append("\" }");

        Files.writeString(Paths.get("data", snapshot.getID(), createSnapshotFilename(snapshot)), extendedJsonObject.toString());

        String zipFilePath = snapshot.getID() + ".zip";

        Map<String, String> env = new HashMap<>();
        env.put("create", "true");

        Path path = Paths.get("data", zipFilePath);
        try (FileSystem fs = FileSystems.newFileSystem(path, env)) {
            Path nf = fs.getPath(createSnapshotFilename(snapshot));
            Files.write(nf, extendedJsonObject.toString().getBytes());
        }
    }

    private static String createSnapshotFilename(Snapshot snapshot){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FILEDATEFORMAT);
        return snapshot.getID() + "@" + snapshot.getDateTime().format(formatter) + ".json";
    }

    private static void loadSnapshots(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FILEDATEFORMAT);
        Path oldDirectoryPath = Paths.get("data", currentSnapshotDirectoryName);
        try(Stream<Path> filesStream = Files.walk(oldDirectoryPath)) {
            filesStream
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            JSONObject snapshotJson = new JSONObject(Files.readString(file));

                            City city = new City(snapshotJson,
                                    settings.getSetting(Settings.COUNTRY_NAME),
                                    settings.getSetting(Settings.CITY_NAME));

                            Snapshot snapshot = new Snapshot(city,
                                    settings.getSetting(Settings.COUNTRY_NAME),
                                    LocalDateTime.parse(snapshotJson.getString("snapshot_time"), formatter));
                            snapshots.add(snapshot);
                            Main.currentSnap = snapshot;
                        } catch (IOException | JSONException | CityNotFoundException | CountryNotFoundException e) {
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
                            Main.log.severe("Couldn't delete file (" + file.getFileName() + "): " + e);
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

    private static final String FILEDATEFORMAT = "yyyy-MM-dd_HH:mm:ss";

    private static class EnvironmentSetupException extends RuntimeException{}
    private static class SnapshotLoadException extends RuntimeException{}
}
