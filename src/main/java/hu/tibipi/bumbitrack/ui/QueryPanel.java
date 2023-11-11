package hu.tibipi.bumbitrack.ui;

import hu.tibipi.bumbitrack.core.Main;
import hu.tibipi.bumbitrack.core.QueryManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.function.Function;

public class QueryPanel extends JPanel {
    JButton testButton;

    private final JPanel queryLinesP;
    private final ArrayList<QueryLineItem> queryLines;
    private String selectedOption;
    private JLabel outputLb;
    private JComboBox<String> optionsCb;
    private JButton queryLineAddBt;

    QueryPanel(){
        this.setBorder(new EmptyBorder(20, 20, 20, 20));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        EmptyBorder internalBorder = new EmptyBorder(10, 10, 10, 10);

        queryLinesP = new JPanel();
        queryLinesP.setLayout(new BoxLayout(queryLinesP, BoxLayout.Y_AXIS));
        queryLinesP.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(queryLinesP);
        this.add(Box.createVerticalStrut(24));

        queryLines = new ArrayList<>();

        queryLineAddBt = new JButton("Add");
        queryLineAddBt.addActionListener(t -> {
            QueryLineItem queryLineItem = new QueryLineItem();
            queryLineItem.setBorder(new LineBorder(Color.DARK_GRAY, 2));
            queryLinesP.add(queryLineItem);
            queryLines.add(queryLineItem);
            queryLinesP.revalidate();
            queryLinesP.repaint();
        });
        queryLineAddBt.setBorder(internalBorder);
        this.add(queryLineAddBt);
        this.add(Box.createVerticalStrut(16));

        testButton = new JButton("Test query");
        testButton.setBorder(internalBorder);
        this.add(testButton);
    }

    void setTestButtonActionListener(Function<QueryManager, Object> actionListener){
        testButton.addActionListener(new ClickActionListener<>(actionListener, Main.qm));
    }

    static class ClickActionListener<T> implements ActionListener {
        Function<T, Object> function;
        T t;
        ClickActionListener(Function<T, Object> function, T instance){
            this.function = function;
            t = instance;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            function.apply(t);
        }
    }



}
