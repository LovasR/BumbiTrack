package hu.tibipi.bumbitrack.ui;

import hu.tibipi.bumbitrack.core.AppUI;
import hu.tibipi.bumbitrack.core.QueryManager;

import java.util.function.Function;

public class UIManager implements AppUI {
    MainWindow mainWindow;

    public UIManager(){
        mainWindow = new MainWindow();
    }

    @Override
    public void start() {
        mainWindow.setVisible(true);
    }

    @Override
    public void addTestQueryActionListener(Function<QueryManager, Object> queryAction) {
        mainWindow.getQueryPanel().setTestButtonActionListener(queryAction);
    }
}
