package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.LegalPerson;
import de.cneubauer.domain.dao.LegalPersonDao;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaQuery;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

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
            entity.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        }
        entity.setModifiedDate(Timestamp.valueOf(LocalDateTime.now()));
    }
}
