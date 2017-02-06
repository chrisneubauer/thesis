package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.CorporateForm;
import de.cneubauer.domain.dao.CorporateFormDao;

import java.sql.Date;
import java.time.LocalDate;

/**
 * Created by Christoph Neubauer on 26.10.2016.
 * DAO implementation of business object CorporateForm
 */
public class CorporateFormDaoImpl extends AbstractDao<CorporateForm> implements CorporateFormDao {
    public CorporateFormDaoImpl() {
        super(CorporateForm.class);
    }

    /**
     * Hook method to apply additional logic upon save
     * @param entity  the corporate form that should be saved
     */
    @Override
    public void onSave(CorporateForm entity) {
        if(entity.getCreatedDate() == null) {
            entity.setCreatedDate(Date.valueOf(LocalDate.now()));
        }
        entity.setModifiedDate(Date.valueOf(LocalDate.now()));
    }
}
