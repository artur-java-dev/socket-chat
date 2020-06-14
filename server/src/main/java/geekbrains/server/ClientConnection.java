package geekbrains.server;


import geekbrains.server.domain.User;
import geekbrains.server.service.AuthService;
import geekbrains.server.service.UserService;
import org.slf4j.Logger;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

import static geekbrains.server.ServerCommands.*;
import static java.util.stream.Collectors.joining;
import static org.slf4j.LoggerFactory.getLogger;


public class ClientConnection
{

  private Server server;
  private Socket socket;
  private DataInputStream in;
  private DataOutputStream out;
  private User user;
  private static final Logger log = getLogger(ClientConnection.class);
  private static final String AUTH_FAIL_MSG = "Неверные логин/пароль";
  private static final String ACCOUNT_ACTIVE_MSG = "Учетная запись уже используется";


  public ClientConnection(Server _server, Socket _socket)
  {
	try
	{
	  server = _server;
	  socket = _socket;
	  in = new DataInputStream(socket.getInputStream());
	  out = new DataOutputStream(socket.getOutputStream());

	  Thread thread = new Thread(this::sessionWork);
	  thread.start();
	}
	catch (IOException e)
	{
	  throw new RuntimeException("Проблемы при создании клиентского соединения");
	}
  }


  public User getUser()
  {
	return user;
  }


  public void sendMessage(String msg)
  {
	try
	{
	  out.writeUTF(msg);
	}
	catch (IOException e)
	{
	  log.error("Ошибка отправки сообщения", e);
	}
  }


  public String recieveMessage()
  throws ConnectionException
  {
	String msg = null;
	try
	{
	  msg = in.readUTF();
	  if (user != null)
		log.info(user.getNick() + " отправил сообщение: '" + msg + "'");
	}
	catch (SocketException | EOFException e)
	{
	  throw new ConnectionException();
	}
	catch (IOException e)
	{
	  log.error("Ошибка получения сообщения", e);
	}
	return msg;
  }


  private void sessionWork()
  {
	try
	{
	  authentication();
	  processMessages();
	}
	catch (ConnectionException e)
	{
	  log.info("Клиентское соединение потеряно");
	}
	finally
	{
	  disconnect();
	}
  }


  private void disconnect()
  {
	server.unsubscribe(this);
	try
	{
	  in.close();
	}
	catch (IOException e)
	{
	  log.error(e.getMessage());
	}
	try
	{
	  out.close();
	}
	catch (IOException e)
	{
	  log.error(e.getMessage());
	}
	try
	{
	  socket.close();
	}
	catch (IOException e)
	{
	  log.error(e.getMessage());
	}
  }


  private void processMessages()
  throws ConnectionException
  {
	while (true)
	{
	  String msg = recieveMessage();
	  if (msg == null) continue;

	  if (msg.equals(DISCONNECT_COMMAND)) return;

	  if (msg.startsWith(SEARCH_CONTACTS_COMMAND))
		searchContacts(msg);
	  else if (msg.startsWith(ADD_CONTACT_COMMAND))
		addContact(msg);
	  else if (msg.startsWith(REMOVE_CONTACT_COMMAND))
		removeContact(msg);
	  else if (msg.equals(GET_CONTACTS_COMMAND))
		getContacts();
	  else if (msg.startsWith(MSG_COMMAND))
		server.sendMessageToClient(this, msg);
	}
  }


  private void removeContact(String msg)
  {
	String contact = msg.replaceFirst(REMOVE_CONTACT_COMMAND, "").trim();

	UserService service = server.getUserService();
	boolean success = service.removeUserContact(user, contact);
	if (success) sendMessage(msg);
  }


  private void addContact(String msg)
  {
	String contact = msg.replaceFirst(ADD_CONTACT_COMMAND, "").trim();

	UserService service = server.getUserService();
	boolean success = service.addUserContact(user, contact);
	if (success) sendMessage(msg);
  }


  private void getContacts()
  {
	Set<User> contacts = user.getContacts();

	if (contacts.isEmpty()) return;

	String res = createContactsResponse(contacts, GET_CONTACTS_COMMAND);
	sendMessage(res);
  }


  private String createContactsResponse(Collection<User> contacts, String command)
  {
	String prefix = command + " ";
	String resp = contacts.stream()
						  .map(User::getNick)
						  .collect(joining(" ", prefix, ""));
	return resp;
  }


  private void searchContacts(String msg)
  {
	String str = msg.replaceFirst(SEARCH_CONTACTS_COMMAND, "").trim();

	UserService service = server.getUserService();
	List<User> users = service.searchUsers(str);
	users.remove(user);

	String res = createContactsResponse(users, SEARCH_CONTACTS_COMMAND);
	sendMessage(res);
  }


  private void authentication()
  throws ConnectionException
  {
	while (true)
	{
	  String msg = recieveMessage();
	  if (msg == null) continue;

	  if (msg.equals(DISCONNECT_COMMAND))
		return;

	  if (msg.startsWith(AUTH_COMMAND))
		if (doAuth(msg))
		  return;

	  if (msg.startsWith(REGISTER_COMMAND))
		doReg(msg);
	}
  }


  private void doReg(String msg)
  {
	AuthService service = server.getAuthService();

	String[] parts = msg.split("\\s");
	String username = parts[1];
	String password = parts[2];
	String nick = parts[3];
	String result = service.registerUser(username, password, nick);

	sendMessage(REGISTER_RESPONSE + " " + result);
  }


  private boolean doAuth(String msg)
  {
	AuthService service = server.getAuthService();

	String[] parts = msg.split("\\s");
	String username = parts[1];
	String password = parts[2];
	Optional<User> authUser = service.authUser(username, password);

	if (!authUser.isPresent())
	{
	  sendMessage(AUTH_FAIL_MSG);
	  return false;
	}

	if (server.isUserActive(authUser.get()))
	{
	  sendMessage(ACCOUNT_ACTIVE_MSG);
	  return false;
	}

	user = authUser.get();
	sendMessage(AUTH_RESPONSE_OK + " " + user.getNick());
	server.subscribe(this);
	return true;
  }


  private class ConnectionException
		  extends Exception
  {

  }

}