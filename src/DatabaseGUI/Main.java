package DatabaseGUI;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application implements EventHandler<ActionEvent>{

  private static final String DATABASE_URL = "jdbc:derby:lib\\salesDB";
  private static final String USERNAME = "twittwer";
  private static final String PASSWORD = "12345";
  private static final String DEFAULT_QUERY = "SELECT lastName FROM customers WHERE customerID = 1";

  private static DBTableModel myTable;
  private static ResultSet resultSet;

  private static Button queryButton;
  private static TextField queryBox;
  private static Label queryAnswer;

  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root = FXMLLoader.load(getClass().getResource("DatabaseGUI.fxml"));

    try {
      myTable = new DBTableModel(DATABASE_URL, USERNAME, PASSWORD, DEFAULT_QUERY);

      GridPane myGrid = new GridPane();
      myGrid.setVgap(10);
      myGrid.setHgap(10);

      queryBox = new TextField(DEFAULT_QUERY);
      queryButton = new Button("Select Query");
      queryAnswer = new Label("I'm the default text");

      myGrid.add(queryBox, 0, 0, 1, 1);
      myGrid.add(queryButton, 1, 0, 1, 10);
      myGrid.add(queryAnswer, 0, 1, 1, 1);

      queryButton.setOnAction(this);

      primaryStage.setTitle("Hello World");
      primaryStage.setScene(new Scene(myGrid, 300, 275));
      primaryStage.show();

      //final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(myTable);
    } catch (SQLException sqlEx) {
      System.out.println("Issue occurred with database.");
      myTable.disconnectFromDatabase();
    }
  }

  @Override
  public void handle(ActionEvent event) {
    if (event.getSource() == queryButton){
      try {
        myTable.setQuery(queryBox.getText());

        resultSet = myTable.getResultSet();
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

        queryAnswer.setText(myTable.getQueryAnswer());
      } catch (SQLException sqlEx) {
        System.out.println("Issue with SQL statement");

        try {
          myTable.setQuery(DEFAULT_QUERY);
          queryBox.setText(DEFAULT_QUERY);
        } catch (SQLException sqlEx2) {
          System.out.println("Database error");
        }
      }
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
