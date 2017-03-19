package de.cneubauer.domain.dao;

import de.cneubauer.domain.bo.Position;

import java.util.Collection;

/**
 * Created by Christoph Neubauer on 15.11.2016.
 * DAO for business object AccountRecord
 */
public interface PositionDao extends IDao<Position> {
    Collection<Position> getAllByScanId(int id);
}
