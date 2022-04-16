package com.upworks.ipfilterservice.repository;

import com.upworks.ipfilterservice.entity.FilterRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilterRuleRepository extends JpaRepository<FilterRuleEntity, Long> {

  @Query("SELECT t FROM FilterRuleEntity t WHERE t.sourceCidr = ?1 AND t.destinationCidr = ?2")
  List<FilterRuleEntity> findBy(String sourceIp, String destinationIp);
}
