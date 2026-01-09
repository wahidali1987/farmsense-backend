package com.farmsense.repository;

import com.farmsense.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BatchJpaRepository extends JpaRepository<Batch, UUID> {

    Optional<Batch> findByBatchCode(String batchCode);
}
