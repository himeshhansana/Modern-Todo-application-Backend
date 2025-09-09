package utils;

import java.io.InputStream;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author hasi
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {
            InputStream in = HibernateUtil.class.getClassLoader().getResourceAsStream("hibernate.cfg.xml");
            System.out.println(in == null ? "Not found" : "Found");
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable e) {
            System.out.println("Initial SessionFactory creation failed." + e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        sessionFactory.close();
    }
}
