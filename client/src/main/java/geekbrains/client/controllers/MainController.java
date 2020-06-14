package geekbrains.client.controllers;


import geekbrains.client.ServerConnection;
import geekbrains.client.data.Contacts;
import geekbrains.client.data.History;
import geekbrains.client.exceptions.ServerConnectionException;
import geekbrains.client.utils.UI;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static javafx.scene.control.SelectionMode.SINGLE;


public class MainController
		implements Initializable
{

  private ServerConnection conn;
  private Contacts contacts;
  public ListView<String> contactsView;
  public TextArea messagesView;
  public TextField messageInput;
  public ContextMenu contactsCtxMenu;


  @Override
  public void initialize(URL location, ResourceBundle resources)
  {
	try
	{
	  conn = ServerConnection.getInstance();
	  conn.setShowMessageCallback(this::addMessageToHistory);
	  conn.setShowServerMessageUI(this::showServerMessage);
	  conn.connect();

	  UI.showModal("/Login.fxml", "Autorization", e -> exit());

	  initContactsView();
	}
	catch (IOException | ServerConnectionException e)
	{
	  UI.showError(e);
	  exit();
	}
  }


  public void sendMsg()
  {
	String msg = messageInput.getText();
	if (msg.isEmpty()) return;

	ObservableList<String> selected = contactsView.getSelectionModel().getSelectedItems();

	if (selected.isEmpty())
	{
	  UI.showInfo("No selected contact");
	  return;
	}

	try
	{
	  String nick = selected.get(0);
	  conn.sendMessageToContact(nick, msg);
	  messageInput.clear();
	  messageInput.requestFocus();
	  messagesView.appendText("Me: " + msg);
	  messagesView.appendText("\n");
	  History.getInstance().addMessage(nick, "Me: " + msg);
	}
	catch (IOException e)
	{
	  UI.showError(e);
	}
  }


  public void exit()
  {
	try
	{
	  conn.disconnect();
	}
	catch (IOException e)
	{
	  UI.showError(e);
	}
	finally
	{
	  conn.close();
	  History.getInstance().save();
	  Platform.exit();
	}
  }


  public void openContactsForm()
  {
	try
	{
	  UI.showModal("/Contacts.fxml", "Contacts Search");
	}
	catch (IOException e)
	{
	  UI.showError(e);
	}
  }


  public void removeContact()
  {
	ObservableList<String> selected = contactsView.getSelectionModel().getSelectedItems();

	if (selected.isEmpty())
	{
	  UI.showInfo("No selected contact");
	  return;
	}

	try
	{
	  conn.removeContact(selected.get(0), result ->
	  {
		Contacts.getInstance().remove(result);
		UI.showInfo("Contact successfully removed");
	  });
	}
	catch (IOException e)
	{
	  UI.showError(e);
	}
  }


  private void showServerMessage(String msg)
  {
	UI.showInfo(msg);
  }


  private boolean confirmNewContact(String from)
  {
	if (Contacts.getInstance().contains(from))
	  return true;

	try
	{
	  String question = from + " sent you a message.\n" +
						"Add " + from + " to your contacts?";

	  if (UI.showConfirmation(question))
	  {
		conn.addContact(from, result -> Contacts.getInstance().add(result));
		return true;
	  }
	}
	catch (IOException e)
	{
	  UI.showError(e);
	  return false;
	}

	return false;
  }


  private void initContactsView()
  throws IOException
  {
	contacts = Contacts.getInstance();
	conn.getContacts(contacts1 ->
					 {
					   contacts.set(contacts1);
					   new Thread(() -> History.getInstance().load()).start();
					 });

	MultipleSelectionModel<String> sm = contactsView.getSelectionModel();
	sm.setSelectionMode(SINGLE);
	sm.selectedItemProperty()
	  .addListener((observ, oldVal, newVal) -> showHistory(newVal));

	contactsView.setItems(contacts.getReference());
  }


  private void showHistory(String contact)
  {
	List<String> msgs = History.getInstance().get(contact);

	messagesView.clear();
	msgs.forEach(x -> messagesView.appendText(x + "\n"));
  }


  private void addMessageToHistory(String from, String msg)
  {
	if (!confirmNewContact(from))
	  return;

	History.getInstance().addMessage(from, msg);

	if (from.equals(getSelectedContact()))
	  showHistory(from);
  }


  private String getSelectedContact()
  {
	ObservableList<String> selected = contactsView.getSelectionModel()
												  .getSelectedItems();
	return selected.isEmpty() ? null : selected.get(0);
  }

}