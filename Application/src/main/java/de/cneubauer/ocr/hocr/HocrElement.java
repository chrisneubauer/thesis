package de.cneubauer.ocr.hocr;

/**
 * Created by Christoph Neubauer on 16.02.2017.
 */
public abstract class HocrElement {
    String position;
    String value;

    public String getValue() {
        return this.value;
    }

    public String getPosition() {
        return position;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
