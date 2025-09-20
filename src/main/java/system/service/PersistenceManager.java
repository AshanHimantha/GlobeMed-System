package system.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Singleton to manage the JPA EntityManagerFactory.
 * Ensures the factory is created only once.
 */
public class PersistenceManager {
    private static PersistenceManager instance;
    private final EntityManagerFactory emf;

    private PersistenceManager() {

        emf = Persistence.createEntityManagerFactory("globemed-pu");
    }

    public static PersistenceManager getInstance() {
        if (instance == null) {
            instance = new PersistenceManager();
        }
        return instance;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}