package DatabaseGUI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnect {
  private static Connection connection;
  private static final String URL = "jdbc:derby:lib\\salesDB";
  private static final String USER = "twittwer";
  private static final String PASSWORD = "12345";

  public static Connection connect() throws SQLException {
    connection = DriverManager.getConnection(URL, USER, PASSWORD);
    return connection;
  }

  public static void disconnect() throws SQLException{
    try{
      if (connection != null && !connection.isClosed()) {
        connection.close();
      }
    } catch (Exception e){
      throw e;
    }
  }
}
