package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.dao.IDao;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaQuery;
import java.io.File;
import java.util.List;

/**
 * Created by Christoph Neubauer on 06.10.2016.
 * Abstract DAO for general configuration purpose
 */
public abstract class AbstractDao<T> implements IDao<T> {
    private Session session;
    private SessionFactory sessionFactory;
    private Configuration config;
    private Class<T> clazz;

    AbstractDao(Class<T> paramClass) {
        this.clazz = paramClass;
        this.config = new Configuration();
        File configFile = new File("src/main/resources/hibernate.cfg.xml");
        this.getConfig().configure(configFile);
        this.sessionFactory = this.getConfig().buildSessionFactory();
    }

    /**
     * @return  the session to the database. Opens a new session if there is none
     */
    public Session getSession() {
        Session current = this.getSessionFactory().getCurrentSession();
        if (current == null) {
            this.session = this.getSessionFactory().openSession();
        } else {
            this.session = current;
        }
        return this.session;
    }

    SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    private Configuration getConfig() {
        return config;
    }

    public T getById(int id) {
            T result;
            Session getSession = this.getSessionFactory().openSession();
            result = getSession.get(clazz, id);
            getSession.close();
            return result;
    }

    public List<T> getAll() {
        Session getSession = this.getSessionFactory().openSession();
        Transaction tx = getSession.beginTransaction();
        try {
            CriteriaQuery<T> query = getSession.getCriteriaBuilder().createQuery(clazz);
            query.select(query.from(clazz));

            Query<T> q2 = getSession.createQuery(query);
            return q2.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
        }
        return null;
    }

    public void save(T entity) {
        Session saveSession = this.getSessionFactory().openSession();
        Transaction tx = saveSession.beginTransaction();
        try {
            this.onSave(entity);

            saveSession.save(entity);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            saveSession.close();
        }
    }

    protected abstract void onSave(T entity);

    public void stopAccess() {
        if (this.session != null) {
            this.session.close();
        }

        if (!this.getSessionFactory().isClosed()) {
            this.getSessionFactory().close();
        }
    }
}
