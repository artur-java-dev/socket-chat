package geekbrains.client;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;

import static javafx.stage.StageStyle.TRANSPARENT;


public class MainApp
		extends Application
{

  private static final String TITLE = "JavaFX Chat";
  private static final int WIDTH = 800;
  private static final int HEIGHT = 800;


  @Override
  public void start(Stage stage)
  throws Exception
  {
	URL res = getClass().getResource("/Main.fxml");
	FXMLLoader loader = new FXMLLoader(res);
	Parent root = loader.load();
	Scene scene = new Scene(root, WIDTH, HEIGHT);
	scene.setFill(Color.TRANSPARENT);

	stage.setScene(scene);
	stage.setTitle(TITLE);
	stage.initStyle(TRANSPARENT);
	stage.show();
  }


  public static void main(String... args)
  {
	launch(args);
  }


}