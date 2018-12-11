package DatabaseGUI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnect {
  private static Connection connection;
  private static final String URL = "jdbc:derby:lib\\salesDB"; // relative path to salesDB database
  private static final String USER = "twittwer"; // username to access database
  // I'm aware FindBugs-IDEA is yelling at me about this password, I'm just not sure how else
  // to use the password connection.
  private static final String PASSWORD = "12345"; // password to access database

  /**
   * Attempts to make a connection with the static URL, username and password.
   * @return returns a Connection object.
   * @throws SQLException
   */
  public static Connection connect() throws SQLException {
    connection = DriverManager.getConnection(URL, USER, PASSWORD);
    return connection;
  }

  /**
   * The disconnect method is performed to disconnect from the database.
   * @throws SQLException database connection may result in an SQLException
   */
  public static void disconnect() throws SQLException{
    try{
      // if there IS a connection AND said connection is not closed, go ahead and close it.
      if (connection != null && !connection.isClosed()) {
        connection.close();
      }
    } catch (Exception e){
      throw e; // don't know what exception would be here, but go ahead and throw it.
    }
  }
}
