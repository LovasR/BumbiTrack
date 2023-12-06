package hu.tibipi.bumbitrack.ui;

import hu.tibipi.bumbitrack.core.*;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * A representation of a {@link Filter} in UI.
 * @param <T> template parameter to Filter
 */
public class QueryLineItem<T> extends JPanel {
    private final Class<T> queriedType;
    private final JComboBox<String> valueCb;
    private final JComboBox<String> comparatorCb;
    private final JTextField valueTf;

    private final String[] stationOptions =
            {"available", "contains", "capacity", "distance", "is bike", "currbikes"};
    private final String[] bikeOptions =
            {"contains"};

    /**
     * Constructor to QueryLineItem.
     *
     * @param removeFunction the remove function to parent to call on remove.
     * @param queriedType type of {@link Filter} this line represents.
     */
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

    /**
     * Converts the current state of the filter configuration into a {@link Filter} object.
     * This method acts as a handle to improve code readability.
     *
     * @return A {@link Filter} object configured based on the current settings.
     */
    public Filter<T> toFilter(){
        return toFilterInternal(
                queriedType,
                (String) valueCb.getSelectedItem(),
                valueTf.getText(),
                (String) comparatorCb.getSelectedItem()
        );
    }

    /**
     * Fault-tolerant string to int.
     *
     * @param string String to be parsed.
     * @return the string parsed or 0 on error.
     */
    private int permissiveIntParse(String string){
        try {
            return Integer.parseInt(string);
        } catch(NumberFormatException e){
            return 0;
        }
    }

    /**
     * Fault-tolerant string to boolean.
     *
     * @param string String to be parsed.
     * @return the string parsed or 0 on error.
     */
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

    /**
     * The internal part of toFilter. Creates a filter based on the label specified, along with other elements from GUI.
     *
     * @param classType type of filter to create.
     * @param label label that specifies the getter to use.
     * @param comparedValue string representation of the value to compare to.
     * @param comparatorLabel string representation of the comparator to use.
     * @return returns a filter in the specified class.
     */
    @SuppressWarnings("unchecked")
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
                    return (Filter<T>) new DistanceFilter(
                            SnapshotManager.getSnapshots().get(SnapshotManager.getSnapshots().size() - 1).getCity().getPlace(),
                            permissiveIntParse(comparedValue)
                    );
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

    /**
     * Converts a string comparator to a BiPredicate.
     *
     * @param label a string of the comparator.
     * @return a BiPredicate naturally associated with the string.
     * @param <C> the type to compare.
     */
    @SuppressWarnings("unchecked")
    private <C> BiPredicate<C, C> stringToPredicate(String label){
        return switch (label) {
            case "==" -> Object::equals;
            case "<=" -> (c1, c2) -> ((Comparable<C>) c1).compareTo(c2) <= 0;
            case ">=" -> (c1, c2) -> ((Comparable<C>) c1).compareTo(c2) >= 0;
            case "not" -> (c1, c2) -> !c1.equals(c2);
            default -> throw new IllegalArgumentException("Comparator selection error");
        };
    }



}
