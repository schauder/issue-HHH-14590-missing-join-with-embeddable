package org.hibernate.bugs;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
public class JPAUnitTestCase {

	private EntityManagerFactory entityManagerFactory;

	@Before
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory( "templatePU" );
	}

	@After
	public void destroy() {
		entityManagerFactory.close();
	}

	@Test
	public void plain() throws Exception {
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();

		createRoot(em);
		Long loaded = queryForPlainEmbeddable(em);

		assertNotNull(loaded);

		em.getTransaction().commit();
		em.close();
	}

	@Test
	public void secondarySingleColumnSelect() throws Exception {
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();

		createRoot(em);
		Object loaded = queryForSecondaryEmbeddableId(em);

		assertNotNull(loaded);

		em.getTransaction().commit();
		em.close();
	}

	@Test
	public void secondaryEntitySelect() throws Exception {
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();

		createRoot(em);
		Object loaded = queryForSecondaryEmbeddable(em);

		assertNotNull(loaded);

		em.getTransaction().commit();
		em.close();
	}


	@Test
	public void secondarySingleColumnSelectNoJoin() throws Exception {
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();

		createRoot(em);
		Object loaded = queryForSecondaryEmbeddableIdNoJoin(em);

		assertNotNull(loaded);

		em.getTransaction().commit();
		em.close();
	}

	private Long queryForSecondaryEmbeddableId(EntityManager em) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<RootEntity> root = query.from(RootEntity.class);

		Join<Object, Object> join = root.join("secondaryEmbeddable");

		Path<Object> name = join.get("secName");

		query.select(root.get("id")).where(builder.equal(name, "secondary"));

		return em.createQuery(query).getSingleResult();
	}

	private Long queryForPlainEmbeddable(EntityManager em) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<RootEntity> root = query.from(RootEntity.class);

		Join<Object, Object> join = root.join("plainEmbeddable");

		Path<Object> name = join.get("plainName");

		query.select(root.get("id")).where(builder.equal(name, "primary"));

		return em.createQuery(query).getSingleResult();
	}

	private RootEntity queryForSecondaryEmbeddable(EntityManager em) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RootEntity> query = builder.createQuery(RootEntity.class);
		Root<RootEntity> root = query.from(RootEntity.class);

		Join<Object, Object> join = root.join("secondaryEmbeddable");

		Path<Object> name = join.get("secName");

		query.select(root).where(builder.equal(name, "secondary"));

		return em.createQuery(query).getSingleResult();
	}

	private Long queryForSecondaryEmbeddableIdNoJoin(EntityManager em) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<RootEntity> root = query.from(RootEntity.class);

		Path<Object> path = root.get("secondaryEmbeddable");

		Path<Object> name = path.get("secName");

		query.select(root.get("id")).where(builder.equal(name, "secondary"));

		return em.createQuery(query).getSingleResult();
	}

	private RootEntity createRoot(EntityManager em) {

		PlainEmbeddable plainEmbeddable = new PlainEmbeddable();
		plainEmbeddable.plainName = "primary";
		plainEmbeddable.plainValue = "one";

		SecondTableEmbeddable secondTableEmbeddable = new SecondTableEmbeddable();
		secondTableEmbeddable.secName = "secondary";
		secondTableEmbeddable.secValue = "two";

		RootEntity root = new RootEntity();
		root.rootValue = "root";
		root.plainEmbeddable = plainEmbeddable;
		root.secondaryEmbeddable = secondTableEmbeddable;

		em.persist(root);
		return root;
	}
}
