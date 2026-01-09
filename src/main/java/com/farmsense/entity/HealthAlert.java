package com.farmsense.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "health_alerts")
@SQLDelete(sql = "UPDATE batches SET deleted_at = now(), deleted_by = ? WHERE batch_id = ?")
@Where(clause = "deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthAlert extends BaseEntity {

    @Id
    @Column(name = "alert_id")
    private UUID alertId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private Batch batch;

    @Column(name = "risk_level", length = 10)
    private String riskLevel; // LOW / MEDIUM / HIGH

    @Column(name = "message")
    private String message;

    @Column(name = "confidence", precision = 4, scale = 2)
    private BigDecimal confidence;

    @Column(name = "source")
    private String source;

    @Column(name = "is_reviewed")
    private Boolean isReviewed;
}
