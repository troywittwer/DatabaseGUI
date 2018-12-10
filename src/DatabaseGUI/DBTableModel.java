/**
 * Written by Troy Wittwer
 *
 * This class is largely based on "ResultSetTableModel" class from Oracle's BooksDB example
 * project.
 *
 * Future note: to create a table in JavaFX, follow the guide below:
 *
 * https://docs.oracle.com/javafx/2/ui_controls/table-view.htm
 * https://docs.oracle.com/javase/8/javafx/user-interface-tutorial/table-view.htm
 */

package DatabaseGUI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class DBTableModel {

  private final Connection connection;
  private final Statement sqlStatement;
  private ObservableList<ObservableList> data;
  private ResultSet resultSet;
  private ResultSetMetaData metaData;
  private int numberOfRows;
  private TableView actualTable = new TableView();

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
    data = FXCollections.observableArrayList();

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

    /**
     * this for loop was found on StackOverflow.
     *
     * https://stackoverflow.com/questions/18941093/how-to-fill-up-a-tableview-with-database-data
     */

/*
    for (int column = 0; column < metaData.getColumnCount(); column++){
      final int currColumn = column;
      TableColumn col = new TableColumn(metaData.getColumnName(column + 1));
      col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>(){
        public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param){
          return new SimpleStringProperty(param.getValue().get(currColumn).toString());
        }
      });

      actualTable.getColumns().addAll(col);
      System.out.println("Column["+column+"] ");
    }



    while (resultSet.next()){
      addRow();
    }
    actualTable.setItems(data);
*/
    // The first command sets the ResultSet object to focus on the very last object.
    // The second command returns the current row number. Since it's focusing on the last object,
    // integer value returned will represent the total number of rows.
    // Apparently it's required for correct output.
    resultSet.last(); // Not being used, yet.
    numberOfRows = resultSet.getRow(); // Not being used, yet.
  }// end of setQuery method

  public ResultSet getResultSet(){
    return resultSet;
  }

  public void addRow() throws SQLException{
    ObservableList<String> row = FXCollections.observableArrayList();
    for (int column = 1; column <= metaData.getColumnCount(); column++){
      row.add(resultSet.getString(column));
    }
    System.out.println("Row [1] added " + row);
    data.add(row);
  }

  public TableView getTableView(){
    return actualTable;
  }

  public ObservableList<ObservableList> getData(){
    return data;
  }

  /**
   * This method will set the value of the column name as the text in the left label. It makes it
   * easier to tell what is currently being displayed.
   */
  public String getQueryColumnName(int i) {
    try {
      return metaData.getColumnName(i);
    } catch (SQLException sqlEx) {
      return "Issue with database";
    }
  }

  public int getTotalQueryColumns(){
    try{
      return metaData.getColumnCount();
    }
    catch(SQLException sqlEx){
      System.out.println("Issue with a database");
      return -1;
    }
  }

  public int getTotalQueryRows(){
    return numberOfRows;
  }

  /**
   * As the initial project submission, this method is only returning a single column of output from
   * the ResultSet object. For this reason, I've only typed "1" as the argument for the column
   * index. In the next submission, this not have such a trivial argument.
   */
  public String getQueryAnswer(int i) {
    try {
      return resultSet.getString(i);
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
