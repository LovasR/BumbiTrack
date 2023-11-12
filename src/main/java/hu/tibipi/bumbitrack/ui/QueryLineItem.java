package hu.tibipi.bumbitrack.ui;

import javax.swing.*;
import java.awt.*;

public class QueryLineItem extends JPanel {

    private JComboBox<String> valueCb;
    private JComboBox<String> comparatorCb;
    private JTextField valueTf;
    private JButton deleteBt;

    public QueryLineItem(QueryPanel queryPanel) {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setMaximumSize(new Dimension(800, 55));

        valueCb = new JComboBox<>(new String[]{"Test1", "Test2"});
        comparatorCb = new JComboBox<>(new String[]{"==", "<="});
        valueTf = new JTextField(12);
        deleteBt = new JButton("Delete");
        deleteBt.addActionListener(t -> queryPanel.removeQueryLine(this));

        this.add(valueCb);
        this.add(comparatorCb);
        this.add(valueTf);
        this.add(deleteBt);
    }
}
