/**
 * Written by Troy Wittwer
 *
 * This class contains the main method, database connection values, and creates the initial GUI
 * window. Since JavaFX is involved, the class must implement the EventHandler interface in order to
 * apply actions to GUI objects like buttons.
 */

package DatabaseGUI;

import com.sun.corba.se.impl.io.TypeMismatchException;
import java.sql.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.event.*;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Callback;

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
  private static final String DEFAULT_QUERY = "SELECT *\nFROM employees";

  /**
   * Basic components of the initial GUI turn-in. queryBox is a TextArea that the user can type a
   * query into. queryButton is a Button that the user can press to run the query. queryAnswer is a
   * Label that displays the result of the query. To keep it simple, this label only displays one
   * column of the output.
   *
   * These were created as class variables so that the handle method could access them.
   */

  private static DBTableModel myTable;
  private static CustomAnimation myAnimation;
  private static GridPane outerGrid;
  private static GridPane innerLeftGrid;
  private static GridPane innerRightGrid;

  private static HBox guiButtons;
  private static HBox shapeStuff;
  private static HBox sliderStuff;

  private static Button queryButton;
  private static Button resetButton;
  private static Button colorButton;
  private static Label sliderLabel;
  private static TextField sliderValue;
  private static TextArea queryBox;
  private static Slider timeSlider;

  private static ColorPicker shapeColor = new ColorPicker(Color.BLACK);
  private static Circle myCircle;
  private static Line myLine;

  private static ObservableList<ObservableList> data;
  private static TableView table = new TableView();


  @Override
  public void start(Stage primaryStage) throws Exception {
    // Might not need this Parent object.
    Parent root = FXMLLoader.load(getClass().getResource("DatabaseGUI.fxml"));

    /**
     * Try block attempts to create the database connection and sets up the GUI, afterwards.
     */
    try {
      // Create a GridPane for a very basic GUI. It's got a text field, button, and a label.
      outerGrid = new GridPane();
      innerLeftGrid = new GridPane();
      innerLeftGrid.setVgap(10); // vertical gap of 10 pixels between components
      innerLeftGrid.setHgap(10); // height gap of 10 pixels between components

      innerRightGrid = new GridPane();
      innerRightGrid.setVgap(10);
      innerRightGrid.setHgap(10);

      guiButtons = new HBox();
      shapeStuff = new HBox();
      sliderStuff = new HBox();

      queryBox = new TextArea(DEFAULT_QUERY); // populate the text field with the default query
      queryButton = new Button("Select Query"); // Button's text reads as "Select Query"
      resetButton = new Button("Reset Table");

      colorButton = new Button("Change Color");
      colorButton.setOnAction(e -> myCircle.setFill(shapeColor.getValue()));

      myCircle = new Circle();
      myCircle.setCenterX(100.0f);
      myCircle.setCenterY(100.0f);
      myCircle.setRadius(50.0f);
      myCircle.setFill(shapeColor.getValue());

      myLine = new Line(0, 0, 0, 50);
      myLine.setTranslateX(100);
      myLine.setTranslateY(100);
      myLine.setStroke(Color.WHITE);

      Group clockyThing = new Group(myCircle, myLine);

      timeSlider = new Slider(0, 200, 200);
      sliderLabel = new Label("Width:");
      sliderValue = new TextField(Double.toString(timeSlider.getValue()));

      /**
       * The slider will automatically update the value of the text field when used.
       */
      timeSlider.valueProperty().addListener(new ChangeListener<Number>() {
        public void changed(ObservableValue<? extends Number> obvValues,
            Number oldValue, Number newValue) {
          double truncatedValue = (int) timeSlider.getValue();
          sliderValue.setText(Double.toString(truncatedValue));
        }
      });

      /**
       * The text field can also update the slider's position when a value is typed and the enter
       * key is pressed.
       */
      sliderValue.setOnAction(e -> {
        try {
          double userNumber = Double.parseDouble(sliderValue.getText());
          timeSlider.setValue(userNumber);
        } catch (NullPointerException nullEx) {
          System.out.println("Null value submitted for slider value.");
          timeSlider = new Slider(0, 100, 100);
          sliderValue = new TextField(Double.toString(timeSlider.getValue()));
        } catch (Exception wrongTypeEx) {
          System.out.println("Incorrect slider input. Please enter a number.");
        }
      });

      sliderStuff.getChildren().addAll(sliderLabel, sliderValue, timeSlider);

      guiButtons.getChildren().addAll(queryButton, resetButton);
      guiButtons.setSpacing(10);
      guiButtons.setMinWidth(350);

      shapeStuff.getChildren().addAll(colorButton, shapeColor);
      shapeStuff.setSpacing(10);
      shapeStuff.setMinWidth(350);

      queryBox.setPrefWidth(700);
      queryBox.setPrefHeight(100);
      table.setMaxWidth(700);

      //queryBox.setAlignment(Pos.TOP_LEFT); // was used for TextField, not for TextArea.

      // add the components to the GridPane object.
      innerLeftGrid.add(queryBox, 0, 0, 1, 1);
      innerLeftGrid.add(table, 0, 2, 1, 2);
      innerLeftGrid.setPadding(new Insets(25, 25, 25, 25));

      innerRightGrid.add(guiButtons, 0, 0, 1, 1);
      innerRightGrid.add(shapeStuff, 0, 1, 1, 1);
      innerRightGrid.add(clockyThing, 0, 2, 1, 1);
      innerRightGrid.add(sliderStuff, 0, 3, 1, 1);
      innerRightGrid.setPadding(new Insets(25, 25, 25, 25));
      innerRightGrid.setPrefWidth(350);

      outerGrid.add(innerLeftGrid, 0, 0, 1, 2);
      outerGrid.add(innerRightGrid, 1, 0, 1, 2);

      queryButton.setOnAction(this); // leads to the handle method. Defines what button does.
      resetButton.setOnAction(this);

      primaryStage.setTitle("Final GUI Project");
      Scene myScene = new Scene(outerGrid, 1100, 350);
      myScene.getStylesheets().add("myCss.css"); // CSS cannot be used in community version
      primaryStage.setScene(myScene); // add GridPane to scene
      primaryStage.show();

      // Need to find the JavaFX equivalent to TableRowSorter & TableModel.
      //final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(myTable);
    }// end of try
    /**
     * Since the database connection is the only place that may lead to an exception at this point,
     * it's technically the only catch block needed.
     */
    /*
    catch (SQLException sqlEx) {
      System.out.println("Issue occurred with database.");
      myTable.disconnectFromDatabase();
    }
    */ catch (Exception ex) { // Just in case.
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
      Connection c;
      data = FXCollections.observableArrayList();

      // Make sure to clear all columns from previous query.
      table.getItems().clear();
      table.getColumns().clear();

      try {
        c = DatabaseConnect.connect();
        ResultSet rs = c.createStatement().executeQuery(queryBox.getText());
        ResultSetMetaData rsmd = rs.getMetaData();

        for (int column = 0; column < rsmd.getColumnCount(); column++) {
          final int currColumn = column;
          TableColumn col = new TableColumn(rsmd.getColumnName(column + 1));

          col.setCellValueFactory(
              new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                public ObservableValue<String> call(
                    CellDataFeatures<ObservableList, String> param) {
                  return new SimpleStringProperty(param.getValue().get(currColumn).toString());
                }
              });

          table.getColumns().addAll(col);
        }

        while (rs.next()) {
          ObservableList<String> row = FXCollections.observableArrayList();
          for (int column = 1; column <= rsmd.getColumnCount(); column++) {
            row.add(rs.getString(column));
          }
          data.add(row);
        }
        table.setItems(data);
        rs.close();
        DatabaseConnect.disconnect();
      } catch (SQLException sqlEx) { // myTable may result in an SQLException
        System.out.println("Issue with SQL statement");

        try { // sets the TextArea back to default text
          myTable.setQuery(DEFAULT_QUERY);
          queryBox.setText(DEFAULT_QUERY);
        } catch (SQLException sqlEx2) { // again, myTable may result in an SQLException
          System.out.println("Database error");
        }//end of catch
      }// end of catch
    }//end of if

    else if (event.getSource() == resetButton) {
      queryBox.setText(DEFAULT_QUERY);
      shapeColor.getCustomColors().remove(shapeColor);
      table.getItems().clear();
      table.getColumns().clear();
    }// end of else-if

    else if (event.getSource() == colorButton) {
      myCircle.setFill(shapeColor.getValue());
    }

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
