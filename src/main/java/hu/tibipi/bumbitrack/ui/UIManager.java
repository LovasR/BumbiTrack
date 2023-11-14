package hu.tibipi.bumbitrack.ui;

import hu.tibipi.bumbitrack.core.AppUI;
import hu.tibipi.bumbitrack.core.Filter;
import hu.tibipi.bumbitrack.core.QueryManager;

import java.util.ArrayList;
import java.util.List;
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
    public void setQueryRunners(Function<QueryManager, Object> stationQuery, Function<QueryManager, Object> bikeQuery) {
        mainWindow.getQueryPanel().setQueryRunner(stationQuery, bikeQuery);
    }

    @Override
    public <T> List<Filter<T>> createFiltersFromQueryLines(){
        ArrayList<Filter<T>> filters = new ArrayList<>();
        for(QueryLineItem<T> queryLineItem : mainWindow.getQueryPanel().getChosenQueryLines()){
            filters.add(queryLineItem.toFilter());
        }
        return filters;
    }
}
