package com.farmsense.repository;

import com.farmsense.dto.BatchCardProjection;
import com.farmsense.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class BatchCriteriaRepository {

    @PersistenceContext
    private EntityManager em;

    /* ================= MAIN QUERY ================= */
    public List<BatchCardProjection> findBatchCards(
            UUID userId,
            String status,
            String birdType,
            String sortBy,
            String sortDir,
            int page,
            int size
    ) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<BatchCardProjection> cq =
                cb.createQuery(BatchCardProjection.class);

        Root<Batch> batch = cq.from(Batch.class);
        Join<Batch, Farm> farm = batch.join("farm");

        /* ================= SUBQUERIES ================= */

        /* -------- LATEST PROFIT -------- */
        Subquery<LocalDateTime> maxProfitDate =
                cq.subquery(LocalDateTime.class);
        Root<ProfitSnapshot> ps2 = maxProfitDate.from(ProfitSnapshot.class);

        Expression<LocalDateTime> createdAtExpr =
                ps2.get("createdAt").as(LocalDateTime.class);

        maxProfitDate.select(cb.greatest(createdAtExpr))
                .where(cb.equal(ps2.get("batch"), batch));

        Subquery<Double> profitSub = cq.subquery(Double.class);
        Root<ProfitSnapshot> ps = profitSub.from(ProfitSnapshot.class);

        profitSub.select(ps.get("expectedProfit"))
                .where(
                        cb.equal(ps.get("batch"), batch),
                        cb.equal(ps.get("createdAt"), maxProfitDate)
                );

        /* -------- LATEST DAILY WEIGHT -------- */
        Subquery<Integer> maxDay = cq.subquery(Integer.class);
        Root<DailyEntry> d2 = maxDay.from(DailyEntry.class);

        maxDay.select(cb.max(d2.get("dayNo")))
                .where(cb.equal(d2.get("batch"), batch));

        Subquery<Double> weightSub = cq.subquery(Double.class);
        Root<DailyEntry> d1 = weightSub.from(DailyEntry.class);

        weightSub.select(d1.get("avgWeightG"))
                .where(
                        cb.equal(d1.get("batch"), batch),
                        cb.equal(d1.get("dayNo"), maxDay)
                );

        /* -------- HEALTH RISK -------- */
        Subquery<String> healthSub = cq.subquery(String.class);
        Root<HealthAlert> ha = healthSub.from(HealthAlert.class);

        Expression<String> riskExpr =
                ha.get("riskLevel").as(String.class);

        healthSub
                .select(cb.greatest(riskExpr))
                .where(cb.equal(ha.get("batch"), batch));


        /* ================= SELECT ================= */
        cq.select(cb.construct(
                BatchCardProjection.class,
                batch.get("batchId"),
                batch.get("batchCode"),
                batch.get("birdType"),
                batch.get("startDate"),
                batch.get("status"),
                weightSub,
                healthSub,
                profitSub
        ));

        /* ================= WHERE ================= */
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(farm.get("userId"), userId));

        if (status != null) {
            predicates.add(cb.equal(batch.get("status"), status));
        }

        if (birdType != null) {
            predicates.add(cb.equal(batch.get("birdType"), birdType));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        /* ================= SORT ================= */
        Expression<?> orderExpr;

        if ("profit".equalsIgnoreCase(sortBy)) {
            orderExpr = profitSub;
        }
        else if ("age".equalsIgnoreCase(sortBy)) {
            // Age calculation Java/service layer me hogi
            orderExpr = batch.get("startDate");
        }
        else {
            orderExpr = batch.get("createdAt");
        }

        cq.orderBy(
                "asc".equalsIgnoreCase(sortDir)
                        ? cb.asc(orderExpr)
                        : cb.desc(orderExpr)
        );

        /* ================= EXECUTE ================= */
        TypedQuery<BatchCardProjection> query = em.createQuery(cq);
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList();
    }

    /* ================= COUNT ================= */
    public long countBatches(UUID userId, String status, String birdType) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<Batch> batch = cq.from(Batch.class);
        Join<Batch, Farm> farm = batch.join("farm");

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(farm.get("userId"), userId));

        if (status != null) {
            predicates.add(cb.equal(batch.get("status"), status));
        }

        if (birdType != null) {
            predicates.add(cb.equal(batch.get("birdType"), birdType));
        }

        cq.select(cb.count(batch))
                .where(predicates.toArray(new Predicate[0]));

        return em.createQuery(cq).getSingleResult();
    }
}
