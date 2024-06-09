package persistance;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;


public class HibernateUtil {
    private static  SessionFactory sessionFactory=setup();

    protected static SessionFactory setup() {

        // A SessionFactory is set up once for an application!
        Logger log = Logger.getLogger("org.hibernate");
        log.setLevel(Level.WARNING);
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure() // configures settings
                // from
                // hibernate.cfg.xml
                .build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
            StandardServiceRegistryBuilder.destroy(registry);
        }
        return sessionFactory;
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void exit() {
        sessionFactory = HibernateUtil.getSessionFactory();
        sessionFactory.close();
    }

}