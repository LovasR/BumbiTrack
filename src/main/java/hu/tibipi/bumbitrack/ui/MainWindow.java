package hu.tibipi.bumbitrack.ui;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    JTabbedPane mainPane;

    JPanel queryPanel;
    JPanel routePanel;
    JPanel statisticsPanel;
    JPanel settingsPanel;

    MainWindow(){
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(new Dimension(1200, 800));
        this.setPreferredSize(new Dimension(800, 600));

        queryPanel = new JPanel();
        routePanel = new JPanel();
        statisticsPanel = new JPanel();
        settingsPanel = new JPanel();

        mainPane = new JTabbedPane();
        mainPane.addTab("Query", queryPanel);
        mainPane.addTab("Route", routePanel);
        mainPane.addTab("Statistics", statisticsPanel);
        mainPane.addTab("Settings", settingsPanel);

        this.add(mainPane);

        createQueryPanel();
    }

    void createQueryPanel(){
        JLabel hellow = new JLabel("Hello World");
        queryPanel.add(hellow);
    }

}
