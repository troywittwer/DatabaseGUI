package DatabaseGUI;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.util.Duration;

public class CustomAnimation {

  private static double width;
  private static Timeline timeline;

  private static DoubleProperty x = new SimpleDoubleProperty();
  private static DoubleProperty y = new SimpleDoubleProperty();

  CustomAnimation() {
    width = 200;
    timeline = new Timeline(
        new KeyFrame(Duration.seconds(0), new KeyValue(x, 0), new KeyValue(y, 0))
        , new KeyFrame(Duration.seconds(5), new KeyValue(x, width), new KeyValue(y, 0)));
    timeline.setAutoReverse(true);
    timeline.setCycleCount(Timeline.INDEFINITE);
  }

  CustomAnimation(double width){
    this.width = width;
    timeline = new Timeline(
        new KeyFrame(Duration.seconds(0), new KeyValue(x, 0), new KeyValue(y, 0))
        , new KeyFrame(Duration.seconds(5), new KeyValue(x, width), new KeyValue(y, 0)));
    timeline.setAutoReverse(true);
    timeline.setCycleCount(Timeline.INDEFINITE);
  }

  /*
  CustomAnimation(double userTime, double width){
    this.width = width;
    timeline = new Timeline(
        new KeyFrame(Duration.seconds(0), new KeyValue(x, 0), new KeyValue(y, 0))
        , new KeyFrame(Duration.seconds(userTime), new KeyValue(x, width), new KeyValue(y, 0)));
    timeline.setAutoReverse(true);
    timeline.setCycleCount(Timeline.INDEFINITE);
  }
  */
  public Timeline getTimeline(){
    return timeline;
  }

  public double getWidth(){
    return width;
  }

  public void setTimeline(double userTime){
    timeline = new Timeline(
        new KeyFrame(Duration.seconds(0), new KeyValue(x, 0), new KeyValue(y, 0))
        , new KeyFrame(Duration.seconds(userTime), new KeyValue(x, width), new KeyValue(y, 0)));
  }

  public void setWidth(double width){
    this.width = width;
  }

}
