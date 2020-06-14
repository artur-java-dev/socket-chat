package geekbrains.client.data;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static javafx.collections.FXCollections.observableList;


public class Contacts
{

  private ObservableList<String> data;
  private static Contacts inst;


  private Contacts()
  {
	data = FXCollections.synchronizedObservableList(observableList(new ArrayList<>()));
  }


  public static Contacts getInstance()
  {
	if (inst == null)
	{
	  synchronized (Contacts.class)
	  {
		if (inst == null) inst = new Contacts();
	  }
	}

	return inst;
  }


  public void set(String[] contacts)
  {
	if (contacts.length == 0)
	  return;

	data.clear();
	data.addAll(contacts);
  }


  public List<String> getData()
  {
	return Collections.unmodifiableList(data);
  }


  public void add(String contact)
  {
	data.add(contact);
  }


  public void remove(String contact)
  {
	data.remove(contact);
  }


  public ObservableList<String> getReference()
  {
	return data;
  }


  public boolean contains(String contact)
  {
	return data.contains(contact);
  }

}