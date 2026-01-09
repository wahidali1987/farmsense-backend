package com.farmsense.entity;

import com.farmsense.enums.BatchStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "batches")
@SQLDelete(sql = "UPDATE batches SET deleted_at = now(), deleted_by = ? WHERE batch_id = ?")
@Where(clause = "deleted_at IS NULL")
@Data
public class Batch extends BaseEntity {

    @Id
    @Column(name = "batch_id")
    private UUID batchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id")
    private Farm farm;

    private String batchCode;
    private String birdType;

    private LocalDate startDate;

    @Enumerated(EnumType.STRING)
    private BatchStatus status;

    private int initialBirds;
    private int currentBirds;
}

