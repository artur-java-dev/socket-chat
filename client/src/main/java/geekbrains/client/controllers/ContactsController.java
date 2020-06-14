package geekbrains.client.controllers;


import geekbrains.client.ServerConnection;
import geekbrains.client.data.Contacts;
import geekbrains.client.utils.UI;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static geekbrains.server.ServerCommands.SEARCH_CONTACTS_COMMAND;
import static javafx.scene.control.SelectionMode.SINGLE;


public class ContactsController
		implements Initializable
{

  private ServerConnection conn;
  public TextField searchInput;
  public ListView<String> searchResults;
  public Button addContact;


  public void addContact()
  {
	ObservableList<String> selected = searchResults.getSelectionModel().getSelectedItems();
	if (selected.isEmpty()) return;

	String addContact = selected.get(0);

	if (Contacts.getInstance().contains(addContact))
	{
	  UI.showInfo("Such contact was added already");
	  return;
	}

	try
	{
	  this.addContact.setDisable(true);
	  conn.addContact(addContact, result ->
	  {
		Contacts.getInstance().add(result);
		UI.showInfo("Contact successfully added");
		close();
	  });
	}
	catch (IOException e)
	{
	  UI.showError(e);
	}
  }


  public void search()
  {
	String search = searchInput.getText().trim();
	if (search.isEmpty()) return;

	try
	{
	  conn.sendMessage(SEARCH_CONTACTS_COMMAND + " " + search);
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
	conn.setShowContactsUI(this::showContacts);

	MultipleSelectionModel<String> sm = searchResults.getSelectionModel();
	sm.setSelectionMode(SINGLE);

	ChangeListener<String> listener = (observable, oldValue, newValue) -> addContact.setDisable(false);
	sm.selectedItemProperty().addListener(listener);

	ListChangeListener<String> changeListener = change -> addContact.setDisable(true);
	searchResults.getItems().addListener(changeListener);
  }


  private void close()
  {
	Stage win = (Stage) searchInput.getScene().getWindow();
	win.close();
  }


  private void showContacts(String response)
  {
	String[] contacts = response.replaceFirst(SEARCH_CONTACTS_COMMAND, "")
								.trim().split("\\s");
	searchResults.getItems().setAll(contacts);
  }

}