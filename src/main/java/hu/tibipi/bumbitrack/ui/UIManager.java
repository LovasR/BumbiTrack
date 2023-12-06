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

    /**
     * Deletes the loading screen displayed on the main window.
     */
    @Override
    public void deleteLoadingScreen(){
        mainWindow.deleteLoadingScreen();
    }

    /**
     * Updates the loading status displayed on the main window.
     * @param progress The progress value to update the loading status.
     */
    @Override
    public void updateLoadingStatus(int progress) {
        mainWindow.updateLoadingStatus(progress);
    }

    /**
     * Starts the user interface by setting the main window to visible.
     */
    @Override
    public void start() {
        mainWindow.setVisible(true);
    }

    /**
     * Sets the query runners for station and bike queries in the query panel.
     * @param stationQuery The query runner for stations.
     * @param bikeQuery The query runner for bikes.
     */
    @Override
    public void setQueryRunners(Function<QueryManager, Object> stationQuery, Function<QueryManager, Object> bikeQuery) {
        mainWindow.getQueryPanel().setQueryRunner(stationQuery, bikeQuery);
    }

    /**
     * Creates filters from the query lines selected in the user interface.
     * @param <T> The type of the filter.
     * @return A list of filters based on the selected query lines.
     */
    @SuppressWarnings("unchecked")
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

    /**
     * Retrieves the bike name to follow from the user interface.
     * @return The bike name to follow.
     */
    @Override
    public String getBikeNameToFollow() {
        return mainWindow.getRoutePanel().getBikeNameToFollow();
    }

    /**
     * Sets the route query runner in the route panel.
     * @param routeQuery The query runner for routes.
     */
    @Override
    public void setRouteQueryRunner(Function<QueryManager, Void> routeQuery) {
        mainWindow.getRoutePanel().setFollowBt(routeQuery);
    }

    /**
     * Retrieves the route limit from the user interface.
     * @return The route limit value.
     */
    @Override
    public int getRouteLimit(){
        return mainWindow.getRoutePanel().getResultLimit();
    }

    /**
     * Sets the query results in the query panel of the user interface.
     * @param results The results to display in the query panel.
     */
    @Override
    public void setQueryResults(List<Station> results) {
        mainWindow.getQueryPanel().setResultsToTreeView(results);
    }

    /**
     * Sets the route results in the route panel of the user interface.
     * @param resultRoute The route results to display in the route panel.
     */
    @Override
    public void setRouteResults(Route resultRoute){
        mainWindow.getRoutePanel().setResultsToTreeView(resultRoute);
    }

    /**
     * Sets the statistics query runner in the statistics panel.
     * @param statisticsQuery The query runner for statistics.
     */
    @Override
    public void setStatisticsQueryRunner(Function<QueryManager, Void> statisticsQuery) {
        mainWindow.getStatisticsPanel().setQueryRunner(statisticsQuery);
    }

    /**
     * Retrieves the chosen statistics getter from the user interface.
     * @return The chosen statistics getter.
     */
    @Override
    public String getChosenStatisticsGetter() {
        return mainWindow.getStatisticsPanel().getChosenGetter();
    }

    /**
     * Retrieves the statistics limit from the user interface.
     * @return The statistics limit value.
     */
    @Override
    public int getStatisticsLimit() {
        return mainWindow.getStatisticsPanel().getStatisticsLimit();
    }

    /**
     * Sets the statistics data in the statistics panel of the user interface.
     * @param data The data to display in the statistics panel.
     */
    @Override
    public void setStatisticsData(List<Integer> data) {
        mainWindow.getStatisticsPanel().setDataToGraph(data);
    }
}
