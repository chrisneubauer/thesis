package de.cneubauer.database;

import de.cneubauer.domain.bo.Creditor;
import de.cneubauer.domain.bo.Keyword;

import java.util.List;

/**
 * Created by Christoph Neubauer on 16.02.2017.
 */
public final class DBInformationHolder {
    private List<Keyword> keywords;
    private List<Creditor> creditors;

    public DBInformationHolder(List<Keyword> keywordList, List<Creditor> creditorList) {
        keywords = keywordList;
        creditors = creditorList;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public List<Creditor> getCreditors() {
        return creditors;
    }
}
