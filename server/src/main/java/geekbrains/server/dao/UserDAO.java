package geekbrains.server.dao;


import geekbrains.server.domain.User;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.*;


public class UserDAO
		extends AbstractDAO<User>
{


  public UserDAO(EntityManager entityManager)
  {
	super(User.class, entityManager);
  }


  public void deleteByID(long id)
  {
	super.disableTransactionControl();
	beginTrans();

	try
	{
	  Optional<User> user = findByID(id);
	  if (user.isPresent())
		delete(user.get());
	  else
		throw new IllegalArgumentException("no user with such id");
	}
	finally
	{
	  endTrans();
	}
  }


  public void deleteAll()
  {
	super.disableTransactionControl();
	beginTrans();

	try
	{
	  Query query = entityManager.createQuery("DELETE FROM users");
	  query.executeUpdate();
	}
	finally
	{
	  endTrans();
	}
  }


  public Optional<User> findByUsername(String username)
  {
	TypedQuery<User> query = entityManager.createNamedQuery("User.findByUsername", User.class);
	query.setParameter("username", username);
	try
	{
	  User user = query.getSingleResult();
	  return ofNullable(user);
	}
	catch (NoResultException e)
	{
	  return empty();
	}
  }


  public Optional<User> findByNick(String nick)
  {
	TypedQuery<User> query = entityManager.createNamedQuery("User.findByNick", User.class);
	query.setParameter("nick", nick);
	try
	{
	  User user = query.getSingleResult();
	  return ofNullable(user);
	}
	catch (NoResultException e)
	{
	  return empty();
	}
  }


  public List<User> findByNickStartingWith(String part)
  {
	TypedQuery<User> query = entityManager.createNamedQuery("User.findByNickStartingWith", User.class);
	query.setParameter("part", part);
	return query.getResultList();
  }

}