package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.CommunityHealthWorkers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityHealthWorkersRepository extends JpaRepository<CommunityHealthWorkers, Long> {

    Optional<CommunityHealthWorkers> findByCode(String code);

    List<CommunityHealthWorkers> findByStatus(CommunityHealthWorkers.Status status);

    List<CommunityHealthWorkers> findByHospitalId(Long hospitalId);

    List<CommunityHealthWorkers> findByStatusAndHospitalId(CommunityHealthWorkers.Status status, Long hospitalId);

    List<CommunityHealthWorkers> findByRegion(String region);

    List<CommunityHealthWorkers> findByCity(String city);

    List<CommunityHealthWorkers> findByRegionAndStatus(String region, CommunityHealthWorkers.Status status);

    @Query("SELECT c FROM CommunityHealthWorkers c WHERE " +
           "(:region IS NULL OR c.region = :region) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:city IS NULL OR c.city = :city)")
    Page<CommunityHealthWorkers> searchWithFilters(
            @Param("region") String region,
            @Param("status") CommunityHealthWorkers.Status status,
            @Param("city") String city,
            Pageable pageable);

    @Query("SELECT COUNT(c) FROM CommunityHealthWorkers c WHERE c.region = :region")
    Long countByRegion(@Param("region") String region);

    @Query("SELECT COUNT(c) FROM CommunityHealthWorkers c WHERE c.status = :status")
    Long countByStatus(@Param("status") CommunityHealthWorkers.Status status);
}


