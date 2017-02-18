package de.cneubauer.domain.dao;

import de.cneubauer.domain.bo.DocumentCase;
import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.util.DocumentCaseSet;

import java.util.List;

/**
 * Created by Christoph Neubauer on 15.02.2017.
 */
public interface DocumentCaseDao extends IDao<DocumentCase> {
    List<DocumentCase> getAllByCreditorName(String name);

    void saveCases(DocumentCaseSet set);

    int getHighestCaseId();
}
