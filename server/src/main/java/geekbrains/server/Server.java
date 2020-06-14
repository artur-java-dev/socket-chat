package geekbrains.server;


import geekbrains.server.dao.SessionFactorySingleton;
import geekbrains.server.dao.UserDAO;
import geekbrains.server.domain.User;
import geekbrains.server.service.AuthService;
import geekbrains.server.service.DatabaseAuthService;
import geekbrains.server.service.UserService;
import org.slf4j.Logger;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static geekbrains.server.ServerCommands.*;
import static org.slf4j.LoggerFactory.getLogger;


public class Server
{

  private List<ClientConnection> clients;
  private AuthService authService;
  private UserService userService;
  private static final Logger log = getLogger(Server.class);
  private static final EntityManager entityManager =
		  SessionFactorySingleton.getInstance().createEntityManager();


  public Server()
  {
	clients = new ArrayList<>();
	UserDAO dao = new UserDAO(entityManager);
	authService = new DatabaseAuthService(dao);
	userService = new UserService(dao);
  }


  public void start(int port)
  {
	try (ServerSocket server = new ServerSocket(port))
	{
	  while (true)
	  {
		log.info("Сервер ожидает подключения");
		Socket socket = server.accept();
		new ClientConnection(this, socket);
		log.info("Клиент подключился");
	  }
	}
	catch (IOException e)
	{
	  log.error("Ошибка сервера", e);
	}
  }


  public AuthService getAuthService()
  {
	return authService;
  }


  public synchronized boolean isUserActive(User user)
  {
	return clients.stream()
				  .anyMatch(x -> x.getUser().equals(user));
  }


  public synchronized void broadcastMessage(String msg)
  {
	clients.forEach(x -> x.sendMessage(msg));
  }


  public synchronized void sendMessageToClient(ClientConnection from, String msg)
  {
	String[] tokens = msg.split("\\s");
	String nickTo = tokens[1];
	ClientConnection client = getClientByNick(nickTo);

	if (client != null)
	{
	  String nickFrom = from.getUser().getNick();
	  String msgToClient = msg.replaceFirst(MSG_COMMAND, MSG_FROM)
							  .replaceFirst(nickTo, nickFrom);

	  client.sendMessage(msgToClient);
	}
	else
	{
	  String msgServer = SERVER_MSG + " " + nickTo + " offline";
	  from.sendMessage(msgServer);
	}
  }


  public synchronized void subscribe(ClientConnection client)
  {
	clients.add(client);
  }


  public synchronized void unsubscribe(ClientConnection client)
  {
	clients.remove(client);
  }


  public UserService getUserService()
  {
	return userService;
  }


  private ClientConnection getClientByNick(String nick)
  {
	return clients.stream()
				  .filter(x -> x.getUser().getNick().equals(nick))
				  .findFirst()
				  .orElse(null);
  }

}