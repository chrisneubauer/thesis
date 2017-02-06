package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.Country;
import de.cneubauer.domain.dao.CountryDao;

import java.sql.Date;
import java.time.LocalDate;

/**
 * Created by Christoph Neubauer on 26.10.2016.
 * DAO implementation of business object Country
 */
public class CountryDaoImpl extends AbstractDao<Country> implements CountryDao {
    public CountryDaoImpl() {
        super(Country.class);
    }

    /**
     * Hook method to apply additional logic upon save
     * @param entity  the country that should be saved
     */
    @Override
    public void onSave(Country entity) {
        if(entity.getCreatedDate() == null) {
            entity.setCreatedDate(Date.valueOf(LocalDate.now()));
        }
        entity.setModifiedDate(Date.valueOf(LocalDate.now()));
    }
}
