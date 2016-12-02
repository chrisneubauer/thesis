package de.cneubauer.learning;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Optional;

/**
 * Created by Christoph Neubauer on 21.11.2016.
 */
public class SimpleDataSample implements DataSample {

    private Map<String, Object> values = Maps.newHashMap();

    /** Column name which contains data labels. */
    private String labelColumn;

    private SimpleDataSample(String labelColumn, String[] header, Object... dataValues) {
        super();
        this.labelColumn = labelColumn;
        for (int i = 0; i < header.length; i++) {
            this.values.put(header[i], dataValues[i]);
        }
    }

    @Override
    public Optional<Object> getValue(String column) {
        return Optional.ofNullable(values.get(column));
    }

    @Override
    public Label getLabel() {
        return (Label)values.get(labelColumn);
    }

    public static DataSample newClassificationDataSample(String[] header, Object... values) {
        Preconditions.checkArgument(header.length == values.length);
        return new SimpleDataSample(null, header, values);
    }

    public static DataSample newSimpleDataSample(String labelColumn, String[] header, Object... values) {
        Preconditions.checkArgument(header.length == values.length);
        return new SimpleDataSample(labelColumn, header, values);
    }

}