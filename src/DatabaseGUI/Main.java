package DatabaseGUI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {



  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root = FXMLLoader.load(getClass().getResource("DatabaseGUI.fxml"));
    primaryStage.setTitle("Hello World");
    primaryStage.setScene(new Scene(root, 300, 275));
    primaryStage.show();
  }


  public static void main(String[] args) {
    final String DATABASE_URL = "jdbc:derby:lib\\salesDB";
    final String USERNAME = "twittwer";
    final String PASSWORD = "12345";

    try{
      Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
    }
    catch(SQLException sqlE){
      sqlE.printStackTrace();
    }
    catch(Exception e){
      System.out.println("Some other error, aside from SQL");
    }

    launch(args);
  }
}
