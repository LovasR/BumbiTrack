package hu.tibipi.bumbitrack.ui;

import hu.tibipi.bumbitrack.core.Main;
import hu.tibipi.bumbitrack.core.QueryManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Function;

public class QueryPanel extends JPanel {
    JButton testButton;
    QueryPanel(){
        JLabel hellow = new JLabel("Hello World");
        this.add(hellow);
        testButton = new JButton("Test query");
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
