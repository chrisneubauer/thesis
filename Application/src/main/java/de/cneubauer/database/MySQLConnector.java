package de.cneubauer.database;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import de.cneubauer.util.config.ConfigHelper;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Christoph Neubauer on 05.10.2016.
 *
 */
public class MySQLConnector {
    private final String USERNAME = ConfigHelper.getValue("databaseUsername");
    private final String PW = ConfigHelper.getValue("databasePassword");
    private final String SERVERNAME = ConfigHelper.getValue("databaseServername");
    private final String DATABASE = ConfigHelper.getValue("databaseName");
    private final int PORT = Integer.valueOf(ConfigHelper.getValue("databasePort"));

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
            // try again
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
