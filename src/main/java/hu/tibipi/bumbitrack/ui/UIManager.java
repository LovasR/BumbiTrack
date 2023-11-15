package hu.tibipi.bumbitrack.ui;

import hu.tibipi.bumbitrack.core.*;

import java.time.LocalDateTime;
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

    @Override
    public String getBikeNameToFollow() {
        return mainWindow.getRoutePanel().getBikeNameToFollow();
    }

    @Override
    public void setRouteQueryRunner(Function<QueryManager, Void> routeQuery) {
        mainWindow.getRoutePanel().setFollowBt(routeQuery);
    }


    @Override
    public void setResultsToCurrent(List<Station> results, List<LocalDateTime> times) {
        switch(mainWindow.getMainPane().getSelectedIndex()){
            case 0:
                //QueryPanel
                mainWindow.getQueryPanel().setResultsToTreeView(results);
                break;
            case 1:
                //RoutePanel
                mainWindow.getRoutePanel().setResultsToTreeView(results, times);
                break;
            default:
                return;
        }
    }


}
