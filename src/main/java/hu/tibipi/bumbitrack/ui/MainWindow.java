package hu.tibipi.bumbitrack.ui;

import hu.tibipi.bumbitrack.core.Main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainWindow extends JFrame {
    private JTabbedPane mainPane;

    private QueryPanel queryPanel;
    private RoutePanel routePanel;
    private StatisticsPanel statisticsPanel;
    private JPanel settingsPanel;

    private JProgressBar loadingPb;
    private boolean areTabsCreated;
    private final transient Object tabsCreatedLock;

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

    void updateLoadingStatus(int progress){
        loadingPb.setValue(progress);
    }

    void deleteLoadingScreen(){
        this.getContentPane().removeAll();

        waitUntilTabsCreated();

        mainPane.addTab("Query", queryPanel);
        mainPane.addTab("Route", routePanel);
        mainPane.addTab("Statistics", statisticsPanel);
        mainPane.addTab("Settings", settingsPanel);

        this.setContentPane(mainPane);
        this.revalidate();
        this.repaint();
    }

    private void createLoadingScreen() {

        JPanel loadingP = new JPanel();
        loadingP.setLayout(new BoxLayout(loadingP, BoxLayout.Y_AXIS));
        loadingP.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel loadingLb = new JLabel("Loading . . .");
        loadingLb.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadingLb.setAlignmentY(Component.CENTER_ALIGNMENT);
        loadingP.add(Box.createVerticalGlue()); // Add space above the label
        loadingP.add(loadingLb);

        loadingPb = new JProgressBar();
        loadingPb.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadingP.add(loadingPb);
        loadingP.add(Box.createVerticalGlue());

        this.setContentPane(loadingP);
        this.setLocationRelativeTo(null);
    }

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

    QueryPanel getQueryPanel(){
        waitUntilTabsCreated();
        return queryPanel;
    }

    JTabbedPane getMainPane(){
        waitUntilTabsCreated();
        return mainPane;
    }

    RoutePanel getRoutePanel(){
        waitUntilTabsCreated();
        return routePanel;
    }

    StatisticsPanel getStatisticsPanel(){
        waitUntilTabsCreated();
        return statisticsPanel;
    }
}
