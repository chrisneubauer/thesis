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
    HocrElement parent;

    public HocrElement getParent() {
        return parent;
    }

    public void setParent(HocrElement parent) {
        this.parent = parent;
    }

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

    public HocrElement getByPosition(int[] position, int threshold) {
        for (HocrElement element : this.subElements) {
            String[] stringPos = element.getPosition().split("\\+");
            // 0: startX, 1: startY, 2: endX, 3: endY
            int[] pos = new int[] {Integer.valueOf(stringPos[0]), Integer.valueOf(stringPos[1]), Integer.valueOf(stringPos[2]), Integer.valueOf(stringPos[3])};

            // adding threshold as an impreciness value
            boolean xStartsEarlier = pos[0] - threshold <= position[0];
            boolean yStartsEarlier = pos[1] - threshold <= position[1];
            boolean xEndsLater = pos[2] + threshold >= position[2];
            boolean yEndsLater = pos[3] + threshold >= position[3];

            if (xStartsEarlier && yStartsEarlier && xEndsLater && yEndsLater) {
                return element;
            }
        }
        return null;
    }

    public HocrElement getByStartingPosition(int startX, int startY, int threshold) {
        for (HocrElement element : this.subElements) {
            String[] stringPos = element.getPosition().split("\\+");
            // 0: startX, 1: startY, 2: endX, 3: endY
            int[] pos = new int[] {Integer.valueOf(stringPos[0]), Integer.valueOf(stringPos[1]), Integer.valueOf(stringPos[2]), Integer.valueOf(stringPos[3])};

            // adding threshold as an impreciness value
            boolean xStartsEarlier = pos[0] - threshold <= startX;
            boolean yStartsEarlier = pos[1] - threshold <= startY;

            if (xStartsEarlier && yStartsEarlier) {
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

    public List<HocrElement> getElementsByPosition(int[] positions, int threshold) {
        List<HocrElement> result = new LinkedList<>();

        for (HocrElement element : this.subElements) {
            // should take the smallest possible elements that are inside the area
            String[] stringPos = element.getPosition().split("\\+");
            // 0: startX, 1: startY, 2: endX, 3: endY
            int[] pos = new int[] {Integer.valueOf(stringPos[0]), Integer.valueOf(stringPos[1]), Integer.valueOf(stringPos[2]), Integer.valueOf(stringPos[3])};

            // adding threshold as an impreciness value
            boolean xStartsEarlier = pos[0] - threshold <= positions[0];
            boolean yStartsEarlier = pos[1] - threshold <= positions[1];
            boolean xEndsLater = pos[2] + threshold >= positions[2];
            boolean yEndsLater = pos[3] + threshold >= positions[3];

            if (xStartsEarlier && yStartsEarlier && xEndsLater && yEndsLater) {
                result.add(element);
            }
        }
        return result;
    }

    public List<HocrElement> getRecursiveElementsByPosition(int[] positions, int threshold) {
        List<HocrElement> result = new LinkedList<>();
        if (this.getSubElements() != null && this.getSubElements().size() > 0) {
            for (HocrElement element : this.getSubElements()) {
                result.addAll(element.getRecursiveElementsByPosition(positions, threshold));
            }
        } else {
            // deepest point reached | hocrWord
            String[] stringPos = this.getPosition().split("\\+");

            // 0: startX, 1: startY, 2: endX, 3: endY
            int[] pos = new int[] {Integer.valueOf(stringPos[0]), Integer.valueOf(stringPos[1]), Integer.valueOf(stringPos[2]), Integer.valueOf(stringPos[3])};

            // adding threshold as an impreciness value
            boolean xInside = pos[0] + threshold >= positions[0];
            boolean yInside = pos[1] + threshold >= positions[1];
            boolean xEndInside = pos[2] - threshold <= positions[2];
            boolean yEndInside = pos[3] - threshold <= positions[3];

            if (xInside && yInside && xEndInside && yEndInside) {
                result.add(this);
            }

            return result;
        }
        return result;
    }
}
