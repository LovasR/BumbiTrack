package hu.tibipi.bumbitrack.ui;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    JTabbedPane mainPane;

    private QueryPanel queryPanel;
    private JPanel routePanel;
    private JPanel statisticsPanel;
    private JPanel settingsPanel;

    MainWindow(){
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(new Dimension(1200, 800));
        this.setPreferredSize(new Dimension(800, 600));

        queryPanel = new QueryPanel();
        routePanel = new JPanel();
        statisticsPanel = new JPanel();
        settingsPanel = new JPanel();

        mainPane = new JTabbedPane();
        mainPane.addTab("Query", queryPanel);
        mainPane.addTab("Route", routePanel);
        mainPane.addTab("Statistics", statisticsPanel);
        mainPane.addTab("Settings", settingsPanel);

        this.add(mainPane);
    }

    QueryPanel getQueryPanel(){
        return queryPanel;
    }
}
