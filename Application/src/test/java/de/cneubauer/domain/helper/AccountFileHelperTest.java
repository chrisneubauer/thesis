package de.cneubauer.domain.helper;

import de.cneubauer.util.RecordTrainingEntry;
import org.junit.Test;
import org.springframework.util.Assert;

import static org.junit.Assert.*;

/**
 * Created by Christoph Neubauer on 07.01.2017.
 */
public class AccountFileHelperTest {
    @Test
    public void addAccountingRecord() throws Exception {

    }

    @Test
    public void findAccountingRecord() throws Exception {
        RecordTrainingEntry entry = AccountFileHelper.findAccountingRecord("Kartoffeln");
        Assert.notNull(entry);
    }

}