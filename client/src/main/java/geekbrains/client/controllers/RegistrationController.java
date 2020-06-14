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

import static geekbrains.server.ServerCommands.*;


public class RegistrationController
		implements Initializable
{

  private ServerConnection conn;
  public TextField usernameInput;
  public TextField nickInput;
  public PasswordField passwordInput;
  public PasswordField passwordRepeatInput;
  public Text message;


  public void register()
  {
	if (!isValidInput()) return;

	try
	{
	  String username = usernameInput.getText();
	  String password = passwordInput.getText();
	  String nick = nickInput.getText();
	  conn.sendMessage(REGISTER_COMMAND + " " + username + " " + password + " " + nick);
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
	conn.setShowRegisterResponseUI(this::showRegisterResponse);
	usernameInput.requestFocus();
  }


  private void showRegisterResponse(String response)
  {
	if (!response.endsWith("success"))
	{
	  message.setText(response.replaceFirst(REGISTER_RESPONSE, ""));
	  return;
	}

	UI.showInfo("Registration successful");
	Stage win = (Stage) message.getScene().getWindow();
	win.close();
  }


  private boolean isValidInput()
  {
	String pwd = passwordInput.getText();
	String pwdr = passwordRepeatInput.getText();
	if (usernameInput.getText().isEmpty())
	{
	  UI.showError("Form Error", "Please enter user name");
	  return false;
	}
	if (nickInput.getText().isEmpty())
	{
	  UI.showError("Form Error", "Please enter nick");
	  return false;
	}
	if (pwd.isEmpty())
	{
	  UI.showError("Form Error", "Please enter a password");
	  return false;
	}
	if (pwdr.isEmpty())
	{
	  UI.showError("Form Error", "Please repeat a password");
	  return false;
	}
	if (!pwdr.equals(pwd))
	{
	  UI.showError("Form Error", "Passwords must match");
	  return false;
	}
	return true;
  }

}