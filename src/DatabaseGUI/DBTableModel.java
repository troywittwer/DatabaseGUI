package DatabaseGUI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class DBTableModel {
  private final Connection connection;
  private final Statement sqlStatement;
  private ResultSet resultSet;
  private ResultSetMetaData metaData;
  private int numberOfRows;

  private boolean databaseConnected = false;

  public DBTableModel(String url, String userName, String pw, String sqlQuery) throws SQLException {
    connection = DriverManager.getConnection(url, userName, pw);

    sqlStatement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
        ResultSet.CONCUR_READ_ONLY);

    databaseConnected = true;

    setQuery(sqlQuery);
  }

  public ResultSet getResultSet(){
    return resultSet;
  }

  public void setQuery(String sqlQuery) throws SQLException {
    resultSet = sqlStatement.executeQuery(sqlQuery);

    metaData = resultSet.getMetaData();

    resultSet.last();
    numberOfRows = resultSet.getRow();
  }

  public String getQueryAnswer(){
    try{
      return resultSet.getString(1);
    }
    catch (SQLException sqlEx){
      return "Issue with database";
    }
  }

  public void disconnectFromDatabase(){
    if(databaseConnected){
      try{
        resultSet.close();
        sqlStatement.close();
        connection.close();
      }
      catch(SQLException sqlEx){
        sqlEx.printStackTrace();
      }
      finally{
        databaseConnected = false;
      }
    }
  }
}
