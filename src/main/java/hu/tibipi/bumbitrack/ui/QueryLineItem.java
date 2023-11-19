package hu.tibipi.bumbitrack.ui;

import hu.tibipi.bumbitrack.core.*;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class QueryLineItem<T> extends JPanel {
    private final Class<T> queriedType;
    private final JComboBox<String> valueCb;
    private final JComboBox<String> comparatorCb;
    private final JTextField valueTf;

    private final String[] stationOptions =
            {"available", "contains", "capacity", "distance", "is bike", "currbikes"};
    private final String[] bikeOptions =
            {"contains"};

    public QueryLineItem(Function<QueryLineItem, Void> removeFunction, Class<T> queriedType) {
        this.queriedType = queriedType;
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setMaximumSize(new Dimension(800, 55));

        if(queriedType == Station.class) {
            valueCb = new JComboBox<>(stationOptions);
        } else {
            valueCb = new JComboBox<>(bikeOptions);
        }

        comparatorCb = new JComboBox<>(new String[]{"==", "<=", ">=", "not"});

        valueTf = new JTextField(12);

        JButton deleteBt = new JButton("Delete");

        deleteBt.addActionListener(t -> removeFunction.apply(this));

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

    private boolean booleanParse(String string){
        string = string.toLowerCase();
        switch (string){
            case "true",  "1":
                return true;

            case "false", "0":
            default:
                return false;
        }
    }

    private Filter<T> toFilterInternal(Class<T> classType, String label, String comparedValue, String comparatorLabel){

        if(classType == Station.class) {
            switch (label) {
                case "available":
                    Function<Station, Integer> getterFunction1 = Main.getStationGetterFunction("getBikesAvailable");
                    return (Filter<T>) new GeneralFilter<>(
                            getterFunction1,
                            permissiveIntParse(comparedValue),
                            stringToPredicate(comparatorLabel)
                    );
                case "contains":
                    Function<Station, String> getterFunction2 = Main.getStationGetterFunction("getName");
                    return (Filter<T>) new NameFilter<>(
                            getterFunction2,
                            comparedValue
                    );
                case "capacity":
                    Function<Station, Integer> getterFunction3 = Main.getStationGetterFunction("getBikeCapacity");
                    return (Filter<T>) new GeneralFilter<>(
                            getterFunction3,
                            permissiveIntParse(comparedValue),
                            stringToPredicate(comparatorLabel)
                    );
                case "distance":
                    break;
                case "is_bike":
                    Function<Station, Boolean> getterFunction5 = Main.getStationGetterFunction("isBike");
                    return (Filter<T>) new GeneralFilter<>(
                            getterFunction5,
                            booleanParse(comparedValue),
                            stringToPredicate(comparatorLabel)
                    );
                case "currbikes":
                    Function<Station, Integer> getterFunction6 =  Main.getStationGetterFunction("getBikesNumber");
                    return (Filter<T>) new GeneralFilter<>(
                            getterFunction6,
                            permissiveIntParse(comparedValue),
                            stringToPredicate(comparatorLabel)
                    );
                default:
                    throw new IllegalArgumentException("Getter selection error");
            }
        } else if(classType == Bike.class){
            switch (label){
                case "contains":
                    Function<Bike, String> getterFunction1 = Main.getBikeGetterFunction("getName");
                    return (Filter<T>) new NameFilter<>(
                        getterFunction1,
                        comparedValue
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



}
