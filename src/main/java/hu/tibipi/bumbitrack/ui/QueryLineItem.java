package hu.tibipi.bumbitrack.ui;

import hu.tibipi.bumbitrack.core.*;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class QueryLineItem<T> extends JPanel {
    private final Class<T> queriedType;
    private final JComboBox<String> valueCb;
    private final JComboBox<String> comparatorCb;
    private final JTextField valueTf;

    public QueryLineItem(QueryPanel queryPanel, Class<T> queriedType) {
        this.queriedType = queriedType;
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setMaximumSize(new Dimension(800, 55));

        valueCb = new JComboBox<>(new String[]{"available", "contains", "capacity", "distance", "is bike", "currbikes"});

        comparatorCb = new JComboBox<>(new String[]{"==", "<=", ">=", "not"});

        valueTf = new JTextField(12);

        JButton deleteBt = new JButton("Delete");

        deleteBt.addActionListener(t -> queryPanel.removeQueryLine(this));

        this.add(valueCb);
        this.add(comparatorCb);
        this.add(valueTf);
        this.add(deleteBt);
    }

    public Filter<T> toFilter(){
        //this is only a handle to improve readability
        return toFilterInternal(
                queriedType,
                (String) valueCb.getSelectedItem(),
                valueTf.getText(),
                (String) comparatorCb.getSelectedItem()
        );
    }

    private int permissiveIntParse(String string){
        try {
            return Integer.parseInt(string);
        } catch(NumberFormatException e){
            return 0;
        }
    }

    private Filter<T> toFilterInternal(Class<T> classType, String label, String comparedValue, String comparatorLabel){

        if(classType == Station.class) {
            switch (label) {
                case "available":
                    return new GeneralFilter<>(
                            createGetterFunction(classType, "getBikesAvailable"),
                            permissiveIntParse(comparedValue),
                            stringToPredicate(comparatorLabel)
                    );
                case "contains":
                    return new GeneralFilter<>(
                            createGetterFunction(classType, "getName"),
                            comparedValue,
                            stringToPredicate(comparatorLabel)
                    );
                case "capacity":
                    return new GeneralFilter<>(
                            createGetterFunction(classType, "getBikeCapacity"),
                            permissiveIntParse(comparedValue),
                            stringToPredicate(comparatorLabel)
                    );
                case "distance":
                    break;
                case "is_bike":
                    return new GeneralFilter<>(
                            createGetterFunction(classType, "isBike"),
                            permissiveIntParse(comparedValue),
                            stringToPredicate(comparatorLabel)
                    );
                case "currbikes":
                    return new GeneralFilter<>(
                            createGetterFunction(classType, "getBikesNumber"),
                            permissiveIntParse(comparedValue),
                            stringToPredicate(comparatorLabel)
                    );
                default:
                    throw new IllegalArgumentException("Getter selection error");
            }
        }
        return null;
    }

    private <C> BiPredicate<C, C> stringToPredicate(String label){
        switch(label){
            case "==":
                return Object::equals;
            case "<=":
                return (c1, c2) -> ((Comparable<C>) c1).compareTo(c2) <= 0;
            case ">=":
                return (c1, c2) -> ((Comparable<C>) c1).compareTo(c2) >= 0;
            case "not":
                return (c1, c2) -> !c1.equals(c2);
            default:
                throw new IllegalArgumentException("Comparator selection error");
        }
    }

    private static LogRecord generateLambdaErrorRecord(Exception e){
        return new LogRecord(Level.SEVERE, e.toString() + "\n\nEXITING");
    }

    private static <T, R> Function<T, R> createGetterFunction(Class<T> classType, String propertyName) {
        return (T input) -> {
            try {
                Method method = classType.getMethod(propertyName);
                Object result = method.invoke(input);
                return (R) result;
            } catch (Exception e) {
                Main.log.log(generateLambdaErrorRecord(e));
                System.exit(1);
                return null;
            }
        };
    }
}
