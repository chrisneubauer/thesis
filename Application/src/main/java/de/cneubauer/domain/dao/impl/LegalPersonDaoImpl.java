package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.LegalPerson;
import de.cneubauer.domain.dao.LegalPersonDao;

import java.sql.Date;
import java.time.LocalDate;

/**
 * Created by Christoph Neubauer on 05.10.2016.
 * Implementation of LegalPersonDAO
 */
public class LegalPersonDaoImpl extends AbstractDao<LegalPerson> implements LegalPersonDao {

    public LegalPersonDaoImpl() {
        super(LegalPerson.class);
    }

    // hook-method before saving
    @Override
    public void onSave(LegalPerson entity) {
        if(entity.getCreatedDate() == null) {
            entity.setCreatedDate(Date.valueOf(LocalDate.now()));
        }
        entity.setModifiedDate(Date.valueOf(LocalDate.now()));
    }
}
