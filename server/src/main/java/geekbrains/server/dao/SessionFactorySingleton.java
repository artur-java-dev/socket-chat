package geekbrains.server.dao;


import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class SessionFactorySingleton
{

  private static final String HIBERNATE_CONFIG = "hibernate.cfg.xml";
  private static SessionFactory inst;
  private static Configuration config;


  static
  {
	config = new Configuration();
	config = config.configure(HIBERNATE_CONFIG);
  }


  private SessionFactorySingleton()
  {
  }


  public static SessionFactory getInstance()
  {
	if (inst == null)
	{
	  synchronized (SessionFactory.class)
	  {
		if (inst == null) inst = config.buildSessionFactory();
	  }
	}

	return inst;
  }

}