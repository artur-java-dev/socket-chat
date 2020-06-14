package geekbrains.server.dao;


import java.util.Collection;
import java.util.Optional;


public interface DAOInterface<T>
{

  void insert(T entity);

  void delete(T entity);

  void update(T entity);

  Optional<T> findByID(Long id);

  Collection<T> findAll();

}