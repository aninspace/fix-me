package edu.school42.fixme.market.repository;

import edu.school42.fixme.market.model.FixMessageEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class FixMessagesRepository {

	private final EntityManagerFactory factory = Persistence.createEntityManagerFactory("fixMeMarket");
	private final EntityManager entityManager = factory.createEntityManager();

	public void update(FixMessageEntity entity) {
		entityManager.getTransaction().begin();
		entityManager.persist(entity);
		entityManager.getTransaction().commit();
	}

}
