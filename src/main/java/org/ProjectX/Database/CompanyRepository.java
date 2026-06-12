package org.ProjectX.Database;
import org.ProjectX.entity.CompanyEntity;
import org.ProjectX.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.net.URL;
import java.util.List;

public class CompanyRepository {
    private static final CompanyRepository INSTANCE = new CompanyRepository();
    private CompanyRepository() {};

    public static CompanyRepository getInstance() {
        return INSTANCE;
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
