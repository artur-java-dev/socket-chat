package geekbrains.client.utils;


import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;

import static javafx.scene.control.Alert.AlertType.*;
import static javafx.stage.Modality.APPLICATION_MODAL;
import static javafx.stage.StageStyle.UTILITY;


public class UI
{

  public static void showError(Throwable e)
  {
	String clazz = e.getClass().getSimpleName();
	String msg = e.getMessage();
	Alert alert = new Alert(ERROR);
	alert.setTitle("Application Error");
	alert.setHeaderText(clazz);
	alert.setContentText(msg);
	alert.showAndWait();
  }


  public static void showError(String title, String msg)
  {
	Alert alert = new Alert(ERROR);
	alert.setTitle(title);
	alert.setContentText(msg);
	alert.setHeaderText(null);
	alert.showAndWait();
  }


  public static void showInfo(String msg)
  {
	Alert alert = new Alert(INFORMATION);
	alert.setContentText(msg);
	alert.setTitle("Information");
	alert.setHeaderText(null);
	alert.showAndWait();
  }


  public static boolean showConfirmation(String msg)
  {
	Alert alert = new Alert(CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL);
	alert.setTitle("Confirmation");
	alert.setHeaderText(null);

	if (alert.showAndWait().get() == ButtonType.OK)
	  return true;
	else
	  return false;
  }


  public static void showModal(String path, String title, Window parent,
							   Runnable beforeShow, EventHandler<WindowEvent> onClose)
  throws IOException
  {
	URL res = UI.class.getResource(path);
	FXMLLoader loader = new FXMLLoader(res);
	Parent root = loader.load();
	Scene scene = new Scene(root);
	Stage stage = new Stage();
	stage.initModality(APPLICATION_MODAL);
	stage.setTitle(title);
	stage.initOwner(parent);
	if (onClose != null)
	  stage.setOnCloseRequest(onClose);
	stage.setResizable(false);
	stage.initStyle(UTILITY);
	stage.setScene(scene);
	if (beforeShow != null)
	  beforeShow.run();
	stage.showAndWait();
  }


  public static void showModal(String path, String title, EventHandler<WindowEvent> onClose)
  throws IOException
  {
	showModal(path, title, null, null, onClose);
  }


  public static void showModal(String path, String title, Window parent)
  throws IOException
  {
	showModal(path, title, parent, null, null);
  }


  public static void showModal(String path, String title)
  throws IOException
  {
	showModal(path, title, null, null, null);
  }


  public static void updateUI(Runnable task)
  {
	if (Platform.isFxApplicationThread())
	  task.run();
	else
	  Platform.runLater(task);
  }

}