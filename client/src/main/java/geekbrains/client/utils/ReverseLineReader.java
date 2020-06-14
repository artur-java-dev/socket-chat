package geekbrains.client.utils;


import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

import static java.lang.Math.max;
import static java.nio.channels.FileChannel.MapMode.READ_ONLY;


public class ReverseLineReader
		implements AutoCloseable
{

  private long filePos;
  private ByteArrayOutputStream out = new ByteArrayOutputStream();
  private ByteBuffer buf;
  private int bufPos;
  private byte lastLineBreak = '\n';
  private final RandomAccessFile raf;
  private final FileChannel channel;
  private final String encoding;
  private static final int BUFFER_SIZE = 8192;
  private static final char CR = '\r';
  private static final char LF = '\n';
  private static final String EMPTY_STRING = "";


  public ReverseLineReader(Path path)
  throws IOException
  {
	this(path, null);
  }


  public ReverseLineReader(Path path, String fileEncoding)
  throws IOException
  {
	raf = new RandomAccessFile(path.toFile(), "r");
	encoding = fileEncoding;
	channel = raf.getChannel();
	filePos = raf.length();
  }


  @Override
  public void close()
  throws IOException
  {
	raf.close();
  }


  public String readLine()
  throws IOException
  {
	while (true)
	{
	  if (bufPos < 0)
	  {
		if (filePos == 0)
		{
		  if (out == null) return null;
		  String line = bufToString();
		  out = null;
		  return line;
		}

		long start = max(filePos - BUFFER_SIZE, 0);
		long len = filePos - start;

		buf = channel.map(READ_ONLY, start, len);
		bufPos = (int) len;
		filePos = start;

		// Ignore Empty New Lines
		byte c = buf.get(--bufPos);

		if (c == CR || c == LF)
		  while (bufPos > 0 && (c == CR || c == LF))
		  {
			bufPos--;
			c = buf.get(bufPos);
		  }

		if (!(c == CR || c == LF))
		  bufPos++;// IS THE NEW LINE
	  }


	  while (bufPos-- > 0)
	  {
		byte c = buf.get(bufPos);

		if (c == CR || c == LF)
		{
		  if (c != lastLineBreak)
		  {
			lastLineBreak = c;
			continue;
		  }
		  lastLineBreak = c;
		  return bufToString();
		}

		out.write(c);
	  }

	}
  }


  private String bufToString()
  throws UnsupportedEncodingException
  {
	if (out.size() == 0)
	  return EMPTY_STRING;

	byte[] bytes = out.toByteArray();

	for (int i = 0; i < bytes.length / 2; i++)
	{
	  byte t = bytes[i];
	  bytes[i] = bytes[bytes.length - i - 1];
	  bytes[bytes.length - i - 1] = t;
	}

	out.reset();

	if (encoding == null) return new String(bytes);

	return new String(bytes, encoding);
  }

}