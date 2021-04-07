package org.api;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.sql.*;

public class SQLite {
    public String databaseUrlJdbc;
    private Integer numberOfColsInLogTable = 7; //this attribute update every time

    private enum columns {
        sha1,
        verapdf_rest_response,
        request_time,
        verapdf_rest_request_time,
        status_code,
        error_message,
        request_timestamp,
    }

    private String sqlCreateQuery = String.format("create table stagpdfa_logs (%s text, %s text, %s integer, %s integer, %s integer, %s text, %s TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP)",
            columns.sha1, columns.verapdf_rest_response, columns.request_time, columns.verapdf_rest_request_time, columns.status_code, columns.error_message, columns.request_timestamp);


    public SQLite(String databaseUrlJdbc, String cleanDatabaseTableAtStart) {
        this.databaseUrlJdbc = databaseUrlJdbc;

        //https://shinesolutions.com/2007/08/04/how-to-close-jdbc-resources-properly-every-time/
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(databaseUrlJdbc);
            Statement statement = null;
            // create a database connection
            try {
                statement = connection.createStatement();
                statement.setQueryTimeout(30);  // set timeout to 30 sec.

                DatabaseMetaData dbm = connection.getMetaData();
                ResultSet tables = dbm.getTables(null, null, "stagpdfa_logs", null);
                if (tables.next()) {
                    //tables.getMetaData().getColumnCount();
                    System.out.println("table stagpdfa_logs, exist");
                    tables.close();
                    //cleanDatabaseTableAtStart==true, If you want to delete content of existing database table at the start of program
                    if (cleanDatabaseTableAtStart.equals("true")) {
                        statement.executeUpdate("delete from stagpdfa_logs");
                    }

                    //find out how many columns is in currently saved table stagpdfa_logs and how many columns is in enum Columns, if numbers are not same drop old table "stagpdfa_logs and create new"
                    Integer colNumber = statement.executeQuery("select * from stagpdfa_logs limit 1").getMetaData().getColumnCount();
                    if (!colNumber.equals(columns.values().length)) {
                        statement.executeUpdate("drop table stagpdfa_logs");
                        statement.executeUpdate(sqlCreateQuery);
                    }

                } else {
                    statement.executeUpdate(sqlCreateQuery);
                    tables.close();
                }
            } catch (SQLException e) {
                System.err.println(ExceptionUtils.getStackTrace(e));
            } finally {
                try {
                    if (statement != null)
                        statement.close();
                } catch (SQLException e) {
                    // connection close failed.
                    System.err.println(ExceptionUtils.getStackTrace(e));
                }
            }
        } catch (SQLException sql) {
            System.err.println(ExceptionUtils.getStackTrace(sql));
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(ExceptionUtils.getStackTrace(e));
            }
        }
    }

    public void insertStagpdfaLogs(String sha1, String verapdf_rest_response, Integer request_time, Integer verapdf_rest_request_time, Integer status_code, String error_message) {
        //https://shinesolutions.com/2007/08/04/how-to-close-jdbc-resources-properly-every-time/
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(databaseUrlJdbc);
            Statement statement = null;
            // create a database connection
            try {
                statement = connection.createStatement();
                statement.setQueryTimeout(30);  // set timeout to 30 sec.

                String sqlInsertQuery = String.format("INSERT INTO stagpdfa_logs (%s ,%s, %s, %s, %s, %s) VALUES (?,?,?,?,?,?)", columns.sha1, columns.verapdf_rest_response, columns.request_time, columns.verapdf_rest_request_time, columns.status_code, columns.error_message, columns.request_timestamp);
                PreparedStatement pstmt = connection.prepareStatement(sqlInsertQuery);

                //https://stackoverflow.com/questions/17207088/how-to-use-java-variable-to-insert-values-to-mysql-table
                pstmt.setString(1, sha1);
                pstmt.setString(2, verapdf_rest_response);
                pstmt.setInt(3, request_time);
                pstmt.setInt(4, verapdf_rest_request_time);
                pstmt.setInt(5, status_code);
                pstmt.setString(6, error_message);
                pstmt.executeUpdate();

                pstmt.close();
            } catch (SQLException e) {
                System.err.println(ExceptionUtils.getStackTrace(e));
            } finally {
                try {
                    if (statement != null)
                        statement.close();

                } catch (SQLException e) {
                    // connection close failed.
                    System.err.println(ExceptionUtils.getStackTrace(e));
                }
            }
        } catch (SQLException sql) {
            System.err.println(ExceptionUtils.getStackTrace(sql));
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(ExceptionUtils.getStackTrace(e));
            }
        }
    }

    public void printSQLContentOnConsole() {
        ///https://shinesolutions.com/2007/08/04/how-to-close-jdbc-resources-properly-every-time/
        Connection connection = null;
        try {

            connection = DriverManager.getConnection(databaseUrlJdbc);
            Statement statement = null;
            // create a database connection
            try {
                statement = connection.createStatement();
                statement.setQueryTimeout(30);  // set timeout to 30 sec.

                ResultSet rs = statement.executeQuery("select * from stagpdfa_logs");
                System.out.println("sha1|verapdf_rest_response|request_time|verapdf_rest_request_time|status_code|error_message|request_timestamp");
                while (rs.next()) {
                    // read the result set
                    System.out.print(rs.getString("sha1") + "| ");
                    System.out.print(rs.getString("verapdf_rest_response") + "| ");
                    System.out.print(rs.getInt("request_time") + "| ");
                    System.out.print(rs.getInt("verapdf_rest_request_time") + "| ");
                    System.out.print(rs.getInt("status_code") + "| ");
                    if (!rs.getString("error_message").isEmpty()) {
                        System.out.print(rs.getString("error_message").substring(0, 40) + "| ");
                    }
                    //TO DO printing request_timestamp
                    System.out.println("");//end of line
                }

            } catch (SQLException e) {
                System.err.println(ExceptionUtils.getStackTrace(e));
            } finally {
                try {
                    if (statement != null)
                        statement.close();

                } catch (SQLException e) {
                    // connection close failed.
                    System.err.println(ExceptionUtils.getStackTrace(e));
                }
            }
        } catch (SQLException sql) {
            System.err.println(ExceptionUtils.getStackTrace(sql));
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(ExceptionUtils.getStackTrace(e));
            }
        }
    }
}
