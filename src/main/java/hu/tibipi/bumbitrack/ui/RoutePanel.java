package hu.tibipi.bumbitrack.ui;

import hu.tibipi.bumbitrack.core.Main;
import hu.tibipi.bumbitrack.core.QueryManager;
import hu.tibipi.bumbitrack.core.Station;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;

public class RoutePanel extends JPanel {

    private final JTextField bikeChooseTf;

    private final JButton followBt;

    private final DefaultTreeModel resultTrModel;

    RoutePanel(){
        this.setBorder(new EmptyBorder(20, 20, 20, 20));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        EmptyBorder internalBorder = new EmptyBorder(10, 10, 10, 10);

        JPanel bikeChooseP = new JPanel();
        bikeChooseP.setMaximumSize(new Dimension(800, 55));
        bikeChooseP.setLayout(new BoxLayout(bikeChooseP, BoxLayout.X_AXIS));
        JLabel bikeChooseLb = new JLabel("Choose bike to follow: ");
        bikeChooseLb.setBorder(internalBorder);
        bikeChooseTf = new JTextField(10);
        bikeChooseTf.setBorder(internalBorder);
        bikeChooseP.add(bikeChooseLb);
        bikeChooseP.add(bikeChooseTf);
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

    void setResultsToTreeView(List<Station> results, List<LocalDateTime> times){
        Main.log.info("Showing route...");
        DefaultMutableTreeNode root = ((DefaultMutableTreeNode) resultTrModel.getRoot());
        root.removeAllChildren();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String lastEntryLabel = "";
        for(int i = 0; i < results.size(); i++){
            String entryLabel = results.get(i).getName();

            if(i == results.size() - 1) {
                createResultNode(root, entryLabel, times.get(i), formatter);
                break;
            }
            if(!entryLabel.equals(lastEntryLabel) || !results.get(i + 1).getName().equals(entryLabel)){
                createResultNode(root, entryLabel, times.get(i), formatter);
            }

            lastEntryLabel = entryLabel;
        }


        resultTrModel.reload();
    }

    private void createResultNode(DefaultMutableTreeNode root, String entryLabel, LocalDateTime time, DateTimeFormatter formatter){
        if(entryLabel.isEmpty())
            entryLabel = "In transit or out of system";

        root.add(new DefaultMutableTreeNode(
                "[" + time.format(formatter) + "]  "
                        + entryLabel));
    }
}
