/**
 * Written by Troy Wittwer
 *
 * This class contains the main method, database connection values, and creates the initial GUI
 * window. Since JavaFX is involved, the class must implement the EventHandler interface in order to
 * apply actions to GUI objects like buttons.
 */

package DatabaseGUI;

// I probably shouldn't have used all of these wildcards. Program is a little slow on start up.

import java.sql.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.event.*;
import java.sql.SQLException;
import javafx.application.Application;
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

  // The default query is written into the TextArea whenever reset or an invalid query is entered.
  private static final String DEFAULT_QUERY = "SELECT *\nFROM employees";

  // The highest level Pane objects (aside from scene)
  private GridPane outerGrid;
  private GridPane innerLeftGrid;
  private GridPane innerRightGrid;

  // Contain the lowest level object, like buttons, shapes, text fields, and sliders.
  private HBox dbButtons;
  private HBox shapeStuff;
  private HBox sliderStuff;

  // The lowest the level objects
  private Button queryButton;
  private Button resetButton;
  private Button colorButton;
  private Label sliderLabel;
  private TextField sliderValue;
  private TextArea queryBox;
  private Slider timeSlider;

  // This stuff is used for the shape object. Color is initially set to black for visibility.
  private ColorPicker shapeColor = new ColorPicker(Color.BLACK);
  private Circle myCircle;
  private Line myLine;
  //private static CustomAnimation myAnimation; // unused

  // Used for database query results (if any)
  private Connection c;
  private Statement s;
  private ResultSet rs;
  private ObservableList<ObservableList> data;
  private static TableView table = new TableView();


  @Override
  public void start(Stage primaryStage) throws Exception {

    /**
     * Try block attempts to create the database connection and sets up the GUI, afterwards.
     */

    // Create a GridPane for a very basic GUI. It's got a text field, button, and a label.
    outerGrid = new GridPane();

    // Instantiate the innerLeftGrid GridPane. Give the components some elbow room.
    innerLeftGrid = new GridPane();
    innerLeftGrid.setVgap(10); // vertical gap of 10 pixels between components
    innerLeftGrid.setHgap(10); // height gap of 10 pixels between components

    // Instantiate the innerRightGrid GridPane. Give the components some elbow room.
    innerRightGrid = new GridPane();
    innerRightGrid.setVgap(10);
    innerRightGrid.setHgap(10);

    // instantiate the HBox objects
    dbButtons = new HBox();
    shapeStuff = new HBox();
    sliderStuff = new HBox();

    queryBox = new TextArea(DEFAULT_QUERY); // populate the text field with the default query
    queryButton = new Button("Select Query"); // Button's text reads as "Select Query"
    resetButton = new Button("Reset Table");

    /**
     * The color button updates the color of the Shape object (a circle) to the color listed in
     * the ColorPicker object.
     */
    colorButton = new Button("Change Color");
    colorButton.setOnAction(e -> myCircle.setFill(shapeColor.getValue()));

    myCircle = new Circle();
    myCircle.setCenterX(100.0f);
    myCircle.setCenterY(100.0f);
    myCircle.setRadius(50.0f);
    myCircle.setFill(shapeColor.getValue());

    /**
     * This is a line that I wanted to use to simulate an analogue clock, but I couldn't figure out
     * how to set up all the positions in time.
     */

    myLine = new Line(0, 0, 0, 50);
    myLine.setTranslateX(100);
    myLine.setTranslateY(100);
    myLine.setStroke(Color.WHITE);

    // This Group glues the Circle and Line object together.
    Group clockyThing = new Group(myCircle, myLine);

    // The timeSlider would have been able to affect the distance traveled by the clockyThing
    timeSlider = new Slider(0, 200, 200);
    sliderLabel = new Label("Width:");
    sliderValue = new TextField(Double.toString(timeSlider.getValue()));
    sliderValue.setMaxWidth(50);

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

    // add the items associated with the slider to an HBox
    sliderStuff.getChildren().addAll(sliderLabel, sliderValue, timeSlider);

    // add the items associated with the database to an HBox
    dbButtons.getChildren().addAll(queryButton, resetButton);
    dbButtons.setSpacing(10);
    dbButtons.setMinWidth(300);

    // add the items associated with the shape object to an HBox
    shapeStuff.getChildren().addAll(colorButton, shapeColor);
    shapeStuff.setSpacing(10);

    queryBox.setPrefWidth(750);
    queryBox.setPrefHeight(100);
    table.setMaxWidth(750);

    //queryBox.setAlignment(Pos.TOP_LEFT); // was used for TextField, not for TextArea.

    // add the components to the inner-left GridPane object.
    innerLeftGrid.add(queryBox, 0, 0, 1, 1);
    innerLeftGrid.add(table, 0, 2, 1, 2);
    innerLeftGrid.setPadding(new Insets(25, 25, 25, 25));
    innerLeftGrid.setPrefWidth(800);

    // add the components to the inner-right GridPane object
    innerRightGrid.add(dbButtons, 0, 0, 1, 1);
    innerRightGrid.add(shapeStuff, 0, 1, 1, 1);
    innerRightGrid.add(clockyThing, 0, 2, 1, 1);
    innerRightGrid.add(sliderStuff, 0, 3, 1, 1);
    innerRightGrid.setPadding(new Insets(25, 25, 25, 25));
    innerRightGrid.setPrefWidth(300);

    // add both inner GridPane objects to the parent GridPane object
    outerGrid.add(innerLeftGrid, 0, 0, 1, 2);
    outerGrid.add(innerRightGrid, 1, 0, 1, 2);

    queryButton.setOnAction(this); // leads to the handle method. Defines what button does.
    //resetButton.setOnAction(this);

    primaryStage.setTitle("Final GUI Project");
    Scene myScene = new Scene(outerGrid, 1100, 350);
    myScene.getStylesheets().add("myCss.css"); // CSS cannot be used in community version
    primaryStage.setScene(myScene); // add GridPane to scene
    primaryStage.show();

  }// end of try

  /**
   * The handle method may end up having more components to work with, that's why I've got an
   * if-statement to find out what called the EventHandler. Since the queryButton object is the only
   * thing calling EventHandler at the moment, it's the only if-statement present.
   */
  @Override
  public void handle(ActionEvent event) {
    /**
     * if the user hits the "Select Query" button.
     */
    if (event.getSource() == queryButton) { // User hit the query button.
      data = FXCollections.observableArrayList(); // get ObservableList ready to go

      // Make sure to clear all columns from previous query.
      table.getItems().clear();
      table.getColumns().clear();

      try {
        c = DatabaseConnect.connect(); // attempt to connect to the database
        s = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        //the result set will consist of the statement provided by the user in the text-area
        try {
          rs = s.executeQuery(queryBox.getText());
          ResultSetMetaData rsmd = rs.getMetaData(); // rsmd consists of rs' metadata

          /**
           * Since the TabelView doesn't know exactly how many query results will be received, the for
           * loop needs to be dynamic. Without knowing the number of columns or rows, the for loop
           * must start using meta data extracted from the ResultSet object.
           *
           * rs = the ResultSet created from the user's query
           *
           * rsmd = the metadata extracted from the user's query results. For this loop's use, the
           * most valuable meta data are the total number of columns and the column's name.
           *
           * column = the counter variable used to determine how many columns exist within the query
           *
           * currColumn = an unchangeable value declared and initialized at the beginning of each
           * for-loop iteration. This value is used when retrieving the value type stored in column.
           *
           * col = that particular loop iteration's TableColumn object. This is added to the
           * TableView object as a column at the end of the loop's iteration.
           */
          for (int column = 0; column < rsmd.getColumnCount(); column++) {
            final int currColumn = column;
            TableColumn col = new TableColumn(rsmd.getColumnName(column + 1));

            col.setCellValueFactory(
                new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                  public ObservableValue<String> call(
                      CellDataFeatures<ObservableList, String> elem) {
                    return new SimpleStringProperty(elem.getValue().get(currColumn).toString());
                  }
                });

            table.getColumns().addAll(col); // add the TableColumn to the TableView
          }

          /**
           * While there are still results in the ResultSet object, continue to iterate through
           * this loop.
           *
           * This loop is responsible for creating new rows. Again, the rows need to be dynamic since
           * the user doesn't necessarily know how many rows will be returned by the query.
           *
           * The for-loop's counter variable starts at 1 because row 0 consisted of the table's
           * column names.
           *
           * Each iteration of the for loop adds a String received from that particular column and row
           * to the local ObservableList object, row.
           *
           * At the end of the while-loop iteration, an entire row of information is added to the
           * class-level ObservableList collection.
           */
          while (rs.next()) {
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int column = 1; column <= rsmd.getColumnCount(); column++) {
              row.add(rs.getString(column));
            }
            data.add(
                row); // add the ObservableList<String> row to the ObservableList<ObservableList>
          }

          /**
           * At the end of the try statement, all rows stored in the class-level ObservableList
           * collection are added to the class-level TableView object.
           */
          table.setItems(data);
        } catch (SQLException sqlEx2) {
          System.out.println("Issue with SQL statement");
          queryBox.setText(DEFAULT_QUERY); // set TextArea back to default query.

        }

        rs.close(); // close the ResultSet object
        DatabaseConnect.disconnect(); // disconnect from the database
      } catch (SQLException sqlEx) { // attempting to connect to a database may result in an error
        System.out.println("Issue with SQL statement");
        queryBox.setText(DEFAULT_QUERY); // set TextArea back to default query.
      }// end of catch
    }//end of if

    /**
     * If the user selects the "Reset Table" button
     */
    else if (event.getSource() == resetButton) {
      queryBox.setText(DEFAULT_QUERY); // set the TextArea back to the default query

      // clear the items and columns from the table
      table.getItems().clear();
      table.getColumns().clear();
    }// end of else-if
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
