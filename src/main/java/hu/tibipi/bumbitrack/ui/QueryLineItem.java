package hu.tibipi.bumbitrack.ui;

import javax.swing.*;
import java.awt.*;

public class QueryLineItem extends JPanel {

    JComboBox<String> valueCb;
    JComboBox<String> comparatorCb;
    JTextField valueTf;

    public QueryLineItem() {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setMaximumSize(new Dimension(800, 55));

        valueCb = new JComboBox<>(new String[]{"Test1", "Test2"});
        comparatorCb = new JComboBox<>(new String[]{"==", "<="});
        valueTf = new JTextField(12);

        this.add(valueCb);
        this.add(comparatorCb);
        this.add(valueTf);
    }
}
