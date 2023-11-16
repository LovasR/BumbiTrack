package hu.tibipi.bumbitrack.core;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    public static void initSnapshotManager(){
        settings = new Settings();

        int updateFrequency = settings.getSetting(Settings.UPDATE_FREQUENCY);
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(SnapshotManager::createNewSnapshot, 0, updateFrequency, TimeUnit.SECONDS);

        setupEnvironment();
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
        Files.writeString(Paths.get("data", snapshot.getID(), createSnapshotFilename(snapshot)), jsonObject);

        String zipFilePath = snapshot.getID() + ".zip";

        Map<String, String> env = new HashMap<>();
        env.put("create", "true");

        Path path = Paths.get("data", zipFilePath);
        try (FileSystem fs = FileSystems.newFileSystem(path, env)) {
            Path nf = fs.getPath(createSnapshotFilename(snapshot));
            Files.write(nf, jsonObject.getBytes());
        }
    }

    private static String createSnapshotFilename(Snapshot snapshot){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
        return snapshot.getID() + "@" + snapshot.getDateTime().format(formatter) + ".json";
    }

    private static void setupEnvironment() {
        String directoryName = Snapshot.createID(settings.getSetting(Settings.COUNTRY_NAME), settings.getSetting(Settings.CITY_NAME));

        File dataDirectory = new File(Paths.get("data").toUri());
        File oldDirectory = null;
        for(File file : Objects.requireNonNull(dataDirectory.listFiles())){
            if(file.isDirectory()){
                if(file.getName().equals(directoryName)){
                    //if there is already a directory with the name, the environment is considered set up
                    return;
                }
                oldDirectory = file;
            }
        }

        if(oldDirectory == null){
            createNewSnapshotDirectory(directoryName);
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

        createNewSnapshotDirectory(directoryName);
    }

    private static void createNewSnapshotDirectory(String directoryName){
        File snapshotDirectory = new File(Paths.get("data", directoryName).toUri());

        if(!snapshotDirectory.mkdir()){
            Main.log.severe("Couldn't create used snapshot directory");
        }
    }

    private static class EnvironmentSetupException extends RuntimeException{}
}
