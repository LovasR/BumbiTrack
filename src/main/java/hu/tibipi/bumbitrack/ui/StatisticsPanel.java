package hu.tibipi.bumbitrack.ui;

import hu.tibipi.bumbitrack.core.Main;
import hu.tibipi.bumbitrack.core.QueryManager;
import hu.tibipi.bumbitrack.core.Station;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class StatisticsPanel extends JPanel {

    private final LineGraphPanel lineGraphPanel;

    private final List<QueryLineItem> queryLineItems;

    private final JPanel queryLinesP;

    private final JComboBox<String> getterCb;
    private static final String[] getterOptions = {"Available bikes", "Bike capacity", "Bikes"};

    private final JButton runQueryBt;

    StatisticsPanel(){
        this.setBorder(new EmptyBorder(20, 20, 20, 20));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        EmptyBorder internalBorder = new EmptyBorder(10, 10, 10, 10);

        queryLineItems = new ArrayList<>();

        JLabel testLb = new JLabel("Test");
        testLb.setBorder(internalBorder);
        testLb.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(testLb);

        queryLinesP = new JPanel();
        queryLinesP.setLayout(new BoxLayout(queryLinesP, BoxLayout.Y_AXIS));
        queryLinesP.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(queryLinesP);
        this.add(Box.createVerticalStrut(16));

        this.add(createAddBt(internalBorder));
        this.add(Box.createVerticalStrut(16));

        JLabel getterLb = new JLabel("Set which attribute to aggregate:");
        getterLb.setBorder(internalBorder);
        getterCb = new JComboBox<>(getterOptions);
        getterCb.setMaximumSize(new Dimension(800, 10));
        getterCb.setAlignmentX(Component.LEFT_ALIGNMENT);
        getterCb.setBorder(internalBorder);
        this.add(getterLb);
        this.add(getterCb);

        runQueryBt = new JButton("Run");
        runQueryBt.setBorder(internalBorder);
        runQueryBt.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(runQueryBt);

        lineGraphPanel = new LineGraphPanel(testLb.getForeground());
        lineGraphPanel.setBorder(internalBorder);
        lineGraphPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(lineGraphPanel);
    }

    private JButton createAddBt(EmptyBorder internalBorder) {
        JButton queryLineAddBt = new JButton("Add");
        queryLineAddBt.addActionListener(t -> {
            QueryLineItem<?> queryLineItem = new QueryLineItem<>(this::removeQueryLine, Station.class);
            queryLineItem.setBorder(new LineBorder(Color.DARK_GRAY, 2));
            queryLinesP.add(queryLineItem);
            getQueryLines().add(queryLineItem);
            queryLinesP.revalidate();
            queryLinesP.repaint();
        });
        queryLineAddBt.setBorder(internalBorder);
        queryLineAddBt.setSize(new Dimension(800, 60));
        queryLineAddBt.setAlignmentX(Component.LEFT_ALIGNMENT);
        return queryLineAddBt;
    }

    public void setQueryRunner(Function<QueryManager, Void> statisticsQuery){
        runQueryBt.addActionListener(t -> statisticsQuery.apply(Main.qm));
    }

    public Void removeQueryLine(QueryLineItem queryLineItem){
        getQueryLines().remove(queryLineItem);
        queryLinesP.remove(queryLineItem);
        this.revalidate();
        this.repaint();

        return null;
    }

    public List<QueryLineItem> getQueryLines(){
        return queryLineItems;
    }

    public String getChosenGetter(){
        switch ((String) Objects.requireNonNull(getterCb.getSelectedItem())){
            case "Available bikes":
                return "getBikesAvailable";
            case "Bike capacity":
                return "getBikesCapacity";
            case "Bikes":
                return "getBikesNumber";
            default:
                throw new IllegalStateException();
        }
    }

    public void setDataToGraph(List<Integer> data){
        lineGraphPanel.setValues(data);
    }
}
