package hu.tibipi.bumbitrack.core;

import hu.tibipi.bumbitrack.ui.UIManager;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static final Logger log = Logger.getLogger(Main.class.getName());
    static Snapshot currentSnap = null;
    public static final QueryManager qm = new QueryManager();
    public static void main(String[] args){
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        log.log(Level.INFO, "Initialized");

        Thread initialSnapshotGet = new Thread(Snapshot::createNewSnapshot);
        initialSnapshotGet.start();

        AppUI appUI = new UIManager();
        appUI.start();
        appUI.addTestQueryActionListener(t -> qm.testStationUIGeneratedQuery(appUI));
    }

}
