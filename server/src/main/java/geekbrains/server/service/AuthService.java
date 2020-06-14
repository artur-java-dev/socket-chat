package geekbrains.server.service;


import geekbrains.server.domain.User;

import java.util.Optional;


public interface AuthService
{

  Optional<User> authUser(String username, String password);

  String registerUser(String username, String password, String nick);

}