package de.cneubauer.transformation.cbr;

/**
 * Created by Christoph Neubauer on 14.02.2017.
 */
public class CbrField {
    private CbrWord wordI;
    private CbrWord wordJ;
    private double threshold;
    private String nature;
    private CbrPosition position;

    public String getNature() {
        return this.nature;
    }

    public CbrWord getWordI() {
        return wordI;
    }

    public void setWordI(CbrWord wordI) {
        this.wordI = wordI;
    }

    public CbrWord getWordJ() {
        return wordJ;
    }

    public void setWordJ(CbrWord wordJ) {
        this.wordJ = wordJ;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public CbrPosition getPosition() {
        return position;
    }

    public void setPosition(CbrPosition position) {
        this.position = position;
    }
}
