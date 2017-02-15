package de.cneubauer.transformation.cbr;

import java.util.List;

/**
 * Created by Christoph Neubauer on 14.02.2017.
 */
public class CbrBlock {
    private String pattern; //e.g. AABBC
    private List<CbrWord> words;
    private CbrPosition position;
}
