package de.cneubauer.domain.bo;

/**
 * Created by Christoph Neubauer on 15.11.2016.
 * Business Object for table AccountType
 */
public class AccountType {
    private int id;
    private int type;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
