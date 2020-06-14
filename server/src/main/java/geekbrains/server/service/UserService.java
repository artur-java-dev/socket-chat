package geekbrains.server.service;


import geekbrains.server.dao.UserDAO;
import geekbrains.server.domain.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class UserService
{

  private UserDAO userRepository;


  public UserService(UserDAO dao)
  {
	userRepository = dao;
  }


  public List<User> searchUsers(String searchStr)
  {
	if (searchStr.length() < 4)
	{
	  Optional<User> user = userRepository.findByNick(searchStr);
	  return
			  user.map(Collections::singletonList)
				  .orElse(Collections.emptyList());
	}

	return userRepository.findByNickStartingWith(searchStr);
  }


  public boolean addUserContact(User user, String contactNick)
  {
	Optional<User> contact = userRepository.findByNick(contactNick);
	if (contact.isPresent())
	{
	  user.getContacts().add(contact.get());
	  userRepository.update(user);
	  return true;
	}

	return false;
  }


  public boolean removeUserContact(User user, String contactNick)
  {
	boolean removed = user.getContacts()
						  .removeIf(x -> x.getNick().equals(contactNick));
	if (removed)
	  userRepository.update(user);

	return removed;
  }

}