package de.cneubauer.learning;

/**
 * Created by Christoph Neubauer on 21.11.2016.
 */
public abstract class Label {
    /**
     * Label value used to print to predictions output.
     *
     * @return Print label
     */
    public abstract String getPrintValue();

    /**
     * @return Label name
     */
    public abstract String getName();

    /**
     * Force overriding equals.
     */
    public abstract boolean equals(final Object o);

    /**
     * Force overriding hashCode.
     */
    public abstract int hashCode();
}
