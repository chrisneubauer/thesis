package de.cneubauer.ocr.hocr;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Christoph Neubauer on 16.02.2017.
 * Abstract structure of an HocrElement
 */
public abstract class HocrElement {
    String id;
    String position;
    String value;
    private List<HocrElement> subElements;

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

    public HocrElement getByPosition(int[] position) {
        for (HocrElement element : this.subElements) {
            String[] stringPos = element.getPosition().split("\\+");
            // 0: startX, 1: startY, 2: endX, 3: endY
            int[] pos = new int[] {Integer.valueOf(stringPos[0]), Integer.valueOf(stringPos[1]), Integer.valueOf(stringPos[2]), Integer.valueOf(stringPos[3])};

            // adding 20 px as an impreciness value
            boolean xStartsEarlier = pos[0] -20 <= position[0];
            boolean yStartsEarlier = pos[1] -20 <= position[1];
            boolean xEndsLater = pos[2] +20 >= position[2];
            boolean yEndsLater = pos[3] +20 >= position[3];

            if (xStartsEarlier && yStartsEarlier && xEndsLater && yEndsLater) {
                return element;
            }
        }
        return null;
    }

    void addSubElement(HocrElement sub) {
        if (this.subElements == null) {
            this.subElements = new LinkedList<>();
        }
        this.subElements.add(sub);
    }

    public List<HocrElement> getSubElements() {
        return subElements;
    }

    HocrElement getSubElement(String elementId) {
        for (HocrElement element : this.getSubElements()) {
            if (element.getId().equals(elementId)) {
                return element;
            }
        }
        return null;
    }

    public String getId() {
        return id;
    }
}
