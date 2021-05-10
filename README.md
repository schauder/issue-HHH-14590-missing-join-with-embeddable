Reproducer for https://hibernate.atlassian.net/browse/HHH-14590

== Join not rendered when requested for embedded in secondary table if a single column is selected using the Criteria API

Related Spring Data issue: https://github.com/spring-projects/spring-data-jpa/issues/2209

Given the following entities

```
@SecondaryTable(name = "secondary")
@Entity
public class RootEntity {
@Id
@GeneratedValue
Long id;

	String rootValue;

	@Embedded
	PlainEmbeddable plainEmbeddable;

	@Embedded
	SecondTableEmbeddable secondaryEmbeddable;
}
```
```
@Embeddable
public class PlainEmbeddable {
String plainName;
String plainValue;
}
```
```
@Embeddable
public class SecondTableEmbeddable {
@Column(table = "secondary")
String secName;
@Column(table = "secondary")
String secValue;
}
```

This query fails because it renders a SQL statement with a missing join:

```
CriteriaBuilder builder = em.getCriteriaBuilder();
CriteriaQuery<Long> query = builder.createQuery(Long.class);
Root<RootEntity> root = query.from(RootEntity.class);

		Join<Object, Object> join = root.join("secondaryEmbeddable");

		Path<Object> name = join.get("secName");

		query.select(root.get("id")).where(builder.equal(name, "secondary"));

		em.createQuery(query).getSingleResult();
```

The rendered select is (Note the missing join and the wrong table used for `secName`):
```
select
rootentity0_.id as col_0_0_
from
RootEntity rootentity0_
where
rootentity0_1_.secName=?
```


The following variants work fine:

**Embeddable not stored in secondary table**
```
CriteriaBuilder builder = em.getCriteriaBuilder();
CriteriaQuery<Long> query = builder.createQuery(Long.class);
Root<RootEntity> root = query.from(RootEntity.class);

Join<Object, Object> join = root.join("plainEmbeddable");  // <<=== !!!!!!!!

Path<Object> name = join.get("plainName");

query.select(root.get("id")).where(builder.equal(name, "primary"));

em.createQuery(query).getSingleResult();
```

**Selecting the full entity**
```
CriteriaBuilder builder = em.getCriteriaBuilder();
CriteriaQuery<RootEntity> query = builder.createQuery(RootEntity.class);
Root<RootEntity> root = query.from(RootEntity.class);

Join<Object, Object> join = root.join("secondaryEmbeddable");

Path<Object> name = join.get("secName");

query.select(root).where(builder.equal(name, "secondary")); // <<=== !!!!!!!!

em.createQuery(query).getSingleResult();
```

**Using a path instead of a join**
```
CriteriaBuilder builder = em.getCriteriaBuilder();
CriteriaQuery<Long> query = builder.createQuery(Long.class);
Root<RootEntity> root = query.from(RootEntity.class);

Path<Object> path = root.get("secondaryEmbeddable"); // <<=== !!!!!!!!

Path<Object> name = path.get("secName");

query.select(root.get("id")).where(builder.equal(name, "secondary"));

em.createQuery(query).getSingleResult();
```