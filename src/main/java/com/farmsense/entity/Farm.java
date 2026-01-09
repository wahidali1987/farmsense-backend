package com.farmsense.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "farms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Farm extends BaseEntity {

    @Id
    @Column(name = "farm_id")
    private UUID farmId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "farm_name")
    private String farmName;

    @Column(name = "location")
    private String location;
}
