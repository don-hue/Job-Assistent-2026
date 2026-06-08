package org.ProjectX.Database;
import org.ProjectX.dto.JobDto;
import org.ProjectX.entity.CompanyEntity;
import org.ProjectX.entity.JobEntity;
import org.ProjectX.entity.SearchUrlEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.ProjectX.util.HibernateUtil;


import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Collections;
import java.util.List;

public class LocalDAO {
    private static final LocalDAO INSTANCE = new LocalDAO();
    private LocalDAO() {}

    public static LocalDAO getInstance() {
        return  INSTANCE;
    }

    private static final String URL = "jdbc:h2:file:./data/localDB";
    private static final String USER = "localUser";
    private static final String PASSWORD = "";



    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public void saveJobs(JobDto dto){
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        try( Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            try {
                CompanyEntity company = session
                        .createQuery("FROM CompanyEntity WHERE companyName = :name", CompanyEntity.class)
                        .setParameter("name", dto.companyName())
                        .uniqueResult();

                if (company == null) {
                    company = new CompanyEntity();
                    company.setCompanyName(dto.companyName());
                    company.setShowCompany(true);
                    session.persist(company);
                }


                JobEntity job = session
                        .createQuery("FROM JobEntity WHERE company.companyName = :name AND jobTitle = :job", JobEntity.class )
                        .setParameter("name",dto.companyName())
                        .setParameter("job", dto.job())
                        .uniqueResult();

                if(job == null) {
                    job = new JobEntity();
                    job.setJobTitle(dto.job());
                    job.setCompany(company);
                }

                session.persist(job);

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
            throw e;
        }
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
    public void updateCompanyURL(URL url, Long id) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        try(Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            try {
                CompanyEntity company = session.get(CompanyEntity.class, id);
                if (company != null) {
                    company.setUrl(url);
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
    public void updateCompany(String company){
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        try(Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                CompanyEntity searchedCompany = session
                        .createQuery("FROM CompanyEntity WHERE companyName = :company", CompanyEntity.class)
                        .setParameter("company", company)
                        .uniqueResult();

                if (searchedCompany != null) {
                    searchedCompany.setShowCompany(false);
                }

                tx.commit();

            } catch (Exception e) {
                if (tx != null && tx.isActive()) {
                    tx.rollback(); // ✅ session still open here
                }
                System.out.println("Error in updateCompany()" + e.getMessage());
                throw e; // or return false
            }

        } catch (Exception e) {
            System.out.println("Error in updateCompany()" + e.getMessage());
        }
    }
    public List<JobEntity> getAllJobs() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = null;

        try(session) {
            tx = session.beginTransaction();
            List<JobEntity> jobs = session
                    .createQuery("FROM JobEntity WHERE company.showCompany = true", JobEntity.class)
                    .getResultList();

            tx.commit();

            return jobs == null
                    ? Collections.emptyList()
                    : jobs;

        } catch (Exception e) {
            System.out.println("Error :" + e.getMessage());
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }

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
    public List<CompanyEntity> getCompaniesWithoutUrl(){
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        try( Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                List<CompanyEntity> companies = session.createQuery("FROM CompanyEntity WHERE url IS NUll", CompanyEntity.class)
                        .getResultList();

                tx.commit();
                return companies;

            } catch (Exception e) {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
                System.out.println("Error" + e.getMessage());
                throw e;
            }

        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
            throw e;
        }
    }

}
