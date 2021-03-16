package org.api;

import java.sql.*;

public class SQLite {
    public String databaseUrlJdbc;

    public SQLite(String  databaseUrlJdbc, String cleanDatabaseTableAtStart) {
        this.databaseUrlJdbc=databaseUrlJdbc;

        //https://shinesolutions.com/2007/08/04/how-to-close-jdbc-resources-properly-every-time/
        Connection connection=null;
        try{
            connection= DriverManager.getConnection(databaseUrlJdbc);
            Statement statement =null;
            // create a database connection
            try{
                statement= connection.createStatement();
                statement.setQueryTimeout(30);  // set timeout to 30 sec.
                    DatabaseMetaData dbm = connection.getMetaData();
                    ResultSet tables = dbm.getTables(null, null, "stagpdfa_logs", null);
                    if (tables.next()) {
                        System.out.println("table stagpdfa_logs, exist");
                        tables.close();

                        //cleanDatabaseTableAtStart==true, If you want to delete content of existing database table at the start of program
                        if(cleanDatabaseTableAtStart.equals("true")){
                            statement.executeUpdate("delete from stagpdfa_logs");
                        }

                    } else {
                        statement.executeUpdate("create table stagpdfa_logs (sha1 text,  verapdf_rest_response text, request_time integer, verapdf_rest_request_time integer)");
                        tables.close();
                    }
            }catch(SQLException e){
                System.err.println(e.getMessage());
            }finally {
                try
                {
                    if(statement != null)
                        statement.close();
                }
                catch(SQLException e)
                {
                    // connection close failed.
                    System.err.println(e.getMessage());
                }
            }
        }catch(SQLException sql){
            System.err.println(sql.getMessage());
        }finally{
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }

    public void insertStagpdfaLogs(String sha1, String verapdf_rest_response, Integer request_time, Integer verapdf_rest_request_time ){
        //https://shinesolutions.com/2007/08/04/how-to-close-jdbc-resources-properly-every-time/
        Connection connection=null;
        try{
            connection= DriverManager.getConnection(databaseUrlJdbc);
            Statement statement =null;
            // create a database connection
            try{
                statement= connection.createStatement();
                statement.setQueryTimeout(30);  // set timeout to 30 sec.

                PreparedStatement pstmt = connection.prepareStatement("INSERT INTO stagpdfa_logs (sha1 ,verapdf_rest_response, request_time, verapdf_rest_request_time) VALUES (?,?,?,?)");

                //https://stackoverflow.com/questions/17207088/how-to-use-java-variable-to-insert-values-to-mysql-table
                pstmt.setString(1, sha1 );
                pstmt.setString(2, verapdf_rest_response );
                pstmt.setInt(3, request_time);
                pstmt.setInt(4, verapdf_rest_request_time);
                pstmt.executeUpdate();

                pstmt.close();
            }catch(SQLException e){
                System.err.println(e.getMessage());
            }finally {
                try
                {
                    if(statement != null)
                        statement.close();

                }
                catch(SQLException e)
                {
                    // connection close failed.
                    System.err.println(e.getMessage());
                }
            }
        }catch(SQLException sql){
            System.err.println(sql.getMessage());
        }finally{
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }

    public void printSQLContentOnConsole(){
        ///https://shinesolutions.com/2007/08/04/how-to-close-jdbc-resources-properly-every-time/
        Connection connection=null;
        try{

            connection= DriverManager.getConnection(databaseUrlJdbc);
            Statement statement =null;
            // create a database connection
            try{
                statement= connection.createStatement();
                statement.setQueryTimeout(30);  // set timeout to 30 sec.

                ResultSet rs = statement.executeQuery("select * from stagpdfa_logs");
                System.out.println("sha1|verapdf_rest_response|request_time|verapdf_rest_request_time");
                while(rs.next())
                {
                    // read the result set
                    System.out.print(rs.getString("sha1")+"| ");
                    System.out.print(rs.getString("verapdf_rest_response")+"| ");
                    System.out.print(rs.getInt("request_time")+"| ");
                    System.out.println(rs.getInt("verapdf_rest_request_time")+"| ");
                }

            }catch(SQLException e){
                System.err.println(e.getMessage());
            }finally {
                try
                {
                    if(statement != null)
                        statement.close();

                }
                catch(SQLException e)
                {
                    // connection close failed.
                    System.err.println(e.getMessage());
                }
            }
        }catch(SQLException sql){
            System.err.println(sql.getMessage());
        }finally{
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }
}
