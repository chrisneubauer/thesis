package de.cneubauer.transformation.cbr;

/**
 * Created by Christoph Neubauer on 14.02.2017.
 */
public class CbrPosition {
    private int fromX;
    private int fromY;
    private int toX;
    private int toY;

    public CbrPosition(int minX, int minY, int maxX, int maxY) {
        this.fromX = minX;
        this.fromY = minY;
        this.toX = maxX;
        this.toY = maxY;
    }

    public int getFromX() {
        return fromX;
    }

    public void setFromX(int fromX) {
        this.fromX = fromX;
    }

    public int getFromY() {
        return fromY;
    }

    public void setFromY(int fromY) {
        this.fromY = fromY;
    }

    public int getToX() {
        return toX;
    }

    public void setToX(int toX) {
        this.toX = toX;
    }

    public int getToY() {
        return toY;
    }

    public void setToY(int toY) {
        this.toY = toY;
    }
}
