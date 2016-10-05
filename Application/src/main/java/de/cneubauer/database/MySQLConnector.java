package de.cneubauer.database;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Christoph Neubauer on 05.10.2016.
 *
 */
public class MySQLConnector {
    private final String USERNAME = "root";
    private final String PW = "toor";
    private final String SERVERNAME = "localhost";
    private final String DATABASE = "ferd_transformator";
    private final int PORT = 3306;

    public Connection connect() throws SQLException {
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

    public String getUSERNAME() {
        return USERNAME;
    }

    public String getPW() {
        return PW;
    }

    public String getSERVERNAME() {
        return SERVERNAME;
    }

    public String getDATABASE() {
        return DATABASE;
    }

    public int getPORT() {
        return PORT;
    }
}
