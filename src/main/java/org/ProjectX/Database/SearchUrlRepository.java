package org.ProjectX.Database;
import org.ProjectX.entity.CompanyEntity;
import org.ProjectX.entity.SearchUrlEntity;
import org.ProjectX.util.HibernateUtil;
import org.ProjectX.util.Utils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.List;

public class SearchUrlRepository {
    private static final SearchUrlRepository INSTANCE = new SearchUrlRepository();
    private SearchUrlRepository() {};
    Utils utils = Utils.getInstance();
    public static SearchUrlRepository getInstance() {
        return INSTANCE;
    }

    public void saveSearch(String url) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        try( Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            try {
                SearchUrlEntity urlDB = session
                        .createQuery("FROM SearchUrlEntity WHERE url = :url", SearchUrlEntity.class)
                        .setParameter("url", url)
                        .uniqueResult();

                if (urlDB == null) {
                    urlDB = new SearchUrlEntity();
                    urlDB.setUrl(url);
                    session.persist(urlDB);
                }

                tx.commit();

            } catch (Exception e) {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
                System.out.println("Error" + e.getMessage());
                throw e;
            }

        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
        }
    }
    public void saveCommerzBankSearch(String api){
        SessionFactory factory = HibernateUtil.getSessionFactory();
        try(Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                SearchUrlEntity search = session.createQuery("FROM SearchUrlEntity WHERE url = :api", SearchUrlEntity.class)
                        .setParameter("api", api)
                        .uniqueResult();

                if (search == null ){
                    search = new SearchUrlEntity();
                    search.setUrl(api);
                    session.persist(search);
                }
                tx.commit();
            } catch (RuntimeException e) {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
                System.out.println("Error" + e.getMessage());
                throw e;
            }
        } catch(Exception e) {
            System.out.println("Error" + e.getMessage());
            throw e;

        }
    }
    public List<SearchUrlEntity> getAllSearchUrls() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            List<SearchUrlEntity> urls = session
                    .createQuery("FROM SearchUrlEntity", SearchUrlEntity.class)
                    .getResultList();

            tx.commit();
            return urls;
        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
            throw e;
        }
    }
}
