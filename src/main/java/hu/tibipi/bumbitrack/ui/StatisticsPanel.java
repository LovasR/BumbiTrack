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
    private final JTextField limitTf;
    private static final String[] getterOptions = {"Available bikes", "Bike capacity", "Bikes"};

    private final JButton runQueryBt;

    /**
     * Constructs a StatisticsPanel object, initializing the GUI components for displaying statistics.
     * The panel includes components for defining query lines, setting attribute aggregation options,
     * specifying limits, running queries, and displaying the resulting line graph.
     */
    StatisticsPanel(){
        this.setBorder(new EmptyBorder(20, 20, 20, 20));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        EmptyBorder internalBorder = new EmptyBorder(10, 10, 10, 10);

        queryLineItems = new ArrayList<>();

        queryLinesP = new JPanel();
        queryLinesP.setLayout(new BoxLayout(queryLinesP, BoxLayout.Y_AXIS));
        queryLinesP.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(queryLinesP);
        this.add(Box.createVerticalStrut(16));

        this.add(createAddBt(internalBorder));
        this.add(Box.createVerticalStrut(16));

        JPanel statisticsGridP = new JPanel();
        statisticsGridP.setLayout(new GridLayout(2, 2));
        JLabel getterLb = new JLabel("Set attribute to aggregate:");
        getterLb.setBorder(internalBorder);
        getterLb.setAlignmentX(Component.LEFT_ALIGNMENT);
        getterCb = new JComboBox<>(getterOptions);
        getterCb.setMaximumSize(new Dimension(getterCb.getWidth(), 10));
        getterCb.setAlignmentX(Component.LEFT_ALIGNMENT);
        getterCb.setBorder(internalBorder);
        statisticsGridP.add(getterLb);
        statisticsGridP.add(getterCb);
        JLabel limitLb = new JLabel("Set limit:");
        limitLb.setBorder(internalBorder);
        limitLb.setAlignmentX(Component.LEFT_ALIGNMENT);
        limitTf = new JTextField("1000", 5);
        limitTf.setBorder(internalBorder);
        limitTf.setSize(new Dimension(400, 10));
        statisticsGridP.add(limitLb);
        statisticsGridP.add(limitTf);
        statisticsGridP.setAlignmentX(Component.LEFT_ALIGNMENT);
        statisticsGridP.setMaximumSize(new Dimension(1000, 100));
        this.add(statisticsGridP);
        this.add(Box.createVerticalStrut(24));

        runQueryBt = new JButton("Run");
        runQueryBt.setBorder(internalBorder);
        runQueryBt.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(runQueryBt);
        this.add(Box.createVerticalStrut(16));

        lineGraphPanel = new LineGraphPanel(getterLb.getForeground());
        lineGraphPanel.setBorder(internalBorder);
        lineGraphPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(lineGraphPanel);
    }

    /**
     * Creates and configures a JButton to add a new query line item when clicked.
     *
     * @param internalBorder An EmptyBorder object defining internal padding for the button.
     * @return The configured JButton for adding a new query line item.
     */
    private JButton createAddBt(EmptyBorder internalBorder) {
        JButton queryLineAddBt = new JButton("Add filter");
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

    /**
     * Sets the ActionListener for the 'Run' button to execute the statistics query.
     *
     * @param statisticsQuery A Function taking a QueryManager and returning Void, representing the statistics query.
     */
    public void setQueryRunner(Function<QueryManager, Void> statisticsQuery){
        runQueryBt.addActionListener(t -> statisticsQuery.apply(Main.qm));
    }

    /**
     * Removes the specified query line item from the panel.
     *
     * @param queryLineItem The QueryLineItem object to be removed.
     * @return Always returns null after removing the query line item.
     */
    public Void removeQueryLine(QueryLineItem queryLineItem){
        getQueryLines().remove(queryLineItem);
        queryLinesP.remove(queryLineItem);
        this.revalidate();
        this.repaint();

        return null;
    }

    /**
     * Retrieves the list of query line items present in the StatisticsPanel.
     *
     * @return The list of QueryLineItem objects representing the query lines.
     */
    public List<QueryLineItem> getQueryLines(){
        return queryLineItems;
    }

    /**
     * Gets the chosen getter method based on the selected item in the JComboBox.
     *
     * @return A String representing the chosen getter method for attribute aggregation.
     * @throws IllegalStateException if an unexpected item is selected in the JComboBox.
     */
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

    int getStatisticsLimit(){
        try {
            return Integer.parseInt(limitTf.getText());
        } catch (NumberFormatException e){
            Main.log.warning("Non number in textfield, defaulting to 1000");
            return 1000;
        }
    }

    public void setDataToGraph(List<Integer> data){
        lineGraphPanel.setValues(data);
    }
}
