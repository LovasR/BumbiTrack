package hu.tibipi.bumbitrack.ui;

import hu.tibipi.bumbitrack.core.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A panel that runs {@link Filter}s on the most recent available {@link Snapshot}
 */
public class QueryPanel extends JPanel {
    JButton runQueryBt;

    private final JPanel queryLinesP;
    private final ArrayList<QueryLineItem> queryLinesStation;
    private final ArrayList<QueryLineItem> queryLinesBike;
    private final JComboBox<String> typeSelectCb;

    private final DefaultTreeModel resultTrModel;

    private enum ChosenType{
        STATION, BIKE
    }

    ChosenType currentlyChosenType = ChosenType.STATION;

    QueryPanel(){
        this.setBorder(new EmptyBorder(20, 20, 20, 20));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        EmptyBorder internalBorder = new EmptyBorder(10, 10, 10, 10);

        queryLinesP = new JPanel();
        queryLinesStation = new ArrayList<>();
        queryLinesBike = new ArrayList<>();

        JPanel typeSelectP = new JPanel();
        typeSelectP.setLayout(new BoxLayout(typeSelectP, BoxLayout.X_AXIS));
        JLabel typeSelectLb = new JLabel("Select a type to query: ");
        typeSelectP.add(typeSelectLb);
        typeSelectCb = new JComboBox<>(new String[] {"Station", "Bike"});
        typeSelectCb.addActionListener(t -> {
            String selected = (String) typeSelectCb.getSelectedItem();
            assert selected != null;

            if(selected.equals("Station")){
                currentlyChosenType = ChosenType.STATION;
            } else if(selected.equals("Bike")){
                currentlyChosenType = ChosenType.BIKE;
            }

            queryLinesP.removeAll();
            for(QueryLineItem queryLineItem : getChosenQueryLines()) {
                queryLinesP.add(queryLineItem);
            }
            queryLinesP.revalidate();
            queryLinesP.repaint();
        });
        typeSelectCb.setBorder(internalBorder);
        typeSelectCb.setMaximumSize(new Dimension(300, 75));
        typeSelectCb.setAlignmentX(Component.LEFT_ALIGNMENT);
        typeSelectP.add(typeSelectCb);
        typeSelectP.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(typeSelectP);
        this.add(Box.createVerticalStrut(24));

        queryLinesP.setLayout(new BoxLayout(queryLinesP, BoxLayout.Y_AXIS));
        queryLinesP.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(queryLinesP);
        this.add(Box.createVerticalStrut(24));

        JButton queryLineAddBt = createAddBt(internalBorder);
        this.add(queryLineAddBt);
        this.add(Box.createVerticalStrut(16));

        JTree resultTr = new JTree();
        resultTr.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultTrModel = new DefaultTreeModel(new DefaultMutableTreeNode("."));
        resultTr.setModel(resultTrModel);
        resultTr.setRootVisible(false);
        JScrollPane resultTrScrollPane = new JScrollPane(resultTr);
        resultTrScrollPane.setLayout(new ScrollPaneLayout());
        resultTrScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(resultTrScrollPane);
        this.add(Box.createVerticalStrut(16));

        runQueryBt = new JButton("Run query");
        runQueryBt.setBorder(internalBorder);
        runQueryBt.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(runQueryBt);
    }

    public List<QueryLineItem> getChosenQueryLines(){
        if(currentlyChosenType == ChosenType.STATION){
            return queryLinesStation;
        } else if (currentlyChosenType == ChosenType.BIKE) {
            return queryLinesBike;
        } else {
            return new ArrayList<>();
        }
    }

    private Class getChosenType(){
        if(currentlyChosenType == ChosenType.STATION){
            return Station.class;
        } else if (currentlyChosenType == ChosenType.BIKE) {
            return Bike.class;
        } else {
            return Object.class;
        }
    }

    private JButton createAddBt(EmptyBorder internalBorder) {
        JButton queryLineAddBt = new JButton("Add");
        queryLineAddBt.addActionListener(t -> {
            QueryLineItem<?> queryLineItem = new QueryLineItem<>(this::removeQueryLine, getChosenType());
            queryLineItem.setBorder(new LineBorder(Color.DARK_GRAY, 2));
            queryLinesP.add(queryLineItem);
            getChosenQueryLines().add(queryLineItem);
            queryLinesP.revalidate();
            queryLinesP.repaint();
        });
        queryLineAddBt.setBorder(internalBorder);
        queryLineAddBt.setSize(new Dimension(800, 60));
        queryLineAddBt.setAlignmentX(Component.LEFT_ALIGNMENT);
        return queryLineAddBt;
    }

    public Void removeQueryLine(QueryLineItem queryLineItem){
        getChosenQueryLines().remove(queryLineItem);
        queryLinesP.remove(queryLineItem);
        this.revalidate();
        this.repaint();

        return null;
    }

    void setQueryRunner(Function<QueryManager, Object> stationQuery, Function<QueryManager, Object> bikeQuery){
        runQueryBt.addActionListener(t -> {
            if(currentlyChosenType == ChosenType.STATION){
                stationQuery.apply(Main.qm);
            } else if(currentlyChosenType == ChosenType.BIKE){
                bikeQuery.apply(Main.qm);
            }
        });
    }

    void setResultsToTreeView(List<Station> results){
        Main.log.info("Updating results...");
        DefaultMutableTreeNode root = ((DefaultMutableTreeNode) resultTrModel.getRoot());
        root.removeAllChildren();

        if(getChosenType() == Station.class){
            for(Station station : results){
                root.add(new DefaultMutableTreeNode(station.getName()));
            }
        } else {
            for(Station station : results){
                DefaultMutableTreeNode stationNode = new DefaultMutableTreeNode(station.getName());
                for(Bike bike : station.getBikes()){
                    stationNode.add(new DefaultMutableTreeNode(bike.getName()));
                }
                root.add(stationNode);
            }
        }

        resultTrModel.reload();
    }

}
