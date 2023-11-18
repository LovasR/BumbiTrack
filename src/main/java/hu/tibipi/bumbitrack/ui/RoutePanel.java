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
        resultLimitTf = new JTextField("100", 5);
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

    public void setFollowBt(Function<QueryManager, Void> routeQuery){
        followBt.addActionListener(t -> routeQuery.apply(Main.qm));
    }

    String getBikeNameToFollow(){
        return bikeChooseTf.getText();
    }

    int getResultLimit(){
        return Integer.parseInt(resultLimitTf.getText());
    }

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

    private String intToTwoCharacter(long num){
        return (num < 10 ? "0" + num : String.valueOf(num));
    }

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
