package de.cneubauer.domain.dao;

import java.util.List;

/**
 * Created by Christoph Neubauer on 06.10.2016.
 * Interface with common methods
 */
public interface IDao<T> {
    public T getById(int id);
    public List<T> getAll();
    public void save(T entity);
}
