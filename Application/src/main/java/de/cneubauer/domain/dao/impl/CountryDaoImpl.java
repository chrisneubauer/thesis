package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.Country;
import de.cneubauer.domain.dao.CountryDao;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Christoph Neubauer on 26.10.2016.
 */
public class CountryDaoImpl extends AbstractDao<Country> implements CountryDao {
    public CountryDaoImpl() {
        super(Country.class);
    }

    @Override
    public void onSave(Country entity) {
        if(entity.getCreatedDate() == null) {
            entity.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        }
        entity.setModifiedDate(Timestamp.valueOf(LocalDateTime.now()));
    }
}
