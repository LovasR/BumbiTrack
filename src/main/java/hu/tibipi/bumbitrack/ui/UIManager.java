package hu.tibipi.bumbitrack.ui;

import hu.tibipi.bumbitrack.core.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class UIManager implements AppUI {
    MainWindow mainWindow;

    public UIManager(){
        mainWindow = new MainWindow();
    }

    @Override
    public void deleteLoadingScreen(){
        mainWindow.deleteLoadingScreen();
    }

    @Override
    public void updateLoadingStatus(int progress) {
        mainWindow.updateLoadingStatus(progress);
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
        List<QueryLineItem> queryLineItems;
        switch (mainWindow.getMainPane().getSelectedIndex()){
            case 0:
                queryLineItems = mainWindow.getQueryPanel().getChosenQueryLines();
                break;
            case 2:
                queryLineItems = mainWindow.getStatisticsPanel().getQueryLines();
                break;
            default:
                throw new IllegalStateException();
        }
        for(QueryLineItem<T> queryLineItem : queryLineItems){
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
    public int getRouteLimit(){
        return mainWindow.getRoutePanel().getResultLimit();
    }

    @Override
    public void setQueryResults(List<Station> results) {
        mainWindow.getQueryPanel().setResultsToTreeView(results);
    }

    @Override
    public void setRouteResults(Route resultRoute){
        mainWindow.getRoutePanel().setResultsToTreeView(resultRoute);
    }

    @Override
    public void setStatisticsQueryRunner(Function<QueryManager, Void> statisticsQuery) {
        mainWindow.getStatisticsPanel().setQueryRunner(statisticsQuery);
    }

    @Override
    public String getChosenStatisticsGetter() {
        return mainWindow.getStatisticsPanel().getChosenGetter();
    }

    @Override
    public void setStatisticsData(List<Integer> data) {
        mainWindow.getStatisticsPanel().setDataToGraph(data);
    }
}
