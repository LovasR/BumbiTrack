package hu.tibipi.bumbitrack.ui;

import hu.tibipi.bumbitrack.core.Main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainWindow extends JFrame {
    private JTabbedPane mainPane; // Tabbed pane for organizing different panels

    private QueryPanel queryPanel; // Panel for querying data
    private RoutePanel routePanel; // Panel for displaying routes
    private StatisticsPanel statisticsPanel; // Panel for displaying statistics
    private JPanel settingsPanel; // Panel for application settings

    private JProgressBar loadingPb; // Progress bar for loading status
    private boolean areTabsCreated; // Flag to check if tabs are created
    private final transient Object tabsCreatedLock; // Lock for deferred tab creation


    /**
     * Constructs the main window of the application, setting its initial dimensions and initiating the loading screen.
     * It starts a thread for deferred tab creation to improve initialization performance.
     */
    MainWindow(){
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(new Dimension(1200, 800));
        this.setPreferredSize(new Dimension(800, 600));

        createLoadingScreen();

        areTabsCreated = false;
        tabsCreatedLock = new Object();
        Thread tabCreation = new Thread(this::deferredTabCreation);
        tabCreation.start();
    }

    /**
     * Update the loading status by setting the progress of the loading progress bar.
     *
     * @param progress The progress value to be set on the loading progress bar.
     */
    void updateLoadingStatus(int progress){
        loadingPb.setValue(progress);
    }

    /**
     * Deletes the loading screen and displays the main content of the application, including different tabs
     * such as Query, Route, Statistics, and Settings. Waits until tabs are created before adding them to the main window.
     */
    void deleteLoadingScreen(){
        this.getContentPane().removeAll();

        waitUntilTabsCreated();

        mainPane.addTab("Query", queryPanel);
        mainPane.addTab("Route", routePanel);
        mainPane.addTab("Statistics", statisticsPanel);

        this.setContentPane(mainPane);
        this.revalidate();
        this.repaint();
    }

    /**
     * Creates a loading screen to display while the application is initializing.
     * It contains a progress bar and a label indicating the loading process.
     */
    private void createLoadingScreen() {

        JPanel loadingP = new JPanel();
        loadingP.setLayout(new BoxLayout(loadingP, BoxLayout.Y_AXIS));
        loadingP.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel loadingLb = new JLabel("Loading...");
        loadingLb.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadingLb.setAlignmentY(Component.CENTER_ALIGNMENT);
        loadingP.add(Box.createVerticalGlue()); // Add space above the label
        loadingP.add(loadingLb);
        loadingP.add(Box.createVerticalStrut(10));

        loadingPb = new JProgressBar();
        loadingPb.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadingP.add(loadingPb);
        loadingP.add(Box.createVerticalGlue());

        this.setContentPane(loadingP);
        this.setLocationRelativeTo(null);
    }


    /**
     * Initiates the deferred creation of tabs for different functionalities in the application.
     * Creates query, route, statistics, and settings panels within a tabbed pane when called.
     */
    private void deferredTabCreation(){
        queryPanel = new QueryPanel();
        routePanel = new RoutePanel();
        statisticsPanel = new StatisticsPanel();
        settingsPanel = new JPanel();

        mainPane = new JTabbedPane();
        areTabsCreated = true;
        synchronized (tabsCreatedLock) {
            tabsCreatedLock.notifyAll();
        }
    }

    /**
     * Waits until the tabs (Query, Route, Statistics, and Settings) are created before proceeding.
     * It utilizes a lock to synchronize the waiting process.
     */
    void waitUntilTabsCreated(){
        synchronized(tabsCreatedLock) {
            while (!areTabsCreated) {
                try {
                    tabsCreatedLock.wait();
                } catch (InterruptedException e) {
                    Main.log.warning("Waiting interrupted" + e.getLocalizedMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * Retrieves the QueryPanel once the tabs are created. Waits until the tabs are created if they are not ready.
     *
     * @return The QueryPanel component.
     */
    QueryPanel getQueryPanel(){
        waitUntilTabsCreated();
        return queryPanel;
    }

    /**
     * Retrieves the RoutePanel once the tabs are created. Waits until the tabs are created if they are not ready.
     *
     * @return The RoutePanel component.
     */
    JTabbedPane getMainPane(){
        waitUntilTabsCreated();
        return mainPane;
    }

    /**
     * Retrieves the RoutePanel once the tabs are created. Waits until the tabs are created if they are not ready.
     *
     * @return The RoutePanel component.
     */
    RoutePanel getRoutePanel(){
        waitUntilTabsCreated();
        return routePanel;
    }

    /**
     * Retrieves the StatisticsPanel once the tabs are created. Waits until the tabs are created if they are not ready.
     *
     * @return The StatisticsPanel component.
     */
    StatisticsPanel getStatisticsPanel(){
        waitUntilTabsCreated();
        return statisticsPanel;
    }
}
