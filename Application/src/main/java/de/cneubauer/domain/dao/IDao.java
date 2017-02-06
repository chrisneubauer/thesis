package de.cneubauer.domain.dao;

import java.util.List;

/**
 * Created by Christoph Neubauer on 06.10.2016.
 * Interface with common methods
 */
public interface IDao<T> {
    /**
     * @param id  the id of the object which is searched for
     * @return  the found object or null if nothing is found
     */
    T getById(int id);

    /**
     * @return  a list of all objects existent
     */
    List<T> getAll();

    /**
     * @param entity  the entity to be saved in the database
     */
    void save(T entity);
}
