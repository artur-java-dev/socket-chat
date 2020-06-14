package geekbrains.client.utils;


import java.util.LinkedList;


public class Stack<T>
{

  private LinkedList<T> list;


  public Stack()
  {
	list = new LinkedList<>();
  }


  public void push(T element)
  {
	list.addFirst(element);
  }


  public T pop()
  {
	return list.removeFirst();
  }


  public T top()
  {
	return list.getFirst();
  }


  public boolean isEmpty()
  {
	return list.isEmpty();
  }


  public int size()
  {
	return list.size();
  }

}