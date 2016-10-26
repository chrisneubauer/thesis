package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.CorporateForm;
import de.cneubauer.domain.dao.CorporateFormDao;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Christoph Neubauer on 26.10.2016.
 */
public class CorporateFormDaoImpl extends AbstractDao<CorporateForm> implements CorporateFormDao {
    public CorporateFormDaoImpl() {
        super(CorporateForm.class);
    }

    @Override
    public void onSave(CorporateForm entity) {
        if(entity.getCreatedDate() == null) {
            entity.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        }
        entity.setModifiedDate(Timestamp.valueOf(LocalDateTime.now()));
    }
}
