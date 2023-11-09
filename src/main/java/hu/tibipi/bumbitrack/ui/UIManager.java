package hu.tibipi.bumbitrack.ui;

import hu.tibipi.bumbitrack.core.AppUI;

public class UIManager implements AppUI {
    MainWindow mainWindow;

    public UIManager(){
        mainWindow = new MainWindow();
    }

    @Override
    public void start() {
        mainWindow.setVisible(true);
    }
}
