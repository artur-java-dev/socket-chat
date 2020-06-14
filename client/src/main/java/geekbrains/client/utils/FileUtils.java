package geekbrains.client.utils;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;


public class FileUtils
{


  private static final String FILE_ENCODING = "UTF-8";


  public static List<String> readLastLines(Path path, int linesCount)
  throws IOException
  {
	LinkedList<String> lines = new LinkedList<>();
	ReverseLineReader reader = new ReverseLineReader(path, FILE_ENCODING);

	String line;
	int i = 0;
	while ((line = reader.readLine()) != null && i != linesCount)
	{
	  lines.addFirst(line);
	  i++;
	}

	return lines;
  }


  public static void writeLines(Path path, List<String> lines)
  throws IOException
  {
	Files.write(path, lines);
  }

}