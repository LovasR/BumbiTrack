package hu.tibipi.bumbitrack.ui;

import hu.tibipi.bumbitrack.core.Main;
import hu.tibipi.bumbitrack.core.QueryManager;
import hu.tibipi.bumbitrack.core.Route;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public class RoutePanel extends JPanel {

    private final JTextField bikeChooseTf;

    private final JTextField resultLimitTf;

    private final JButton followBt;

    private final DefaultTreeModel resultTrModel;

    /**
     * Constructs a RoutePanel that displays UI components related to routes.
     * It contains fields to choose a bike to follow, set result limits, display routes, and a button to initiate a query.
     */
    RoutePanel(){
        this.setBorder(new EmptyBorder(20, 20, 20, 20));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        EmptyBorder internalBorder = new EmptyBorder(10, 10, 10, 10);

        JPanel bikeChooseP = new JPanel();
        bikeChooseP.setMaximumSize(new Dimension(1000, 55));
        bikeChooseP.setLayout(new BoxLayout(bikeChooseP, BoxLayout.X_AXIS));
        JLabel bikeChooseLb = new JLabel("Choose bike to follow: ");
        bikeChooseLb.setBorder(internalBorder);
        bikeChooseTf = new JTextField(10);
        bikeChooseTf.setBorder(internalBorder);
        JLabel resultLimitLb = new JLabel("Set limit: ");
        resultLimitLb.setBorder(internalBorder);
        resultLimitTf = new JTextField("1000", 5);
        resultLimitTf.setBorder(internalBorder);
        bikeChooseP.add(bikeChooseLb);
        bikeChooseP.add(bikeChooseTf);
        bikeChooseP.add(resultLimitLb);
        bikeChooseP.add(resultLimitTf);
        bikeChooseP.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(bikeChooseP);
        this.add(Box.createVerticalStrut(16));

        followBt = new JButton("Show Route");
        followBt.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(followBt);
        this.add(Box.createVerticalStrut(16));

        JTree resultTr = new JTree();
        resultTrModel = new DefaultTreeModel(new DefaultMutableTreeNode("."));
        resultTr.setModel(resultTrModel);
        resultTr.setRootVisible(false);
        JScrollPane resultTrScrollPane = new JScrollPane(resultTr);
        resultTrScrollPane.setLayout(new ScrollPaneLayout());
        resultTrScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(resultTrScrollPane);
    }

    /**
     * Associates an action with the "Follow" button, triggering a route query execution upon button click.
     * @param routeQuery The function responsible for executing the route query.
     */
    public void setFollowBt(Function<QueryManager, Void> routeQuery){
        followBt.addActionListener(t -> routeQuery.apply(Main.qm));
    }

    /**
     * Retrieves the bike name to follow entered in the text field.
     * @return The bike name entered in the text field for route following.
     */
    String getBikeNameToFollow(){
        return bikeChooseTf.getText();
    }

    /**
     * Retrieves the result limit set by the user in the text field.
     * @return The limit for the number of results to be displayed.
     */
    int getResultLimit(){
        return Integer.parseInt(resultLimitTf.getText());
    }

    /**
     * Displays the results of the obtained route in the tree view.
     * @param resultRoute The route obtained from the query to be displayed in the tree view.
     */
    void setResultsToTreeView(Route resultRoute){
        Main.log.info("Showing route...");
        DefaultMutableTreeNode root = ((DefaultMutableTreeNode) resultTrModel.getRoot());
        root.removeAllChildren();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        int lengthOfTimestamps = "[yyyy-MM-dd HH:mm:ss]    [yyyy-MM-dd HH:mm:ss]".length();

        int longestNameOnRoute = getLongestStationNameOnRoute(resultRoute);
        for(Route.RouteItem routeItem : resultRoute.getStops()){

            if(routeItem instanceof Route.RouteStop routeStop) {
                root.add(new DefaultMutableTreeNode(createRouteStopLabel(routeStop, formatter, longestNameOnRoute)));
            } else {
                root.add(new DefaultMutableTreeNode(createRouteGapLabel((Route.RouteGap) routeItem, longestNameOnRoute, lengthOfTimestamps)));
            }
        }

        resultTrModel.reload();
    }

    /**
     * Retrieves the length of the longest station name in the provided route.
     * @param route The route containing station information.
     * @return The length of the longest station name.
     */
    private int getLongestStationNameOnRoute(Route route) {
        int currentMax = "In transit".length();
        for (Route.RouteItem routeItem : route.getStops()) {
            if(routeItem instanceof Route.RouteStop routeStop) {
                int stationNameLength = routeStop.getStation().getName().length();
                if (stationNameLength > currentMax)
                    currentMax = stationNameLength;
            }
        }
        return currentMax;
    }

    /**
     * Creates a label string for a route stop in a specific format.
     * @param routeStop The route stop for which the label is to be generated.
     * @param formatter The DateTimeFormatter used for formatting timestamps.
     * @param longestNameLength The length of the longest station name.
     * @return The formatted label string for the route stop.
     */
    private String createRouteStopLabel(Route.RouteStop routeStop, DateTimeFormatter formatter, int longestNameLength){
        String startTimestamp = "[" + formatter.format(routeStop.getStart()) + "]  ";
        String name = routeStop.getStation().getName();
        if(name.isEmpty()){
            name = "In transit";
        }
        String fillerSpace = " ";
        String endTimestamp = "  [" + formatter.format(routeStop.getEnd()) + "]";

        return startTimestamp + name + fillerSpace.repeat(longestNameLength - name.length()) + endTimestamp;
    }

    /**
     * Converts an integer to a two-character string representation.
     * @param num The integer to be converted.
     * @return A two-character string representation of the integer.
     */
    private String intToTwoCharacter(long num){
        return (num < 10 ? "0" + num : String.valueOf(num));
    }

    /**
     * Creates a label string for a route gap in a specific format.
     * @param routeGap The route gap for which the label is to be generated.
     * @param longestNameLength The length of the longest station name.
     * @param lengthOfTimestamps The length of the timestamps in the label.
     * @return The formatted label string for the route gap.
     */
    private String createRouteGapLabel(Route.RouteGap routeGap, int longestNameLength, int lengthOfTimestamps){
        long days = routeGap.getDuration().toDays();
        long hours = routeGap.getDuration().toHours();

        String durationString = "";
        if(days > 0)
            durationString += intToTwoCharacter(days) + " day" + (days > 1 ? "s" : "");
        if(hours > 0)
            durationString += intToTwoCharacter(hours) + " hour" + (hours > 1 ? "s" : "");

        String fillerSpace = "―";
        String header = "Gap of ";
        //filler num rounded so it shift left rather than left
        int numOfFiller = Math.round((float)
                (longestNameLength + lengthOfTimestamps - durationString.length() - header.length()) / 2);

        String resultString = fillerSpace.repeat(numOfFiller) + header + durationString;
        return resultString +
                fillerSpace.repeat(lengthOfTimestamps + longestNameLength - resultString.length());
    }
}
