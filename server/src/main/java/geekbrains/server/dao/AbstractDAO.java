package geekbrains.server.dao;


import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.text.MessageFormat.format;


public abstract class AbstractDAO<T>
		implements DAOInterface<T>
{

  private Class<T> clazz;
  private boolean enableTransaction;
  protected EntityManager entityManager;


  protected AbstractDAO(Class<T> entityClass, EntityManager em)
  {
	clazz = entityClass;
	entityManager = em;
	enableTransaction = true;
  }


  @Override
  public void insert(T entity)
  {
	if (enableTransaction) beginTrans();
	entityManager.persist(entity);
	if (enableTransaction) endTrans();
  }


  @Override
  public void delete(T entity)
  {
	if (enableTransaction) beginTrans();
	entityManager.remove(entity);
	if (enableTransaction) endTrans();
  }


  @Override
  public void update(T entity)
  {
	if (enableTransaction) beginTrans();
	entityManager.merge(entity);
	if (enableTransaction) endTrans();
  }


  @Override
  public Optional<T> findByID(Long id)
  {
	if (enableTransaction) beginTrans();
	T entity = entityManager.find(clazz, id);
	if (enableTransaction) endTrans();

	return Optional.ofNullable(entity);
  }


  @Override
  public Collection<T> findAll()
  {
	if (enableTransaction) beginTrans();
	String q = format("SELECT e FROM {0} e", clazz.getSimpleName());
	TypedQuery<T> query = entityManager.createQuery(q, clazz);
	List<T> all = query.getResultList();
	if (enableTransaction) endTrans();

	return all;
  }


  protected void endTrans()
  {
	entityManager.getTransaction().commit();
	enableTransaction = true;
  }


  protected void beginTrans()
  {
	entityManager.getTransaction().begin();
  }


  protected void disableTransactionControl()
  {
	enableTransaction = false;
  }

}