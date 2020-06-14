package geekbrains.server;


import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;


class ServerApp
{

  static final int PORT = 8189;
  static final Logger log = getLogger(ServerApp.class);


  public static void main(String... args)
  {
	Server server = new Server();

	log.info("Сервер запущен на порту " + PORT);
	server.start(PORT);
  }

}