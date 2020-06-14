package geekbrains.server.service;


import geekbrains.server.dao.UserDAO;
import geekbrains.server.domain.User;

import java.util.Optional;


public class DatabaseAuthService
		implements AuthService
{

  private UserDAO userRepository;
  private static final String REGISTER_FAIL_USERNAME = "логин занят другим пользователем";
  private static final String REGISTER_FAIL_NICK = "ник занят другим пользователем";
  private static final String REGISTER_OK = "success";


  public DatabaseAuthService(UserDAO dao)
  {
	userRepository = dao;
  }


  @Override
  public Optional<User> authUser(String username, String password)
  {
	Optional<User> user = userRepository.findByUsername(username);

	if (user.isPresent())
	{
	  if (user.get().getPassword().equals(password))
		return user;
	}

	return Optional.empty();
  }


  @Override
  public String registerUser(String username, String password, String nick)
  {
	Optional<User> user = userRepository.findByUsername(username);

	if (user.isPresent())
	  return REGISTER_FAIL_USERNAME;

	user = userRepository.findByNick(nick);

	if (user.isPresent())
	  return REGISTER_FAIL_NICK;

	userRepository.insert(new User(username, password, nick));
	return REGISTER_OK;
  }

}