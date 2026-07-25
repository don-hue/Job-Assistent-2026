package org.ProjectX.database;
import org.ProjectX.entity.SearchUrlEntity;
import org.ProjectX.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.List;

public class SearchUrlRepository {
    private static final SearchUrlRepository INSTANCE = new SearchUrlRepository();
    private SearchUrlRepository() {}
    public static SearchUrlRepository getInstance() {
        return INSTANCE;
    }

    public void saveSearch(List<String> urls, String keyword, String portal, String portalCode, String radius, boolean isCustom) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        try( Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                SearchUrlEntity search = session
                        .createQuery("""
                            SELECT s
                            FROM SearchUrlEntity s
                            JOIN s.urls u
                            WHERE u = :url
                        """, SearchUrlEntity.class)
                        .setParameter("url", urls.getFirst())
                        .uniqueResult();

                if (search == null) {
                    search = new SearchUrlEntity();
                    search.setUrl(urls);
                    search.setKeyword(keyword);
                    search.setPortal(portal);
                    search.setPostal_code(portalCode);
                    search.setRadius(radius);
                    search.setIsCustom(isCustom);
                    session.persist(search);
                }
                tx.commit();

            } catch (Exception e) {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
                System.out.println("Error" + e.getMessage());
                throw new RuntimeException("Crawl failed" + e);
            }

        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
            throw new RuntimeException("Crawl failed" + e);
        }
    }
    public void saveCommerzBankSearch(List<String> api, String keyword){
        SessionFactory factory = HibernateUtil.getSessionFactory();
        try(Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                SearchUrlEntity search = session
                        .createQuery("""
                            SELECT s
                            FROM SearchUrlEntity s
                            JOIN s.urls u
                            WHERE u = :api
                        """, SearchUrlEntity.class)
                        .setParameter("api", api.getFirst())
                        .uniqueResult();

                if (search == null ){
                    search = new SearchUrlEntity();
                    search.setUrl(api);
                    search.setKeyword(keyword);
                    search.setPortal("Commerzbank");
                    search.setIsCustom(false);
                    session.persist(search);
                }
                tx.commit();
            } catch (RuntimeException e) {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
                System.out.println("Error" + e.getMessage());
                throw new RuntimeException("Crawl failed" + e);
            }
        } catch(Exception e) {
            System.out.println("Error" + e.getMessage());
            throw new RuntimeException("Crawl failed" + e);

        }
    }
    public List<SearchUrlEntity> getAllSearchUrls() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                List<SearchUrlEntity> urls = session
                        .createQuery("FROM SearchUrlEntity", SearchUrlEntity.class)
                        .getResultList();

                tx.commit();
                return urls;
            } catch (RuntimeException e) {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
                System.out.println("Error" + e.getMessage());
                throw new RuntimeException("Crawl failed" + e);
            }
        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
            throw new RuntimeException("Crawl failed" + e);
        }
    }
    public void deleteSearch(String url) {
        SessionFactory factory = HibernateUtil.getSessionFactory();
        try(Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.createMutationQuery("DELETE FROM SearchUrlEntity WHERE url = :url")
                        .setParameter("url", url)
                        .executeUpdate();
                tx.commit();
            } catch (RuntimeException e) {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
                System.out.println("Error" + e.getMessage());
                throw new RuntimeException("Crawl failed" + e);
            }
        } catch(Exception e) {
            System.out.println("Error" + e.getMessage());
            throw new RuntimeException("Crawl failed" + e);
        }
    }
    public void updateSearch( String keyword,String postalCode, String radius, Long id){
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        try( Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                SearchUrlEntity entity = session.get(SearchUrlEntity.class, id);
                if(entity != null && keyword != null) {
                    entity.setKeyword(keyword);
                }
                if(entity != null && radius != null) {
                    entity.setRadius(radius);
                }
                if(entity != null && postalCode != null) {
                    entity.setPostal_code(postalCode);
                }
                tx.commit();

            } catch (Exception e) {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
                System.out.println("Error" + e.getMessage());
                throw new RuntimeException("Crawl failed" + e);
            }

        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
            throw new RuntimeException("Crawl failed" + e);
        }

    }
}
