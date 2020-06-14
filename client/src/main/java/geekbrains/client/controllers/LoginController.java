package geekbrains.client.controllers;


import geekbrains.client.ServerConnection;
import geekbrains.client.utils.UI;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static geekbrains.server.ServerCommands.AUTH_COMMAND;


public class LoginController
		implements Initializable
{

  private ServerConnection conn;
  public TextField usernameInput;
  public PasswordField passwordInput;
  public Text message;


  public void login()
  {
	try
	{
	  String username = usernameInput.getText();
	  String password = passwordInput.getText();
	  conn.sendMessage(AUTH_COMMAND + " " + username + " " + password);
	  usernameInput.clear();
	  passwordInput.clear();
	  usernameInput.requestFocus();
	}
	catch (IOException e)
	{
	  UI.showError(e);
	}
  }


  public void createAccount()
  {
	try
	{
	  Stage win = (Stage) message.getScene().getWindow();
	  win.hide();
	  UI.showModal("/Registration.fxml", "Registration");
	  win.showAndWait();
	}
	catch (IOException e)
	{
	  UI.showError(e);
	}
  }


  @Override
  public void initialize(URL location, ResourceBundle resources)
  {
	conn = ServerConnection.getInstance();
	conn.setShowAuthResponseUI(this::showAuthResponse);
	conn.setShowMainUI(this::close);
	//test
	usernameInput.setText("ivan");
	passwordInput.setText("1234");
	login();
  }


  private void close()
  {
	Stage win = (Stage) message.getScene().getWindow();
	win.close();
  }


  private void showAuthResponse(String response)
  {
	message.setText(response);
  }

}