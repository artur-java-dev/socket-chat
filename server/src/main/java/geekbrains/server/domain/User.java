package geekbrains.server.domain;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;


@Entity
@NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE username = :username")
@NamedQuery(name = "User.findByNick", query = "SELECT u FROM User u WHERE nick = :nick")
@NamedQuery(name = "User.findByNickStartingWith",
			query = "SELECT u FROM User u WHERE lower(nick) like concat(lower(:part),'%')")
@Table(name = "users")
public class User
		implements Serializable
{

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "username", unique = true, nullable = false)
  private String username;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "nick", unique = true, nullable = false)
  private String nick;
  @ManyToMany
  @JoinTable(name = "contacts",
			 joinColumns = @JoinColumn(name = "id"),
			 inverseJoinColumns = @JoinColumn(name = "contact_id")
  )
  private Set<User> contacts;


  public User()
  {
  }


  public User(String username, String password, String nick)
  {
	setUsername(username);
	setPassword(password);
	setNick(nick);
  }


  public Set<User> getContacts()
  {
	return contacts;
  }


  public void setContacts(Set<User> contacts)
  {
	this.contacts = contacts;
  }


  public String getNick()
  {
	return nick;
  }


  public void setNick(String nick)
  {
	this.nick = nick;
  }


  public Long getId()
  {
	return id;
  }


  public void setId(Long id)
  {
	this.id = id;
  }


  public String getUsername()
  {
	return username;
  }


  public void setUsername(String username)
  {
	this.username = username;
  }


  public String getPassword()
  {
	return password;
  }


  public void setPassword(String password)
  {
	this.password = password;
  }

}