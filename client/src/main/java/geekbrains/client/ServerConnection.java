package geekbrains.client;


import geekbrains.client.exceptions.ServerConnectionException;
import geekbrains.server.ServerCommands;
import org.slf4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static geekbrains.client.utils.UI.updateUI;
import static geekbrains.server.ServerCommands.*;
import static org.slf4j.LoggerFactory.getLogger;


public class ServerConnection
{

  private Socket socket;
  private DataInputStream in;
  private DataOutputStream out;
  private boolean authorized;


  private BiConsumer<String, String> showMessageUI;
  private Consumer<String> showServerMessageUI;
  private Consumer<String> showAuthResponseUI;
  private Consumer<String> showRegisterResponseUI;
  private Consumer<String> showContacts;
  private Consumer<String[]> getContactsUIcallback;
  private Consumer<String> addContactCallback;
  private Consumer<String> removeContactCallback;
  private Runnable showMainUI;
  private Thread.UncaughtExceptionHandler exceptionHandler;


  private static final String SERVER_HOST = "localhost";
  private static final int SERVER_PORT = 8189;
  private static final Logger log = getLogger(ServerConnection.class);
  private static ServerConnection conn;


  private ServerConnection()
  {
	exceptionHandler = new ExceptionHandler();
	setAuthorized(false);
  }


  public static ServerConnection getInstance()
  {
	if (conn == null)
	{
	  synchronized (ServerConnection.class)
	  {
		if (conn == null)
		  conn = new ServerConnection();
	  }
	}

	return conn;
  }


  public void setShowMessageCallback(BiConsumer<String, String> callback)
  {
	showMessageUI = callback;
  }


  public void setShowServerMessageUI(Consumer<String> callback)
  {
	showServerMessageUI = callback;
  }


  public void setShowAuthResponseUI(Consumer<String> callback)
  {
	showAuthResponseUI = callback;
  }


  public void setShowRegisterResponseUI(Consumer<String> callback)
  {
	showRegisterResponseUI = callback;
  }


  public void setShowMainUI(Runnable callback)
  {
	showMainUI = callback;
  }


  public void setShowContactsUI(Consumer<String> callback)
  {
	showContacts = callback;
  }


  public void sendMessage(String msg)
  throws IOException
  {
	out.writeUTF(msg);
  }


  public void sendMessageToContact(String contact, String msg)
  throws IOException
  {
	String cmd = MSG_COMMAND + " " + contact + " " + msg;
	conn.sendMessage(cmd);
  }


  public void connect()
  throws ServerConnectionException
  {
	try
	{
	  socket = new Socket(SERVER_HOST, SERVER_PORT);
	  in = new DataInputStream(socket.getInputStream());
	  out = new DataOutputStream(socket.getOutputStream());
	}
	catch (IOException e)
	{
	  throw new ServerConnectionException("Server not found");
	}

	Thread t = new Thread(this::sessionWork);
	t.setName("session thread");
	t.setUncaughtExceptionHandler(exceptionHandler);
	t.setDaemon(true);
	t.start();
  }


  public void disconnect()
  throws IOException
  {
	sendMessage(ServerCommands.DISCONNECT_COMMAND);
  }


  public void close()
  {
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


  public String recieveMessage()
  throws IOException
  {
	return in.readUTF();
  }


  public void getContacts(Consumer<String[]> callback)
  throws IOException
  {
	getContactsUIcallback = callback;
	conn.sendMessage(GET_CONTACTS_COMMAND);
  }


  public void addContact(String contact, Consumer<String> callback)
  throws IOException
  {
	addContactCallback = callback;
	String msg = ADD_CONTACT_COMMAND + " " + contact;
	conn.sendMessage(msg);
  }


  public void removeContact(String contact, Consumer<String> callback)
  throws IOException
  {
	removeContactCallback = callback;
	String msg = REMOVE_CONTACT_COMMAND + " " + contact;
	conn.sendMessage(msg);
  }


  private void sessionWork()
  {
	try
	{
	  waitAuthResponse();
	  recieveMessages();
	}
	catch (Exception e)
	{
	  log.error(e.getMessage());
	}
  }


  private void recieveMessages()
  throws IOException
  {
	while (true)
	{
	  String msg = recieveMessage();

	  if (msg.startsWith(SEARCH_CONTACTS_COMMAND))
	  {
		updateUI(() -> showContacts.accept(msg));
	  }
	  else if (msg.startsWith(GET_CONTACTS_COMMAND))
	  {
		String[] contacts = msg.replaceFirst(GET_CONTACTS_COMMAND, "")
							   .trim()
							   .split("\\s");
		updateUI(() -> getContactsUIcallback.accept(contacts));
	  }
	  else if (msg.startsWith(ADD_CONTACT_COMMAND))
	  {
		String contact = msg.replaceFirst(ADD_CONTACT_COMMAND, "").trim();
		updateUI(() -> addContactCallback.accept(contact));
	  }
	  else if (msg.startsWith(REMOVE_CONTACT_COMMAND))
	  {
		String contact = msg.replaceFirst(REMOVE_CONTACT_COMMAND, "").trim();
		updateUI(() -> removeContactCallback.accept(contact));
	  }
	  else if (msg.startsWith(MSG_FROM))
	  {
		String[] tokens = msg.split("\\s");
		String nick = tokens[1];
		String msgFrom = msg.replaceFirst(MSG_FROM, "")
							.replaceFirst(nick, "")
							.trim();
		updateUI(() -> showMessageUI.accept(nick, msgFrom));
	  }
	  else if (msg.startsWith(SERVER_MSG))
	  {
		String msgServer = msg.replaceFirst(SERVER_MSG, "").trim();
		updateUI(() -> showServerMessageUI.accept(msgServer));
	  }
	}
  }


  private void waitAuthResponse()
  throws IOException
  {
	while (true)
	{
	  String response = recieveMessage();

	  if (response.startsWith(AUTH_RESPONSE_OK))
	  {
		setAuthorized(true);
		updateUI(() -> showMainUI.run());
		break;
	  }

	  if (response.startsWith(REGISTER_RESPONSE))
	  {
		updateUI(() -> showRegisterResponseUI.accept(response));
		continue;
	  }

	  updateUI(() -> showAuthResponseUI.accept(response));
	}
  }


  private void setAuthorized(boolean flag)
  {
	authorized = flag;
  }


  private class ExceptionHandler
		  implements Thread.UncaughtExceptionHandler
  {

	@Override
	public void uncaughtException(Thread t, Throwable e)
	{
	  String msg = "in " + t.getName() + " occured error :\n" + e.getCause();
	  log.error(msg);
	}

  }

}