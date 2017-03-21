package de.cneubauer.domain.dao;

import de.cneubauer.domain.bo.DocumentCase;
import de.cneubauer.util.DocumentCaseSet;

import java.util.List;

/**
 * Created by Christoph Neubauer on 15.02.2017.
 * DAO for business object DocumentCase
 */
public interface DocumentCaseDao extends IDao<DocumentCase> {
    /**
     * @param name the name of the creditor
     * @return a list of all DocumentCases that are linked to the specified creditor
     */
    List<DocumentCase> getAllByCreditorName(String name);

    /**
     * Saves all cases that are present in the documentCaseSet
     * @param set the DocumentCaseSet containing all DocumentCases that should be saved
     */
    void saveCases(DocumentCaseSet set);

    /**
     * @return the highest existing case id
     */
    int getHighestCaseId();

    /**
     * Saves all DocumentCases that are in the given list
     * @param positionCases the cases that should be saved
     */
    void saveAll(List<DocumentCase> positionCases);
}
