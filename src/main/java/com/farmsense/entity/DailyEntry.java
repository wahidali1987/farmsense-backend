package com.farmsense.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "daily_entries")
@SQLDelete(sql = "UPDATE batches SET deleted_at = now(), deleted_by = ? WHERE batch_id = ?")
@Where(clause = "deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyEntry extends BaseEntity {

    @Id
    @Column(name = "entry_id")
    private UUID entryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private Batch batch;

    @Column(name = "day_no", nullable = false)
    private Integer dayNo;

    @Column(name = "avg_weight_g", precision = 6, scale = 2)
    private BigDecimal avgWeightG;

    @Column(name = "feed_per_bird_g", precision = 6, scale = 2)
    private BigDecimal feedPerBirdG;

    @Column(name = "water_per_bird_ml", precision = 6, scale = 2)
    private BigDecimal waterPerBirdMl;

    @Column(name = "mortality_count")
    private Integer mortalityCount;
}



