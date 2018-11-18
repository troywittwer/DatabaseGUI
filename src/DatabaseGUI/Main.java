/**
 * Written by Troy Wittwer
 *
 * This class contains the main method, database connection values, and creates the initial GUI
 * window. Since JavaFX is involved, the class must implement the EventHandler interface in order to
 * apply actions to GUI objects like buttons.
 */

package DatabaseGUI;

import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * The Oracle example of the Books database project used ActionListener, but I was having trouble
 * getting it to work. I'm pretty sure this was because their example used Javax.swing while this
 * assignment uses JavaFX. EventHandler appears to be JavaFX's equivalent to ActionListener.
 */
public class Main extends Application implements EventHandler<ActionEvent> {

  /**
   * These four values are used to connect to the database. They include the database's URL,
   * username, password, and a default query. The default query isn't actually necessary, but it
   * sounded like a good idea if the user ever forgets how to form a proper SQL statement.
   */
  private static final String DATABASE_URL = "jdbc:derby:lib\\salesDB";
  private static final String USERNAME = "twittwer";
  private static final String PASSWORD = "12345";
  private static final String DEFAULT_QUERY = "SELECT lastName\nFROM employees\nWHERE employeeID = 1";

  /**
   * Basic components of the initial GUI turn-in. queryBox is a TextArea that the user can type a
   * query into. queryButton is a Button that the user can press to run the query. queryAnswer is a
   * Label that displays the result of the query. To keep it simple, this label only displays one
   * column of the output.
   *
   * These were created as class variables so that the handle method could access them.
   */
  private static DBTableModel myTable;
  private static Button queryButton;
  private static TextArea queryBox;
  private static Label queryAnswer;

  @Override
  public void start(Stage primaryStage) throws Exception {
    // Might not need this Parent object.
    Parent root = FXMLLoader.load(getClass().getResource("DatabaseGUI.fxml"));

    /**
     * Try block attempts to create the database connection and sets up the GUI, afterwards.
     */
    try {
      // Form a database connection.
      myTable = new DBTableModel(DATABASE_URL, USERNAME, PASSWORD, DEFAULT_QUERY);

      // Create a GridPane for a very basic GUI. It's got a text field, button, and a label.
      GridPane myGrid = new GridPane();
      myGrid.setVgap(10); // vertical gap of 10 pixels between components
      myGrid.setHgap(10); // height gap of 10 pixels between components

      Label testLabel = new Label("Last name: ");
      queryBox = new TextArea(DEFAULT_QUERY); // populate the text field with the default query
      queryButton = new Button("Select Query"); // Button's text reads as "Select Query"
      queryAnswer = new Label("I'm the default text"); // Couldn't tell where the label was.

      queryBox.setPrefWidth(400);
      queryBox.setPrefHeight(100);
      //queryBox
      //queryBox.setAlignment(Pos.TOP_LEFT);

      // add the three components to the GridPane object.
      myGrid.add(queryBox, 0, 0, 1, 1);
      myGrid.add(queryButton, 1, 0, 1, 1);
      myGrid.add(testLabel, 0, 1, 1, 1);
      myGrid.add(queryAnswer, 1, 1, 1, 1);

      queryButton.setOnAction(this); // leads to the handle method. Defines what button does.

      primaryStage.setTitle("Initial GUI Project");
      primaryStage.setScene(new Scene(myGrid, 800, 350)); // add GridPane to scene
      primaryStage.show();

      // Need to find the JavaFX equivalent to TableRowSorter & TableModel.
      //final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(myTable);
    }// end of try
    /**
     * Since the database connection is the only place that may lead to an exception at this point,
     * it's technically the only catch block needed.
     */ catch (SQLException sqlEx) {
      System.out.println("Issue occurred with database.");
      myTable.disconnectFromDatabase();
    } catch (Exception ex) { // Just in case.
      System.out.println("The error could be literally anything other than SQL.");
    }// end of catch
  }// end of try

  /**
   * The handle method may end up having more components to work with, that's why I've got an
   * if-statement to find out what called the EventHandler. Since the queryButton object is the only
   * thing calling EventHandler at the moment, it's the only if-statement present.
   */
  @Override
  public void handle(ActionEvent event) {
    if (event.getSource() == queryButton) { // User hit the query button.
      try {
        myTable.setQuery(queryBox.getText()); // sets up the query for the myTable object.
        queryAnswer.setText(myTable.getQueryAnswer()); // writes query answer to the label
      } catch (SQLException sqlEx) { // myTable may result in an SQLException
        System.out.println("Issue with SQL statement");

        try { // sets the TextArea back to default text
          myTable.setQuery(DEFAULT_QUERY);
          queryBox.setText(DEFAULT_QUERY);
        } catch (SQLException sqlEx2) { // again, myTable may result in an SQLException
          System.out.println("Database error");
        }//end of catch
      }//end of catch
    }//end of if
  }// end of handle method

  /**
   * main method only leads to the start method.
   *
   * @param args this parameter may contain info if user runs the program from the command prompt.
   */
  public static void main(String[] args) {
    launch(args);
  }// end of main method
}// end of Main class
