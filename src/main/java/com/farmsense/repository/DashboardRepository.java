package com.farmsense.repository;

import com.farmsense.dto.DashboardBatchDto;
import com.farmsense.dto.DashboardResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DashboardRepository {

    private final EntityManager em;

    /* ================= SUMMARY ================= */

    public DashboardResponse.Summary fetchSummary(UUID userId) {

        Object[] r = (Object[]) em.createNativeQuery("""
        SELECT
            COUNT(b.batch_id),
            COUNT(*) FILTER (WHERE b.status = 'ACTIVE'),
            COALESCE(SUM(b.current_birds), 0),
            COALESCE(MAX(h.risk_level), 'LOW'),
            COALESCE(SUM(ps.expected_profit), 0)
        FROM batches b
        JOIN farms f ON f.farm_id = b.farm_id
        LEFT JOIN health_alerts h ON h.batch_id = b.batch_id
        LEFT JOIN LATERAL (
            SELECT expected_profit
            FROM profit_snapshots p
            WHERE p.batch_id = b.batch_id
            ORDER BY p.created_at DESC
            LIMIT 1
        ) ps ON true
        WHERE f.user_id = :userId
    """)
                .setParameter("userId", userId)
                .getSingleResult();

        return DashboardResponse.Summary.builder()
                .totalBatches(((Number) r[0]).intValue())
                .activeBatches(((Number) r[1]).intValue())
                .totalAliveBirds(((Number) r[2]).intValue())
                .overallRisk((String) r[3])
                .todayEstimatedProfit(((Number) r[4]).doubleValue())
                .build();
    }


    /* ================= BATCH LIST ================= */

    public List<DashboardResponse.BatchCard> fetchBatchCards(UUID userId) {

        List<Object[]> rows = em.createNativeQuery("""
        WITH latest_daily AS (
            SELECT DISTINCT ON (batch_id)
                batch_id, avg_weight_g, feed_per_bird_g
            FROM daily_entries
            ORDER BY batch_id, day_no DESC
        ),
        latest_profit AS (
            SELECT DISTINCT ON (batch_id)
                batch_id, total_cost, expected_revenue, expected_profit, cost_per_bird
            FROM profit_snapshots
            ORDER BY batch_id, created_at DESC
        ),
        health_summary AS (
            SELECT
                batch_id,
                COUNT(*) FILTER (WHERE is_reviewed = false) AS active_alerts,
                MAX(risk_level) AS risk_level,
                MAX(message) AS last_message,
                MAX(confidence) AS confidence
            FROM health_alerts
            GROUP BY batch_id
        )
        SELECT
            b.batch_id,
            b.batch_code,
            b.bird_type,
            (CURRENT_DATE - b.start_date),
            b.status,

            b.initial_birds,
            b.current_birds,

            ld.avg_weight_g,
            fp.recommended_feed_g,

            ld.feed_per_bird_g,

            hs.risk_level,
            hs.active_alerts,
            hs.last_message,
            hs.confidence,

            lp.total_cost,
            lp.expected_revenue,
            lp.expected_profit,
            lp.cost_per_bird

        FROM batches b
        JOIN farms f ON f.farm_id = b.farm_id
        LEFT JOIN latest_daily ld ON ld.batch_id = b.batch_id
        LEFT JOIN feed_plan_master fp ON fp.day_no = (CURRENT_DATE - b.start_date)
        LEFT JOIN health_summary hs ON hs.batch_id = b.batch_id
        LEFT JOIN latest_profit lp ON lp.batch_id = b.batch_id
        WHERE f.user_id = :userId
        ORDER BY b.created_at DESC
    """)
                .setParameter("userId", userId)
                .getResultList();

        return rows.stream().map(this::mapBatchCard).toList();
    }

    private DashboardResponse.BatchCard mapBatchCard(Object[] r) {

        int initial = ((Number) r[5]).intValue();
        int alive = ((Number) r[6]).intValue();

        double mortalityPercent =
                initial == 0 ? 0 :
                        ((initial - alive) * 100.0) / initial;

        double avgWeight = r[7] == null ? 0 : ((Number) r[7]).doubleValue();
        double expectedWeight = r[8] == null ? 0 : ((Number) r[8]).doubleValue();
        double deviation = expectedWeight == 0 ? 0 :
                ((avgWeight - expectedWeight) * 100) / expectedWeight;

        return DashboardResponse.BatchCard.builder()
                .batchId((UUID) r[0])
                .batchCode((String) r[1])
                .birdType((String) r[2])
                .ageInDays(((Number) r[3]).intValue())
                .status((String) r[4])

                .birds(DashboardResponse.Birds.builder()
                        .initial(initial)
                        .alive(alive)
                        .mortalityPercent(mortalityPercent)
                        .build())

                .growth(DashboardResponse.Growth.builder()
                        .avgWeightG(avgWeight)
                        .expectedWeightG(expectedWeight)
                        .growthStatus(avgWeight >= expectedWeight ? "ON_TRACK" : "SLOW")
                        .deviationPercent(deviation)
                        .build())

                .feed(DashboardResponse.Feed.builder()
                        .recommendedPerBirdG(expectedWeight)
                        .actualPerBirdG(r[9] == null ? 0 : ((Number) r[9]).doubleValue())
                        .deviationPercent(0)
                        .status("OK")
                        .build())

                .health(DashboardResponse.Health.builder()
                        .riskLevel((String) r[10])
                        .activeAlerts(r[11] == null ? 0 : ((Number) r[11]).intValue())
                        .lastAlertMessage((String) r[12])
                        .confidence(r[13] == null ? 0 : ((Number) r[13]).doubleValue())
                        .build())

                .profit(DashboardResponse.Profit.builder()
                        .totalCost(r[14] == null ? 0 : ((Number) r[14]).doubleValue())
                        .expectedRevenue(r[15] == null ? 0 : ((Number) r[15]).doubleValue())
                        .expectedProfit(r[16] == null ? 0 : ((Number) r[16]).doubleValue())
                        .costPerBird(r[17] == null ? 0 : ((Number) r[17]).doubleValue())
                        .build())

                .actions(DashboardResponse.Actions.builder()
                        .viewDetails(true)
                        .priority(mortalityPercent > 1 ? "ATTENTION" : "NORMAL")
                        .build())

                .build();
    }


}
