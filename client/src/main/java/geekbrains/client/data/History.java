package geekbrains.client.data;


import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static geekbrains.client.utils.FileUtils.*;
import static org.slf4j.LoggerFactory.getLogger;


public class History
{

  private final Map<String, List<String>> historyByNick;
  private static final Logger log = getLogger(History.class);
  private static final String HISTORY_DIR = "client/history";
  private static final String FILE_EXT = ".txt";
  private static final int LAST_LINES_COUNT = 100;
  private static History inst;


  private History()
  {
	historyByNick = new HashMap<>();
  }


  public static History getInstance()
  {
	if (inst == null)
	{
	  synchronized (History.class)
	  {
		if (inst == null) inst = new History();
	  }
	}

	return inst;
  }


  public void load()
  {
	try
	{
	  Path dir = Paths.get(HISTORY_DIR);

	  if (!dir.toFile().exists())
		dir.toFile().mkdir();

	  Files.list(dir).forEach(this::loadFile);
	}
	catch (IOException e)
	{
	  log.error(e.getMessage());
	}
  }


  public void addMessage(String nick, String msg)
  {
	historyByNick.putIfAbsent(nick, new LinkedList<>());
	historyByNick.get(nick)
				 .add(msg);
  }


  public List<String> get(String nick)
  {
	return historyByNick.getOrDefault(nick, new LinkedList<>());
  }


  public void save()
  {
	historyByNick.forEach(this::saveToFile);
  }


  private void loadFile(Path path)
  {
	try
	{
	  List<String> contacts = Contacts.getInstance().getData();
	  String name = path.getFileName().toString().replaceFirst(FILE_EXT, "");

	  if (!contacts.contains(name))
		return;

	  List<String> lines = readLastLines(path, LAST_LINES_COUNT);
	  historyByNick.put(name, lines);
	}
	catch (IOException e)
	{
	  log.error(e.getMessage());
	}
  }


  private void saveToFile(String nick, List<String> history)
  {
	try
	{
	  Path path = Paths.get(HISTORY_DIR + "/" + nick + FILE_EXT);
	  writeLines(path, history);
	}
	catch (IOException e)
	{
	  log.error(e.getMessage());
	}
  }

}