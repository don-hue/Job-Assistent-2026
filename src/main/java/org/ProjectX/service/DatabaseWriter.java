package org.ProjectX.service;

import org.ProjectX.entity.CompanyEntity;
import org.ProjectX.entity.JobEntity;
import org.ProjectX.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseWriter implements Runnable {

    public static final JobEntity POISON_PILL = new JobEntity();
    private static final int BATCH_SIZE = 10;

    private final BlockingQueue<JobEntity> queue;
    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public DatabaseWriter(
            BlockingQueue<JobEntity> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        List<JobEntity> buffer = new ArrayList<>(BATCH_SIZE);
        AtomicInteger amountOfTotalJobs = new AtomicInteger(0);
        AtomicInteger amountOfFlushes = new AtomicInteger(0);
        boolean running = true;
        while (running) {
            try {
                JobEntity job = queue.take();
                amountOfTotalJobs.getAndAdd(1);
                if(job == POISON_PILL) {
                    System.out.println("PoisenPill and the jobsamount is " + amountOfTotalJobs);
                    System.out.println("PoisenPill and flushed " + amountOfFlushes);
                    flushBuffer(buffer);
                    running = false;
                    continue;
                }
                buffer.add(job);

                if (buffer.size() >= BATCH_SIZE) {
                    amountOfFlushes.getAndAdd(1);
                    System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++");
                    System.out.println("Buffer size before flush"+ buffer.size());
                    flushBuffer(buffer);
                    System.out.println("Buffer size after flush"+ buffer.size());
                    System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++");
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

                running = false;
            }
        }
    }

    private void flushBuffer(List<JobEntity> buffer) {
        if (buffer.isEmpty()) {
            return;
        }
        Session session = null;
        Transaction tx = null;

        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();

            for (JobEntity job : buffer) {
                System.out.println("Company " + job.getCompany().getCompanyName() );
                CompanyEntity company = session
                        .createQuery("FROM CompanyEntity WHERE companyName = :name", CompanyEntity.class)
                        .setParameter("name", job.getCompany().getCompanyName())
                        .uniqueResult();


                if(company == null){
                   session.persist(job.getCompany());
                }

                job.setCompany(company);
                session.persist(job);
            }
            session.flush();
            tx.commit();
        } catch (Exception e) {
            System.out.println("Message: " + e.getMessage());
            safeRollback(tx);

        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
            buffer.clear();
        }
    }
    private void safeRollback(Transaction tx) {
        try {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
        } catch (Exception ignored) {
        }
    }
}



