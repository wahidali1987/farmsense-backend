package com.farmsense.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "profit_snapshots")
@SQLDelete(sql = "UPDATE batches SET deleted_at = now(), deleted_by = ? WHERE batch_id = ?")
@Where(clause = "deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfitSnapshot extends BaseEntity {

    @Id
    @Column(name = "snapshot_id")
    private UUID snapshotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private Batch batch;

    @Column(name = "total_cost", precision = 12, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "expected_revenue", precision = 12, scale = 2)
    private BigDecimal expectedRevenue;

    @Column(name = "expected_profit", precision = 12, scale = 2)
    private BigDecimal expectedProfit;

    @Column(name = "cost_per_bird", precision = 8, scale = 2)
    private BigDecimal costPerBird;
}
