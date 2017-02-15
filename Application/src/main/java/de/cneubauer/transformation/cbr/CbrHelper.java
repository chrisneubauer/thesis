package de.cneubauer.transformation.cbr;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Christoph Neubauer on 14.02.2017.
 */
public class CbrHelper {

    public void createFields(List<CbrWord> words) {
        for (int i = 0; i < words.size() - 1; i++) {
            CbrWord wordI = words.get(i);
            CbrWord wordJ = words.get(i+1);

            CbrField field = new CbrField();
            field.setWordI(wordI);
            field.setWordJ(wordJ);
            if (wordI.getNature().equals(wordJ.getNature())) {
                field.setNature(wordI.getNature());
            } else {
                field.setNature("C");
            }
            int minX = wordI.getPosition().getFromX() < wordJ.getPosition().getFromX() ? wordI.getPosition().getFromX() : wordJ.getPosition().getFromX();
            int minY = wordI.getPosition().getFromY() < wordJ.getPosition().getFromY() ? wordI.getPosition().getFromY() : wordJ.getPosition().getFromX();
            int maxX = wordI.getPosition().getToX() > wordJ.getPosition().getToX() ? wordI.getPosition().getToX() : wordJ.getPosition().getToX();
            int maxY = wordI.getPosition().getToY() > wordJ.getPosition().getToY() ? wordI.getPosition().getToY() : wordJ.getPosition().getToY();
            field.setPosition(new CbrPosition(minX, minY, maxX, maxY));
        }
    }

    public List<CbrWord> addAttributes(List<String> words) {
        List<CbrWord> result = new LinkedList<>();

        for (String word : words) {
            CbrWord cbrWord = new CbrWord();
            cbrWord.setValue(word);
            if (StringUtils.isNumeric(word)) {
                cbrWord.setNature("A");
            } else if (StringUtils.isAlpha(word)) {
                cbrWord.setNature("B");
            } else if (StringUtils.isAlphanumeric(word)) {
                cbrWord.setNature("C");
            }
            cbrWord.setKeyword(this.checkIfKeyword(word));
            result.add(cbrWord);
        }
        return result;
    }

    private boolean checkIfKeyword(String word) {
        String[] keywords = this.getKeywords();
        for (String kw : keywords) {
            if (kw.equals(word)) {
                return true;
            }
        }
        return false;
    }

    public String[] getKeywords() {
        File keywords = new File("keywords.txt");
        String content;
        try {
            content = new Scanner(keywords).useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return content.split("\\n");
    }
}
