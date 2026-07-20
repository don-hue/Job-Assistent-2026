package org.ProjectX.database;
import org.ProjectX.dto.JobDto;
import org.ProjectX.entity.CompanyEntity;
import org.ProjectX.entity.JobEntity;
import org.ProjectX.entity.SearchUrlEntity;
import org.ProjectX.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.Collections;
import java.util.List;

public class JobRepository {
    private static final JobRepository INSTANCE = new JobRepository();
    private JobRepository(){}

    public static JobRepository getInstance() {
        return INSTANCE;
    }

    public void saveJob(JobDto dto){
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
                    company.setUrl(dto.companyUrl());
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
                    job.setSearch(dto.search());
                    job.setApplied(false);
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
    public List<JobEntity> getAllJobs() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = null;

        try(session) {
            tx = session.beginTransaction();
            List<JobEntity> jobs = session
                    .createQuery("FROM JobEntity WHERE company.showCompany = true ORDER BY lower(company.companyName)", JobEntity.class)
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
    public void updateAppliedJob(boolean applied,String company, String jobTitle) {
        SessionFactory factory = HibernateUtil.getSessionFactory();
        try(Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();

            try {
                JobEntity job = session
                        .createQuery("FROM JobEntity WHERE jobTitle = :title AND company.companyName = :company ", JobEntity.class)
                        .setParameter("title", jobTitle)
                        .setParameter("company", company)
                        .uniqueResult();
                if (job != null) {
                    job.setApplied(applied);
                }
                tx.commit();
            } catch (Exception e) {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
                System.out.println("Error" + e.getMessage());
                throw e;
            }
        }catch (Exception e) {
            System.out.println("Error" + e.getMessage());
        }
    }
    public void deleteJobsBySearchId(Long id) {
        SessionFactory factory = HibernateUtil.getSessionFactory();
        try(Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.createMutationQuery("DELETE FROM JobEntity WHERE search.id = :id")
                        .setParameter("id", id)
                        .executeUpdate();
                tx.commit();
            } catch (RuntimeException e) {
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
}
