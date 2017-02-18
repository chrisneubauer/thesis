package de.cneubauer.domain.bo;

/**
 * Created by Christoph Neubauer on 15.02.2017.
 */
public class Creditor {
    private int id;
    private String name;
    private LegalPerson legalPerson;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LegalPerson getLegalPerson() {
        return legalPerson;
    }

    public void setLegalPerson(LegalPerson legalPerson) {
        this.legalPerson = legalPerson;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
