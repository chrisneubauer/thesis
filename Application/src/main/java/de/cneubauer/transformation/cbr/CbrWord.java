package de.cneubauer.transformation.cbr;

/**
 * Created by Christoph Neubauer on 14.02.2017.
 */
public class CbrWord {
    private String value;

    // A = numerical
    // B = alphabetical
    // C = alphanumerical
    private String nature;
    private boolean keyword;

    public CbrPosition getPosition() {
        return position;
    }

    private CbrPosition position;


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public boolean isKeyword() {
        return keyword;
    }

    public void setKeyword(boolean keyword) {
        this.keyword = keyword;
    }
}
