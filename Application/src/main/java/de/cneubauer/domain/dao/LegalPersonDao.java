package de.cneubauer.domain.dao;

import de.cneubauer.domain.bo.LegalPerson;

import java.util.List;

/**
 * Created by Christoph Neubauer on 06.10.2016.
 * Data-Access-Object for LegalPerson
 * Defines specific methods for LegalPerson
 */
public interface LegalPersonDao extends IDao<LegalPerson> {
    /**
     * @return a list of all legalpersons that are <u>not</u> part of the creditors
     */
    List<LegalPerson> getAllDebitors();
}
