package de.cneubauer.database;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import de.cneubauer.util.config.ConfigHelper;

import java.sql.Connection;

/**
 * Created by Christoph Neubauer on 05.10.2016.
 * Connector class to enable access to a MySQL database
 */
public class MySQLConnector {
    private final String USERNAME;
    private final String PW;
    private final String SERVERNAME;
    private final String DATABASE;
    private final int PORT;

    /**
     *
     * @param srv  the servername where the mysql database is
     * @param db  the name of the database to be used
     * @param usr  the username which is used to access the db
     * @param pw  the password for the corresponding user
     * @param prt  the port of the mysql databse (default: 3306)
     */
    public MySQLConnector(String srv, String db, String usr, String pw, int prt) {
        this.USERNAME = usr;
        this.PW = pw;
        this.SERVERNAME = srv;
        this.DATABASE = db;
        this.PORT = prt;
    }

    public MySQLConnector() {
        this.USERNAME = ConfigHelper.getDBUserName();
        this.PW = ConfigHelper.getDBPassword();
        this.SERVERNAME = ConfigHelper.getDBServerName();
        this.DATABASE = ConfigHelper.getDBName();
        this.PORT = ConfigHelper.getDBPort();
    }

    /**
     * Connects to the mysql database using the given credentials
     * @return  returns an open connection to the mysql data source
     */
    public Connection connect() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUser(this.getUSERNAME());
        dataSource.setPassword(this.getPW());
        dataSource.setServerName(this.getSERVERNAME());
        dataSource.setDatabaseName(this.getDATABASE());
        dataSource.setPort(this.getPORT());

        Connection con = null;
        try {
            con = dataSource.getConnection();
        } catch (Exception e) {
            // try again just in case there was a problem with the internet connection
            try {
                Thread.sleep(3000);
                con = dataSource.getConnection();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return con;
    }

    private String getUSERNAME() {
        return USERNAME;
    }

    private String getPW() {
        return PW;
    }

    private String getSERVERNAME() {
        return SERVERNAME;
    }

    private String getDATABASE() {
        return DATABASE;
    }

    private int getPORT() {
        return PORT;
    }
}
