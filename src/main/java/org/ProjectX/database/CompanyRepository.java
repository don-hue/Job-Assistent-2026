package org.ProjectX.database;
import org.ProjectX.entity.CompanyEntity;
import org.ProjectX.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;


public class CompanyRepository {
    private static final CompanyRepository INSTANCE = new CompanyRepository();
    private CompanyRepository() {}

    public static CompanyRepository getInstance() {
        return INSTANCE;
    }
    public void updateShowCompany(String company){
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        try {
            Transaction tx = session.beginTransaction();
            try {
                CompanyEntity searchedCompany = session
                        .createQuery("FROM CompanyEntity WHERE companyName = :company", CompanyEntity.class)
                        .setParameter("company", company)
                        .uniqueResult();

                if (searchedCompany != null) {
                    searchedCompany.setShowCompany(false);
                    session.createMutationQuery(
                                    "DELETE FROM JobEntity WHERE company.id = :companyId")
                            .setParameter("companyId", searchedCompany.getId())
                            .executeUpdate();
                }
                tx.commit();

            } catch (Exception e) {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
                System.out.println("Error in updateCompany()" + e.getMessage());
                throw e;
            }

        } catch (Exception e) {
            System.out.println("Error in updateCompany()" + e.getMessage());
            throw e;
        }
        finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    public List<CompanyEntity> getAllCompanies() {
        SessionFactory factory = HibernateUtil.getSessionFactory();
        Session session = null;
        try {
            session = factory.openSession();
            List<CompanyEntity> companies = session.createQuery("FROM CompanyEntity", CompanyEntity.class)
                    .getResultList();

            return companies;
        } catch (RuntimeException e) {
            throw new RuntimeException("DB failed for" + e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void updateCompany(CompanyEntity company){
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        try {
            Transaction tx = session.beginTransaction();
            try {
                CompanyEntity searchedCompany = session.find(CompanyEntity.class,company.getId());
                if (searchedCompany != null) {
                   session.merge(company);
                }
                tx.commit();

            } catch (Exception e) {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
                System.out.println("Error in updateCompany()" + e.getMessage());
                throw e;
            }

        } catch (Exception e) {
            System.out.println("Error in updateCompany()" + e.getMessage());
            throw e;
        }
        finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

    }

}
