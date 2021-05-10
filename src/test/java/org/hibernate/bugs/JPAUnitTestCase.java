package org.hibernate.bugs;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.function.Function;

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

	// Entities are auto-discovered, so just add them anywhere on class-path
	// Add your tests, using standard JUnit.
	@Test
	public void hhh123Test() throws Exception {
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();

		createRoot(em);
		RootEntity loaded = queryForPlainEmbeddable(em);

		assertNotNull(loaded);

		em.getTransaction().commit();
		em.close();
	}

	private RootEntity queryForPlainEmbeddable(EntityManager em) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RootEntity> query = builder.createQuery(RootEntity.class);
		Root<RootEntity> root = query.from(RootEntity.class);

		Join<Object, Object> join = root.join("plainEmbeddable");

		Path<Object> name = join.get("plainName");

		query.select(root).where(builder.equal(name, "primary"));

		return em.createQuery(query).getSingleResult();
	}

	private <T> T withTransaction(EntityManager entityManager, Function<EntityManager, T> action) {

		T t = action.apply(entityManager);


		return t;
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
		root.secondTableEmbeddable = secondTableEmbeddable;

		em.persist(root);
		return root;
	}
}
