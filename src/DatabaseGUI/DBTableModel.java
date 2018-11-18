/**
 * Written by Troy Wittwer
 *
 * This class is largely based on "ResultSetTableModel" class from Oracle's BooksDB example
 * project.
 */

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

  /**
   * Constructor for the DBTableModel class. This constructor will check to see if a connection can
   * be made, create the connection to the database, assign a true value to the boolean representing
   * whether or not the database has been connected to, and will pass the user's query to the
   * setQuery method.
   *
   * @param url a String literal holding the URL location of the database
   * @param userName a String literal holding the username that I created when setting up database
   * @param pw a String literal holding the password taht I created when setting up database
   * @param sqlQuery a String literal containing the default query.
   * @throws SQLException database connection may result in an SQL error if the connection fails
   */
  public DBTableModel(String url, String userName, String pw, String sqlQuery) throws SQLException {
    // use the first three parameters in an object used to try and connect to the database
    connection = DriverManager.getConnection(url, userName, pw);

    // ResultSet.TYPE_SCROLL_INSENSITIVE means:
    //    The cursor can scroll forward and backward. Also, changes made to database aren't updated
    //    in real time, so the results will not change while viewing.
    // ResultSet.CONCUR_READ_ONLY means:
    //    the information returned cannot be modified. It is read-only.
    sqlStatement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
        ResultSet.CONCUR_READ_ONLY);

    databaseConnected = true; // if this statement has been reached then the database is connected

    // Pass the query over to the setQuery method.
    setQuery(sqlQuery);
  }// end of DBTableModel constructor


  /**
   * This method came from Oracle's ResultSetTableModel class. The only line I'm currently using is
   * the only that assigns an executed query to the ResultSet object.
   *
   * @param sqlQuery a String object containing the user's query.
   * @throws SQLException needed to throw exception due to the try block in the Main class.
   */
  public void setQuery(String sqlQuery) throws SQLException {
    resultSet = sqlStatement.executeQuery(sqlQuery);

    // Not being used, yet, but will provide useful info such as the amount of columns / rows
    // created by the query. These values may change depending on the query used. At the moment,
    // my default query only returns one column of information, so this is not necessary. If the user
    // wanted to return all information relevant to a customer instead of just their last name, this
    // meta data would be useful.
    metaData = resultSet.getMetaData();

    // The first command sets the ResultSet object to focus on the very last object.
    // The second command returns the current row number. Since it's focusing on the last object,
    // integer value returned will represent the total number of rows.
    resultSet.last(); // Not being used, yet.
    numberOfRows = resultSet.getRow(); // Not being used, yet.
  }// end of setQuery method

  /**
   * This method will set the value of the column name as the text in the left label. It makes it
   * easier to tell what is currently being displayed.
   */
  public String getQueryColumnName() {
    try {
      return metaData.getColumnName(1);
    } catch (SQLException sqlEx) {
      return "Issue with database";
    }
  }

  /**
   * As the initial project submission, this method is only returning a single column of output from
   * the ResultSet object. For this reason, I've only typed "1" as the argument for the column
   * index. In the next submission, this not have such a trivial argument.
   */
  public String getQueryAnswer() {
    try {
      return resultSet.getString(1);
    } catch (SQLException sqlEx) { // in case ResultSet object has trouble accessing the SQL result.
      return "Issue with database";
    } // end of catch
  }// end of getQueryAnswer method

  /**
   * Manually disconnects from the database and sets the databaseConnected boolean back to false.
   */
  public void disconnectFromDatabase() {
    if (databaseConnected) {
      try { // closes the three SQL objects.
        resultSet.close();    // close result set
        sqlStatement.close(); // close Statement object
        connection.close();   // close database connection
      } catch (SQLException sqlEx) { // in case one or more close operations ran into an error.
        sqlEx.printStackTrace();
      } finally { // forces the class boolean value back to false in case an issue occurs during try
        databaseConnected = false;
      }// end of finally block
    }// end of if statement
  }// end of disconnectFromDatabase method
}// end of DBTableModel class
