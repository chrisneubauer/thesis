package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.Position;
import de.cneubauer.domain.dao.PositionDao;

/**
 * Created by Christoph Neubauer on 15.11.2016.
 * DAO implementation of business object Record
 */
public class PositionDaoImpl extends AbstractDao<Position> implements PositionDao {
    public PositionDaoImpl() {
        super(Position.class);
    }

    /**
     * Hook method to apply additional logic upon save
     * @param entity  the record that should be saved
     */
    @Override
    protected void onSave(Position entity) {
    }
}
