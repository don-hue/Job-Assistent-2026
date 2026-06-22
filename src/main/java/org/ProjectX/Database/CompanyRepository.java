package org.ProjectX.Database;
import org.ProjectX.entity.CompanyEntity;
import org.ProjectX.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;


public class CompanyRepository {
    private static final CompanyRepository INSTANCE = new CompanyRepository();
    private CompanyRepository() {}

    public static CompanyRepository getInstance() {
        return INSTANCE;
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
    }
}
