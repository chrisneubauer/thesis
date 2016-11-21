package de.cneubauer.learning;

import java.awt.*;
import java.util.Optional;

/**
 * Created by Christoph Neubauer on 21.11.2016.
 */
public interface DataSample {

    /**
     * Get sample data value from specified column.
     *
     * @return Data value.
     */
    Optional<Object> getValue(String column);

    /**
     * Assigned label of training data.
     *
     * @return Label.
     */
    Label getLabel();

    /**
     * Syntactic sugar to check if data has feature.
     *
     * @param feature Feature.
     *
     * @return True if data has feature and false otherwise.
     */
    default boolean has(Feature feature) {
        return feature.belongsTo(this);
    }

}
