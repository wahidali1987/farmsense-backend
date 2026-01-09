package com.farmsense.repository;

import com.farmsense.dto.BatchSummaryDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class BatchRepository {

    private final EntityManager em;

    /* ================= SINGLE BATCH CARD ================= */

    public Object[] fetchBatchCard(UUID batchId, UUID userId) {
        try {
            return (Object[]) em.createNativeQuery("""
                                WITH latest_daily AS (
                                    SELECT DISTINCT ON (batch_id)
                                        batch_id, avg_weight_g, feed_per_bird_g
                                    FROM daily_entries
                                    ORDER BY batch_id, day_no DESC
                                ),
                                latest_profit AS (
                                    SELECT DISTINCT ON (batch_id)
                                        batch_id, total_cost, expected_profit
                                    FROM profit_snapshots
                                    ORDER BY batch_id, created_at DESC
                                ),
                                health_summary AS (
                                    SELECT batch_id, MAX(risk_level) AS risk_level
                                    FROM health_alerts
                                    GROUP BY batch_id
                                )
                                SELECT
                                    b.batch_id,
                                    b.batch_code,
                                    b.bird_type,
                                    (CURRENT_DATE - b.start_date),
                                    b.status,
                                    ld.avg_weight_g,
                                    fp.recommended_feed_g,
                                    ld.feed_per_bird_g,
                                    hs.risk_level,
                                    lp.total_cost,
                                    lp.expected_profit
                                FROM batches b
                                JOIN farms f ON f.farm_id = b.farm_id
                                LEFT JOIN latest_daily ld ON ld.batch_id = b.batch_id
                                LEFT JOIN feed_plan_master fp ON fp.day_no = (CURRENT_DATE - b.start_date)
                                LEFT JOIN health_summary hs ON hs.batch_id = b.batch_id
                                LEFT JOIN latest_profit lp ON lp.batch_id = b.batch_id
                                WHERE b.batch_id = :batchId
                                  AND f.user_id = :userId
                            """)
                    .setParameter("batchId", batchId)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (jakarta.persistence.NoResultException ex) {
            return null;
        }
    }

    /* ================= COUNT FOR PAGINATION ================= */

    public long countBatches(UUID userId, String status, String birdType) {

        StringBuilder sql = new StringBuilder("""
                    SELECT COUNT(*)
                    FROM batches b
                    JOIN farms f ON f.farm_id = b.farm_id
                    WHERE f.user_id = :userId
                """);

        if (status != null) {
            sql.append(" AND b.status = :status ");
        }
        if (birdType != null) {
            sql.append(" AND b.bird_type = :birdType ");
        }

        var q = em.createNativeQuery(sql.toString());
        q.setParameter("userId", userId);

        if (status != null) {
            q.setParameter("status", status);
        }
        if (birdType != null) {
            q.setParameter("birdType", birdType);
        }

        return ((Number) q.getSingleResult()).longValue();
    }

    /* ================= SUMMARY COUNTS ================= */

    public BatchSummaryDto fetchSummary(UUID userId) {

        Object[] r = (Object[]) em.createNativeQuery("""
                            SELECT
                                COUNT(*),
                                COUNT(*) FILTER (WHERE b.status = 'ACTIVE'),
                                COUNT(*) FILTER (WHERE b.status = 'COMPLETED')
                            FROM batches b
                            JOIN farms f ON f.farm_id = b.farm_id
                            WHERE f.user_id = :userId
                        """)
                .setParameter("userId", userId)
                .getSingleResult();

        return new BatchSummaryDto(
                ((Number) r[0]).intValue(),
                ((Number) r[1]).intValue(),
                ((Number) r[2]).intValue()
        );
    }

    /* ================= MAIN LIST QUERY (FIXED) ================= */

    public List<Object[]> fetchBatchCards(
            UUID userId,
            String status,
            String birdType,
            String orderBy,
            String orderDir,
            int offset,
            int limit
    ) {

        String orderColumn =
                "profit".equals(orderBy) ? "lp.expected_profit" :
                        "age".equals(orderBy) ? "(CURRENT_DATE - b.start_date)" :
                                "b.created_at";

        String direction = "asc".equalsIgnoreCase(orderDir) ? "ASC" : "DESC";

        StringBuilder sql = new StringBuilder("""
                    WITH latest_daily AS (
                        SELECT DISTINCT ON (batch_id)
                            batch_id, avg_weight_g, feed_per_bird_g
                        FROM daily_entries
                        ORDER BY batch_id, day_no DESC
                    ),
                    latest_profit AS (
                        SELECT DISTINCT ON (batch_id)
                            batch_id, total_cost, expected_profit
                        FROM profit_snapshots
                        ORDER BY batch_id, created_at DESC
                    ),
                    health_summary AS (
                        SELECT batch_id, MAX(risk_level) AS risk_level
                        FROM health_alerts
                        GROUP BY batch_id
                    )
                    SELECT
                        b.batch_id,
                        b.batch_code,
                        b.bird_type,
                        (CURRENT_DATE - b.start_date),
                        b.status,
                        ld.avg_weight_g,
                        fp.recommended_feed_g,
                        ld.feed_per_bird_g,
                        hs.risk_level,
                        lp.total_cost,
                        lp.expected_profit
                    FROM batches b
                    JOIN farms f ON f.farm_id = b.farm_id
                    LEFT JOIN latest_daily ld ON ld.batch_id = b.batch_id
                    LEFT JOIN feed_plan_master fp ON fp.day_no = (CURRENT_DATE - b.start_date)
                    LEFT JOIN health_summary hs ON hs.batch_id = b.batch_id
                    LEFT JOIN latest_profit lp ON lp.batch_id = b.batch_id
                    WHERE f.user_id = :userId
                """);

        if (status != null) {
            sql.append(" AND b.status = :status ");
        }
        if (birdType != null) {
            sql.append(" AND b.bird_type = :birdType ");
        }

        sql.append(" ORDER BY ")
                .append(orderColumn)
                .append(" ")
                .append(direction)
                .append(" LIMIT :limit OFFSET :offset");

        var query = em.createNativeQuery(sql.toString())
                .setParameter("userId", userId)
                .setParameter("limit", limit)
                .setParameter("offset", offset);

        if (status != null) {
            query.setParameter("status", status);
        }
        if (birdType != null) {
            query.setParameter("birdType", birdType);
        }

        return query.getResultList();
    }
}
