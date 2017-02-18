package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.Keyword;
import de.cneubauer.domain.dao.KeywordDao;

import java.util.List;

/**
 * Created by Christoph Neubauer on 15.02.2017.
 */
public class KeywordDaoImpl extends AbstractDao<Keyword> implements KeywordDao {
    public KeywordDaoImpl() { super(Keyword.class); }

    @Override
    protected void onSave(Keyword entity) {

    }
}
